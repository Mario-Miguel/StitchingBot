package es.uniovi.eii.stitchingbot.ui.fragments.sewingMachines

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.arduinoCommunication.ArduinoCommands
import es.uniovi.eii.stitchingbot.arduinoCommunication.BluetoothService
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.ui.util.ShowDialog
import es.uniovi.eii.stitchingbot.util.Constants.CREATION_MODE
import kotlinx.android.synthetic.main.fragment_sewing_machine_details.*

class SewingMachineDetailsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.Q)
    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_MEDIA_LOCATION
    )

    private var isCreation: Boolean = false
    private var sewingMachineController = SewingMachineController()

    /**
     * Evento para activar obtener una imagen de la galeria del dispositivo móvil
     */
    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) imagePick(it.toString())
    }

    /**
     * Evento para activar obtener una imagen de la cámara del dispositivo móvil
     */
    private val getImageFromCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { if (it) imageTake() }

    /**
     * Evento para pedir al usuario los permisos necesarios para el funcionamiento de esta pantalla
     */
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
                selectImage(requireContext())
            } else {
                ShowDialog.showNotGrantedPermissionsMessage(nonGrantedPermissions, requireContext())
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

        if (arguments != null) {
            isCreation = requireArguments().getBoolean(CREATION_MODE)
        }

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
            onDeleteMachineMenuClick()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Obtiene una imagen de la galería, la guarda en el almacenamiento de la app y la muestra en el
     * componente [imgSewingMachineDetails]
     *
     * Función que se invoca al seleccionar una imagen de la galería del usuario.
     *
     * @param selectedImage url de la imagen seleccionada
     */
    private fun imagePick(selectedImage: String) {
        sewingMachineController.createImageFileAndDispatchAction(requireActivity()) { _, file ->
            sewingMachineController.copyImageToFile(
                selectedImage,
                file,
                requireActivity()
            )
        }
        imgSewingMachineDetails.setImageBitmap(
            sewingMachineController.getImage(requireActivity())
        )
    }

    /**
     * Obtiene una imagen scada con la cámara y la muestra en el componente [imgSewingMachineDetails]
     */
    private fun imageTake() {
        imgSewingMachineDetails.setImageBitmap(
            sewingMachineController.getImage(requireActivity())
        )
    }

    /**
     * Carga la pantalla por defecto que se le mostrará al usuario
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadDefaultScreen() {
        btnSewingMachineAction.setOnClickListener { onSewingMachineActionClick() }
        imgSewingMachineDetails.setOnClickListener {
            getPermissions.launch(permissions)
        }
        txtMotorSteps.editText!!.setText(sewingMachineController.getSewingMachine().motorSteps.toString())
        btnTrySteps.setOnClickListener { onTryStepsButtonClick() }
    }

    /**
     * Cambia los componentes para que se ajusten a la modificación de la máquina de coser
     */
    private fun loadUpdateScreen() {
        sewingMachineController.setSewingMachine(requireArguments().getParcelable("machine")!!)

        btnSewingMachineAction.text = getString(R.string.btn_sewing_machine_text)
        txtSewingMachineName.editText!!.setText(sewingMachineController.getSewingMachine().name)
        txtMotorSteps.editText!!.setText(sewingMachineController.getSewingMachine().motorSteps.toString())

        if (!sewingMachineController.getSewingMachine().imgUrl.isNullOrEmpty()) {
            imgSewingMachineDetails.setImageBitmap(
                sewingMachineController.getImage(requireActivity())
            )
        }
    }

    /**
     * Evento que se lanza al pulsar sobre el botón [btnSewingMachineAction]
     */
    private fun onSewingMachineActionClick() {
        if (checkFields()) {
            val name = txtSewingMachineName.editText!!.text.toString()
            val motorSteps = txtMotorSteps.editText!!.text.toString().toInt()
            sewingMachineController.setSewingMachine(name = name, motorSteps = motorSteps)
            if (isCreation) {
                sewingMachineController.addSewingMachine(requireContext())
                Toast.makeText(requireContext(), "Máquina creada", Toast.LENGTH_LONG).show()
            } else {
                sewingMachineController.updateSewingMachine(requireContext())
                Toast.makeText(requireContext(), "Máquina modificada", Toast.LENGTH_LONG).show()
            }
            goBack()
        } else {
            Toast.makeText(requireContext(), "Rellene todos los campos", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Comprueba que todos los campos requeridos para la modificación/creación de la máquina sean correctos
     *
     * @return true si todos los campos son correctos, false en caso contrario
     */
    private fun checkFields(): Boolean {
        return txtSewingMachineName.editText!!.text.isNotBlank() && txtSewingMachineName.editText!!.text.isNotEmpty() && txtMotorSteps.editText!!.text.toString()
            .isNotBlank()
    }

    /**
     * Evento que se lanza al pulsar sobre el botón [btnTrySteps]
     */
    private fun onTryStepsButtonClick() {
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

    /**
     * Lleva al usuario al fragment ArduinoConnectionFragment
     */
    private fun goToArduinoConnectionFragment() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_arduino_connection)
    }

    /**
     *  Maneja el evento del click en la opción de menú [R.id.action_delete]
     */
    private fun onDeleteMachineMenuClick() {
        ShowDialog.showDialogOK(requireContext(), "¿Está seguro de eliminar la máquina de coser?")
        { _, _ ->
            sewingMachineController.deleteSewingMachine(requireContext())
            goBack()
        }
    }

    /**
     * Muestra el dialogo utilizado para que el usuario decida cómo obtener la imagen de su máquina
     * de coser
     *
     * @param context Conterxto desde el que se llama a la función
     */
    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Sacar foto", "Escoger de la galería", "Cancelar")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Escoge la foto de tu máquina de coser")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Sacar foto" -> {
                    launchTakePictureIntent()
                }
                options[item] == "Escoger de la galería" -> {
                    getImageFromGallery.launch("image/*")
                }
                options[item] == "Cancelar" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    /**
     * Lanza el evento para sacar una foto
     */
    private fun launchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                sewingMachineController.createImageFileAndDispatchAction(requireActivity()) { photoURI, _ ->
                    getImageFromCamera.launch(
                        photoURI
                    )
                }
            }
        }
    }

    /**
     * Lleva una pantalla hacia atrás al usuario
     */
    private fun goBack() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
    }
}