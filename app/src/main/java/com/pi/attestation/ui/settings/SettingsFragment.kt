package com.pi.attestation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pi.attestation.R
import com.pi.attestation.tools.CertificatesManager
import java.io.File

/**
 * [Fragment] displaying the available settings.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentActivity = activity ?: return

        val removeAllCertificates = view.findViewById<MaterialButton>(R.id.removeAllCertificates)
        removeAllCertificates.setOnClickListener {
            MaterialAlertDialogBuilder(fragmentActivity)
                .setTitle(R.string.remove_all_certificates)
                .setMessage(R.string.ask_remove_all_certificates)
                .setPositiveButton(R.string.yes) { _, _ ->
                    CertificatesManager(fragmentActivity.filesDir).removeAll()
                }
                .setNegativeButton(R.string.no) {_, _ ->}
                .show()
        }
    }
}
