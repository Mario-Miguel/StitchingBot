package es.uniovi.eii.stitchingbot.ui.logos

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.TAG
import es.uniovi.eii.stitchingbot.canvas.MyCanvasView
import es.uniovi.eii.stitchingbot.canvas.tools.*
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.ui.sewingMachines.SewingMachineDetailsFragment
import es.uniovi.eii.stitchingbot.util.ImageManager
import kotlinx.android.synthetic.main.fragment_create_logo.*
import java.io.File


private const val CREATION_MODE = "creation"
private const val LOGO = "logo"

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class CreateLogoFragment : Fragment() {

    private var isCreation: Boolean = true
    lateinit var canvas: MyCanvasView
    private val imageManager = ImageManager()

    private lateinit var logo: Logo
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
            if(logo.id>=0)
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
        val image = imageManager.getImageFromUri(currentPhotoUri, requireActivity())!!
        canvas.setImage(image)
    }


    private fun modifyLogo(){
        val bitmap = canvas.getBitmapToSave()
        currentPhotoUri = Uri.parse(logo.imgUrl)
        val file = File(currentPhotoUri.path!!)

        imageManager.copyImage(bitmap, file)

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
        val file = imageManager.createImageFile(requireActivity())

        imageManager.copyImage(bitmap, file)

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
        imageManager.deleteImageFile(currentPhotoUri)
        val databaseConnection = LogoDatabaseConnection(requireContext())
        databaseConnection.open()
        databaseConnection.delete(logo)
        databaseConnection.close()
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
    }


    private fun loadSummaryScreen(){
        val bundle = bundleOf("logo" to logo)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_summary, bundle)
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