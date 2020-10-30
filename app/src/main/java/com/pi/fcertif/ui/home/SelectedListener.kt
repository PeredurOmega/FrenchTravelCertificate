package com.pi.fcertif.ui.home

/**
 * Interface used to notify selection changes.
 */
interface SelectedListener {

    /**
     * Indicates that the item is selected.
     * @param animate [Boolean] True if we want to animate the selection, false otherwise.
     */
    fun selected(animate: Boolean)

    /**
     * Indicates that the item is unselected.
     * @param animate [Boolean] True if we want to animate the un-selection, false otherwise.
     */
    fun unselected(animate: Boolean)
}