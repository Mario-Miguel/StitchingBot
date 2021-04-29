package es.uniovi.eii.stitchingbot.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.adapter.DevicesListAdapter
import es.uniovi.eii.stitchingbot.bluetooth.MyBluetoothService
import kotlinx.android.synthetic.main.fragment_arduino_configuration.*
import java.util.*

var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class ArduinoConfigurationFragment : Fragment() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var deviceAdapter : DevicesListAdapter
    private val REQUEST_ENABLE_BT: Int = 3
    private val REQUEST_PERMISSION_BLUETOOTH: Int = 5


    var createConnectThread: MyBluetoothService? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arduino_configuration, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initBluetoothAdapter()
        //Register receiver
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)

        initUI()

    }


    override fun onDestroy() {
        super.onDestroy()

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery()
        }
        requireActivity().unregisterReceiver(receiver)

        createConnectThread?.cancel()


    }



    /**
     * Función que inicia el BluetoothAdapter. Se encarga de pedir los permisos necesarios para el
     * correcto funcionamiento.
     */
    private fun initBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Log.i("BluetoothStitching", "No hay bluetooth disponible")
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.i("BluetoothStitching", "hay bluetooth disponible")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        if (ContextCompat.checkSelfPermission(this.requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Not to annoy user.
                Log.i("BluetoothStitching", "Permission must be granted to use the app.")
                //Toast.makeText(this, "Permission must be granted to use the app.", Toast.LENGTH_SHORT).show();
            } else {

                // Request permission.
                ActivityCompat.requestPermissions(this.requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSION_BLUETOOTH)
            }
        } else {
            // Permission has already been granted.
            Log.i("BluetoothStitching", "Permission already granted.")
            //Toast.makeText(this, "Permission already granted.", Toast.LENGTH_SHORT).show();
        }

    }


    private fun initUI(){
        //Se crea el listener
        val handler = DevicesListAdapter.OnItemClickListener{ device -> bondDevice(device)}
        //Se crea el adapter con el listener
        deviceAdapter = DevicesListAdapter(handler)

        rvDevicesList.adapter = deviceAdapter
        rvDevicesList.layoutManager = LinearLayoutManager(this.context)
        rvDevicesList.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        fabDiscoverDevices.setOnClickListener { startDiscovery() }

    }

    private fun bondDevice(bluetoothDevice: BluetoothDevice){
        createConnectThread = MyBluetoothService(bluetoothAdapter, bluetoothDevice.address)

        createConnectThread!!.start()
    }

    /**
     * Añade un nuevo dispositivo al adapter
     */
    private fun addDeviceToAdapter(device: BluetoothDevice) {
        Log.i("BluetoothStitching", "Añadido dispositivo")
        deviceAdapter.addElement(device)



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                    Log.i("BluetoothStitching", "No se ha activado el bluetooth")
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_BLUETOOTH -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.requireContext(), "Permission granted.", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this.requireContext(), "Permission must be granted to use the application.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun startDiscovery() {
        deviceAdapter.clearElements()
        if (bluetoothAdapter.isDiscovering) {
            Log.i("BluetoothStitching", "Cancel discovery")
            bluetoothAdapter.cancelDiscovery()
        }
        Log.i("BluetoothStitching", "Start discovery")
        bluetoothAdapter.startDiscovery()
    }


    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val bDevice: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (bDevice != null) {
                        //val device = Device(bDevice)

                        Log.i("BluetoothStitching", "Dispositivo: ${bDevice.name} - ${bDevice.address}")
                        addDeviceToAdapter(bDevice)
                    }
                }
            }
        }
    }





}



