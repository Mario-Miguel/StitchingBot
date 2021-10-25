package es.uniovi.eii.stitchingbot.util.arduinoCommands

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.stitchingbot.ui.fragments.summary.states.*
import es.uniovi.eii.stitchingbot.util.Constants.ASK_FOR_ACTIONS
import es.uniovi.eii.stitchingbot.util.Constants.CONFIGURE_PULLEY
import es.uniovi.eii.stitchingbot.util.Constants.PAUSE_EXECUTION
import es.uniovi.eii.stitchingbot.util.Constants.RESUME_EXECUTION
import es.uniovi.eii.stitchingbot.util.Constants.START_AUTOHOME
import es.uniovi.eii.stitchingbot.util.Constants.START_EXECUTION
import es.uniovi.eii.stitchingbot.util.Constants.STOP_EXECUTION
import es.uniovi.eii.stitchingbot.util.bluetooth.BluetoothService
import java.io.IOException

object ArduinoCommands {

    private val bluetoothService = BluetoothService
    private val stateManager = StateManager
    private var actionsSent: Int = 0

    private val _actualProgress = MutableLiveData<Int>()
    val actualProgress: LiveData<Int>
        get() = _actualProgress


    fun doMotorStepsTest(motorSteps: Int) {
        val stringToSend = "$CONFIGURE_PULLEY;$motorSteps"
        bluetoothService.write(stringToSend)
    }

    fun startExecution(
        translation: MutableList<Pair<Int, Int>>
    ) {
        _actualProgress.postValue(0)
        bluetoothService.startedProcess = true
        stateManager.changeTo(ExecutingState())

        val ordersToSend = createOrdersToSendStrings(translation)

        beginSendingAndReceivingOrders(ordersToSend)

        actionsSent = 0
        stateManager.changeTo(StoppedState())
        _actualProgress.postValue(0)
    }


    private fun beginSendingAndReceivingOrders(ordersToSend: MutableList<String>) {
        val buffer = ByteArray(1024) // buffer store for the stream
        var bytes = 0 // bytes returned from read()
        var hasMessage = false
        Log.i("BluetoothStitching", "Connected thread run")
        bluetoothService.write(START_EXECUTION)

        // Keep listening to the InputStream until an exception occurs
        while (actionsSent < ordersToSend.size) {
            if(!bluetoothService.isBluetoothEnabled()){
                pauseExecution()
            }
            else {
                try {
                    //Read from the InputStream from Arduino until termination character is reached.
                    //Then send the whole String message to Handler.
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
        }

        stateManager.changeTo(StoppedState())
    }

    private fun dispatchReadMessage(readMessage: String, ordersToSend: MutableList<String>) {
        when (readMessage) {
            ASK_FOR_ACTIONS -> {
                sendOrders(ordersToSend)
            }
            STOP_EXECUTION -> {
                actionsSent = ordersToSend.size
            }
        }
    }

    private fun sendOrders(ordersToSend: MutableList<String>) {
        bluetoothService.write(ordersToSend[actionsSent])
        actionsSent++
        _actualProgress.postValue(((actionsSent.toDouble() / ordersToSend.size.toDouble()) * 100).toInt())
    }

    private fun createOrdersToSendStrings(translation: MutableList<Pair<Int, Int>>): MutableList<String> {
        val ordersToSend: MutableList<String> = mutableListOf()
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
        return ordersToSend
    }

    fun pauseExecution() {
        bluetoothService.write(PAUSE_EXECUTION)
        stateManager.changeTo(PausedState())
    }

    fun resumeExecution() {
        bluetoothService.write(RESUME_EXECUTION)
        stateManager.changeTo(ExecutingState())
    }

    fun stopExecution() {
        bluetoothService.write(STOP_EXECUTION)
        stateManager.changeTo(StoppedState())
    }

    fun move(direction: String){
        bluetoothService.write(direction)
    }

    fun startAutoHome() {
        bluetoothService.write(START_AUTOHOME)
    }

}