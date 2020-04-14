package com.pi.attestation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate

/**
 * [RecyclerView.Adapter] used to populate the [RecyclerView] contained certificates.
 * @param profileFilled [Boolean] whether or not the profile is properly filled. According to this,
 * different info will be displayed.
 */
class CertificatesAdapter(private val profileFilled : Boolean)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * [ArrayList] of the certificates used to populate the bound [RecyclerView].
     */
    private val certificates = ArrayList<Certificate>()

    companion object {
        const val FILL_PROFILE_CARD_TYPE = 12
        const val CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE = 13
        const val CREATE_CERTIFICATE_CARD_TYPE = 14
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FILL_PROFILE_CARD_TYPE -> {
                FillProfileCardViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.fill_profile_card, parent, false))
            }
            CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE -> {
                CreateWhenNoneCardViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.create_when_none_card, parent, false))
            }
            CREATE_CERTIFICATE_CARD_TYPE -> {
                BasicCreateCardViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_create_card, parent, false))
            }
            else -> {
                CertificateViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.certificate_card, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return certificates.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0) return
        else (holder as CertificateViewHolder).bindToCertificate(certificates[position - 1])
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0){
            if(profileFilled){
                if(itemCount == 1) CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE
                else CREATE_CERTIFICATE_CARD_TYPE
            }else FILL_PROFILE_CARD_TYPE
        } else 0
    }

    /**
     * Displays the provided certificates into the [RecyclerView].
     * @param certificates Full list of the [Certificate] to display.
     */
    fun setItems(certificates: ArrayList<Certificate>?) {
        val diffResult = DiffUtil.calculateDiff(CertificatesDiffCallback(this.certificates,
            certificates ?: ArrayList()))
        this.certificates.clear()
        this.certificates.addAll(certificates ?: ArrayList())
        diffResult.dispatchUpdatesTo(this)
    }
}