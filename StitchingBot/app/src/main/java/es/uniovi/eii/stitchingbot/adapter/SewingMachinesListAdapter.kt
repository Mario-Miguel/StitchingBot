package es.uniovi.eii.stitchingbot.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.TAG
import es.uniovi.eii.stitchingbot.model.SewingMachine
import kotlinx.android.synthetic.main.card_recycler_view_sewing_machine.view.*

class SewingMachinesListAdapter(
    machinesList: MutableList<SewingMachine>,
    private val listener: (SewingMachine) -> Unit
) : RecyclerView.Adapter<SewingMachinesListAdapter.SewingMachinesViewHolder>() {

    var machines: MutableList<SewingMachine> = machinesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SewingMachinesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recycler_view_sewing_machine, parent, false)
        return SewingMachinesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SewingMachinesViewHolder, position: Int) {
        holder.bind(machines[position], listener)

    }

    override fun getItemCount() = machines.size

    fun addElement(sewingMachine: SewingMachine) {
        machines.add(sewingMachine)
        notifyItemInserted(itemCount)
    }

    fun clearElements() {
        val lastSize = itemCount
        machines.clear()
        notifyItemRangeRemoved(0, lastSize)
    }


    class SewingMachinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(machine: SewingMachine, listener: (SewingMachine) -> Unit) = with(itemView) {
            txtSewingMachineName.text = machine.name

            if (machine.id == -1) {
                Log.i(TAG, "Imagen con el +")
                txtSewingMachineName.text = "Añadir máquina de coser"
                Picasso.get().load(R.drawable.ic_baseline_add_24).into(imgMachine)
            } else if (machine.imgUrl!!.isNotEmpty()) {
                Picasso.get().load(machine.imgUrl).into(imgMachine)
            }

            setOnClickListener { listener(machine) }
        }



    }



}