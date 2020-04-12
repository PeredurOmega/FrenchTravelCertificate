package com.pi.attestation.ui.viewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class CertificateStateAdapter(fragmentActivity: FragmentActivity, private val fileName: String)
    : FragmentStateAdapter(fragmentActivity){

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0) PdfViewerFragment.newInstance(fileName, 0)
                else PdfViewerFragment.newInstance(fileName, 1)
    }
}