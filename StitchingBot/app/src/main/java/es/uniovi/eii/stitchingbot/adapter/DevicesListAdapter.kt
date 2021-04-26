package es.uniovi.eii.stitchingbot.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.model.Device
import es.uniovi.eii.stitchingbot.ui.TYPE_HEADER
import es.uniovi.eii.stitchingbot.ui.TYPE_ITEM
import kotlinx.android.synthetic.main.device_list_header.view.*
import kotlinx.android.synthetic.main.device_list_item.view.*

class DevicesListAdapter(devicesList: MutableList<AdapterItem<Device>>) : RecyclerView.Adapter<DevicesListAdapter.DeviceViewHolder>() {


    private val devices : MutableList<AdapterItem<Device>> = devicesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val itemView = if (viewType == TYPE_HEADER) {
            inflater.inflate(R.layout.device_list_header, parent, false)
        } else {
            inflater.inflate(R.layout.device_list_item, parent, false)
        }

        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            holder.bindHeader(position)
        } else {
            holder.bind(devices[position].value!!)
        }

    }

    override fun getItemViewType(position: Int) = devices[position].viewType

    override fun getItemCount() = devices.size

    fun addDevice(device: BluetoothDevice) {
        val auxDevice = Device(device.name, device.address, device.bondState, device.type)
        devices.add(AdapterItem(auxDevice, TYPE_ITEM))
        notifyItemInserted(itemCount)
    }

    fun clearDevices() {
        devices.clear()
        notifyDataSetChanged()
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(device: Device) = with(itemView) {
//            txtDeviceName.text=device.name
//            if(device.status == BluetoothDevice.BOND_BONDED)
//                txtDeviceExtras.text= "Paried, not connected"
//            else
//                txtDeviceExtras.text=device.mac

        }

        fun bindHeader(completedHeader: Int) = with(itemView) {
            if(completedHeader==0){
                txtDeviceListHeader.text= itemView.context.getString(R.string.connected_devices_header)
            }
            else{
                txtDeviceListHeader.text= itemView.context.getString(R.string.available_devices_header)
            }
        }


    }




}