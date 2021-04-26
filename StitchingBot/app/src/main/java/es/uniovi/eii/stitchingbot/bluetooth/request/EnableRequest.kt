package es.uniovi.eii.stitchingbot.bluetooth.request

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import es.uniovi.eii.stitchingbot.bluetooth.IBluetoothEventListener

class EnableRequest(private val context : Context, private val eventListener: IBluetoothEventListener) : IBluetoothRequest {

    private var requestEnable = false
    private lateinit var bluetoothAdapter : BluetoothAdapter

    init {
        registerReceiver()
    }

    private fun registerReceiver() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        context.registerReceiver(enableReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    fun enableluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            requestEnable = true
            bluetoothAdapter.enable()
        }
        else
            eventListener.onEnable()
    }

    fun disableBluetooth() {
        bluetoothAdapter.disable()
    }

    override fun cleanup() {
        context.unregisterReceiver(enableReceiver)
    }

    private val enableReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (!requestEnable &&
                    BluetoothAdapter.ACTION_STATE_CHANGED != action)
                return

            requestEnable = false
            eventListener.onEnable()
        }
    }

    fun deviceHasBluetooth(): Boolean{
        val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false

        return true;


    }

}