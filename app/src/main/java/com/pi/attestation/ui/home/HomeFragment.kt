package com.pi.attestation.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pi.attestation.R
import com.pi.attestation.ui.creator.CertificateCreatorActivity
import com.pi.attestation.ui.profile.InfoManager
import com.pi.attestation.ui.tools.ViewModelFactory
import java.io.File

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentActivity = activity ?: return

        val homeViewModel = ViewModelProvider(this,
            ViewModelFactory(File(fragmentActivity.filesDir, "certificates.json")))
            .get(HomeViewModel::class.java)

        homeViewModel.certificates.observe(viewLifecycleOwner, Observer {
            val recyclerView: RecyclerView = view.findViewById(R.id.certificatesRV)
            if(!it.isNullOrEmpty()) recyclerView.adapter = CertificatesAdapter(it)
            else view.findViewById<LinearLayout>(R.id.noCertificateLayout).visibility = View.VISIBLE
        })

        val navController = fragmentActivity.findNavController(R.id.nav_host_fragment)


        val infoManager = InfoManager(fragmentActivity)
        val noCertificateButton = view.findViewById<MaterialButton>(R.id.fillProfile)
        val noCertificateText = view.findViewById<TextView>(R.id.noCertificateText)
        if(infoManager.hasBeenFilled(infoManager.retrieveUserInfo())){
            noCertificateButton.setText(R.string.create_certificate)
            noCertificateText.setText(R.string.no_certificate_explanation_profile_filled)
            noCertificateButton.setOnClickListener{
                startActivity(Intent(fragmentActivity, CertificateCreatorActivity::class.java))
            }
        }else{
            noCertificateText.setText(R.string.no_certificate_explanation)
            noCertificateButton.setText(R.string.fill_profile)
            noCertificateButton.setOnClickListener{
                navController.navigate(R.id.nav_profile)
            }
        }
    }
}
