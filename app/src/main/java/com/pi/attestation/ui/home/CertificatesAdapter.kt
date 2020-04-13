package com.pi.attestation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate

class CertificatesAdapter(private val certificates: ArrayList<Certificate>) :
    RecyclerView.Adapter<CertificateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        return CertificateViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.certificate_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return certificates.size
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        holder.bindToCertificate(certificates[position])
    }

    fun getCertificate(position: Int): Certificate {
        return certificates[position]
    }

    fun removeItem(position: Int){
        certificates.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(certificate: Certificate, position: Int) {
        certificates.add(position, certificate)
        notifyItemInserted(position)
    }
}