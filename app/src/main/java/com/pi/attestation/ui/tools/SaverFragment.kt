package com.pi.attestation.ui.tools

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.pi.attestation.R

/**
 * Abstract class that should be extended by any [Fragment] that wish to have save/discard features.
 */
abstract class SaverFragment : Fragment() {

    private var editedListener: EditedListener? = null
    private var shouldExit = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentActivity = activity ?: return
        val navView: NavigationView = fragmentActivity.findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { item: MenuItem ->
            val navController =
                Navigation.findNavController(fragmentActivity, R.id.nav_host_fragment)
            var result = true
            if (shouldNotExit(object : Leaver {
                    override fun onQuit() {
                        navigate(item, navView, navController, fragmentActivity)
                    }
                })) {
                result = false
            }else navigate(item, navView, navController, fragmentActivity)
            result
        }
    }

    private fun navigate(item: MenuItem, navView: NavigationView, navController: NavController,
                         fragmentActivity: FragmentActivity) {
        NavigationUI.setupWithNavController(navView, navController)
        val drawer: DrawerLayout = fragmentActivity.findViewById(R.id.drawer_layout)
        drawer.closeDrawers()
        Navigation.findNavController(fragmentActivity, R.id.nav_host_fragment).navigate(item.itemId)
    }

    /**
     * Registers a [EditedListener] to this [SaverFragment].
     * @param editedListener [EditedListener] to check if the content of the child [Fragment] has been edited.
     */
    protected fun setEditedListener(editedListener: EditedListener) {
        this.editedListener = editedListener
    }

    /**
     * Returns the currently registered [EditedListener].
     * @return [EditedListener] used by this [SaverFragment] to check id the content of the child has been edited.
     */
    fun getEditedListener(): EditedListener? {
        return editedListener
    }

    private fun askForSaving(leaver: Leaver) {
        val context = context
        if (context != null) {
            MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.save_changes)
                .setMessage(R.string.ask_save_changes)
                .setPositiveButton(R.string.save) { _, _ -> saveChanges(leaver) }
                .setNegativeButton(R.string.discard) {_, _ -> leaver.onQuit() }
                .show()
        }
        hideKeyboard()
    }

    protected fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Saves the current changes. This method should be overridden by children.
     */
    protected abstract fun saveChanges(leaver: Leaver?)

    /**
     * Check if the [Fragment] can be left.
     * @param leaver [Leaver] to call when exiting the [Fragment].
     * @return True if we should exit right now, false otherwise. If false the user will also be asked for which saving option he wants (save or
     * discard).
     */
    fun shouldNotExit(leaver: Leaver): Boolean {
        if (!shouldExit) {
            if (editedListener?.hasBeenEdited() == true) {
                askForSaving(leaver)
            } else {
                shouldExit = true
            }
        }
        return !shouldExit
    }
}