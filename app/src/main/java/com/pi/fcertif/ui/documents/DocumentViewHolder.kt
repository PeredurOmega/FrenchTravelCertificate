package com.pi.fcertif.ui.documents

import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.R
import com.pi.fcertif.objects.Document
import com.pi.fcertif.ui.documents.viewer.DocumentViewerActivity
import com.pi.fcertif.ui.home.ItemClickListener
import com.pi.fcertif.ui.home.SelectedListener
import com.pi.fcertif.ui.tools.FlipAnimator


/**
 * [RecyclerView.ViewHolder] used to create a custom document "card" for the [RecyclerView] in
 * [DocumentsFragment].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 * @param itemClickListener [ItemClickListener] to use to remove a profile through contextual
 * action bar.
 */
class DocumentViewHolder(itemView: View, private val itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(itemView), SelectedListener {

    /**
     * [ImageView] where the document icon is displayed.
     */
    private val iconView: ImageView = itemView.findViewById(R.id.icon)

    /**
     * [ImageView] where the icon indicating selection is displayed. Gone when not selected and
     * Visible when selected.
     */
    private val iconSelectedView: ImageView = itemView.findViewById(R.id.iconSelectedView)

    /**
     * Indicating whether or not this item is currently selected. Null means that this item has not
     * been bound.
     */
    private var selected: Boolean? = null

    /**
     * [TextView] where the short name of the bound [Document] is displayed.
     */
    private val shortName: TextView = itemView.findViewById(R.id.shortName)

    /**
     * Binds the provided [Document] to this [DocumentViewHolder].
     * @param document [Document] to bind to this [DocumentViewHolder].
     * @param position [Int] position of this item.
     * @param selectedNow [Boolean] True if the item is selected, false otherwise.
     */
    fun bindToDocument(document: Document, position: Int, selectedNow: Boolean) {
        val context = itemView.context

        when {
            selected == null -> unselected(false)
            selectedNow && selectedNow != selected -> selected(true)
            !selectedNow && selectedNow != selected -> unselected(true)
        }

        iconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.document_96px))

        iconView.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary))

        shortName.text = document.fileName

        itemView.setOnClickListener {
            if (!itemClickListener.selectedForActionMode(position, this, it.resources)) {
                val intent = Intent(it.context, DocumentViewerActivity::class.java)
                intent.putExtra(DocumentViewerActivity.FILE_NAME, document.realFileName())
                intent.putExtra(DocumentViewerActivity.PAGES_COUNT, document.pagesCount)
                it.context.startActivity(intent)
            }
        }

        itemView.setOnLongClickListener {
            itemClickListener.onLongClick(position, this, it.resources)
            return@setOnLongClickListener true
        }
    }

    override fun selected(animate: Boolean) {
        iconView.visibility = View.INVISIBLE
        iconSelectedView.visibility = View.VISIBLE
        iconSelectedView.alpha = 1f
        selected = true
        if (animate) FlipAnimator().flipView(iconSelectedView, iconView, true)
    }

    override fun unselected(animate: Boolean) {
        iconSelectedView.visibility = View.GONE
        iconView.visibility = View.VISIBLE
        iconView.alpha = 1f
        selected = false
        if (animate) FlipAnimator().flipView(iconSelectedView, iconView, false)
    }
}