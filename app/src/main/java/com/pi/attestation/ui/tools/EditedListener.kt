package com.pi.attestation.ui.tools

/**
 * This interface is used to check if a object has been edited.
 */
interface EditedListener {

    /**
     * Returns whether or not this object has been edited.
     * @return [Boolean] True if the object has been edited false otherwise.
     */
    fun hasBeenEdited(): Boolean

    /**
     * Saves / registers all currently made edition.
     */
    fun registerEdition()
}
