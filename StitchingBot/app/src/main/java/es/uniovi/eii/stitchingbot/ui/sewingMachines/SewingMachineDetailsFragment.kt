package es.uniovi.eii.stitchingbot.ui.sewingMachines

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.TAG
import es.uniovi.eii.stitchingbot.database.SewingMachinedatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.util.ImageManager
import es.uniovi.eii.stitchingbot.util.ShowDialog
import kotlinx.android.synthetic.main.fragment_sewing_machine_details.*
import java.io.File
import java.io.IOException


private const val CREATION_MODE = "creation"
private const val SEWING_MACHINE = "machine"

@RequiresApi(Build.VERSION_CODES.Q)
private val PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_MEDIA_LOCATION
)


/**
 * A simple [Fragment] subclass.
 * Use the [SewingMachineDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SewingMachineDetailsFragment : Fragment() {
    private var isCreation: Boolean = false
    private var sewingMachine = SewingMachine()

    private lateinit var currentPhotoUri: Uri
    private lateinit var imageManager: ImageManager

    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { if (it != null) imagePick(it) }

    private val getImageFromCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { if (it) imageTake() }

    private val getPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var granted = true
            val nonGrantedPermissions = mutableListOf<String>()
            permissions.entries.forEach {
                if (!it.value) {
                    granted = false
                    if(it.key == "android.permission.CAMERA")
                        nonGrantedPermissions.add("Camera")
                    else{
                        nonGrantedPermissions.add("Almacenamiento")
                    }
                }
            }

            if (granted) {
                Log.d(TAG, "all permissions granted")
                selectImage(requireContext())
            } else {
                showNotGrantedPermissionsMessage(nonGrantedPermissions)
            }
        }


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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_sewing_machine_details, container, false)
    }


    private fun showNotGrantedPermissionsMessage(nonGrantedPermissions: MutableList<String>) {
        val permissionsString = nonGrantedPermissions.reduce { acc, str -> "$acc - $str" }

        ShowDialog.showDialogOK(
            requireContext(),
            "Se necesitan los permisos: $permissionsString"
        ) { _, _ -> }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageManager = ImageManager()

        if (arguments != null)
            isCreation = requireArguments().getBoolean(CREATION_MODE)

        loadDefaultScreen()

        if (!isCreation) {
            loadUpdateScreen()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!isCreation) {
            menu.clear()
            inflater.inflate(R.menu.delete_button, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            deleteMachine()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun imagePick(selectedImage: Uri) {
        val currentPhotoFile: File

        if (sewingMachine.imgUrl.isNullOrBlank()) {
            currentPhotoFile = imageManager.createImageFile(requireActivity())
            currentPhotoUri = Uri.fromFile(currentPhotoFile)
        } else {
            currentPhotoUri = Uri.parse(sewingMachine.imgUrl)
            currentPhotoFile = File(currentPhotoUri.path!!)
        }

        imageManager.copyImage(
            imageManager.getImageFromUri(
                selectedImage,
                requireActivity()
            ), currentPhotoFile
        )

        sewingMachine.imgUrl = currentPhotoUri.toString()
        imgSewingMachineDetails.setImageBitmap(
            imageManager.getImageFromUri(
                currentPhotoUri,
                requireActivity()
            )
        )
    }

    private fun imageTake() {
        sewingMachine.imgUrl = currentPhotoUri.toString()
        imgSewingMachineDetails.setImageBitmap(
            imageManager.getImageFromUri(
                currentPhotoUri,
                requireActivity()
            )
        )
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadDefaultScreen() {
        btnSewingMachineAction.setOnClickListener { confirmButtonAction() }
        imgSewingMachineDetails.setOnClickListener {
            getPermissions.launch(PERMISSIONS)
        }
        txtMotorSteps.editText!!.setText(sewingMachine.motorSteps.toString())
    }

    private fun loadUpdateScreen() {
        sewingMachine = requireArguments().getParcelable("machine")!!
        Log.i(TAG, "Sewing machine: ${sewingMachine.name}")
        btnSewingMachineAction.text = "Modificar"
        txtSewingMachineName.editText!!.setText(sewingMachine.name)
        txtMotorSteps.editText!!.setText(sewingMachine.motorSteps.toString())
        if (sewingMachine.imgUrl?.isNotEmpty() == true) {
            currentPhotoUri = Uri.parse(sewingMachine.imgUrl)
            imgSewingMachineDetails.setImageBitmap(
                imageManager.getImageFromUri(
                    currentPhotoUri,
                    requireActivity()
                )
            )
        }
    }

    private fun confirmButtonAction() {
        if (checkFields()) {
            sewingMachine.name = txtSewingMachineName.editText!!.text.toString()
            sewingMachine.motorSteps = txtMotorSteps.editText!!.text.toString().toInt()

            val databaseConnection = SewingMachinedatabaseConnection(requireContext())
            if (arguments?.getBoolean(CREATION_MODE) == true) {
                databaseConnection.open()
                databaseConnection.insert(sewingMachine)
                databaseConnection.close()
                Toast.makeText(requireContext(), "Máquina creada", Toast.LENGTH_LONG).show()
                Log.i(
                    TAG,
                    "Insertada maquina de coser: ${sewingMachine.name} - ${sewingMachine.motorSteps} - ${sewingMachine.imgUrl}"
                )
            } else {
                databaseConnection.open()
                databaseConnection.update(sewingMachine)
                databaseConnection.close()

                Toast.makeText(requireContext(), "Máquina modificada", Toast.LENGTH_LONG).show()
                Log.i(
                    TAG,
                    "Update maquina de coser: ${sewingMachine.name} - ${sewingMachine.motorSteps} - ${sewingMachine.imgUrl}"
                )
            }

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Rellene todos los campos", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkFields(): Boolean {
        return txtSewingMachineName.editText!!.text.isNotBlank() && txtSewingMachineName.editText!!.text.isNotEmpty() && txtMotorSteps.editText!!.text.toString()
            .isNotBlank()
    }


    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            Log.i(
                TAG,
                "$it - ${
                    ActivityCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }"
            )
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    private fun deleteMachine() {
        imageManager.deleteImageFile(currentPhotoUri)
        val databaseConnection = SewingMachinedatabaseConnection(requireContext())
        databaseConnection.open()
        databaseConnection.delete(sewingMachine)
        databaseConnection.close()
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
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
                    getImageFromGallery.launch("image/*")
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    imageManager.createImageFile(requireActivity())
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
                    getImageFromCamera.launch(photoURI)
                }
            }
        }
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