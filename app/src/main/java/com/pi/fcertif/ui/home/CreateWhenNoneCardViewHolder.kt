package com.pi.fcertif.ui.home

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pi.fcertif.R
import com.pi.fcertif.ui.creator.CertificateCreatorActivity

/**
 * [RecyclerView.ViewHolder] used to create a create a certificate "card" for the [RecyclerView] in
 * [HomeFragment]. This card should be displayed when the user's profile is complete and when there
 * is no certificates in the view. This card should always be the first card to be displayed in
 * the [RecyclerView].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 */
class CreateWhenNoneCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    init {
        val createCertificateButton =
            itemView.findViewById<MaterialButton>(R.id.createCertificateButton)
        createCertificateButton.setOnClickListener{
            val context = itemView.context
            startActivity(context,
                Intent(context, CertificateCreatorActivity::class.java), null)
        }
    }
}