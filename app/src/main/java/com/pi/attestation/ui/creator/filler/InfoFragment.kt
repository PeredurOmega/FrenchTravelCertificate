package com.pi.attestation.ui.creator.filler

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.objects.DateTime
import com.pi.attestation.objects.Reason
import com.pi.attestation.objects.UserInfo
import com.pi.attestation.tools.CertificateGenerator
import com.pi.attestation.ui.profile.InfoEditionWatcher
import com.pi.attestation.ui.profile.InfoManager
import java.text.SimpleDateFormat
import java.util.*

class InfoFragment : Fragment() {

    private lateinit var reason: Reason
    private lateinit var exitDateTime: DateTime

    companion object{
        private const val REASON = "REASON"
        private const val EXIT_DATE_TIME = "EXIT_DATE_TIME"

        fun newInstance(reason: Reason, exitDateTime: DateTime): InfoFragment{
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentActivity = activity
        if(fragmentActivity != null){
            val infoEditionWatcher = InfoEditionWatcher(fragmentActivity)
            infoEditionWatcher.build(view, UserInfo(null, null, null,
                null, null, null, null)
            )
            val createCertificateButton =
                view.findViewById<MaterialButton>(R.id.createCertificateButton)
            createCertificateButton.setOnClickListener {
                val userInfo = infoEditionWatcher.info
                val infoManager = InfoManager(fragmentActivity)
                if(userInfo != null && infoManager.hasBeenFilled(userInfo)){
                    createCertificate(fragmentActivity)
                }else{
                    Toast.makeText(context, R.string.please_fill_info, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createCertificate(context: Context){
        val timeFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "HH mm"),
            Locale.getDefault())

        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault())

        val certificate = Certificate(DateTime(dateFormat.format(Date()), timeFormat.format(Date())),
            InfoManager(context).retrieveUserInfo(), exitDateTime, reason)

        CertificateGenerator(context, certificate).execute()
    }
}
