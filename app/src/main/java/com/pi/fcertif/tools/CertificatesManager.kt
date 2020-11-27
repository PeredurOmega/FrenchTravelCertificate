package com.pi.fcertif.tools

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pi.fcertif.objects.Certificate
import java.io.*

/**
 * Utility class used to manage certificates in the json file.
 * @param dirFile [File] directory that contains the json file for all certificates. This directory
 * can be obtained by using [android.content.Context.getFilesDir].
 */
class CertificatesManager(dirFile: File) {

    /**
     * Json [File] containing all the certificates of the user.
     */
    private val certificatesFile = File(dirFile, "certificates.json")

    init {
        certificatesFile.createNewFile()
    }

    /**
     * Adds a [Certificate] to the [CertificatesManager.certificatesFile] containing all
     * certificates.
     * @param certificate [Certificate] to add.
     * @param position [Int] where to add the [Certificate].
     */
    fun addCertificate(certificate: Certificate, position: Int) {
        val certificates = getExistingCertificates()
        certificates.add(position, certificate)
        saveCertificates(certificates)
    }

    /**
     * Adds an [ArrayList] of [Certificate] to the [CertificatesManager.certificatesFile] containing
     * all certificates.
     * @param certificatesToAdd [ArrayList] of [Certificate] to add.
     * @param adapterPositions [ArrayList] of [Int] positions where to add [ArrayList] of
     * [Certificate].
     */
    fun addCertificates(
        certificatesToAdd: ArrayList<Certificate>,
        adapterPositions: ArrayList<Int>
    ) {
        if (certificatesToAdd.size != adapterPositions.size) return
        val certificates = getExistingCertificates()
        for (i in 0 until certificatesToAdd.size) {
            certificates.add(adapterPositions[i] - 1, certificatesToAdd[i])
        }
        saveCertificates(certificates)
    }

    /**
     * Removes a [Certificate].
     * @param certificateToRemove [Certificate] to remove.
     */
    fun removeCertificate(certificateToRemove: Certificate) {
        val certificates = getExistingCertificates()
        if (certificates.size == 0) return
        var index = 0
        while (index < certificates.size && certificates[index] != certificateToRemove) {
            index++
        }
        while (index >= certificates.size) {
            index--
        }
        certificates.removeAt(index)
        saveCertificates(certificates)
    }

    /**
     * Saves an [ArrayList] of [Certificate] discarding all previous certificates in the
     * [CertificatesManager.certificatesFile].
     * @param certificates Full [ArrayList] of [Certificate] that the json file containing
     * certificates should contain.
     * @see getExistingCertificates
     */
    private fun saveCertificates(certificates: ArrayList<Certificate>) {
        val gson = Gson()
        val fileOutputStream = FileOutputStream(certificatesFile)
        fileOutputStream.write(gson.toJson(certificates).toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    /**
     * Retrieves all existing certificates in the [CertificatesManager.certificatesFile].
     * @return The full [ArrayList] of [Certificate] contained in the
     * [CertificatesManager.certificatesFile].
     */
    fun getExistingCertificates(): ArrayList<Certificate> {
        val gson = Gson()
        var text = ""
        try {
            val inputStream: InputStream = FileInputStream(certificatesFile)
            val stringBuilder = StringBuilder()
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var receiveString: String?
            while (bufferedReader.readLine().also { receiveString = it } != null) {
                stringBuilder.append(receiveString)
            }
            inputStream.close()
            text = stringBuilder.toString()
        } catch (e: FileNotFoundException) {
            Log.e("ATTESTATION", e.message ?: "")
        } catch (e: IOException) {
            Log.e("ATTESTATION", e.message ?: "")
        }
        val type = object : TypeToken<ArrayList<Certificate>?>() {}.type
        val certificates: ArrayList<Certificate>? = gson.fromJson(text, type)
        return certificates ?: ArrayList()
    }

    /**
     * Removes all the certificates by deleting the [CertificatesManager.certificatesFile].
     * @param cacheDir [File] where to find pdf files.
     */
    fun removeAll(cacheDir: File) {
        val certificates = getExistingCertificates()
        certificatesFile.delete()
        for (certificate in certificates) {
            deletePdf(certificate, cacheDir)
        }
    }

    /**
     * Removes an [ArrayList] of [Certificate].
     * @param certificatesToRemove [ArrayList] of [Certificate] to remove.
     */
    fun removeCertificates(certificatesToRemove: ArrayList<Certificate>) {
        val certificates = getExistingCertificates()
        for (certificateToRemove in certificatesToRemove) {
            var index = 0
            val certificatesCount = certificates.size
            if (certificatesCount == 0) break
            while (index < certificates.size && certificates[index] != certificateToRemove) {
                index++
            }
            if(index < certificates.size) certificates.removeAt(index)
        }
        saveCertificates(certificates)
    }

    /**
     * Deletes the pdf bound to a [Certificate]. This action is final and if executed the pdf will
     * be fully deleted. This method should be called only when we are sure that the user really
     * wants to delete a certificate.
     * @param certificate [Certificate] bound to the pdf to delete. This [Certificate] should also
     * be deleted when this action is done.
     * @param cacheDir [File] where to find pdf files.
     */
    fun deletePdf(certificate: Certificate, cacheDir: File) {
        File(cacheDir, certificate.pdfFileName).delete()
    }

    /**
     * Deletes the pdf bound to each [Certificate] in the provided [ArrayList]. This action is
     * final and if executed the pdf will be fully deleted. This method should be called only when
     * we are sure that the user really wants to delete a certificate.
     * @param certificatesToDelete [ArrayList] of [Certificate] bound to the pdf to delete. Those
     * certificates should also be deleted when this action is done.
     * @param cacheDir [File] where to find pdf files.
     */
    fun deletePdfFiles(certificatesToDelete: ArrayList<Certificate>, cacheDir: File) {
        for (certificateToDelete in certificatesToDelete) {
            deletePdf(certificateToDelete, cacheDir)
        }
    }

    /**
     * Returns a certificate matching with the specified pdf file's name.
     * @param pdfFileName [String] Name of the pdf file.
     * @return A [Certificate] matching with the specified pdf file's path. Returns null in case no
     * certificate was found (should never occur).
     */
    fun getCertificate(pdfFileName: String): Certificate? {
        val certificates = getExistingCertificates()
        for (certificate in certificates) {
            if (certificate.pdfFileName == pdfFileName) return certificate
        }
        return null
    }
}