package com.pi.fcertif.objects

import android.content.res.Resources
import com.pi.fcertif.R

/**
 * Utility object used to represent create all the reasons that can be used for an exit on a
 * certificate. This class behaves more or less like an array.
 * @param resources [Resources] where to find reasons' names and descriptions in local language.
 */
class Reasons(private val resources: Resources) {

    /**
     * Number of reasons contained. Should match the number of reasons on the "paper" certificate
     * provided by the French Ministry of Home Affairs.
     */
    private val size = 8

    /**
     * [Array] created by using [Reasons#getReason]. The size of this [Array] matches
     * [Reasons#size].
     */
    private val reasons = Array(size) { getReason(it) }

    /**
     * Utility method used to populates [Reasons#reasons].
     * @param position [Int] Position of the reason in the [Array].
     */
    private fun getReason(position: Int): Reason {
        return when (position) {
            0 -> Reason(
                resources.getString(R.string.work_reason_short_name),
                resources.getString(R.string.work_reason_description),
                "business_96px", -0xff6978, 0, "travail"
            )
            1 -> Reason(
                resources.getString(R.string.animals_reason_short_name),
                resources.getString(R.string.animals_reason_description),
                "dog_96px", -0xb350b0, 7, "animaux"
            )
            2 -> Reason(
                resources.getString(R.string.family_reason_short_name),
                resources.getString(R.string.family_reason_description),
                "family_96px", -0x16e19d, 2, "famille"
            )
            3 -> Reason(
                resources.getString(R.string.health_reason_short_name),
                resources.getString(R.string.health_reason_description),
                "caduceus_96px", -0xfc560c, 1, "sante"
            )
            4 -> Reason(
                resources.getString(R.string.judicial_or_administrative_reason_short_name),
                resources.getString(R.string.judicial_or_administrative_reason_description),
                "law_96px", -0x98c549, 4, "convocation"
            )
            5 -> Reason(
                resources.getString(R.string.general_interest_task_reason_short_name),
                resources.getString(R.string.general_interest_task_reason_description),
                "work_96px", -0x3223c7, 5, "missions"
            )
            6 -> Reason(
                resources.getString(R.string.transit_reason_short_name),
                resources.getString(R.string.transit_reason_description),
                "airplane_96px", -0xa5fa, 6, "transits"
            )
            else -> Reason(
                resources.getString(R.string.handicap_reason_short_name),
                resources.getString(R.string.handicap_reason_description),
                "wheelchair_96px", -8825528, 3, "handicap"
            )
        }
    }

    /**
     * Operator used to retrieve a [Reason] the same way we would for an [Array].
     * @param position [Int] Position of the [Reason] to retrieve.
     */
    operator fun get(position: Int): Reason {
        return reasons[position]
    }

    /**
     * Returns [Reasons#size].
     */
    fun size(): Int {
        return size
    }
}