package com.pi.certificate.ui.creator.filler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pi.certificate.R
import com.pi.certificate.objects.Reasons

/**
 * [RecyclerView.Adapter] used to populate the [RecyclerView] with [Reasons].
 * @param resources [Resources] used to create [Reasons] in [ReasonsAdapter#reasons].
 * @param reasonListener [ReasonListener] to provide to [ReasonViewHolder].
 */
class ReasonsAdapter(resources: Resources, private val reasonListener: ReasonListener): RecyclerView.Adapter<ReasonViewHolder>() {

    /**
     * [Reasons] to show in the [RecyclerView]
     */
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