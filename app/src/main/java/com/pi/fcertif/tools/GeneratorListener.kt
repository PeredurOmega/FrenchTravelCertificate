package com.pi.fcertif.tools

/**
 * Interface to notify when a [CertificatesGenerator] is done.
 */
interface GeneratorListener {

    /**
     * Called when a [CertificatesGenerator] is (successfully) done.
     */
    fun onGenerated()
}