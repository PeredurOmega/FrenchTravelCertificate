package com.pi.fcertif.ui.creator.filler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.R
import com.pi.fcertif.objects.Reason
import com.pi.fcertif.objects.Reasons

/**
 * [RecyclerView.Adapter] used to populate the [RecyclerView] with [Reasons].
 * @param resources [Resources] used to create [Reasons] in [ReasonsAdapter#reasons].
 * @param reasonListener [ReasonListener] to provide to [ReasonViewHolder].
 */
class ReasonsAdapter(resources: Resources, private val reasonListener: ReasonListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * [Reasons] to show in the [RecyclerView]
     */
    private val reasons = Reasons(resources)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) ReasonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.reason_card, parent, false),
            reasonListener
        )
        else SectionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.section_title, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reason = reasons[position]
        if (reason is Reason && holder is ReasonViewHolder) holder.bindReason(reason, position)
        else if (reason is String && holder is SectionViewHolder) holder.bindSectionTitle(reason)
    }

    override fun getItemViewType(position: Int): Int {
        return if(reasons[position] is Reason) 0 else 1
    }

    override fun getItemCount(): Int {
        return reasons.size()
    }
}