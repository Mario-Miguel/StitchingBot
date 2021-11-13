package es.uniovi.eii.stitchingbot.arduinoCommunication

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
import java.io.IOException

object ArduinoCommands {

    private val stateManager = StateManager
    private var actionsSent: Int = 0
    private val privateActualProgress = MutableLiveData<Int>()
    val actualProgress: LiveData<Int>
        get() = privateActualProgress


    /**
     * Método que envía la orden al robot de hacer la prueba de los pasos del motor
     *
     * @param motorSteps, Cantidad de pasos que se desea que se mueve el motor
     */
    fun doMotorStepsTest(motorSteps: Int) {
        val stringToSend = "$CONFIGURE_PULLEY;$motorSteps"
        BluetoothService.write(stringToSend)
    }

    /**
     * Método encargado de el proceso de comunicación entre el robot y la aplicación.
     *
     * Envía y recibe órdenes del robot y actualiza los estados de ejecución.
     *
     * @param translation, Lista con las coordenadas que se han de enviar al robot.
     */
    fun startExecution(
        translation: MutableList<Pair<Int, Int>>
    ) {
        privateActualProgress.postValue(0)
        BluetoothService.startedProcess = true
        stateManager.changeTo(ExecutingState())

        val ordersToSend = createOrdersToSendStrings(translation)

        beginSendingAndReceivingOrders(ordersToSend)

        actionsSent = 0
        stateManager.changeTo(StoppedState())
        privateActualProgress.postValue(0)
    }


    /**
     * Método que maneja el envío y recepción de órdenes del robot.
     *
     * @param ordersToSend, lista de ordenes que se le han de pasar al robot.
     */
    private fun beginSendingAndReceivingOrders(ordersToSend: MutableList<String>) {
        val buffer = ByteArray(1024)
        var bytes = 0
        var hasMessage = false
        BluetoothService.write(START_EXECUTION)

        while (actionsSent < ordersToSend.size) {
            if (!BluetoothService.isBluetoothEnabled()) {
                pauseExecution()
            } else {
                try {
                    buffer[bytes] = BluetoothService.read()
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

    /**
     * Método que decide que se debe hacer en función del mensaje que se recibe del robot.
     *
     * @param readMessage, mensaje enviado por el robot
     * @param ordersToSend, lista de ordenes que se le han de pasar al robot.
     */
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

    /**
     * Método que envía órdenes al robot a través de la clase BluetoothService.
     *
     * Además, también actualiza el número de órdenes enviadas, así como el progreso actual de la
     * ejecución.
     *
     * @param ordersToSend, listqa con las ordenes que se han de enviar.
     */
    private fun sendOrders(ordersToSend: MutableList<String>) {
        BluetoothService.write(ordersToSend[actionsSent])
        actionsSent++
        privateActualProgress.postValue(((actionsSent.toDouble() / ordersToSend.size.toDouble()) * 100).toInt())
    }

    /**
     * Metodo que crea las ordenes que se van a enviar al robot.
     *
     * Crea un lista de Strings formados por paquetes de coordenadas. Cada pareja de coordenadas
     * están separadas por un ';' mientras que cada una de las coordenadas de la pareja está separada
     * por una ','
     *
     * @return MutableList<String> con los paquetes de coordenadas
     */
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

    /**
     * Envía al robot la orden de pausar la ejecución.
     *
     * También cambia el estado de la ejecución de la app a Paused
     */
    fun pauseExecution() {
        BluetoothService.write(PAUSE_EXECUTION)
        stateManager.changeTo(PausedState())
    }

    /**
     * Envía al robot la orden de continuar con la ejecución.
     *
     * También cambia el estado de la ejecución de la app a Executing
     */
    fun resumeExecution() {
        BluetoothService.write(RESUME_EXECUTION)
        stateManager.changeTo(ExecutingState())
    }

    /**
     * Envía al robot la orden de parar la ejecución.
     *
     * También cambia el estado de la ejecución de la app a Stopped
     */
    fun stopExecution() {
        BluetoothService.write(STOP_EXECUTION)
        stateManager.changeTo(StoppedState())
    }

    /**
     * Envía al robot la orden de moverse en una dirección concreta
     *
     * @param direction, dirección en la que se desea mover
     */
    fun move(direction: String) {
        BluetoothService.write(direction)
    }

    /**
     * Envía al robot la orden de ejecutar el autohome
     */
    fun startAutoHome() {
        BluetoothService.write(START_AUTOHOME)
    }

}