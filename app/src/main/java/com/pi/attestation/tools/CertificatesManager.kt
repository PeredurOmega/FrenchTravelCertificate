package com.pi.attestation.tools

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pi.attestation.objects.Certificate
import java.io.*

/**
 * Utility class used to manage certificates in the json file.
 * @param dirFile [File] directory that contains the json file for all certificates.
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
    fun addCertificate(certificate: Certificate, position: Int){
        val certificates = getExistingCertificates()
        certificates.add(position, certificate)
        saveCertificates(certificates)
    }

    /**
     * Removes a [Certificate].
     * @param certificateToRemove [Certificate] to remove.
     */
    fun removeCertificate(certificateToRemove: Certificate){
        val certificates = getExistingCertificates()
        var index = 0
        while (index < certificates.size &&
            certificates[index].pdfPath != certificateToRemove.pdfPath){
            index++
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
    private fun saveCertificates(certificates: ArrayList<Certificate>){
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
     */
    fun removeAll() {
        certificatesFile.delete()
        //TODO WE SHOULD ALSO REMOVE ALL PDF
    }
}