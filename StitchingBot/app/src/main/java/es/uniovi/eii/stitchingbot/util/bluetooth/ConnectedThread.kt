package es.uniovi.eii.stitchingbot.util.bluetooth

import android.app.Activity
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


/* =============================== Thread for Data Transfer =========================================== */
class ConnectedThread(
    private val mmInStream: InputStream,
    private val mmOutStream: OutputStream,
    activity: Activity
) : Thread() {

    private var mActivity:Activity = activity
    private var mHandler: Handler = Handler(activity.mainLooper)

    private lateinit var translation: MutableList<Triple<Int, Int, Boolean>>


    override fun run() {

        val ordersToSend = mutableListOf<String>()
        val auxString = StringBuilder()
        var counter = 0

        for (coord in translation) {
            auxString.append("${coord.first},${coord.second},${if (coord.third) 1 else 0};")
            counter++
            if (counter == 50) {
                ordersToSend.add(auxString.toString())
                auxString.clear()
                counter = 0
            }
        }


        val buffer = ByteArray(1024) // buffer store for the stream
        var bytes = 0 // bytes returned from read()
        var hasMessage = false
        Log.i("BluetoothStitching", "Connected thread run")

        counter = 0
        mmOutStream.write("S".toByteArray())


        // Keep listening to the InputStream until an exception occurs
        while (counter < ordersToSend.size) {
            try {
                /*
                Read from the InputStream from Arduino until termination character is reached.
                Then send the whole String message to GUI Handler.
                 */
                buffer[bytes] = mmInStream.read().toByte()
                if (buffer[bytes] != 0.toByte())
                    hasMessage = true

                var readMessage: String
                if (buffer[bytes] == '\n'.toByte()) {
                    readMessage = String(buffer, 0, bytes).trim()
                    Log.e("Arduino Message", readMessage)

                    //AVANZO 2cm
                    if (readMessage == "M") {
                        mmOutStream.write(ordersToSend[counter].toByteArray())
                        counter++
                        //progressBar.progress = counter * 50
                    }
                    bytes = 0
                    hasMessage = false
                    buffer.fill(0)
                } else if (hasMessage) {
                    bytes++
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
        BluetoothService.isInExecution = false
        Log.i(TAG, "Sa acabao")

    }

    /* Call this from the main activity to send data to the remote device */
//    fun write(input: String) {
//        val bytes = input.toByteArray() //converts entered String into bytes
//        Log.i("BluetoothStitching", "Mensaje enviado")
//        try {
//            mmOutStream!!.write(bytes)
//        } catch (e: IOException) {
//            Log.e("Send Error", "Unable to send message", e)
//        }
//    }

    fun write(input: String) {
        val bytes = input.toByteArray() //converts entered String into bytes
        Log.i("BluetoothStitching", "Mensaje enviado")
        try {
            mmOutStream.write(bytes)
        } catch (e: IOException) {
            Log.e("Send Error", "Unable to send message", e)
        }
    }

    /* Call this from the main activity to shutdown the connection */
//    fun cancel() {
//        try {
//            mmSocket.close()
//        } catch (e: IOException) {
//        }
//    }


}