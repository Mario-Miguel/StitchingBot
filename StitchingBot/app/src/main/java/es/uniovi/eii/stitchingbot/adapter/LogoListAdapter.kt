package es.uniovi.eii.stitchingbot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.model.Logo
import kotlinx.android.synthetic.main.card_recycler_view_logo.view.*

class LogoListAdapter(logosList: List<Logo>, val listener: (Logo) -> Unit) : RecyclerView.Adapter<LogoListAdapter.LogoViewHolder>() {

    var logos : List<Logo> = logosList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogoViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_recycler_view_logo, parent, false);
        return LogoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LogoViewHolder, position: Int) {
        holder.bind(logos[position], listener)

    }

    override fun getItemCount() = logos.size


    class LogoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(logo: Logo, listener: (Logo) -> Unit) = with(itemView) {
            txtTitleLogo.text = logo.title
            if(logo.imgUrl.isNotEmpty())
                Picasso.get().load(logo.imgUrl).into(imgLogo)
            setOnClickListener { listener(logo) }
        }

    }
}