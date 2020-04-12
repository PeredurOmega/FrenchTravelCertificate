package com.pi.attestation.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.pi.attestation.R
import com.pi.attestation.objects.UserInfo
import com.pi.attestation.ui.tools.EditedListener
import com.pi.attestation.ui.tools.Leaver
import com.pi.attestation.ui.tools.SaverFragment


class ProfileFragment : SaverFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentActivity = activity
        if(fragmentActivity != null){
            val infoEditionWatcher = InfoEditionWatcher(fragmentActivity)
            infoEditionWatcher.build(view, InfoManager(fragmentActivity).retrieveUserInfo())
            setEditedListener(infoEditionWatcher)

            val createCertificateButton =
                view.findViewById<MaterialButton>(R.id.createCertificateButton)
            createCertificateButton.setText(R.string.save_profile)
            createCertificateButton.setOnClickListener {
                saveChanges(null)
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
            } else {
                Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveInfo(context: Context, editionWatcher: InfoEditionWatcher, leaver: Leaver?,
                         userInfo: UserInfo) {
        InfoManager(context).saveUserInfo(userInfo)
        editionWatcher.registerEdition()
        leaver?.onQuit()
    }
}
