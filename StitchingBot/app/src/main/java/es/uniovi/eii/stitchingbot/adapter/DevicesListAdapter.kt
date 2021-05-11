package es.uniovi.eii.stitchingbot.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.stitchingbot.R
import kotlinx.android.synthetic.main.device_list_item.view.*

class DevicesListAdapter(private val listener : OnItemClickListener) : RecyclerView.Adapter<DevicesListAdapter.ViewHolder>() {


    private val devices = ArrayList<BluetoothDevice>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.device_list_item, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position], listener)
    }


    override fun getItemCount() = devices.size

    fun addElement(device: BluetoothDevice){
        devices.add(device)
        notifyItemInserted(itemCount)
    }

    fun clearElements(){
        val lastSize = itemCount
        devices.clear()
        notifyItemRangeRemoved(0, lastSize)
    }

    fun interface OnItemClickListener{
        fun onItemClick(device: BluetoothDevice)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(device: BluetoothDevice, listener: OnItemClickListener) {
            itemView.txtDeviceName.text=device.name
            if(device.bondState == BluetoothDevice.BOND_BONDED)
                itemView.txtDeviceExtras.text= "Paried, not connected"
            else
                itemView.txtDeviceExtras.text=device.address

            itemView.setOnClickListener { listener.onItemClick(device) }
        }




    }




}