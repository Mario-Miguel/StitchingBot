package es.uniovi.eii.stitchingbot.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.MyBluetoothService
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.translator.Translator
import es.uniovi.eii.stitchingbot.util.ImageManager
import kotlinx.android.synthetic.main.fragment_summary.*


private const val LOGO = "logo"
private const val SEWING_MACHINE = "machine"
private const val ARDUINO = "arduino"
private const val SUMMARY = "summary"

/**
 * A simple [Fragment] subclass.
 * Use the [SummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SummaryFragment : Fragment() {
    private var logo: Logo? = null
    private var sewingMachine: SewingMachine? = null

    private var bluetoothService: MyBluetoothService = MyBluetoothService

    private lateinit var translator: Translator
    private var translationDone : Boolean = false

    private val imageManager = ImageManager()

    private lateinit var translation: MutableList<Triple<Int, Int, Boolean>>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val auxLogo = requireArguments().getParcelable<Logo>(LOGO)
            val auxSewingMachine = requireArguments().getParcelable<SewingMachine>(SEWING_MACHINE)
            if (auxLogo != null)
                logo = auxLogo
            if (auxSewingMachine != null)
                sewingMachine = auxSewingMachine

        }

        if (logo != null) {
            val logoImage =
                imageManager.getImageFromUri(Uri.parse(logo!!.imgUrl), requireActivity())
            imgLogoSummary.setImageBitmap(logoImage)
        }

        loadListeners()


        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        // Instead of String any types of data can be used
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(ARDUINO)
            ?.observe(viewLifecycleOwner) {
                updateArduinoStatus(it)
            }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<SewingMachine>(
            SEWING_MACHINE
        )
            ?.observe(viewLifecycleOwner) {
                updateSewingMachineStatus(it)
            }


        btnStartExecution.isEnabled =
            logo != null && sewingMachine != null && bluetoothService.getConnectionSocket()?.isConnected == true && translationDone
        btnStartTranslate.setOnClickListener { startTranslation() }

        btnStartExecution.setOnClickListener { startExecution() }
    }


    //##################################################################################################
    private fun loadListeners() {
        cardViewLogo.setOnClickListener { loadLogoFragment() }
        cardViewSewingMachine.setOnClickListener { loadSewingMachineFragment() }
        cardViewArduino.setOnClickListener { loadArduinoFragment() }
    }


    private fun loadArduinoFragment() {
        val bundle = bundleOf(SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        if (bluetoothService.getConnectionSocket()?.isConnected == true)
            navController.navigate(R.id.nav_arduino_configuration, bundle)
        else
            navController.navigate(R.id.nav_arduino_connection, bundle)
    }


    private fun loadLogoFragment() {
        val bundle = bundleOf("creation" to false, LOGO to logo, SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_create_logo, bundle)
    }


    private fun loadSewingMachineFragment() {
        val bundle = bundleOf(SUMMARY to true)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_sewing_machines, bundle)
    }


    //##################################################################################################
    private fun updateSewingMachineStatus(sewingMachine: SewingMachine) {
        this.sewingMachine = sewingMachine
        imgSewingMachineSummary.setImageBitmap(
            imageManager.getImageFromUri(
                Uri.parse(sewingMachine.imgUrl),
                requireActivity()
            )
        )
        txtSewingMachineSummary.text = sewingMachine.name
    }

    private fun updateArduinoStatus(status: String) {
        this.bluetoothService = MyBluetoothService
        if (bluetoothService.getConnectionSocket()?.isConnected == true) {
            txtArduinoSummary.text = "Estado: $status"
            imgRobotSummary.setImageDrawable(
                getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_check_24
                )
            )
        } else {
            txtArduinoSummary.text = "Estado: $status"
            imgRobotSummary.setImageDrawable(
                getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_clear_24
                )
            )
        }

    }

//##################################################################################################

    private fun startTranslation() {
        translator = Translator(imageManager.getImageFromUri(Uri.parse(logo!!.imgUrl), requireActivity())!!)

//        translationDone = translator.run()
        translation = translator.run()
        translationDone = true

        btnStartExecution.isEnabled =
            logo != null && sewingMachine != null && bluetoothService.getConnectionSocket()?.isConnected == true && translationDone


    }

    private fun startExecution(){
        bluetoothService.startExecution(translation)

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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(logo: Logo, sewingMachine: SewingMachine) =
            SummaryFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LOGO, logo)
                    putParcelable(SEWING_MACHINE, sewingMachine)
                }
            }
    }
}