package com.pi.attestation.ui.creator.filler

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pi.attestation.R
import com.pi.attestation.objects.Reason


class ReasonViewHolder(itemView: View, private val reasonListener: ReasonListener) : RecyclerView.ViewHolder(itemView) {

    private val iconView: ImageView = itemView.findViewById(R.id.icon)
    private val shortName: TextView = itemView.findViewById(R.id.shortName)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val shortView: ConstraintLayout = itemView.findViewById(R.id.shortView)
    private val detailView: ConstraintLayout = itemView.findViewById(R.id.detailView)
    private val pickButton: MaterialButton = itemView.findViewById(R.id.pickButton)
    private lateinit var onGlobalLayoutListener: OnGlobalLayoutListener

    fun bindReason(reason: Reason){
        shortName.text = reason.shortName

        val context = itemView.context

        iconView.setImageDrawable(context.getDrawable(
            context.resources.getIdentifier(reason.iconName, "drawable",
                context.packageName)))

        iconView.backgroundTintList = ColorStateList.valueOf(reason.color)

        shortView.setOnClickListener {
            run {
                description.text = reason.fullDescription
                detailView.visibility = if (detailView.visibility == View.VISIBLE) View.GONE
                                        else View.VISIBLE
                onGlobalLayoutListener = OnGlobalLayoutListener {
                    if (detailView.visibility == View.VISIBLE) {
                        reasonListener.onDetailsOpened(reason.id)
                    }
                    detailView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
                }
                detailView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
            }
        }

        pickButton.setOnClickListener {
            run {
                reasonListener.pick(reason)
            }
        }
    }
}