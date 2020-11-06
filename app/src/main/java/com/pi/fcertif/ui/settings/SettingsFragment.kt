package com.pi.fcertif.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.analytics.FirebaseAnalytics
import com.pi.fcertif.R
import com.pi.fcertif.tools.CertificatesManager
import com.pi.fcertif.ui.tools.RatePrompt

/**
 * [Fragment] displaying the available settings.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                    CertificatesManager(fragmentActivity.filesDir)
                        .removeAll(fragmentActivity.cacheDir)
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .show()
        }

        val rateApp = view.findViewById<MaterialButton>(R.id.rateApp)
        rateApp.setOnClickListener {
            val firebaseAnalytics = FirebaseAnalytics.getInstance(fragmentActivity)
            RatePrompt(fragmentActivity).rate(firebaseAnalytics)
        }

        val shareApp = view.findViewById<MaterialButton>(R.id.shareApp)
        shareApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.app_shared_text))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val sharedPref = fragmentActivity.getSharedPreferences(
            fragmentActivity.resources.getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
        val creationTimeAhead = sharedPref.getBoolean(
            fragmentActivity.resources.getString(R.string.creation_time_ahead),
            false
        )

        val before15Minutes = view.findViewById<SwitchMaterial>(R.id.before15Minutes)
        before15Minutes.isChecked = creationTimeAhead
        before15Minutes.setOnCheckedChangeListener { _, isChecked ->
            run {
                context?.let {
                    val resources = it.resources
                    val editor = sharedPref.edit()
                    editor.putBoolean(resources.getString(R.string.creation_time_ahead), isChecked)
                    editor.apply()
                }
            }
        }
    }
}
