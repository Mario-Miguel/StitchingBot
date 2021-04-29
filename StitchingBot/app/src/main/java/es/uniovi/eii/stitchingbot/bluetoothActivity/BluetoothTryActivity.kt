//package es.uniovi.eii.stitchingbot.bluetoothActivity
//
//import android.Manifest
//import android.app.Activity
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.content.pm.PackageManager
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import es.uniovi.eii.stitchingbot.R
//import kotlinx.android.synthetic.main.fragment_arduino_configuration.*
//
//class BluetoothTryActivity : AppCompatActivity() {
//
//    companion object {
//        const val ENABLE_BLUETOOTH = 1
//        const val REQUEST_ENABLE_DISCOVERY = 2
//        const val REQUEST_ACCESS_COARSE_LOCATION = 3
//    }
//
//    /* Broadcast receiver to listen for discovery results. */
//    private val bluetoothDiscoveryResult = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == BluetoothDevice.ACTION_FOUND) {
//                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
//                deviceListAdapter.addDevice(device)
//            }
//        }
//    }
//
//    /* Broadcast receiver to listen for discovery updates. */
//    private val bluetoothDiscoveryMonitor = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            when (intent?.action) {
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
//                    progress_bar.visible()
//                    toast("Scan started...")
//                }
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
//                    progress_bar.invisible()
//                    toast("Scan complete. Found ${deviceListAdapter.itemCount} devices.")
//                }
//            }
//        }
//    }
//
//    private val es.uniovi.eii.stitchingbot.bluetooth.getBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//    private val deviceListAdapter = DevicesAdapter()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_devices)
//        initUI()
//    }
//
//    private fun initUI() {
//        title = "Bluetooth Scanner"
//        recycler_view_devices.adapter = deviceListAdapter
//        recycler_view_devices.layoutManager = LinearLayoutManager(this)
//        recycler_view_devices.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        button_discover.setOnClickListener { initBluetooth() }
//    }
//
//    private fun initBluetooth() {
//
//        if (es.uniovi.eii.stitchingbot.bluetooth.getBluetoothAdapter.isDiscovering) return
//
//        if (es.uniovi.eii.stitchingbot.bluetooth.getBluetoothAdapter.isEnabled) {
//            enableDiscovery()
//        } else {
//            // Bluetooth isn't enabled - prompt user to turn it on
//            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(intent, ENABLE_BLUETOOTH)
//        }
//    }
//
//    private fun enableDiscovery() {
//        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//        startActivityForResult(intent, REQUEST_ENABLE_DISCOVERY)
//    }
//
//    private fun monitorDiscovery() {
//        registerReceiver(bluetoothDiscoveryMonitor, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
//        registerReceiver(bluetoothDiscoveryMonitor, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
//    }
//
//    private fun startDiscovery() {
//        if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            if (es.uniovi.eii.stitchingbot.bluetooth.getBluetoothAdapter.isEnabled && !es.uniovi.eii.stitchingbot.bluetooth.getBluetoothAdapter.isDiscovering) {
//                beginDiscovery()
//            }
//        } else {
//            ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                    REQUEST_ACCESS_COARSE_LOCATION
//            )
//        }
//    }
//
//    private fun beginDiscovery() {
//        registerReceiver(bluetoothDiscoveryResult, IntentFilter(BluetoothDevice.ACTION_FOUND))
//        deviceListAdapter.clearDevices()
//        monitorDiscovery()
//        es.uniovi.eii.stitchingbot.bluetooth.getBluetoothAdapter.startDiscovery()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_ACCESS_COARSE_LOCATION -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    beginDiscovery()
//                } else {
//                    toast("Permission required to scan for devices.")
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            ENABLE_BLUETOOTH -> if (resultCode == Activity.RESULT_OK) {
//                enableDiscovery()
//            }
//            REQUEST_ENABLE_DISCOVERY -> if (resultCode == Activity.RESULT_CANCELED) {
//                toast("Discovery cancelled.")
//            } else {
//                startDiscovery()
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(bluetoothDiscoveryMonitor)
//        unregisterReceiver(bluetoothDiscoveryResult)
//    }
//}