package es.uniovi.eii.stitchingbot.util

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object BluetoothService {

    private var connectionSocket: BluetoothSocket? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var mmInStream: InputStream
    private lateinit var mmOutStream: OutputStream
    private lateinit var receiver: BroadcastReceiver
    private lateinit var handler: Handler
    var startedProcess = false

    init {
        setHandler({},{})
        initBluetoothAdapter()
    }

    private fun getConnectionSocket(): BluetoothSocket? {
        return connectionSocket
    }

    fun closeConnectionSocket() {
        try {
            if (connectionSocket != null)
                connectionSocket!!.close()
        } catch (e: IOException) {
            Log.e(Constants.TAG_BLUETOOTH, "Could not close the client socket", e)
        }
    }

    fun isConnected(): Boolean{
        return if(connectionSocket == null) false else connectionSocket!!.isConnected
    }

    fun write(input: String) {
        val bytes = input.toByteArray() //converts entered String into bytes
        Log.i("BluetoothStitching", "Mensaje enviado")
        try {
            mmOutStream.write(bytes)
        } catch (e: IOException) {
            Log.e("Send Error", "Unable to send message", e)
        }
    }

    fun read(): Byte{
        return mmInStream.read().toByte()
    }

    fun tryToConnect(bluetoothDevice: BluetoothDevice) {
        bluetoothAdapter!!.cancelDiscovery()
        try {
            //Try to create socket with uuid
            setConnectionSocket(
                bluetoothDevice.createInsecureRfcommSocketToServiceRecord(
                    Constants.MY_UUID
                )
            )
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            getConnectionSocket()!!.connect()
            Log.i(Constants.TAG_BLUETOOTH, "Device connected")
            handler.obtainMessage(Constants.CONNECTING_STATUS, 1, -1).sendToTarget()
        } catch (connectException: IOException) {
            // Unable to connect; close the socket and return.
            closeConnectionSocket()
            handler.obtainMessage(Constants.CONNECTING_STATUS, -1, -1).sendToTarget()

        }
    }

    private fun setReceiver(command:(bDevice:BluetoothDevice)->Unit) {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val bDevice: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (bDevice != null) {
                            Log.i(
                                "BluetoothStitching",
                                "Dispositivo: ${bDevice.name} - ${bDevice.address}"
                            )
                            command(bDevice)
                        }
                    }
                }
            }
        }
    }

    fun registerReceiver(activity: Activity, command:(bDevice:BluetoothDevice)->Unit) {
        setReceiver { device -> command(device) }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(receiver, filter)
    }

    fun unregisterReceiver(activity: Activity){
        activity.unregisterReceiver(receiver)
    }

    fun setHandlerCommands(okCommand:()->Unit, errCommand:()->Unit){
        setHandler(okCommand, errCommand)
    }

    private fun setHandler(okCommand:()->Unit, errCommand:()->Unit){
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    Constants.CONNECTING_STATUS -> when (msg.arg1) {
                        1 -> {
                            Log.i(
                                "BluetoothStitching",
                                "Conectado correctamente, cambiar de pantalla"
                            )
                            okCommand()
                        }
                        -1 -> {
                            Log.i("BluetoothStitching", "No se ha podido conectar")
                            errCommand()
                        }
                    }
                }
            }
        }
    }

    private fun setConnectionSocket(socket: BluetoothSocket) {
        connectionSocket = socket
        mmInStream = connectionSocket!!.inputStream
        mmOutStream = connectionSocket!!.outputStream
    }

//##################################################################################################

    private fun initBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.i("BluetoothStitching", "No hay bluetooth disponible")
        }
    }

    fun enableBluetooth(enableBluetooth: ActivityResultLauncher<Intent>) {
        if (!bluetoothAdapter!!.isEnabled) {
            Log.i("BluetoothStitching", "hay bluetooth disponible")
            enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    fun enableBluetoothAndExecute(enableBluetooth: ActivityResultLauncher<Intent>, function: () -> Unit) {
        enableBluetooth(enableBluetooth)
        if(isBluetoothEnabled()) {
            function()
        }
    }

    fun isBluetoothEnabled(): Boolean{
        return bluetoothAdapter !=null && bluetoothAdapter!!.isEnabled
    }

    fun startDiscovering() {
        if (bluetoothAdapter!!.isDiscovering) {
            Log.i("BluetoothStitching", "Cancel discovery")
            bluetoothAdapter!!.cancelDiscovery()
        }
        Log.i("BluetoothStitching", "Start discovery")
        bluetoothAdapter!!.startDiscovery()
    }

    fun cancelDiscovery() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter!!.cancelDiscovery()
        }
    }


}
