package es.uniovi.eii.stitchingbot.ui.fragments.arduino

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.fragments.summary.states.StateManager
import es.uniovi.eii.stitchingbot.util.Constants.DOWN
import es.uniovi.eii.stitchingbot.util.Constants.LEFT
import es.uniovi.eii.stitchingbot.util.Constants.RIGHT
import es.uniovi.eii.stitchingbot.util.Constants.UP
import es.uniovi.eii.stitchingbot.util.ShowDialog
import es.uniovi.eii.stitchingbot.util.ArduinoCommands
import es.uniovi.eii.stitchingbot.util.BluetoothService
import kotlinx.android.synthetic.main.fragment_arduino_configuration.*
import kotlinx.android.synthetic.main.fragment_arduino_configuration.txtMotorSteps


class ArduinoConfigurationFragment : Fragment() {

    private var bluetoothService: BluetoothService = BluetoothService
    private var arduinoCommands = ArduinoCommands
    private var comesFromSummary: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arduino_configuration, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            comesFromSummary = requireArguments().getBoolean("summary")
        }
        initUI()
    }


    private fun initUI() {
        btnUpArrow.setOnClickListener { checkIsInInitialStateAndExecute{arduinoCommands.move(UP)} }
        btnDownArrow.setOnClickListener { checkIsInInitialStateAndExecute { arduinoCommands.move(DOWN) } }
        btnLeftArrow.setOnClickListener { checkIsInInitialStateAndExecute { arduinoCommands.move(LEFT) }}
        btnRightArrow.setOnClickListener { checkIsInInitialStateAndExecute { arduinoCommands.move(RIGHT) } }
        btnAutoHome.setOnClickListener { checkIsInInitialStateAndExecute { arduinoCommands.startAutoHome() } }

        btnConfigTrySteps.setOnClickListener{checkIsInInitialStateAndExecute {tryStepsButtonAction()}}

        btnAxisDone.setOnClickListener { onClickDone() }
        btnDisconnect.setOnClickListener { onClickDisconnect() }
    }


    private fun checkIsInInitialStateAndExecute(function: () -> Unit) {
        if(StateManager.isInitial()){
            function()
        }
        else{
            ShowDialog.showDialogOK(
                requireContext(),
                "No se puede realizar esta acción, ejecución en curso",
            ) { _, _ ->  }
        }
    }

    private fun tryStepsButtonAction() {
        val motorSteps = txtMotorSteps.editText!!.text.toString().toInt()

        if (bluetoothService.isBluetoothEnabled()) {
            arduinoCommands.doMotorStepsTest(motorSteps)
        } else {
            ShowDialog.showDialogOK(
                requireContext(),
                "Se requiere Tener el bluetooth activado",
            ) { _, _ ->  }
        }
    }

    private fun onClickDone() {
        if (!comesFromSummary) {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.popBackStack()
        } else {
            goBackToSummary("conectado")
        }
    }

    private fun onClickDisconnect() {
        bluetoothService.closeConnectionSocket()
        if (!comesFromSummary) {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.popBackStack()
            navController.navigate(R.id.nav_arduino_connection)
        } else {
            goBackToSummary("desconectado")
        }
    }

    private fun goBackToSummary(status: String){
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.getBackStackEntry(R.id.nav_summary).savedStateHandle.set(
            "arduino",
            status
        )
        navController.popBackStack(R.id.nav_summary, false)
    }
}