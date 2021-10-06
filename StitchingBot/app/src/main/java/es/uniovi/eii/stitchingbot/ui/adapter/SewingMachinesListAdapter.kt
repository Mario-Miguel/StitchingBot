package es.uniovi.eii.stitchingbot.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.uniovi.eii.stitchingbot.R
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

    class SewingMachinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(machine: SewingMachine, listener: (SewingMachine) -> Unit) = with(itemView) {
            txtSewingMachineName.text = machine.name
            if (machine.imgUrl!!.isNotEmpty()) {
                Picasso.get().load(machine.imgUrl).into(imgMachine)
            }
            setOnClickListener { listener(machine) }
        }
    }


}