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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.tools.CertificatesManager
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
            if(!it.isNullOrEmpty()){
                val adapter = CertificatesAdapter(it)
                enableSwipeToDelete(view, recyclerView, adapter)
                recyclerView.adapter = adapter
            }
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

    private fun enableSwipeToDelete(view: View, recyclerView: RecyclerView,
                                    adapter: CertificatesAdapter){
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(view.context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    val certificate = adapter.getCertificate(position)
                    removeItem(position, adapter, certificate)
                    Snackbar.make(view, R.string.certificate_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo) {
                            restoreItem(position, adapter, certificate)
                            recyclerView.scrollToPosition(position)
                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun removeItem(position: Int, adapter: CertificatesAdapter, certificate: Certificate){
        adapter.removeItem(position)
        CertificatesManager().removeCertificate(certificate,
            File(activity?.filesDir, "certificates.json"))
    }

    private fun restoreItem(position: Int, adapter: CertificatesAdapter, certificate: Certificate){
        adapter.addItem(certificate, position)
        CertificatesManager().addCertificate(certificate,
            File(activity?.filesDir, "certificates.json"), position)
    }
}
