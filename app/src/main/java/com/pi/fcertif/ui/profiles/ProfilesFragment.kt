package com.pi.fcertif.ui.profiles

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pi.fcertif.R
import com.pi.fcertif.ui.home.SwipeToDeleteCallback
import com.pi.fcertif.ui.profile.InfoManager
import com.pi.fcertif.ui.tools.ViewModelProfilesFactory


/**
 * [Fragment] displayed when we open the app (as "profiles" [Fragment]). This [Fragment] displays
 * all the profiles.
 */
class ProfilesFragment : Fragment(), ActionModeListener {

    private lateinit var profilesViewModel: ProfilesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profiles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentActivity = activity ?: return

        profilesViewModel = ViewModelProvider(
            this,
            ViewModelProfilesFactory(InfoManager(fragmentActivity))
        ).get(ProfilesViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.profilesRV)
        val adapter = ProfilesAdapter(this)
        recyclerView.adapter = adapter
        enableSwipeToDelete(view, recyclerView)

        profilesViewModel.profiles.observe(viewLifecycleOwner, {
            adapter.setItems(it)
        })
    }

    /**
     * Enables the swipe to delete feature. With this feature enabled the user can delete a
     * [com.pi.fcertif.objects.UserInfo] just by swiping to the left. When deleting a
     * [com.pi.fcertif.objects.UserInfo] a [Snackbar] will be
     * shown to provide a "undo" feature to the user in case it was accidental.
     * @param view [View] where to create the [Snackbar] displayed in case of
     * [com.pi.fcertif.objects.UserInfo] deletion.
     * @param recyclerView [RecyclerView] where the feature should be enabled.
     * @see SwipeToDeleteCallback
     */
    private fun enableSwipeToDelete(view: View, recyclerView: RecyclerView) {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(view.context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition - 1
                    if (position < 0) return
                    val profile = profilesViewModel.getProfile(position) ?: return
                    val fragmentActivity = activity
                    if (fragmentActivity != null) {
                        profilesViewModel.removeItem(position)
                        Snackbar.make(view, R.string.profile_deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) {
                                profilesViewModel.addItem(profile, position)
                                recyclerView.scrollToPosition(position)
                            }.show()
                    } else Toast.makeText(
                        view.context, R.string.unknown_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun startActionMode(actionModeCallback: ActionMode.Callback): ActionMode? {
        return activity?.startActionMode(actionModeCallback)
    }

    override fun deleteAction(toBeRemovedList: ArrayList<Int>) {
        val adapterPositions = ArrayList(toBeRemovedList)
        val profiles = profilesViewModel.getProfiles(toBeRemovedList)
        val fragmentActivity = activity
        if (fragmentActivity != null && profiles != null) {
            profilesViewModel.removeItems(adapterPositions)

            val view = view
            if (view != null) {
                Snackbar.make(
                    view, if (toBeRemovedList.size == 1) R.string.profile_deleted
                    else R.string.profiles_deleted,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.undo) {
                    profilesViewModel.addItems(profiles, adapterPositions)
                }.show()
            }
        } else Toast.makeText(fragmentActivity, R.string.unknown_error, Toast.LENGTH_SHORT)
            .show()
    }
}
