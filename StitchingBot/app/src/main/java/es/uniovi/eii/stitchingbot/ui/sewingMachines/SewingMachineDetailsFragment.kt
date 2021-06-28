package es.uniovi.eii.stitchingbot.ui.sewingMachines

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.TAG
import es.uniovi.eii.stitchingbot.database.SewingMachinedatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine
import kotlinx.android.synthetic.main.fragment_sewing_machine_details.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


private const val CREATION_MODE = "creation"
private const val SEWING_MACHINE = "machine"

@RequiresApi(Build.VERSION_CODES.Q)
private val PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_MEDIA_LOCATION
)
private const val PERMISSION_ALL = 73
private const val REQUEST_IMAGE_CAPTURE = 0
private const val REQUEST_IMAGE_PICK = 1

/**
 * A simple [Fragment] subclass.
 * Use the [SewingMachineDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SewingMachineDetailsFragment : Fragment() {
    private var isCreation: Boolean = false

    private var sewingMachine = SewingMachine()
    private lateinit var currentPhotoPath: String
    private lateinit var currentPhotoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCreation = it.getBoolean(CREATION_MODE)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sewing_machine_details, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null)
            isCreation = requireArguments().getBoolean(CREATION_MODE)

        loadDefaultScreen()

        if (!isCreation) {
            loadUpdateScreen()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                    sewingMachine.imgUrl = currentPhotoUri.toString()
                    imgSewingMachineDetails.setImageBitmap(getImageFromUri(currentPhotoUri))
                }
                REQUEST_IMAGE_PICK -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.data
                    var currentPhotoFile:File

                    if(!sewingMachine.imgUrl.isNullOrBlank()) {
                        currentPhotoFile = createImageFile()
                        currentPhotoUri = Uri.fromFile(currentPhotoFile)
                    }
                    else{
                        currentPhotoFile = File(currentPhotoUri.path)
                    }

                    copyImage(getImageFromUri(selectedImage), currentPhotoFile)

                    sewingMachine.imgUrl = currentPhotoUri.toString()
                    imgSewingMachineDetails.setImageBitmap(getImageFromUri(currentPhotoUri))
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadDefaultScreen(){
        createSpinner()
        btnSewingMachineAction.setOnClickListener { confirmButtonAction() }
        imgSewingMachineDetails.setOnClickListener {
            if (!hasPermissions(requireContext(), *PERMISSIONS)) {
                Log.i(TAG, "not has permissions")
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_ALL)
            } else
                selectImage(requireContext())
        }
    }


    private fun loadUpdateScreen(){
        sewingMachine = requireArguments().getParcelable("machine")!!
        Log.i(TAG, "Sewing machine: ${sewingMachine.name}")
        btnSewingMachineAction.text = "Modificar"
        txtSewingMachineName.editText!!.setText(sewingMachine.name)
        spinnerSewingMachineDetails.setSelection(if (sewingMachine.hasPedal) 1 else 0)
        if(sewingMachine.imgUrl?.isNotEmpty() == true) {
            currentPhotoUri=Uri.parse(sewingMachine.imgUrl)
            imgSewingMachineDetails.setImageBitmap(getImageFromUri(currentPhotoUri))
        }
        showDeleteButton()
    }


    private fun showDeleteButton(){
        btnDeleteSewingMachine.visibility=View.VISIBLE
        btnDeleteSewingMachine.isClickable=true
        btnDeleteSewingMachine.setOnClickListener { deleteMachine() }
    }


    private fun createSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinnerSewingMachineDetails,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerSewingMachineDetails.adapter = adapter
        }
    }


    private fun confirmButtonAction() {
        if (checkFields()) {
            sewingMachine.name = txtSewingMachineName.editText!!.text.toString()
            sewingMachine.hasPedal = spinnerSewingMachineDetails.selectedItemPosition > 0

            val databaseConnection = SewingMachinedatabaseConnection(requireContext())
            if (arguments?.getBoolean(CREATION_MODE) == true) {
                databaseConnection.open()
                databaseConnection.insert(sewingMachine)
                databaseConnection.close()
                Toast.makeText(requireContext(), "Máquina creada", Toast.LENGTH_LONG).show()
                Log.i(
                    TAG,
                    "Insertada maquina de coser: ${sewingMachine.name} - ${sewingMachine.hasPedal} - ${sewingMachine.imgUrl}"
                )
            }
            else{
                databaseConnection.open()
                databaseConnection.update(sewingMachine)
                databaseConnection.close()

                Toast.makeText(requireContext(), "Máquina modificada", Toast.LENGTH_LONG).show()
                Log.i(
                    TAG,
                    "Update maquina de coser: ${sewingMachine.name} - ${sewingMachine.hasPedal} - ${sewingMachine.imgUrl}"
                )
            }

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.popBackStack()
        }
    }

    private fun checkFields(): Boolean {
        return txtSewingMachineName.editText!!.text.isNotBlank() && txtSewingMachineName.editText!!.text.isNotEmpty()
    }


    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_ALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.requireContext(), "Permission granted.", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    Toast.makeText(
                        this.requireContext(),
                        "Permission must be granted to use the application.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

//####################################################################################################
                val perms: MutableMap<String, Int> = HashMap()

                // Initialize the map with both permissions
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {

                        Log.d(TAG, "all permissions granted")
                        selectImage(requireContext())

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")

                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.CAMERA
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
                            showDialogOK(
                                "Camera and Storage Read Permission required for this app"
                            ) { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> ActivityCompat.requestPermissions(
                                        requireActivity(),
                                        PERMISSIONS,
                                        PERMISSION_ALL
                                    )
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Go to settings and enable permissions",
                                Toast.LENGTH_LONG
                            ).show()
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }


    private fun deleteMachine(){
        deleteImageFile()
        val databaseConnection = SewingMachinedatabaseConnection(requireContext())
        databaseConnection.open()
        databaseConnection.delete(sewingMachine)
        databaseConnection.close()
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
    }

    private fun deleteImageFile(){
        //TODO hacer mensaje de confirmación
        val file = File(currentPhotoUri.path)
        if (file.exists()){
            file.delete()
        }

    }


    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your sewing machine picture")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    dispatchTakePictureIntent()
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun copyImage(bitmap: Bitmap?, createdImageFile: File) {
        val destination = FileOutputStream(createdImageFile)
        if(bitmap!=null){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, destination)
            destination.flush()
        }

        destination.close()
    }

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


    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "SEWMACH_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.i(TAG, "Error creando archivo")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    Log.i(TAG, it.absolutePath)
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "es.uniovi.eii.stitchingbot",
                        it
                    )
                    currentPhotoUri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }




    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
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
        fun newInstance(isCreation: Boolean, sewingMachine: SewingMachine) =
            SewingMachineDetailsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(CREATION_MODE, isCreation)
                    putParcelable(SEWING_MACHINE, sewingMachine)
                }
            }
    }
}