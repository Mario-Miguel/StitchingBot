package es.uniovi.eii.stitchingbot.ui.fragments.summary

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.core.os.persistableBundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.arduinoCommunication.ArduinoCommands
import es.uniovi.eii.stitchingbot.arduinoCommunication.BluetoothService
import es.uniovi.eii.stitchingbot.arduinoCommunication.Translator
import es.uniovi.eii.stitchingbot.controller.LogoController
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.ui.fragments.summary.states.StateManager
import es.uniovi.eii.stitchingbot.ui.util.ShowDialog
import es.uniovi.eii.stitchingbot.util.*
import es.uniovi.eii.stitchingbot.util.Constants.TAG_TRANSLATE
import kotlinx.android.synthetic.main.fragment_arduino_connection.*
import kotlinx.android.synthetic.main.fragment_summary.*

class SummaryFragment : Fragment() {

    var logoController = LogoController()
    var sewingMachineController = SewingMachineController()
    val translator: Translator = Translator
    private val stateManager = StateManager
    private val bluetoothService = BluetoothService
    private val arduinoCommands: ArduinoCommands = ArduinoCommands

    /**
     * Evento para activar el bluetooth
     */
    private val enableBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.i("BluetoothStitching", "No se ha activado el bluetooth")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onPause() {
        super.onPause()
        pbExecution.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        stateManager.showInterface(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadButtonsListeners()
        loadIncomingResources()
        loadCardViews()
        loadLiveDataObservers()
    }


//##################################################################################################

    /**
     * Inicia los listeners de los botones de la interfaz
     */
    private fun loadButtonsListeners() {
        stateManager.showInterface(this)
        btnStartTranslate.setOnClickListener { onStartTranslateButtonClick() }
        btnStartExecution.setOnClickListener { onStartExecutionButtonClick() }
        btnPauseExecution.setOnClickListener { arduinoCommands.pauseExecution() }
        btnStopExecution.setOnClickListener { arduinoCommands.stopExecution() }
        btnResumeExecution.setOnClickListener { onResumeExecutionButtonClick() }
    }

    /**
     * Carga los elementos que se le pasan a este fragment como argumentos
     */
    private fun loadIncomingResources() {
        if (arguments != null) {
            val auxLogo = requireArguments().getParcelable<Logo>(Constants.LOGO)
            if (auxLogo != null) {
                if (auxLogo.id != logoController.getLogo().id) {
                    translator.translationDone = false
                }
                logoController.setLogo(auxLogo)
                arduinoCommands.privateLogo.value = auxLogo
            }
        }

        if (arguments == null
            && arduinoCommands.privateLogo.value != null
            && arduinoCommands.privateMachine.value != null
        ) {
            logoController.setLogo(arduinoCommands.privateLogo.value!!)
            sewingMachineController.setSewingMachine(arduinoCommands.privateMachine.value!!)
        }
    }

    /**
     * Inicia los listenes de las CardViews del fragment. Tambien carga los datos de la máquina de
     * coser seleccionada y del estado de conexión con el robot
     */
    private fun loadCardViews() {
        cardViewLogo.setOnClickListener { loadLogoFragment() }
        cardViewSewingMachine.setOnClickListener { loadSewingMachineFragment() }
        cardViewArduino.setOnClickListener { loadArduinoFragment() }

        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<SewingMachine>(
            Constants.SEWING_MACHINE
        )
            ?.observe(viewLifecycleOwner) {
                sewingMachineController.setSewingMachine(it)
                arduinoCommands.privateMachine.value = it
                updateSewingMachineCard()
            }

        updateArduinoStatus()

        if (logoController.isLogoSelected()) {
            loadImageToCard(imgLogoSummary, logoController.getLogo().imgUrl)
        }
    }

    /**
     * Carga los observers necesarios para actualizar las barras de progreso y los estrados de la ejecución
     */
    private fun loadLiveDataObservers() {
        translator.actualProgress.observe(viewLifecycleOwner,
            { newProgress -> updateProgressBar("Traducción completada", newProgress) })
        arduinoCommands.actualProgress.observe(viewLifecycleOwner,
            { newProgress -> updateProgressBar("Ejecución completada", newProgress) })
        stateManager.actualState.observe(viewLifecycleOwner) {
            stateManager.showInterface(this)
        }
    }

    /**
     *
     */
    private fun updateProgressBar(completionMessage: String, newProgress: Int) {
        pbExecution.progress = newProgress
        Log.i(
            TAG_TRANSLATE,
            "Progress 0 - $newProgress - ${stateManager.actualState.value.toString()}"
        )
        if (newProgress == 0) {
            if (this@SummaryFragment.isVisible)
                stateManager.showInterface(this)
        }
        if (newProgress == 100) {
            Log.i(TAG_TRANSLATE, "Progress: 100 - $newProgress")
            if (this@SummaryFragment.isVisible) {
                stateManager.changeToInitial()
                stateManager.showInterface(this)
                ShowDialog.showInfoDialog(requireActivity().applicationContext, completionMessage)
            }
        }
    }

    /**
     * Carga la imagen de la máquina de coser seleccionada en el CardView correspondiente
     */
    private fun loadImageToCard(imgComponent: ImageView, imgUrl: String?) {
        val image = sewingMachineController.getImage(requireActivity(), imgUrl)
        imgComponent.setImageBitmap(image)
    }

//##################################################################################################

    /**
     * Lleva al usuario al fragmento ArduinoConnectionFragment o ArduinoConfigurationFragment
     * en función del estado de conexión.
     */
    private fun loadArduinoFragment() {
        val bundle = bundleOf(Constants.SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        if (bluetoothService.isConnected())
            navController.navigate(R.id.nav_arduino_configuration, bundle)
        else
            navController.navigate(R.id.nav_arduino_connection, bundle)
    }

    /**
     * Lleva al usuario al fragmento LogoEditorFragment
     */
    private fun loadLogoFragment() {
        val bundle =
            bundleOf("creation" to false, Constants.LOGO to logoController.getLogo())
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_create_logo, bundle)
    }

    /**
     * Lleva al usuario al fragmento SewingMachinesListFragment
     */
    private fun loadSewingMachineFragment() {
        val bundle = bundleOf(Constants.SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_sewing_machines, bundle)
    }

//##################################################################################################

    /**
     * Actualiza la CardView que contiene la imagen de la máquina de coser para que se corresponda
     * con la seleccionada
     */
    private fun updateSewingMachineCard() {
        loadImageToCard(imgSewingMachineSummary, sewingMachineController.getSewingMachine().imgUrl)
        txtSewingMachineSummary.text = sewingMachineController.getSewingMachine().name
    }

    /**
     * Actualiza la CardView que contiene el estado de conexión con el robot para que se corresponda
     * con este
     */
    private fun updateArduinoStatus() {
        val hasConnection = bluetoothService.isConnected()
        txtArduinoSummary.text = getString(
            R.string.txt_arduino_summary,
            if (hasConnection) "conectado" else "desconectado"
        )
        imgRobotSummary.setImageDrawable(
            getDrawable(
                requireContext(),
                if (hasConnection)
                    R.drawable.ic_baseline_check_24
                else
                    R.drawable.ic_baseline_clear_24
            )
        )
    }

//##################################################################################################

    /**
     * Evento lanzado al pulsar el botón [btnStartTranslate]
     */
    private fun onStartTranslateButtonClick() {
        Thread {
            stateManager.startTranslate()
            translator.image = logoController.getImage(requireActivity())
            translator.run { StateManager.changeToInitial() }
        }.start()
    }

    /**
     * Evento lanzado al pulsar el botón [btnStartExecution]
     */
    private fun onStartExecutionButtonClick() {
        bluetoothService.enableBluetoothAndExecute(enableBluetooth) {
            Thread {
                arduinoCommands.startExecution(
                    translator.translation
                )
            }.start()
        }
    }

    /**
     * Evento lanzado al pulsar el botón [btnResumeExecution]
     */
    private fun onResumeExecutionButtonClick() {
        bluetoothService.enableBluetoothAndExecute(enableBluetooth) { arduinoCommands.resumeExecution() }
    }
}