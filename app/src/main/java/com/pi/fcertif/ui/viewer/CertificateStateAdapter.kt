package com.pi.fcertif.ui.viewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * [FragmentStateAdapter] used to populate the [androidx.viewpager2.widget.ViewPager2] containing
 * the page 1 of the provided pdf and the page 2 of the provided pdf using [PdfViewerFragment].
 * @param fragmentActivity [FragmentActivity] where lives the bound
 * [androidx.viewpager2.widget.ViewPager2]
 * @param filePath [String] Path of the pdf.
 */
class CertificateStateAdapter(fragmentActivity: FragmentActivity, private val filePath: String) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) PdfViewerFragment.newInstance(filePath, 0)
        else PdfViewerFragment.newInstance(filePath, 1)
    }
}