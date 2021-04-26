package es.uniovi.eii.stitchingbot.bluetooth

import android.app.Activity
import android.util.Log

class BluetoothEventListener() : IBluetoothEventListener {


    override fun onEnable() {
    }

    override fun onDiscovering() {
        Log.i("BluetoothStitching", "Se están buscando dispositivos")
    }

    override fun onDiscovered() {
        Log.i("BluetoothStitching", "Se encontró el dispositivo")
    }

    override fun onConnecting() {
    }

    override fun onConnected(isSuccess: Boolean) {
    }

    override fun onPairing() {
    }

    override fun onPaired() {
    }

    override fun onDisconnecting() {
    }

    override fun onDisconnected() {
    }
}