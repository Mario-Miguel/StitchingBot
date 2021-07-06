package es.uniovi.eii.stitchingbot.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.util.ShowDialog
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


const val TAG = "BluetoothStitching"

object MyBluetoothService {

    var isInExecution = false

    private var connectionSocket: BluetoothSocket? = null
    private lateinit var mmInStream: InputStream
    private lateinit var mmOutStream: OutputStream

    private lateinit var handler: Handler
    private var startedProcess = false

    fun setConnectionSocket(socket: BluetoothSocket) {
        this.connectionSocket = socket
        mmInStream = connectionSocket!!.inputStream
        mmOutStream = connectionSocket!!.outputStream
    }

    fun getConnectionSocket(): BluetoothSocket? {
        return this.connectionSocket
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }

    fun getHandler(): Handler {
        return this.handler
    }

    fun closeConnectionSocket() {
        try {
            if (connectionSocket != null)
                connectionSocket!!.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
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

    fun startExecution(
        translation: MutableList<Triple<Int, Int, Boolean>>,
        progressBar: ProgressBar,
        activity: Activity
    ) {
        isInExecution=true
        val navView = activity.findViewById(R.id.nav_view) as NavigationView
        navView.menu.getItem(navView.menu.size()-1).isVisible=true
        Thread {
            startedProcess = true
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
                            progressBar.progress = counter*50
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
            isInExecution=false
            Log.i(TAG, "Sa acabao")
            ShowDialog.showDialogOK(activity.applicationContext, "Ejecución terminada") { _, _ -> }
            val navController = activity.findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_summary, null)

        }


    }

}

//Detectar el objeto completo -> Mirar a ver loq ue esta rodeado por ceroas y despues coserlo de izquierda a derecha y de arriba a abajo
//Si no hacer un grafo uniendo los nodos, lo que habia pensao que no me salio
