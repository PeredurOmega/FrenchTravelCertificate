package com.pi.fcertif.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pi.fcertif.R
import com.pi.fcertif.objects.UserInfo
import com.pi.fcertif.ui.creator.CertificateCreatorActivity
import com.pi.fcertif.ui.tools.EditedListener
import com.pi.fcertif.ui.tools.Leaver
import com.pi.fcertif.ui.tools.SaverFragment

/**
 * [SaverFragment] displaying user's info and enabling him to edit his info.
 * @see [SaverFragment]
 */
class ProfileFragment : SaverFragment() {

    companion object {

        /**
         * Key value for retrieving the id of the profile to edit if this argument is not provided
         * a new profile will be created from scratch.
         */
        const val PROFILE = "PROFILE"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentActivity = activity
        if (fragmentActivity != null) {
            val infoEditionWatcher = InfoEditionWatcher()
            val infoManager = InfoManager(fragmentActivity)
            val profileCount = infoManager.profileCount()
            infoEditionWatcher.build(
                view,
                infoManager.retrieveUserInfo(id = arguments?.getString(PROFILE) ?: "new"),
                profileCount > 1 || (profileCount == 1 && arguments?.getString(PROFILE) == null)
            )
            setEditedListener(infoEditionWatcher)

            val createCertificateButton =
                view.findViewById<MaterialButton>(R.id.createCertificateButton)
            createCertificateButton.setText(R.string.save_profile)
            createCertificateButton.setOnClickListener {
                saveChanges(object : Leaver {
                    override fun onQuit() {
                        MaterialAlertDialogBuilder(fragmentActivity)
                            .setTitle(R.string.create_a_certificate)
                            .setMessage(R.string.ask_for_creating_a_certificate)
                            .setPositiveButton(R.string.create) { _, _ ->
                                fragmentActivity.startActivity(
                                    Intent(
                                        fragmentActivity,
                                        CertificateCreatorActivity::class.java
                                    )
                                )
                            }
                            .setNegativeButton(R.string.no) { _, _ -> }
                            .show()
                    }
                })
            }
        }
    }

    override fun saveChanges(leaver: Leaver?) {
        val editedListener: EditedListener? = getEditedListener()
        if (editedListener is InfoEditionWatcher) {
            val editionWatcher: InfoEditionWatcher = editedListener
            val userInfo = editionWatcher.info
            val context = context
            if (userInfo != null && context != null) {
                saveInfo(context, editionWatcher, leaver, userInfo)
            } else Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Tries to save the info currently written in this [ProfileFragment]. In case it fails because
     * info are incomplete or malformed a [Toast] will be shown. This method automatically hides the
     * keyboard as the user should not be editing his info during the process.
     * @param context [Context] provided to the [InfoManager]
     * @param editionWatcher [InfoEditionWatcher] used to register the edition in case the save is
     * successful.
     * @param leaver [Leaver] to notify in case it's not null and the save is successful.
     * @param userInfo [UserInfo] to save.
     * @see [InfoManager]
     * @see [InfoEditionWatcher]
     * @see [Leaver]
     */
    private fun saveInfo(
        context: Context,
        editionWatcher: InfoEditionWatcher,
        leaver: Leaver?,
        userInfo: UserInfo
    ) {
        hideKeyboard()
        if (userInfo.incomplete()) {
            Toast.makeText(context, R.string.incomplete_profile, Toast.LENGTH_SHORT).show()
        } else {
            if (userInfo.hasMalformedBirthdate()) {
                Toast.makeText(context, R.string.malformed_birthdate, Toast.LENGTH_SHORT).show()
            } else {
                InfoManager(context).saveUserInfo(userInfo)
                editionWatcher.registerEdition()
                Toast.makeText(context, R.string.profile_saved, Toast.LENGTH_SHORT).show()
                leaver?.onQuit()
            }
        }
    }
}