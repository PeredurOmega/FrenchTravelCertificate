package com.pi.attestation.ui.home

import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.ui.tools.FlipAnimator
import com.pi.attestation.ui.viewer.CertificateViewerActivity


/**
 * [RecyclerView.ViewHolder] used to create certificate "card" for the [RecyclerView] in
 * [HomeFragment].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 * @param itemClickListener [ItemClickListener] to use to remove a certificate through contextual
 * action bar.
 */
class CertificateViewHolder(itemView: View, private val itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(itemView), SelectedListener{

    /**
     * [ImageView] where the icon of the bound [Certificate]'s  [com.pi.attestation.objects.Reason]
     * is displayed.
     * @see Certificate.reason
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
    private var selected : Boolean? = null

    /**
     * [TextView] where the creation time and the creation date of the bound [Certificate] is
     * displayed.
     * @see Certificate.creationDateTime
     */
    private val timeAndDate: TextView = itemView.findViewById(R.id.timeAndDate)

    /**
     * [TextView] where the short name of the bound [Certificate]'s
     * [com.pi.attestation.objects.Reason] is displayed.
     * @see Certificate.reason
     */
    private val shortName: TextView = itemView.findViewById(R.id.shortName)

    /**
     * Binds the provided [Certificate] to this [CertificateViewHolder].
     * @param certificate [Certificate] to bind to this [CertificateViewHolder].
     * @param selectedNow [Boolean] True if the item is selected, false otherwise.
     */
    fun bindToCertificate(certificate: Certificate, position: Int, selectedNow : Boolean){
        val context = itemView.context

        when {
            selected == null -> unselected(false)
            selectedNow && selectedNow != selected -> selected(true)
            !selectedNow && selectedNow != selected -> unselected(true)
        }

        iconView.setImageDrawable(context.getDrawable(
            context.resources.getIdentifier(certificate.reason.iconName, "drawable",
                context.packageName)))

        iconView.backgroundTintList = ColorStateList.valueOf(certificate.reason.color)

        timeAndDate.text = timeAndDate.context.getString(R.string.date_time_placeholder,
            certificate.creationDateTime.date, certificate.creationDateTime.time)
        shortName.text = certificate.reason.shortName

        itemView.setOnClickListener {
            if(!itemClickListener.selectedForActionMode(position, this,
                    it.resources)){
                val intent = Intent(it.context, CertificateViewerActivity::class.java)
                intent.putExtra(CertificateViewerActivity.FILE_PATH, certificate.pdfPath)
                it.context.startActivity(intent)
            }
        }

        itemView.setOnLongClickListener {
            //TODO MAINTAIN TO SHARE OPTION
            itemClickListener.onLongClick(position, this, it.resources)
            return@setOnLongClickListener true
        }
    }

    override fun selected(animate: Boolean) {
        iconView.visibility = View.INVISIBLE
        iconSelectedView.visibility = View.VISIBLE
        iconSelectedView.alpha = 1f
        selected = true
        if(animate) FlipAnimator().flipView(iconSelectedView, iconView, true)
    }

    override fun unselected(animate: Boolean){
        iconSelectedView.visibility = View.GONE
        iconView.visibility = View.VISIBLE
        iconView.alpha = 1f
        selected = false
        if(animate) FlipAnimator().flipView(iconSelectedView, iconView, false)
    }
}