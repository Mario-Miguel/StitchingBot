package es.uniovi.eii.stitchingbot.model

import android.bluetooth.BluetoothDevice
import java.util.*

//Meter tambien el UUID del dispositivo
data class Device(var name: String, val mac: String, val status: Int, val type: Int, val uuid: UUID?) {
    constructor(bluetoothDevice: BluetoothDevice) :
            this(bluetoothDevice.name,
                    bluetoothDevice.address,
                    bluetoothDevice.bondState,
                    bluetoothDevice.type, null)

    override fun toString(): String {

        return "$name - $mac - $status - $type - ${uuid ?: "No tiene UUID"}"

    }
}