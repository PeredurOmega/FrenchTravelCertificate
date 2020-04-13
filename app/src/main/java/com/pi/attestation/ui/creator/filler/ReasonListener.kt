package com.pi.attestation.ui.creator.filler

import com.pi.attestation.objects.Reason

interface ReasonListener {

    fun pick(reason: Reason)

    fun onDetailsOpened(position: Int)

}