package com.pi.attestation.ui.home

/**
 * Interface used to manage item clicks and selection.
 */
interface ItemClickListener {

    /**
     * Returns whether or not this item is selected for action mode.
     * @param position [Int] position of the current item in the adapter.
     * @param selectedListener [SelectedListener] to use in case of selection change.
     */
    fun selectedForActionMode(position : Int, selectedListener: SelectedListener) : Boolean

    /**
     * Performs a long click.
     * @param position [Int] position of the current item in the adapter.
     * @param selectedListener [SelectedListener] to use in case of selection change.
     */
    fun onLongClick(position : Int, selectedListener: SelectedListener)
}