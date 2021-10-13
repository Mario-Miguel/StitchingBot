package es.uniovi.eii.stitchingbot.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.stitchingbot.util.Constants.ASK_FOR_ACTIONS
import es.uniovi.eii.stitchingbot.util.Constants.CONFIGURE_PULLEY
import es.uniovi.eii.stitchingbot.util.Constants.PAUSE_EXECUTION
import es.uniovi.eii.stitchingbot.util.Constants.RESUME_EXECUTION
import es.uniovi.eii.stitchingbot.util.Constants.START_EXECUTION
import es.uniovi.eii.stitchingbot.util.Constants.STOP_EXECUTION
import es.uniovi.eii.stitchingbot.util.bluetooth.BluetoothService
import java.io.IOException

object ArduinoCommands {

    private val bluetoothService = BluetoothService
    var isInExecution = false
    var isInPause = false
    private var actionsSent = 0

    private val _actualProgress = MutableLiveData<Int>()
    val actualProgress: LiveData<Int>
        get() = _actualProgress

    fun doMotorStepsTest(motorSteps: Int) {
        val stringToSend = "$CONFIGURE_PULLEY;$motorSteps"
        bluetoothService.write(stringToSend)
    }

    fun doExecution(
        translation: MutableList<Pair<Int, Int>>,
    ) {
        _actualProgress.postValue(0)
        isInExecution = true

        bluetoothService.startedProcess = true

        val ordersToSend = mutableListOf<String>()
        val auxString = StringBuilder()
        var counter = 0
        for (coord in translation) {
            auxString.append("${coord.first},${coord.second};")
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

        actionsSent = 0
        bluetoothService.write(START_EXECUTION)


        // Keep listening to the InputStream until an exception occurs
        while (actionsSent < ordersToSend.size) {
            try {
                /*
                Read from the InputStream from Arduino until termination character is reached.
                Then send the whole String message to GUI Handler.
                 */
                buffer[bytes] = bluetoothService.read()
                if (buffer[bytes] != 0.toByte())
                    hasMessage = true

                var readMessage: String
                if (buffer[bytes] == '\n'.toByte()) {
                    readMessage = String(buffer, 0, bytes).trim()

                    dispatchReadMessage(readMessage, ordersToSend)

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
        isInExecution = false
        _actualProgress.postValue(0)

    }

    private fun dispatchReadMessage(readMessage: String, ordersToSend: MutableList<String>) {
        when(readMessage){
            ASK_FOR_ACTIONS->{
                bluetoothService.write(ordersToSend[actionsSent])
                actionsSent++
                _actualProgress.postValue(((actionsSent.toDouble() / ordersToSend.size.toDouble()) * 100).toInt())
            }
            PAUSE_EXECUTION->{

            }
            RESUME_EXECUTION ->{

            }
            STOP_EXECUTION->{

            }
        }

    }

    fun pauseExecution() {
        bluetoothService.write(PAUSE_EXECUTION)
        isInPause = true
    }

    fun resumeExecution() {
        bluetoothService.write(RESUME_EXECUTION)
        isInPause = false
    }

    fun stopExecution() {
        bluetoothService.write(STOP_EXECUTION)
    }

    fun isConnected(): Boolean {
        return bluetoothService.getConnectionSocket()?.isConnected == true
    }
}