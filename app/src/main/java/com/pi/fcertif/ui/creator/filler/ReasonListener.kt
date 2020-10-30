package com.pi.fcertif.ui.creator.filler

import com.pi.fcertif.objects.Reason

/**
 * Interface that is used to listen to what is happening in [ReasonViewHolder].
 */
interface ReasonListener {

    /**
     * Called when a reason has been picked.
     * @param reason [Reason] that has been picked.
     */
    fun pick(reason: Reason)

    /**
     * Called when the details of a [ReasonViewHolder] have changed of state (VISIBLE / GONE)
     * @param position [Int] Position of the [Reason] for which the details have been
     * opened / shown.
     */
    fun onDetailsOpened(position: Int)
}