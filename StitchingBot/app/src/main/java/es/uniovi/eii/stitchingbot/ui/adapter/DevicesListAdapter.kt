package es.uniovi.eii.stitchingbot.ui.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.stitchingbot.R
import kotlinx.android.synthetic.main.device_list_item.view.*

class DevicesListAdapter(private val listener: (BluetoothDevice) -> Unit) :
    RecyclerView.Adapter<DevicesListAdapter.ViewHolder>() {

    private val devices = ArrayList<BluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.device_list_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position], listener)
    }

    override fun getItemCount() = devices.size

    /**
     * Añade un dispositivo a la lista de dispositivos encontrados
     *
     * @param device dispositivo bluetooth encontrado
     */
    fun addElement(device: BluetoothDevice) {
        devices.add(device)
        notifyItemInserted(itemCount)
    }

    /**
     * Elimina todos los elementos de la lista
     */
    fun clearElements() {
        val lastSize = itemCount
        devices.clear()
        notifyItemRangeRemoved(0, lastSize)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Mapea los datos del dispositivo bluetooth en la vista creada para mostrarlos
         *
         * @param device dispositivo que se desea mapear
         * @param listener Listener que se desea que tenga el elemento de la vista
         */
        fun bind(device: BluetoothDevice, listener: (BluetoothDevice) -> Unit) {
            if (device.name.isNullOrEmpty()) {
                itemView.txtDeviceName.text =
                    itemView.context.getString(R.string.txt_device_name_unknown)
            } else {
                itemView.txtDeviceName.text = device.name
            }
            if (device.bondState == BluetoothDevice.BOND_BONDED)
                itemView.txtDeviceExtras.text =
                    itemView.context.getString(R.string.txt_device_extras_bonded)
            else
                itemView.txtDeviceExtras.text = device.address

            itemView.setOnClickListener { listener(device) }
        }
    }
}