package com.pi.attestation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
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
            else view.findViewById<TextView>(R.id.noCertificate).visibility = View.VISIBLE
        })
    }
}
