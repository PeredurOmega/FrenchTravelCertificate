package com.pi.fcertif.ui.creator.filler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.R

/**
 * [RecyclerView.ViewHolder] that should be used to create a section (for reasons).
 * @param itemView [View] of this [RecyclerView.ViewHolder].
 * @see ReasonListener
 */
class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * [TextView] where the title of the section is displayed is displayed.
     */
    private val title: TextView = itemView.findViewById(R.id.sectionTitle)

    /**
     * Binds the provided [sectionTitle] to this [SectionViewHolder].
     */
    fun bindSectionTitle(sectionTitle: String) {
        this.title.text = sectionTitle
    }
}