package com.pi.fcertif.ui.profiles

import android.view.View
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.R

/**
 * [RecyclerView.ViewHolder] used to create a basic create profile for the [RecyclerView]
 * in [ProfilesFragment].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 */
class BasicCreateProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * [TextView] where we set the text for creating a new profile.
     */
    private val createText: TextView = itemView.findViewById(R.id.createText)

    init {
        createText.setText(R.string.create_a_new_profile)
        itemView.setOnClickListener {
            val navController = Navigation.findNavController((itemView.parent.parent) as View)
            navController.navigate(R.id.nav_profile)
        }
    }
}