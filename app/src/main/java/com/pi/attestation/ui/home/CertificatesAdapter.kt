package com.pi.attestation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate

/**
 * [RecyclerView.Adapter] used to populate the [RecyclerView] containing certificates with
 * [CertificateViewHolder].
 * @param certificates [ArrayList] of [Certificate] that should be displayed.
 */
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

    /**
     * Returns a [Certificate] at a specific position.
     * @param position [Int] Position of the [Certificate] to retrieve.
     * @return [Certificate] found at the provided position.
     */
    fun getCertificate(position: Int): Certificate {
        return certificates[position]
    }

    /**
     * Removes a [Certificate] at a specific position.
     * @param position [Int] Position of the [Certificate] to remove.
     */
    fun removeItem(position: Int){
        certificates.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Adds a [Certificate] at a specific position.
     * @param certificate [Certificate] to add.
     * @param position [Int] Position where to add the [Certificate].
     */
    fun addItem(certificate: Certificate, position: Int) {
        certificates.add(position, certificate)
        notifyItemInserted(position)
    }
}