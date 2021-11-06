package es.uniovi.eii.stitchingbot.ui.fragments.arduino

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.adapter.DevicesListAdapter
import es.uniovi.eii.stitchingbot.ui.util.ShowDialog
import es.uniovi.eii.stitchingbot.arduinoCommunication.BluetoothService
import kotlinx.android.synthetic.main.fragment_arduino_connection.*
import java.util.*

class ArduinoConnectionFragment : Fragment() {

    private lateinit var deviceAdapter: DevicesListAdapter
    private var bluetoothService: BluetoothService = BluetoothService
    private var comesFromSummary: Boolean = false

    /**
     * Evento para activar el bluetooth
     */
    private val enableBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.i("BluetoothStitching", "No se ha activado el bluetooth")
        }
    }

    /**
     * Evento para pedir al usuario los permisos necesarios para el funcionamiento de esta pantalla
     */
    private val getPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var granted = true
            val nonGrantedPermissions = mutableListOf<String>()
            permissions.entries.forEach {
                if (!it.value) {
                    granted = false
                    if (it.key == "android.permission.BLUETOOTH")
                        nonGrantedPermissions.add("Bluetooth")
                    else {
                        nonGrantedPermissions.add("Localizacion")
                    }
                }
            }
            if (!granted) {
                ShowDialog.showNotGrantedPermissionsMessage(nonGrantedPermissions, requireContext())
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arduino_connection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            comesFromSummary = requireArguments().getBoolean("summary")
        }
        if (BluetoothService.isConnected()) {
            startConfigurationFragment()
        }

        initBluetoothAdapter()
        bluetoothService.registerReceiver(requireActivity()) { device -> addDeviceToAdapter(device) }
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothService.cancelDiscovery()
        bluetoothService.unregisterReceiver(requireActivity())
    }

    /**
     * Función que inicia la acción de activar el bluetooth en el dispositivo y pide los permisos
     * necesarios al usuario.
     *
     */
    private fun initBluetoothAdapter() {
        bluetoothService.enableBluetoothAndExecute(enableBluetooth) {
            getPermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH
                )
            )
        }
    }

    /**
     * Inicia los elementos de la interfaz de esta pantalla
     */
    private fun initUI() {
        deviceAdapter = DevicesListAdapter { device -> onDeviceClick(device) }
        rvDevicesList.adapter = deviceAdapter
        rvDevicesList.layoutManager = LinearLayoutManager(this.context)
        rvDevicesList.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
        fabDiscoverDevices.setOnClickListener { onDiscoverFABClick() }
    }

    /**
     * Maneja el evento de click sobre un dispositivo.
     *
     * Empareja el dispositivo sobre el que se ha hecho click con la app
     *
     * @param bluetoothDevice dispositivo seleccionado
     */
    private fun onDeviceClick(bluetoothDevice: BluetoothDevice) {
        Thread {
            this@ArduinoConnectionFragment.requireActivity().runOnUiThread {
                progressBar.visibility = View.VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }

            configureHandler()
            createConnection(bluetoothDevice)

            this@ArduinoConnectionFragment.requireActivity().runOnUiThread {
                progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }.start()
    }

    /**
     *  Configura el Handler que maneja la conexión bluetooth con el dispositivo
     */
    private fun configureHandler() {
        bluetoothService.setHandlerCommands({
            startConfigurationFragment()
        }, {
            ShowDialog.showDialogOK(requireContext(), "No se ha podido conectar") { _, _ -> }
        })
    }

    /**
     * Inicia la conexión con un dispositivo
     *
     * @param bluetoothDevice dispositivo con el que se quiere establecer una conexión
     */
    private fun createConnection(bluetoothDevice: BluetoothDevice) {
        bluetoothService.tryToConnect(bluetoothDevice)
    }

    /**
     * Lleva al usuario al fraagment [ArduinoConfigurationFragment]
     */
    private fun startConfigurationFragment() {
        val bundle = bundleOf("summary" to comesFromSummary)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
        navController.navigate(R.id.nav_arduino_configuration, bundle)
    }

    /**
     * Añade un nuevo dispositivo al adapter
     */
    private fun addDeviceToAdapter(device: BluetoothDevice) {
        deviceAdapter.addElement(device)
    }

    /**
     * Maneja el evento de hacer click sobre [fabDiscoverDevices]
     */
    private fun onDiscoverFABClick() {
        deviceAdapter.clearElements()
        bluetoothService.enableBluetoothAndExecute(enableBluetooth) { bluetoothService.startDiscovering() }
    }

}