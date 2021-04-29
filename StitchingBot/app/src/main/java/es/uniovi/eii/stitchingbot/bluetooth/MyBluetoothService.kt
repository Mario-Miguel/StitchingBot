package es.uniovi.eii.stitchingbot.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import es.uniovi.eii.stitchingbot.ui.MY_UUID
import java.io.IOException


private const val TAG = "BluetoothStitching"
//TODO PASAR ESTO A OTRA CLASE DISTINTA
private const val CONNECTING_STATUS = 1 // used in bluetooth es.uniovi.eii.stitchingbot.bluetooth.getHandler to identify message status


class MyBluetoothService(bluetoothAdapter: BluetoothAdapter, address: String) : Thread() {

    lateinit var handler: Handler
    var mmSocket: BluetoothSocket
    lateinit var connectedThread: ConnectedThread

    override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.cancelDiscovery()
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect()
            Log.i(TAG, "Device connected")
            handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget()
        } catch (connectException: IOException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close()
                Log.i(TAG, "Cannot connect to device")
                handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget()
            } catch (closeException: IOException) {
                Log.i(TAG, "Could not close the client socket", closeException)
            }
            return
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        connectedThread = ConnectedThread(mmSocket, this.handler)
        connectedThread.run()
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

    init {
        /*
        Use a temporary object that is later assigned to es.uniovi.eii.stitchingbot.bluetooth.getMmSocket
        because es.uniovi.eii.stitchingbot.bluetooth.getMmSocket is final.
         */
        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
        var tmp: BluetoothSocket? = null
        val uuid = MY_UUID

        //val uuid = bluetoothDevice.uuids[0].uuid
        try {
            /*
            Get a BluetoothSocket to connect with the given BluetoothDevice.
            Due to Android device varieties,the method below may not work fo different devices.
            You should try using other methods i.e. :
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
             */
            tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
        } catch (e: IOException) {
            Log.e(TAG, "Socket's create() method failed", e)
        }
        mmSocket = tmp!!

        createHandler()
    }


    fun createHandler(){
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    CONNECTING_STATUS -> when (msg.arg1) {
                        1 -> {
                            Log.i("BluetoothStitching", "es.uniovi.eii.stitchingbot.bluetooth.CONNECTING_STATUS -> 1")

                        }
                        -1 -> {
                            Log.i("BluetoothStitching", "es.uniovi.eii.stitchingbot.bluetooth.CONNECTING_STATUS -> -1")
                        }
                    }
                    MESSAGE_READ -> {
                        //READ MESSAGE FROM ARDUINO
                        val arduinoMsg: String = msg.obj.toString() // Read message from Arduino
                        when (arduinoMsg.toLowerCase()) {
                            //TODO cambiar esto por los mensajes del arduino
                            "led is turned on" -> {
                                Log.i("BluetoothStitching", "")
                            }
                            "led is turned off" -> {
                                Log.i("BluetoothStitching", "")
                            }
                        }
                    }
                }
            }
        }
    }
}

