package com.pi.attestation.ui.tools

/**
 * Interface to use when we want to exit a [androidx.fragment.app.Fragment].
 */
interface Leaver {

    /**
     * Method to call when we want to exit a [androidx.fragment.app.Fragment].
     */
    fun onQuit()
}