package com.pi.attestation.ui.creator.filler

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pi.attestation.R
import com.pi.attestation.objects.Reason

class ReasonViewHolder(itemView: View, private val reasonListener: ReasonListener) : RecyclerView.ViewHolder(itemView) {

    private var iconView: ImageView = itemView.findViewById(R.id.icon)
    private var shortName: TextView = itemView.findViewById(R.id.shortName)
    private var description: TextView = itemView.findViewById(R.id.description)
    private var shortView: ConstraintLayout = itemView.findViewById(R.id.shortView)
    private var detailView: ConstraintLayout = itemView.findViewById(R.id.detailView)
    private var pickButton: MaterialButton = itemView.findViewById(R.id.pickButton)

    fun bindReason(reason: Reason){
        shortName.text = reason.shortName

        iconView.setImageResource(reason.icon)
        iconView.backgroundTintList = ColorStateList.valueOf(reason.color)

        shortView.setOnClickListener {
            run {
                description.text = reason.fullDescription
                detailView.visibility = if (detailView.visibility == View.VISIBLE) View.GONE
                                        else View.VISIBLE
            }
        }

        pickButton.setOnClickListener {
            run {
                reasonListener.pick(reason)
            }
        }
    }
}