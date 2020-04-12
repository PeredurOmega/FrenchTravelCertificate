package com.pi.attestation.ui.creator.filler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.objects.Reasons

class ReasonsAdapter(resources: Resources, private val reasonListener: ReasonListener): RecyclerView.Adapter<ReasonViewHolder>() {

    private val reasons =  Reasons(resources)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReasonViewHolder {
        return ReasonViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.reason_card, parent, false), reasonListener)
    }

    override fun onBindViewHolder(holder: ReasonViewHolder, position: Int) {
        holder.bindReason(reasons[position])
    }

    override fun getItemCount(): Int {
        return reasons.size()
    }
}