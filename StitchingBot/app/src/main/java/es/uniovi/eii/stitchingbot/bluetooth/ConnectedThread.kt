package es.uniovi.eii.stitchingbot.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import es.uniovi.eii.stitchingbot.ui.arduino.MESSAGE_READ
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream




/* =============================== Thread for Data Transfer =========================================== */
class ConnectedThread(private val mmSocket: BluetoothSocket, private val handler: Handler) : Thread() {

    private val mmInStream: InputStream?
    private val mmOutStream: OutputStream?

    override fun run() {

        val buffer = ByteArray(1024) // buffer store for the stream
        var bytes = 0 // bytes returned from read()
        Log.i("BluetoothStitching", "Connected thread run")
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                /*
                Read from the InputStream from Arduino until termination character is reached.
                Then send the whole String message to GUI Handler.
                 */
                if (mmInStream != null) {
                    buffer[bytes] = mmInStream.read() as Byte
                }
                var readMessage: String
                if (buffer[bytes] == '\n'.toByte()) {
                    readMessage = String(buffer, 0, bytes)
                    Log.e("Arduino Message", readMessage)
                    handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget()
                    bytes = 0
                } else {
                    bytes++
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    fun write(input: String) {
        val bytes = input.toByteArray() //converts entered String into bytes
        Log.i("BluetoothStitching", "Mensaje enviado")
        try {
            mmOutStream!!.write(bytes)
        } catch (e: IOException) {
            Log.e("Send Error", "Unable to send message", e)
        }
    }

    /* Call this from the main activity to shutdown the connection */
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
        }
    }

    init {
        var tmpIn: InputStream? = null
        var tmpOut: OutputStream? = null

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = mmSocket.inputStream
            tmpOut = mmSocket.outputStream
        } catch (e: IOException) {
        }
        mmInStream = tmpIn
        mmOutStream = tmpOut
    }
}