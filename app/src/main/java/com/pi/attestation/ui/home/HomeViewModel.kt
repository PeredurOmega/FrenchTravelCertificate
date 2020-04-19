package com.pi.attestation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pi.attestation.objects.Certificate
import com.pi.attestation.tools.CertificatesManager
import java.io.*
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList

/**
 * [ViewModel] used to provide content for the [HomeFragment] (retains data even in case of screen
 * rotation).
 */
class HomeViewModel(private val filesDir: File) : ViewModel() {

    /**
     * [MutableLiveData] with an [ArrayList] of all the currently available [Certificate].
     */
    private val _certificates = MutableLiveData<ArrayList<Certificate>>().apply {
        val certificates = CertificatesManager(filesDir).getExistingCertificates()
        value = certificates
    }

    /**
     * Returns [HomeViewModel#_certificates]. To be used by an observer.
     */
    val certificates: LiveData<ArrayList<Certificate>> = _certificates

    /**
     * Adds a [Certificate] to the [HomeViewModel#_certificates]. Any observer will be notified of
     * this change.
     * @param certificate [Certificate] to add.
     * @param position [Int] Position where to add the provided [Certificate] in the [ArrayList].
     */
    fun addItem(certificate: Certificate, position: Int) {
        val certificates = _certificates.value
        if(certificates != null){
            certificates.add(position, certificate)
            _certificates.postValue(certificates)
        }else{
            _certificates.postValue(ArrayList(Collections.singleton(certificate)))
        }
    }

    /**
     * Removes a [Certificate] to the [HomeViewModel#_certificates]. Any observer will be notified of
     * this change.
     * @param position [Int] Position of the [Certificate] to remove.
     */
    fun removeItem(position: Int) {
        val certificates = _certificates.value
        if(certificates != null){
            certificates.removeAt(position)
            _certificates.postValue(certificates)
        }
    }

    /**
     * Returns a [Certificate] according its position [Int].
     * @param position [Int] Position of the [Certificate] to retrieve.
     * @return [Certificate] at the given position.
     */
    fun getCertificate(position: Int): Certificate? {
        return _certificates.value?.get(position)
    }

    /**
     * Returns an [ArrayList] of [Certificate] according their positions in the adapter.
     * @param adapterPositions [ArrayList] containing each position in the adapter of the elements
     * of the [ArrayList] of [Certificate] to retrieve.
     * @return [ArrayList] of [Certificate] of the elements found at the given positions.
     */
    fun getCertificates(adapterPositions: ArrayList<Int>): ArrayList<Certificate>? {
        val certificates = _certificates.value ?: return null
        val certificatesToRemove = ArrayList<Certificate>()
        try {
            for(adapterPosition in adapterPositions){
                certificatesToRemove.add(certificates[adapterPosition - 1])
            }
        }catch (e: IndexOutOfBoundsException){
            return null
        }
        return certificatesToRemove
    }

    /**
     * Removes an [ArrayList] of [Certificate] to the [HomeViewModel#_certificates]. Any observer
     * will be notified of this change.
     * @param adapterPositions [ArrayList] of [Int] containing positions in the adapter of each
     * [Certificate] to remove.
     */
    fun removeItems(adapterPositions: ArrayList<Int>){
        val certificates = _certificates.value ?: return
        var corrector = 1
        for(adapterPosition in adapterPositions){
            certificates.removeAt(adapterPosition - corrector)
            corrector++
        }
        _certificates.postValue(certificates)
    }

    /**
     * Adds an [ArrayList] of [Certificate] to the [HomeViewModel#_certificates]. Any observer will
     * be notified of this change.
     * @param certificatesToAdd [ArrayList] of [Certificate] to add.
     * @param adapterPositions [ArrayList] of [Int] containing the positions where to add each
     * provided [Certificate] in the [ArrayList].
     */
    fun addItems(certificatesToAdd: ArrayList<Certificate>, adapterPositions: ArrayList<Int>) {
        val certificates = _certificates.value
        if(certificates != null && certificatesToAdd.size == adapterPositions.size){
            for(i in 0 until certificatesToAdd.size){
                certificates.add(adapterPositions[i] - 1, certificatesToAdd[i])
            }
            _certificates.postValue(certificates)
        }else{
            _certificates.postValue(ArrayList(certificatesToAdd))
        }
    }
}