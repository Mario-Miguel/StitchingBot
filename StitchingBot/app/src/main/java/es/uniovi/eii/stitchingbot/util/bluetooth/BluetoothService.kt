package es.uniovi.eii.stitchingbot.util.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import es.uniovi.eii.stitchingbot.util.Constants
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object BluetoothService {

    private var connectionSocket: BluetoothSocket? = null
    private lateinit var mmInStream: InputStream
    private lateinit var mmOutStream: OutputStream

    private lateinit var handler: Handler
    var startedProcess = false

    fun setConnectionSocket(socket: BluetoothSocket) {
        connectionSocket = socket
        mmInStream = connectionSocket!!.inputStream
        mmOutStream = connectionSocket!!.outputStream
    }

    fun getConnectionSocket(): BluetoothSocket? {
        return connectionSocket
    }

    fun setHandler(handler: Handler) {
        BluetoothService.handler = handler
    }

    fun getHandler(): Handler {
        return handler
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



}
