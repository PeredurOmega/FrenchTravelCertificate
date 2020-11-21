package com.pi.fcertif.ui.creator.filler

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.pi.fcertif.R
import com.pi.fcertif.objects.Certificate
import com.pi.fcertif.objects.DateTime
import com.pi.fcertif.objects.Reason
import com.pi.fcertif.objects.UserInfo
import com.pi.fcertif.tools.CertificateGenerator
import com.pi.fcertif.ui.profile.InfoEditionWatcher
import com.pi.fcertif.ui.profile.InfoManager

/**
 * [Fragment] that is used to prompt the user for information. This fragment is only reached when
 * the user chooses not to use its registered profile.
 */
class InfoFragment : Fragment() {

    /**
     * Previously selected [Reason] for the certificate that we are currently trying to fill.
     */
    private lateinit var reason: Reason

    /**
     * Previously selected [DateTime] for the certificate that we are currently trying to fill.
     */
    private lateinit var exitDateTime: DateTime

    companion object {

        /**
         * Key value for retrieving [InfoFragment#reason].
         */
        private const val REASON = "REASON"

        /**
         * Key value for retrieving [InfoFragment#exitDateTime].
         */
        private const val EXIT_DATE_TIME = "EXIT_DATE_TIME"

        /**
         * Creates a new instance of [InfoFragment] with the previously selected [Reason] and exit
         * [DateTime] as arguments (enabling to retain arguments even in case of screen rotation).
         * @param reason [Reason] previously selected.
         * @param exitDateTime [DateTime] previously selected.
         * @return New [InfoFragment].
         */
        fun newInstance(reason: Reason, exitDateTime: DateTime): InfoFragment {
            val args = Bundle()
            args.putSerializable(REASON, reason)
            args.putSerializable(EXIT_DATE_TIME, exitDateTime)
            val fragment = InfoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reason = arguments?.get(REASON) as Reason
        exitDateTime = arguments?.get(EXIT_DATE_TIME) as DateTime
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
            infoEditionWatcher.build(
                view, UserInfo(
                    "", "", "",
                    "", "", "", "", "new"
                ), false
            )
            val createCertificateButton =
                view.findViewById<MaterialButton>(R.id.createCertificateButton)
            createCertificateButton.setOnClickListener {
                val userInfo = infoEditionWatcher.info
                val infoManager = InfoManager(fragmentActivity)
                if (userInfo != null && infoManager.hasBeenFilled(userInfo)) {
                    infoManager.saveUserInfo(userInfo)
                    createCertificate(fragmentActivity, userInfo)
                } else {
                    Toast.makeText(context, R.string.please_fill_info, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Creates a certificate once all the info are filled.
     * @param context [Context] to provide to the [CertificateGenerator].
     * @param userInfo [UserInfo] attached to the [Certificate] to create.
     */
    private fun createCertificate(context: Context, userInfo: UserInfo) {
        val certificate = Certificate(context, userInfo, exitDateTime, reason)
        CertificateGenerator(context, certificate, true).execute()
    }
}
