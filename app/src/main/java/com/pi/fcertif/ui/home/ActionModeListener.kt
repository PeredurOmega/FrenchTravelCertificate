package com.pi.fcertif.ui.home

import android.view.ActionMode

/**
 * Interface used to manage actions in a contextual action mode.
 */
interface ActionModeListener {

    /**
     * Starts action mode with a designated [ActionMode.Callback] and returns the [ActionMode].
     * @param actionModeCallback [ActionMode.Callback] to use.
     */
    fun startActionMode(actionModeCallback: ActionMode.Callback): ActionMode?

    /**
     * Deletes a list of items according to their positions in the adapter.
     * @param toBeRemovedList [ArrayList] of [Int] positions in the adapter to delete.
     */
    fun deleteAction(toBeRemovedList: ArrayList<Int>)

    /**
     * Shares a list of items according to their positions in the adapter.
     * @param selectedItemsPositions [ArrayList] of [Int] positions in the adapter to share.
     */
    fun shareAction(selectedItemsPositions: ArrayList<Int>)
}