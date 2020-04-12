package com.pi.attestation.objects

import android.content.res.Resources
import android.graphics.Color
import com.pi.attestation.R

class Reasons(private val resources: Resources) {

    private val size = 7
    private val reasons = Array(size){ getReason(it) }

    private fun getReason(position: Int): Reason{
        return when (position){
            0 -> Reason(resources.getString(R.string.work_reason_short_name),
                resources.getString(R.string.work_reason_description),
                R.drawable.business_96px, Color.parseColor("#008080"), 0)
            1 -> Reason(resources.getString(R.string.purchase_of_necessities_reason_short_name),
                resources.getString(R.string.purchase_of_necessities_reason_description),
                R.drawable.buy_96px, Color.parseColor("#800000"), 1)
            2 -> Reason(resources.getString(R.string.health_reason_short_name),
                resources.getString(R.string.health_reason_description),
                R.drawable.caduceus_96px, Color.parseColor("#000080"), 2)
            3 -> Reason(resources.getString(R.string.family_reason_short_name),
                resources.getString(R.string.family_reason_description),
                R.drawable.family_96px, Color.parseColor("#FFB8C6"), 3)
            4 -> Reason(resources.getString(R.string.sports_or_animals_reason_short_name),
                resources.getString(R.string.sports_or_animals_reason_description),
                R.drawable.exercise_96px, Color.parseColor("#228B22"), 4)
            5 -> Reason(resources.getString(R.string.judicial_or_administrative_reason_short_name),
                resources.getString(R.string.judicial_or_administrative_reason_description),
                R.drawable.law_96px, Color.parseColor("#800080"), 5)
            else -> Reason(resources.getString(R.string.general_interest_task_reason_short_name),
                resources.getString(R.string.general_interest_task_reason_description),
                R.drawable.work_96px, Color.parseColor("#808080"), 6)
        }
    }

    operator fun get(position: Int): Reason{
        return reasons[position]
    }

    fun size(): Int {
        return size
    }
}