package com.pi.fcertif.ui.home

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.ui.creator.CertificateCreatorActivity

/**
 * [RecyclerView.ViewHolder] used to create a basic create certificate "card" for the [RecyclerView]
 * in [HomeFragment]. This card should be displayed when there is one or more cards in the
 * [RecyclerView] and when the user's profile is complete. This card should always be the first
 * card to be displayed in the [RecyclerView].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 */
class BasicCreateCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    init {
        itemView.setOnClickListener{
            val context = it.context
            startActivity(context,
                Intent(context, CertificateCreatorActivity::class.java), null)
        }
    }
}