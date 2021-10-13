package es.uniovi.eii.stitchingbot.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.controller.LogoController
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.util.*

import kotlinx.android.synthetic.main.fragment_arduino_connection.*
import kotlinx.android.synthetic.main.fragment_summary.*



/**
 * A simple [Fragment] subclass.
 * Use the [SummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SummaryFragment : Fragment() {

    private var logoController = LogoController()
    private var sewingMachineController = SewingMachineController()

    private val translator: Translator = Translator
    private val arduinoCommands: ArduinoCommands = ArduinoCommands
    private val imageManager = ImageManager()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onPause() {
        super.onPause()
        hideProgressBar()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadActionButtons()
        loadIncomingResources()
        loadCardViewListeners()
        updateArduinoStatus()
        loadProgressBar()

    }


    //##############################################################################################
    private fun loadActionButtons() {
        updateActionButtonsState()
        btnStartTranslate.setOnClickListener { startTranslation() }
        btnStartExecution.setOnClickListener { startExecution() }

        btnPauseExecution.setOnClickListener { pauseExecution() }
        btnStopExecution.setOnClickListener { stopExecution() }

    }

    private fun updateActionButtonsState() {
        btnStartExecution.isEnabled = checkStartExecutionAvailability()
        btnStartTranslate.isEnabled = checkStartTranslationAvailability()

        if(arduinoCommands.isInExecution) {
            btnStartTranslate.visibility = View.GONE
            btnStartExecution.visibility = View.GONE
            btnStopExecution.visibility = View.VISIBLE
            btnPauseExecution.visibility = View.VISIBLE
        }
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

        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<SewingMachine>(
            Constants.SEWING_MACHINE
        )
            ?.observe(viewLifecycleOwner) {
                sewingMachineController.setSewingMachine(it)
                updateSewingMachineCard()
            }
    }

    private fun loadCardViewListeners() {
        cardViewLogo.setOnClickListener { loadLogoFragment() }
        cardViewSewingMachine.setOnClickListener { loadSewingMachineFragment() }
        cardViewArduino.setOnClickListener { loadArduinoFragment() }
    }

    private fun loadProgressBar() {
        if (arduinoCommands.isInExecution || translator.isInExecution) {
            pbExecution.visibility = View.VISIBLE
        }
        translator.actualProgress.observe(viewLifecycleOwner,
            { newProgress -> updateProgressBar("Traducción completada", newProgress) })
        arduinoCommands.actualProgress.observe(viewLifecycleOwner,
            { newProgress -> updateProgressBar("Ejecución completada", newProgress) })
    }


    private fun updateProgressBar(completionMessage: String, newProgress: Int) {
        pbExecution.progress = newProgress
        if (newProgress==100) {
            if (this@SummaryFragment.isVisible) {
                updateActionButtonsState()
                hideProgressBar()
                ShowDialog.showInfoDialog(requireActivity().applicationContext, completionMessage)
            }
        }
    }


    //##############################################################################################
    private fun checkStartExecutionAvailability(): Boolean {
        return logoController.isLogoSelected()
                && sewingMachineController.isSewingMachineSelected()
                && arduinoCommands.isConnected()
                && translator.translationDone
    }

    private fun checkStartTranslationAvailability(): Boolean {
        return !translator.isInExecution && !translator.translationDone
    }

    private fun hideProgressBar() {
        pbExecution.visibility = View.GONE
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun loadImageToCard(imgComponent: ImageView, imgUrl: String?) {
        val image =
            imageManager.getImageFromUri(
                selectedUri = Uri.parse(imgUrl),
                activity = requireActivity()
            )
        imgComponent.setImageBitmap(image)
    }

    //##############################################################################################
    private fun loadArduinoFragment() {
        val bundle = bundleOf(Constants.SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        if (arduinoCommands.isConnected())
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


    //##############################################################################################
    private fun updateSewingMachineCard() {

        loadImageToCard(imgSewingMachineSummary, sewingMachineController.getSewingMachine().imgUrl)

        txtSewingMachineSummary.text = sewingMachineController.getSewingMachine().name
    }

    private fun updateArduinoStatus() {
        val hasConnection = arduinoCommands.isConnected()
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
        startThreadWithProgressBar {
            translator.image = imageManager.getImageFromUri(
                Uri.parse(logoController.getLogo().imgUrl),
                requireActivity()
            )!!
            translator.run()
        }
    }


    private fun startExecution() {
        startThreadWithProgressBar {
            arduinoCommands.doExecution(
                translator.translation
            )
        }
    }

    private fun pauseExecution(){
        arduinoCommands.pauseExecution()
        btnPauseExecution.text=getString(R.string.btn_resume_execution)
        btnPauseExecution.setOnClickListener { resumeExecution() }
    }

    private fun resumeExecution(){
        arduinoCommands.resumeExecution()
        btnPauseExecution.text=getText(R.string.btn_pause_execution)
        btnPauseExecution.setOnClickListener { resumeExecution() }
    }

    private fun stopExecution(){
        arduinoCommands.stopExecution()

    }

    private fun startThreadWithProgressBar(command: () -> Unit) {
        Thread {
            // display the indefinite progressbar
            this@SummaryFragment.requireActivity().runOnUiThread {
                pbExecution.visibility = View.VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                btnStartTranslate.isEnabled = false
                btnStartExecution.isEnabled = false
            }

            //Add function here
            command()

        }.start()
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
}