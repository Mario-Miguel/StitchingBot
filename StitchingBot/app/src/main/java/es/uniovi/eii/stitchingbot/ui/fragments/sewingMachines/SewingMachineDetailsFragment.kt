package es.uniovi.eii.stitchingbot.ui.fragments.sewingMachines

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.util.bluetooth.BluetoothService
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.util.ArduinoCommands
import es.uniovi.eii.stitchingbot.util.ImageManager
import es.uniovi.eii.stitchingbot.util.ShowDialog
import kotlinx.android.synthetic.main.fragment_sewing_machine_details.*


private const val CREATION_MODE = "creation"
private const val TAG = "SewingMachine"

@RequiresApi(Build.VERSION_CODES.Q)
private val PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_MEDIA_LOCATION
)


class SewingMachineDetailsFragment : Fragment() {
    private var isCreation: Boolean = false
    private var sewingMachineController = SewingMachineController()
    private val imageManager = ImageManager()


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
                    if (it.key == "android.permission.CAMERA")
                        nonGrantedPermissions.add("Camera")
                    else {
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
        val uri = imageManager.updatePhotoUrl(selectedImage, requireActivity(), sewingMachineController.getSewingMachine().imgUrl)
        sewingMachineController.setSewingMachine(imgUrl = uri.toString())

        imgSewingMachineDetails.setImageBitmap(
            imageManager.getImageFromUri(
                activity=requireActivity()
            )
        )
    }

    private fun imageTake() {
        imgSewingMachineDetails.setImageBitmap(
            imageManager.getImageFromUri(
                activity=requireActivity()
            )
        )
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadDefaultScreen() {
        btnSewingMachineAction.setOnClickListener { confirmButtonAction() }
        imgSewingMachineDetails.setOnClickListener {
            getPermissions.launch(PERMISSIONS)
        }
        txtMotorSteps.editText!!.setText(sewingMachineController.getSewingMachine().motorSteps.toString())
        btnTrySteps.setOnClickListener { tryStepsButtonAction() }
    }

    private fun loadUpdateScreen() {
        sewingMachineController.setSewingMachine(requireArguments().getParcelable("machine")!!)

        btnSewingMachineAction.text = "Modificar"
        txtSewingMachineName.editText!!.setText(sewingMachineController.getSewingMachine().name)
        txtMotorSteps.editText!!.setText(sewingMachineController.getSewingMachine().motorSteps.toString())

        if (!sewingMachineController.getSewingMachine().imgUrl.isNullOrEmpty()) {
            imageManager.setPhotoUri(sewingMachineController.getSewingMachine().imgUrl!!)
            imgSewingMachineDetails.setImageBitmap(
                imageManager.getImageFromUri(
                    activity=requireActivity(),
                )
            )
        }
    }

    private fun confirmButtonAction() {
        if (checkFields()) {
            val name = txtSewingMachineName.editText!!.text.toString()
            val motorSteps = txtMotorSteps.editText!!.text.toString().toInt()
            sewingMachineController.setSewingMachine(name=name,imgUrl=imageManager.getPhotoUri().toString(), motorSteps=motorSteps)

            Log.i("SMDETAILS", "img url: ${sewingMachineController.getSewingMachine()}")
            if (isCreation) {
                sewingMachineController.addSewingMachine(requireContext())
                Toast.makeText(requireContext(), "Máquina creada", Toast.LENGTH_LONG).show()
                Log.i(
                    TAG,
                    "Insertada maquina de coser"
                )
            } else {
                sewingMachineController.updateSewingMachine(requireContext())
                Toast.makeText(requireContext(), "Máquina modificada", Toast.LENGTH_LONG).show()
                Log.i(
                    TAG,
                    "Update maquina de coser"
                )
            }

            goBack()
        } else {
            Toast.makeText(requireContext(), "Rellene todos los campos", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkFields(): Boolean {
        return txtSewingMachineName.editText!!.text.isNotBlank() && txtSewingMachineName.editText!!.text.isNotEmpty() && txtMotorSteps.editText!!.text.toString()
            .isNotBlank()
    }

    private fun tryStepsButtonAction() {
        val arduinoCommands = ArduinoCommands
        val motorSteps = txtMotorSteps.editText!!.text.toString().toInt()

        if (BluetoothService.isConnected()) {
            arduinoCommands.doMotorStepsTest(motorSteps)
        } else {
            ShowDialog.showDialogOK(
                requireContext(),
                "El dispositivo no está conectado. \n ¿Desea conectarlo?",
            ) { _, _ -> goToArduinoConnectionFragment() }
        }
    }

    private fun goToArduinoConnectionFragment() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_arduino_connection)
    }


    private fun deleteMachine() {
        imageManager.deleteImageFile()
        sewingMachineController.deleteSewingMachine(requireContext())

        goBack()
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

                // Continue only if the File was successfully created
                imageManager.createPhotoFile(requireActivity())?.also {
                    Log.i(TAG, it.absolutePath)
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "es.uniovi.eii.stitchingbot",
                        it
                    )
                    imageManager.setPhotoUri(photoURI)
                    getImageFromCamera.launch(photoURI)
                }
            }
        }
    }

    private fun goBack() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
    }

    private fun showNotGrantedPermissionsMessage(nonGrantedPermissions: MutableList<String>) {
        val permissionsString = nonGrantedPermissions.reduce { acc, str -> "$acc - $str" }

        ShowDialog.showDialogOK(
            requireContext(),
            "Se necesitan los permisos: $permissionsString"
        ) { _, _ -> }
    }

}