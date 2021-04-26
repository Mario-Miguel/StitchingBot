package es.uniovi.eii.stitchingbot.adapter

import android.bluetooth.BluetoothDevice
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.extension.inflate
import kotlinx.android.synthetic.main.adapter_discovered_devices.view.*

class DevicesAdapter : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    private val devices = ArrayList<BluetoothDevice>()

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int) = ViewHolder(
        container.inflate(R.layout.adapter_discovered_devices)
    )

    override fun getItemCount() = devices.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(devices[position])
    }

    fun addDevice(device: BluetoothDevice) {
        devices.add(device)
        notifyItemInserted(itemCount)
    }

    fun clearDevices() {
        devices.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(device: BluetoothDevice) {
            view.text_view_device_name.text = device.name ?: device.address
        }

    }
}