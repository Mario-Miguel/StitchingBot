package es.uniovi.eii.stitchingbot.ui.fragments.arduino

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.util.bluetooth.BluetoothService
import kotlinx.android.synthetic.main.fragment_arduino_configuration.*


class ArduinoConfigurationFragment : Fragment() {

    var bluetoothService: BluetoothService = BluetoothService
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
        btnUpArrow.setOnClickListener { moveUp() }
        btnDownArrow.setOnClickListener { moveDown() }
        btnLeftArrow.setOnClickListener { moveLeft() }
        btnRightArrow.setOnClickListener { moveRight() }

        btnAxisDone.setOnClickListener { startMainFragment() }
        btnDisconnect.setOnClickListener { disconnectDevice() }

    }


    private fun moveUp() {
        bluetoothService.write("U")
    }

    private fun moveDown() {
        bluetoothService.write("D")
    }

    private fun moveRight() {
        bluetoothService.write("R")
    }

    private fun moveLeft() {
        bluetoothService.write("L")
    }

    private fun startMainFragment() {
        if (!comesFromSummary) {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            /*navController.navigate(R.id.nav_logo_list)*/
            navController.popBackStack()
        } else {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.getBackStackEntry(R.id.nav_summary).savedStateHandle.set(
                "arduino",
                "conectado"
            )
            navController.popBackStack(R.id.nav_summary, false)
        }
    }

    private fun disconnectDevice() {
        bluetoothService.closeConnectionSocket()
        if (!comesFromSummary) {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_arduino_connection)
        } else {

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.getBackStackEntry(R.id.nav_summary).savedStateHandle.set(
                "arduino",
                "desconectado"
            )
            navController.popBackStack(R.id.nav_summary, false)

        }
    }


}