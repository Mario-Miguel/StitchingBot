package es.uniovi.eii.stitchingbot.util

import android.app.Activity
import android.util.Log
import android.widget.ProgressBar
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.util.bluetooth.BluetoothService
import java.io.IOException

object ArduinoCommands {

    private val bluetoothService = BluetoothService

    fun doMotorStepsTest(motorSteps: Int){
        val stringToSend = "C;$motorSteps"
        bluetoothService.write(stringToSend)
    }

    fun doExecution(
        translation: MutableList<Pair<Int, Int>>,
        progressBar: ProgressBar,
        activity: Activity
    ) {
        BluetoothService.isInExecution =true
        val navView = activity.findViewById(R.id.nav_view) as NavigationView
        navView.menu.getItem(navView.menu.size()-1).isVisible=true

        Thread {
            BluetoothService.startedProcess = true
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

            counter = 0
            BluetoothService.write("S")


            // Keep listening to the InputStream until an exception occurs
            while (counter < ordersToSend.size) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = BluetoothService.read()
                    if (buffer[bytes] != 0.toByte())
                        hasMessage = true

                    var readMessage: String
                    if (buffer[bytes] == '\n'.toByte()) {
                        readMessage = String(buffer, 0, bytes).trim()
                        Log.e("Arduino Message", readMessage)

                        //AVANZO 2cm
                        if (readMessage == "M") {
                            Log.d("SEND_ORDERS", "$ordersToSend")
                            BluetoothService.write(ordersToSend[counter])
                            counter++
                            progressBar.progress = ((counter.toDouble()/ordersToSend.size.toDouble())*100).toInt()
                        }
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
            BluetoothService.isInExecution =false
            ShowDialog.showDialogOK(activity.applicationContext, "Ejecución terminada") { _, _ -> }
            val navController = activity.findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_summary, null)
        }.start()
    }
}