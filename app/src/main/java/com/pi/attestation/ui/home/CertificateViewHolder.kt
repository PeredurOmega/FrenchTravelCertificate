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

class CertificateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private val iconView: ImageView = itemView.findViewById(R.id.icon)
    private val timeAndDate: TextView = itemView.findViewById(R.id.timeAndDate)
    private val shortName: TextView = itemView.findViewById(R.id.shortName)

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
            intent.putExtra(CertificateViewerActivity.FILE_NAME, certificate.pdfPath)
            it.context.startActivity(intent)
        }
    }
}