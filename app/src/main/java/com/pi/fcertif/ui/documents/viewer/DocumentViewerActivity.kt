package com.pi.fcertif.ui.documents.viewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pi.fcertif.R

/**
 * [AppCompatActivity] used to display the pages of a [com.pi.fcertif.objects.Document].
 */
class DocumentViewerActivity : AppCompatActivity() {

    companion object {
        /**
         * Key value for the file name.
         */
        const val FILE_NAME = "FILE_NAME"

        /**
         * Document pages count.
         */
        const val PAGES_COUNT = "PAGES_COUNT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        val viewPager: ViewPager2 = findViewById(R.id.addContentViewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        setAdapter(viewPager, tabLayout)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Binds the [DocumentStateAdapter] to the [TabLayout] and the [ViewPager2] provided.
     * @param viewPager [ViewPager2] where we should display the two pdf pages.
     * @param tabLayout [TabLayout] used to navigate in the provided [ViewPager2] and to indicate
     * the position of the current [ViewPager2].
     */
    private fun setAdapter(viewPager: ViewPager2, tabLayout: TabLayout) {
        val fileName = intent.getStringExtra(FILE_NAME)
        val pagesCount = intent.getIntExtra(PAGES_COUNT, 0)
        if (fileName != null) {
            val stateAdapter = DocumentStateAdapter(this, fileName, pagesCount)
            viewPager.adapter = stateAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
                tab.text = getString(R.string.page, position + 1)
            }.attach()
        }
    }
}