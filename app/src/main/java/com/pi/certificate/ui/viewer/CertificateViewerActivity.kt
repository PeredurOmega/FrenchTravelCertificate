package com.pi.certificate.ui.viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.pi.certificate.MainActivity
import com.pi.certificate.R

/**
 * [AppCompatActivity] used to display the two pages of a
 * [com.pi.certificate.objects.Certificate.pdfFileName].
 */
class CertificateViewerActivity : AppCompatActivity() {

    companion object{
        /**
         * Key value for the file path of the pdf.
         */
        const val FILE_PATH = "FILE_PATH"
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

        showExplanationsIfNeeded()
    }

    /**
     * Shows an explanation of how to use the app this app if the user never opened
     * [CertificateViewerActivity] before.
     */
    private fun showExplanationsIfNeeded(){
        Thread {
            val sharedPref = getSharedPreferences(
                getString(R.string.shared_pref),
                Context.MODE_PRIVATE
            )
            val showExplanations =
                sharedPref.getBoolean(getString(R.string.first_certificate_view_key), true)

            if (showExplanations) {
                val editor = sharedPref.edit()
                editor.putBoolean(getString(R.string.first_certificate_view_key), false)
                editor.apply()

                this.runOnUiThread {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.how_to_use_title)
                        .setMessage(R.string.how_to_use_text)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                }
            }
        }.start()
    }

    /**
     * Binds the [CertificateStateAdapter] to the [TabLayout] and the [ViewPager2] provided.
     * @param viewPager [ViewPager2] where we should display the two pdf pages.
     * @param tabLayout [TabLayout] used to navigate in the provided [ViewPager2] and to indicate
     * the position of the current [ViewPager2].
     */
    private fun setAdapter(viewPager: ViewPager2, tabLayout: TabLayout) {
        val fileName = intent.getStringExtra(FILE_PATH)
        if(fileName != null){
            val stateAdapter = CertificateStateAdapter(this, fileName)
            viewPager.adapter = stateAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
                if (position == 0) {
                    tab.text = getString(R.string.certificate)
                } else {
                    tab.text = getString(R.string.qr_code)
                }
            }.attach()
        }
    }

    /**
     * Overrides onBackPressed to only go to the [MainActivity] when pressed (and therefore, never
     * going back to edition once the certificate is generated).
     */
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}