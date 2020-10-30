package com.pi.fcertif.ui.home

import android.content.res.Resources

/**
 * Interface used to manage item clicks and selection.
 */
interface ItemClickListener {

    /**
     * Returns whether or not this item is selected for action mode.
     * @param position [Int] position of the current item in the adapter.
     * @param selectedListener [SelectedListener] to use in case of selection change.
     * @param resources [Resources] to use to retrieve a quantity string.
     */
    fun selectedForActionMode(position : Int, selectedListener: SelectedListener,
                              resources: Resources) : Boolean

    /**
     * Performs a long click.
     * @param position [Int] position of the current item in the adapter.
     * @param selectedListener [SelectedListener] to use in case of selection change.
     * @param resources [Resources] to use to retrieve a quantity string.
     */
    fun onLongClick(position : Int, selectedListener: SelectedListener, resources: Resources)
}