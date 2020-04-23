package com.pi.certificate.ui.home

import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pi.certificate.R

/**
 * [RecyclerView.ViewHolder] used to create a fill profile "card" (with some useful explanations)
 * for the [RecyclerView] in [HomeFragment]. This card should be displayed when the user's profile
 * is incomplete. This card should always be the first card to be displayed in the [RecyclerView].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 */
class FillProfileCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    init {
        val noCertificateButton = itemView.findViewById<MaterialButton>(R.id.fillProfile)
        noCertificateButton.setOnClickListener{
            val navController = Navigation.findNavController((itemView.parent.parent) as View)
            navController.navigate(R.id.nav_profile)
        }
    }
}