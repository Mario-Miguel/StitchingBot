package es.uniovi.eii.stitchingbot.arduinoCommunication

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
import es.uniovi.eii.stitchingbot.util.Constants
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
        setHandler()
        initBluetoothAdapter()
    }

    /**
     * Cierra el socket de conexión bluetooth
     */
    fun closeConnectionSocket() {
        try {
            if (connectionSocket != null)
                connectionSocket!!.close()
        } catch (e: IOException) {
            Log.e(Constants.TAG_BLUETOOTH, "Could not close the client socket", e)
        }
    }

    /**
     * Funcion que devuelve el estado de conexión del socket bluetooth
     */
    fun isConnected(): Boolean {
        return if (connectionSocket == null) false else connectionSocket!!.isConnected
    }

    /**
     * Escribe datos para ser enviados
     */
    fun write(input: String) {
        val bytes = input.toByteArray() //converts entered String into bytes
        Log.i("BluetoothStitching", "Mensaje enviado")
        try {
            mmOutStream.write(bytes)
        } catch (e: IOException) {
            Log.e("Send Error", "Unable to send message", e)
        }
    }

    /**
     * Lee los datos recibidos desde la entrada bluetooth
     */
    fun read(): Byte {
        return mmInStream.read().toByte()
    }

    /**
     * Método que intenta conectar el dispositivo móvil con el dispositivo bluetooth seleccionado
     *
     * @param bluetoothDevice Dispositivo bluetooth con el que establecer conexión
     */
    fun tryToConnect(bluetoothDevice: BluetoothDevice) {
        bluetoothAdapter!!.cancelDiscovery()
        try {
            setConnectionSocket(
                bluetoothDevice.createInsecureRfcommSocketToServiceRecord(
                    Constants.MY_UUID
                )
            )
            connectionSocket!!.connect()
            Log.i(Constants.TAG_BLUETOOTH, "Device connected")
            handler.obtainMessage(Constants.CONNECTING_STATUS, 1, -1).sendToTarget()
        } catch (connectException: IOException) {
            closeConnectionSocket()
            handler.obtainMessage(Constants.CONNECTING_STATUS, -1, -1).sendToTarget()
        }
    }

    /**
     * Inicia el recibidor
     *
     * @param command Acción a ejecutar en caso de que no se encuentre el dispositivo bluetooth
     */
    private fun setReceiver(command: (bDevice: BluetoothDevice) -> Unit) {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val bDevice: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (bDevice != null) {
                            command(bDevice)
                        }
                    }
                }
            }
        }
    }

    /**
     * Registra el receiver en la Activity que se le pasa por parámetro
     *
     * @param activity actividad a la que se le va a asociar el recibidor
     * @param command acción que se desea ejecutar si no se encuentra el dispositivo
     */
    fun registerReceiver(activity: Activity, command: (bDevice: BluetoothDevice) -> Unit) {
        setReceiver { device -> command(device) }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(receiver, filter)
    }

    /**
     * Elimina el recibidor de la Activity asoiciada
     */
    fun unregisterReceiver(activity: Activity) {
        activity.unregisterReceiver(receiver)
    }

    /**
     * Inicia las acciones a ejecutar en caso de éxito o fracaso
     *
     * @param okCommand accion a ejecutar en caso de éxito
     * @param errCommand acción a ejecutar en caso de fracaso
     */
    fun setHandlerCommands(okCommand: () -> Unit, errCommand: () -> Unit) {
        setHandler(okCommand, errCommand)
    }

    /**
     * Inicia el handler para la conexión con el dispositivo
     *
     * @param okCommand accion a ejecutar en caso de éxito
     * @param errCommand acción a ejecutar en caso de fracaso
     */
    private fun setHandler(okCommand: () -> Unit = {}, errCommand: () -> Unit = {}) {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    Constants.CONNECTING_STATUS -> when (msg.arg1) {
                        1 -> {
                            okCommand()
                        }
                        -1 -> {
                            errCommand()
                        }
                    }
                }
            }
        }
    }

    /**
     * Inicia el socket de conexión bluetooth, el Stream de entrada y el Stream de salida
     *
     * @param socket socket de conexión bluetooth
     */
    private fun setConnectionSocket(socket: BluetoothSocket) {
        connectionSocket = socket
        mmInStream = connectionSocket!!.inputStream
        mmOutStream = connectionSocket!!.outputStream
    }

//##################################################################################################
    /**
     * Inicia el [BluetoothAdapter]
     */
    private fun initBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.i("BluetoothStitching", "No hay bluetooth disponible")
        }
    }

    /**
     * Lanza el evento de activación del bluetooth en caso de no estar activado ya
     *
     * @param enableBluetooth evento de activación del bluetooth del dispositivo móvil
     */
    private fun enableBluetooth(enableBluetooth: ActivityResultLauncher<Intent>) {
        if (!bluetoothAdapter!!.isEnabled) {
            Log.i("BluetoothStitching", "hay bluetooth disponible")
            enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    /**
     * Lanza el evento de activación del bluetooth en caso de no estar activado ya. En caso de no
     * activarse, ejecuta la función pasada por parámetro
     *
     * @param enableBluetooth evento de activación del bluetooth del dispositivo móvil
     * @param function función a ejecutar en caso de no activarse el bluetooth
     */
    fun enableBluetoothAndExecute(
        enableBluetooth: ActivityResultLauncher<Intent>,
        function: () -> Unit
    ) {
        enableBluetooth(enableBluetooth)
        if (isBluetoothEnabled()) {
            function()
        }
    }

    /**
     * Devuelve el estado de acivación del bluetooth del dispositivo móvil
     *
     * @return true si está activado o false en caso contrario
     */
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter!!.isEnabled
    }

    /**
     * Inicia el escaneo de dispositivos bluetooth disponibles
     */
    fun startDiscovering() {
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        bluetoothAdapter!!.startDiscovery()
    }

    /**
     * Cancela el escaneo de dispositivos bluetooth disponibles
     */
    fun cancelDiscovery() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter!!.cancelDiscovery()
        }
    }
}
