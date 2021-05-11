package es.uniovi.eii.stitchingbot.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException


const val TAG = "BluetoothStitching"

object MyBluetoothService {


    private var connectionSocket: BluetoothSocket?=null
    private lateinit var handler: Handler

    fun setConnectionSocket(socket: BluetoothSocket){
        this.connectionSocket=socket
    }

    fun getConnectionSocket(): BluetoothSocket? {
        return this.connectionSocket
    }

    fun setHandler(handler: Handler){
        this.handler=handler
    }

    fun getHandler(): Handler {
        return this.handler
    }

    fun closeConnectionSocket(){
        try {
            if (connectionSocket != null)
                connectionSocket!!.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

}

