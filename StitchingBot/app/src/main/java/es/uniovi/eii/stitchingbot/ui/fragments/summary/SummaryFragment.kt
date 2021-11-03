package es.uniovi.eii.stitchingbot.ui.fragments.summary

import android.app.Activity
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
import kotlinx.android.synthetic.main.fragment_summary.*


/**
 * A simple [Fragment] subclass.
 * Use the [SummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SummaryFragment : Fragment() {

    var logoController = LogoController()
    var sewingMachineController = SewingMachineController()

    val translator: Translator = Translator
    private val stateManager = StateManager
    private val bluetoothService = BluetoothService
    private val arduinoCommands: ArduinoCommands = ArduinoCommands


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadActionButtons()
        loadIncomingResources()
        loadCardViews()
        loadLiveDataObservers()
    }

//##################################################################################################

    private fun loadActionButtons() {
        stateManager.showInterface(this)
        btnStartTranslate.setOnClickListener { startTranslation() }
        btnStartExecution.setOnClickListener { startExecution() }

        btnPauseExecution.setOnClickListener { arduinoCommands.pauseExecution() }
        btnStopExecution.setOnClickListener { arduinoCommands.stopExecution() }
        btnResumeExecution.setOnClickListener { resumeExecution() }
    }


    private fun loadIncomingResources() {
        if (arguments != null) {
            val auxLogo = requireArguments().getParcelable<Logo>(Constants.LOGO)
            if (auxLogo != null) {
                if (auxLogo.id != logoController.getLogo().id) {
                    translator.translationDone = false
                }
                logoController.setLogo(auxLogo)
            }
        }

        if (logoController.isLogoSelected()) {
            loadImageToCard(imgLogoSummary, logoController.getLogo().imgUrl)
        }
    }

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
                updateSewingMachineCard()
            }
        updateArduinoStatus()
    }

    private fun loadLiveDataObservers() {
        translator.actualProgress.observe(viewLifecycleOwner,
            { newProgress -> updateProgressBar("Traducción completada", newProgress) })
        arduinoCommands.actualProgress.observe(viewLifecycleOwner,
            { newProgress -> updateProgressBar("Ejecución completada", newProgress) })
        stateManager.actualState.observe(viewLifecycleOwner) {
            stateManager.showInterface(this)
        }
    }

    private fun updateProgressBar(completionMessage: String, newProgress: Int) {
        pbExecution.progress = newProgress
        if (newProgress == 0) {
            if (this@SummaryFragment.isVisible)
                stateManager.showInterface(this)
        }
        if (newProgress == 100) {
            if (this@SummaryFragment.isVisible) {
                stateManager.changeToInitial()
                stateManager.showInterface(this)
                ShowDialog.showInfoDialog(requireActivity().applicationContext, completionMessage)
            }
        }
    }

    private fun loadImageToCard(imgComponent: ImageView, imgUrl: String?) {
        val image = sewingMachineController.getImageFromUrl(requireActivity(), imgUrl)
        imgComponent.setImageBitmap(image)
    }

//##################################################################################################

    private fun loadArduinoFragment() {
        val bundle = bundleOf(Constants.SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        if (bluetoothService.isConnected())
            navController.navigate(R.id.nav_arduino_configuration, bundle)
        else
            navController.navigate(R.id.nav_arduino_connection, bundle)
    }

    private fun loadLogoFragment() {
        val bundle =
            bundleOf("creation" to false, Constants.LOGO to logoController.getLogo())
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
        navController.popBackStack()
        navController.navigate(R.id.nav_create_logo, bundle)
    }

    private fun loadSewingMachineFragment() {
        val bundle = bundleOf(Constants.SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_sewing_machines, bundle)
    }

//##################################################################################################

    private fun updateSewingMachineCard() {
        loadImageToCard(imgSewingMachineSummary, sewingMachineController.getSewingMachine().imgUrl)
        txtSewingMachineSummary.text = sewingMachineController.getSewingMachine().name
    }

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

    private fun startTranslation() {
        Thread {
            stateManager.startTranslate()
            translator.image = logoController.getImage(requireActivity())
            translator.run()
        }.start()
    }


    private fun startExecution() {
        bluetoothService.enableBluetoothAndExecute(enableBluetooth) {
            Thread {
                arduinoCommands.startExecution(
                    translator.translation
                )
            }.start()
        }
    }

    private fun resumeExecution() {
        bluetoothService.enableBluetoothAndExecute(enableBluetooth) { arduinoCommands.resumeExecution() }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param logo Parameter 1.
         * @param sewingMachine Parameter 2.
         * @return A new instance of fragment SummaryFragment.
         */
        @JvmStatic
        fun newInstance(logo: Logo, sewingMachine: SewingMachine) =
            SummaryFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.LOGO, logo)
                    putParcelable(Constants.SEWING_MACHINE, sewingMachine)
                }
            }
    }

    private val enableBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.i("BluetoothStitching", "No se ha activado el bluetooth")
        }
    }
}