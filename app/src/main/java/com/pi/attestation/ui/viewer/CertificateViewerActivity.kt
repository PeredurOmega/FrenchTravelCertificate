package com.pi.attestation.ui.viewer

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
import com.pi.attestation.MainActivity
import com.pi.attestation.R


class CertificateViewerActivity : AppCompatActivity() {

    companion object{
        const val FILE_NAME = "FILE_NAME"
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

    private fun showExplanationsIfNeeded(){
        Thread(Runnable {
            val sharedPref = getSharedPreferences(getString(R.string.shared_pref),
                Context.MODE_PRIVATE)
            val showExplanations =
                sharedPref.getBoolean(getString(R.string.first_certificate_view_key), true)

            if(showExplanations){
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
        }).start()
    }

    private fun setAdapter(viewPager: ViewPager2, tabLayout: TabLayout) {
        val fileName = intent.getStringExtra(FILE_NAME)
        if(fileName != null){
            val stateAdapter = CertificateStateAdapter(this, fileName)
            viewPager.adapter = stateAdapter
            TabLayoutMediator(tabLayout, viewPager,
                TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                    if (position == 0) {
                        tab.text = getString(R.string.certificate)
                    } else {
                        tab.text = getString(R.string.qr_code)
                    }
                }
            ).attach()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}