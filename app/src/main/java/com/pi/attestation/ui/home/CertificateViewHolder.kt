package com.pi.attestation.ui.home

import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.ui.viewer.CertificateViewerActivity

/**
 * [RecyclerView.ViewHolder] used to create certificate "card" for the [RecyclerView] in
 * [HomeFragment].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 */
class CertificateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    /**
     * [ImageView] where the icon of the bound [Certificate]'s  [com.pi.attestation.objects.Reason]
     * is displayed.
     * @see Certificate.reason
     */
    private val iconView: ImageView = itemView.findViewById(R.id.icon)

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
     */
    fun bindToCertificate(certificate: Certificate){
        val context = itemView.context

        iconView.setImageDrawable(context.getDrawable(
            context.resources.getIdentifier(certificate.reason.iconName, "drawable",
                context.packageName)))

        iconView.backgroundTintList = ColorStateList.valueOf(certificate.reason.color)

        timeAndDate.text = timeAndDate.context.getString(R.string.date_time_placeholder,
            certificate.creationDateTime.date, certificate.creationDateTime.time)
        shortName.text = certificate.reason.shortName

        itemView.setOnClickListener {
            val intent = Intent(it.context, CertificateViewerActivity::class.java)
            intent.putExtra(CertificateViewerActivity.FILE_PATH, certificate.pdfPath)
            it.context.startActivity(intent)
        }

        //TODO MAINTAIN TO DELETE OR MORE OPTIONS
    }
}