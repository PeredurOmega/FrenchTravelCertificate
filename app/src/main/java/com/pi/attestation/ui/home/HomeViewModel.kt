package com.pi.attestation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pi.attestation.objects.Certificate
import com.pi.attestation.tools.CertificatesManager
import java.io.*

class HomeViewModel(private val filesDir: File) : ViewModel() {

    private val _certificates = MutableLiveData<ArrayList<Certificate>>().apply {
        val certificates = CertificatesManager(filesDir).getExistingCertificates()
        value = certificates
    }

    val certificates: LiveData<ArrayList<Certificate>> = _certificates
}