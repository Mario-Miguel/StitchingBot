package es.uniovi.eii.stitchingbot.ui.logos

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.TAG
import es.uniovi.eii.stitchingbot.canvas.MyCanvasView
import es.uniovi.eii.stitchingbot.canvas.tools.*
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.database.SewingMachinedatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.ui.sewingMachines.SewingMachineDetailsFragment
import kotlinx.android.synthetic.main.fragment_create_logo.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


private const val CREATION_MODE = "creation"
private const val LOGO = "logo"

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class CreateLogoFragment : Fragment() {

    private var isCreation: Boolean = false
    lateinit var canvas: MyCanvasView

    private lateinit var logo: Logo
    private lateinit var currentPhotoPath: String
    private lateinit var currentPhotoUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_create_logo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.canvas = canvasView
        initializeButtons()

        if (arguments != null) {
            isCreation = requireArguments().getBoolean(CREATION_MODE)
            logo = requireArguments().getParcelable(LOGO)!!
            showLogoImage()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(!isCreation){
            menu.clear()
            inflater.inflate(R.menu.delete_button, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete){
            deleteLogo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeButtons(){
        btnCircleDrawing.setOnClickListener {
            canvas.tool = CircleTool()
        }

        btnSquareDrawing.setOnClickListener {
            canvas.tool = SquareTool()
        }
        btnFreeDrawing.setOnClickListener {
            canvas.tool = FreeDrawingTool()
        }
        btnLineDrawing.setOnClickListener {
            canvas.tool = LineTool()
        }

        btnEraseOption.setOnClickListener {
            canvas.tool = EraseTool()
        }

        btnDone.setOnClickListener { if(isCreation) saveLogo() else modifyLogo() }
        btnSew.setOnClickListener { loadSummaryScreen() }
    }

    private fun showLogoImage(){
        currentPhotoUri = Uri.parse(logo.imgUrl)
        val image = getImageFromUri(currentPhotoUri)!!
        canvas.setImage(image)
    }

    private fun modifyLogo(){
        val bitmap = canvas.getBitmapToSave()

        currentPhotoUri = Uri.parse(logo.imgUrl)
        val file = File(currentPhotoUri.path!!)

        copyImage(bitmap, file)

        logo = Logo(title = "Try", imgUrl = currentPhotoUri.toString())

        val databaseConnection = LogoDatabaseConnection(requireContext())
        databaseConnection.open()
        databaseConnection.update(logo)
        databaseConnection.close()
        Toast.makeText(requireContext(), "Logo modificado", Toast.LENGTH_LONG).show()
        Log.i(
            TAG,
            "Modificado logo: ${logo.title} - ${logo.category} - ${logo.imgUrl}"
        )

    }

    private fun saveLogo() {
        val bitmap = canvas.getBitmapToSave()
        val file = createImageFile()

        copyImage(bitmap, file)

        currentPhotoUri = Uri.fromFile(file)

        logo = Logo(title = "Try", imgUrl = currentPhotoUri.toString())

        val databaseConnection = LogoDatabaseConnection(requireContext())
        databaseConnection.open()
        databaseConnection.insert(logo)
        databaseConnection.close()
        Toast.makeText(requireContext(), "Logo creado", Toast.LENGTH_LONG).show()
        Log.i(
            TAG,
            "Insertado logo: ${logo.title} - ${logo.category} - ${logo.imgUrl}"
        )
    }

    private fun deleteLogo(){
        deleteImageFile()
        val databaseConnection = LogoDatabaseConnection(requireContext())
        databaseConnection.open()
        databaseConnection.delete(logo)
        databaseConnection.close()
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
    }

    private fun deleteImageFile(){
        //TODO hacer mensaje de confirmación
        val file = File(currentPhotoUri.path!!)
        if (file.exists()){
            file.delete()
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "LOGO_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun copyImage(bitmap: Bitmap?, createdImageFile: File) {
        val destination = FileOutputStream(createdImageFile)

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, destination)
            destination.flush()
        }

        destination.close()
    }


    private fun loadSummaryScreen(){
        val bundle = bundleOf("logo" to logo)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_summary, bundle)
    }

    //TODO eliminar cuando termine las pruebas de parsear
    private fun getImageFromUri(imageUri: Uri?): Bitmap? {
        var image: Bitmap
        if (imageUri != null) {
            requireActivity().contentResolver.openFileDescriptor(imageUri, "r")
                .use { pfd ->
                    if (pfd != null) {
                        val matrix = Matrix()
                        matrix.postRotate(180F)
                        image = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                        //Rotate bitmap
                        val ei = requireActivity().contentResolver.openInputStream(imageUri)?.let {
                            ExifInterface(
                                it
                            )
                        }
                        val orientation: Int = ei!!.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )

                        return when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90F)
                            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180F)
                            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270F)
                            ExifInterface.ORIENTATION_NORMAL -> image
                            else -> image
                        }
                    }
                }

        }
        return null
    }


    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isCreation Parameter 1.
         * @return A new instance of fragment SewingMachineDetailsFragment.
         */
        @JvmStatic
        fun newInstance(isCreation: Boolean, logo: Logo) =
            SewingMachineDetailsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(CREATION_MODE, isCreation)
                    putParcelable(LOGO, logo)
                }
            }
    }

}