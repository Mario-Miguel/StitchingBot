package es.uniovi.eii.stitchingbot.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.thread


const val TAG = "BluetoothStitching"

object MyBluetoothService {


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

    fun startExecution(translation: MutableList<Pair<Int, Int>>) {

        startedProcess = true
        val ordersToSend = mutableListOf<String>()
        val auxString = StringBuilder()
        var counter = 0

//        for (coord in translation) {
//            auxString.append("${coord.first},${coord.second};")
//            counter++
//            if (counter == 100) {
//                ordersToSend.add(auxString.toString())
//                auxString.clear()
//                counter = 0
//            }
//        }

        for (i in 1..701) {
            auxString.append("${i},0;")
            counter++
            if (counter == 100) {
                ordersToSend.add(auxString.toString())
                auxString.clear()
                counter = 0
            }
        }


        //thread(start=true, isDaemon = true){
        val buffer = ByteArray(1024) // buffer store for the stream
        var bytes = 0 // bytes returned from read()
        var hasMessage=false
        Log.i("BluetoothStitching", "Connected thread run")

        counter = 0
        mmOutStream.write(ordersToSend[counter].toByteArray())
        counter++

        // Keep listening to the InputStream until an exception occurs
        while (counter < ordersToSend.size) {
            try {
                /*
                Read from the InputStream from Arduino until termination character is reached.
                Then send the whole String message to GUI Handler.
                 */
                    buffer[bytes] = mmInStream.read().toByte()
                if(buffer[bytes] != 0.toByte())
                    hasMessage=true

                var readMessage: String
                if (buffer[bytes] == '\n'.toByte()) {
                    readMessage = String(buffer, 0, bytes).trim()
                    Log.e("Arduino Message", readMessage)

                    //AVANZO 2cm
                    if (readMessage == "M") {
                        mmOutStream.write(ordersToSend[counter].toByteArray())
                        counter++
                    }
                    bytes = 0
                    hasMessage=false
                    buffer.fill(0)
                } else if(hasMessage){
                    bytes++
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
        //}
        Log.i(TAG, "Sa acabao")

    }

}

//Detectar el objeto completo -> Mirar a ver loq ue esta rodeado por ceroas y despues coserlo de izquierda a derecha y de arriba a abajo
//Si no hacer un grafo uniendo los nodos, lo que habia pensao que no me salio
