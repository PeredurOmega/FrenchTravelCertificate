package com.pi.attestation.ui.creator.filler

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pi.attestation.R
import com.pi.attestation.objects.Reason

/**
 * [RecyclerView.ViewHolder] that should be used to create a [Reason] "card" for a [RecyclerView]
 * efficiently.
 * @param itemView [View] of this [RecyclerView.ViewHolder].
 * @param reasonListener [ReasonListener] to notify in case of change.
 * @see ReasonListener
 */
class ReasonViewHolder(itemView: View, private val reasonListener: ReasonListener) : RecyclerView.ViewHolder(itemView) {

    /**
     * [ImageView] where the icon of the [Reason] is displayed.
     * @see Reason.iconName
     */
    private val iconView: ImageView = itemView.findViewById(R.id.icon)

    /**
     * [TextView] where the short name of the bound [Reason] is displayed.
     * @see Reason.shortName
     */
    private val shortName: TextView = itemView.findViewById(R.id.shortName)

    /**
     * [TextView] where the full description of the bound [Reason] is displayed.
     * @see Reason.fullDescription
     */
    private val description: TextView = itemView.findViewById(R.id.description)

    /**
     * [ConstraintLayout] Only view that is displayed when this itemView is not in detail mode. This
     * view contains the icon of the bound [Reason] and the short name of the bound [Reason].
     * @see Reason.iconName
     * @see Reason.fullDescription
     */
    private val shortView: ConstraintLayout = itemView.findViewById(R.id.shortView)

    /**
     * [LinearLayout] that contains details about the bound [Reason] (those details should be
     * constantly provided by the French Ministry of Home Affairs).
     * @see Reason.fullDescription
     */
    private val detailView: LinearLayout = itemView.findViewById(R.id.detailView)

    /**
     * [MaterialButton] that is clicked by the user when he wants to choose the bound [Reason].
     */
    private val pickButton: MaterialButton = itemView.findViewById(R.id.pickButton)

    /**
     * [OnGlobalLayoutListener] used to observe visibility change for details.
     */
    private lateinit var onGlobalLayoutListener: OnGlobalLayoutListener

    /**
     * Binds the provided reason to this [ReasonViewHolder].
     * @param reason [Reason] to bind to this [ReasonViewHolder].
     */
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