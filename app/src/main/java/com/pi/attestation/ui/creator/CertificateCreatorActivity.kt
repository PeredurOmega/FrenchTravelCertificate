package com.pi.attestation.ui.creator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pi.attestation.R

/**
 * [AppCompatActivity] used to create a certificate.
 */
class CertificateCreatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate_creator)
    }
}