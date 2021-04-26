package es.uniovi.eii.stitchingbot.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import es.uniovi.eii.stitchingbot.bluetooth.request.ConnectionRequest
import es.uniovi.eii.stitchingbot.bluetooth.request.DiscoverRequest
import es.uniovi.eii.stitchingbot.bluetooth.request.EnableRequest
import es.uniovi.eii.stitchingbot.bluetooth.request.PairRequest

class BluetoothConnectionService (val context: Context) {
    private var eventListener : IBluetoothEventListener = BluetoothEventListener()
    private val enableRequest = EnableRequest(context, eventListener)
    private val discoverRequest = DiscoverRequest(context, eventListener)
    private val pairRequest = PairRequest(context, eventListener)
    private val connectionRequest = ConnectionRequest(eventListener)

    fun setBluetoothEventListener(listener: IBluetoothEventListener) {
        eventListener = listener
    }

    fun checkDeviceHasBluetooth(): Boolean{
        return enableRequest.deviceHasBluetooth()
    }


    fun enableBluetoothAdapter() {
        enableRequest.enableluetooth()
    }

    fun disableBluetoothAdapter() {
        enableRequest.disableBluetooth()
    }

    fun discoverDevices() {
        discoverRequest.discover()
    }

    fun getDiscoveredDevices(){
        discoverRequest.getDiscoveredDevices()
    }

    fun getPairedDevices(): List<BluetoothDevice>{
        return pairRequest.getPairedDevices();
    }

    fun pairDevice(device : BluetoothDevice) {
        pairRequest.pair(device)
    }

    fun connectDevice(device: BluetoothDevice) {
        connectionRequest.connect(device)
    }

    fun stopConnectDevice() {
        connectionRequest.stopConnect()
    }


    fun cleanUp() {
        enableRequest.cleanup()
        discoverRequest.cleanup()
        pairRequest.cleanup()
    }
}