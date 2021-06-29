package es.uniovi.eii.stitchingbot.ui.arduino

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.MyBluetoothService
import kotlinx.android.synthetic.main.fragment_arduino_configuration.*
import java.io.IOException
import java.io.OutputStream


class ArduinoConfigurationFragment : Fragment() {

    var bluetoothService : MyBluetoothService= MyBluetoothService
    private var mmOutStream: OutputStream? = null
    private var comesFromSummary: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arduino_configuration, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null){
            comesFromSummary = requireArguments().getBoolean("summary")
        }
        initUI()
        startOutputStream()

    }


    private fun initUI(){
        btnUpArrow.setOnClickListener { moveUp() }
        btnDownArrow.setOnClickListener { moveDown() }
        btnLeftArrow.setOnClickListener { moveLeft() }
        btnRightArrow.setOnClickListener { moveRight() }

        btnAxisDone.setOnClickListener { startMainFragment() }
        btnDisconnect.setOnClickListener { disconnectDevice() }

    }


    private fun moveUp(){
        write("U")
    }

    private fun moveDown(){
        write("D")
    }

    private fun moveRight(){
        write("R")
    }

    private fun moveLeft(){
        write("L")
    }

    private fun startMainFragment(){
        if(!comesFromSummary) {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_logo_list)
        }
        else{
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.previousBackStackEntry?.savedStateHandle?.set("arduino", bluetoothService)
            navController.popBackStack()
        }
    }

    private fun disconnectDevice(){
        bluetoothService.closeConnectionSocket()
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_arduino_connection)
    }


    private fun startOutputStream(){
        try {
            mmOutStream = bluetoothService.getConnectionSocket()?.outputStream
        } catch (e: IOException) {
        }

    }

    fun write(input: String) {
        val bytes = input.toByteArray() //converts entered String into bytes
        Log.i("BluetoothStitching", "Mensaje enviado")
        try {
            mmOutStream!!.write(bytes)
        } catch (e: IOException) {
            Log.e("Send Error", "Unable to send message", e)
        }
    }


}