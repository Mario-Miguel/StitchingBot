package es.uniovi.eii.stitchingbot.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.model.Logo
import kotlinx.android.synthetic.main.card_recycler_view_logo.view.*

class LogoListAdapter(private var logosList: List<Logo>, private val listener: (Logo) -> Unit) :
    RecyclerView.Adapter<LogoListAdapter.LogoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recycler_view_logo, parent, false)
        return LogoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LogoViewHolder, position: Int) {
        holder.bind(logosList[position], listener)

    }

    override fun getItemCount() = logosList.size

    class LogoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Mapea los datos del logotipo en la vista creada para mostrarlos
         *
         * @param logo logotipo que se desea mapear
         * @param listener Listener que se desea que tenga el elemento de la vista
         */
        fun bind(logo: Logo, listener: (Logo) -> Unit) = with(itemView) {
            txtTitleLogo.text = logo.title
            if (logo.imgUrl!!.isNotEmpty())
                Picasso.get().load(logo.imgUrl).into(imgLogo)
            setOnClickListener { listener(logo) }
        }
    }
}