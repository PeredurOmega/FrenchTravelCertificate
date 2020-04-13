package com.pi.attestation.objects

import android.content.res.Resources
import com.pi.attestation.R

class Reasons(private val resources: Resources) {

    private val size = 7
    private val reasons = Array(size){ getReason(it) }

    private fun getReason(position: Int): Reason{
        return when (position){
            0 -> Reason(resources.getString(R.string.work_reason_short_name),
                resources.getString(R.string.work_reason_description),
                "business_96px",  -0xff6978, 0)
            1 -> Reason(resources.getString(R.string.purchase_of_necessities_reason_short_name),
                resources.getString(R.string.purchase_of_necessities_reason_description),
                "buy_96px",  -0xa5fa, 1)
            2 -> Reason(resources.getString(R.string.sports_or_animals_reason_short_name),
                resources.getString(R.string.sports_or_animals_reason_description),
                "exercise_96px", -0xb350b0, 2)
            3 -> Reason(resources.getString(R.string.family_reason_short_name),
                resources.getString(R.string.family_reason_description),
                "family_96px",  -0x16e19d, 3)
            4 -> Reason(resources.getString(R.string.health_reason_short_name),
                resources.getString(R.string.health_reason_description),
                "caduceus_96px",  -0xfc560c, 4)
            5 -> Reason(resources.getString(R.string.judicial_or_administrative_reason_short_name),
                resources.getString(R.string.judicial_or_administrative_reason_description),
                "law_96px", -0x98c549, 5)
            else -> Reason(resources.getString(R.string.general_interest_task_reason_short_name),
                resources.getString(R.string.general_interest_task_reason_description),
                "work_96px",  -0x3223c7, 6)
        }
    }

    operator fun get(position: Int): Reason{
        return reasons[position]
    }

    fun size(): Int {
        return size
    }
}