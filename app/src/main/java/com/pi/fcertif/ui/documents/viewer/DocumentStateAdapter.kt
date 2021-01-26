package com.pi.fcertif.ui.documents.viewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * [FragmentStateAdapter] used to populate the [androidx.viewpager2.widget.ViewPager2] containing
 * all the pages of  of the provided file using [DocumentViewerFragment].
 * @param fragmentActivity [FragmentActivity] where lives the bound
 * [androidx.viewpager2.widget.ViewPager2]
 * @param filePath [String] Path of the file.
 * @param pagesCount [String] Number of pages in the file.
 */
class DocumentStateAdapter(
    fragmentActivity: FragmentActivity,
    private val filePath: String,
    private val pagesCount: Int
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return pagesCount
    }

    override fun createFragment(position: Int): Fragment {
        return DocumentViewerFragment.newInstance(filePath, position)
    }
}