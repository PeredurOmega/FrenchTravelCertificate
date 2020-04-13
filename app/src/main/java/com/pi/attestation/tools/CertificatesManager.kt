package com.pi.attestation.tools

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pi.attestation.objects.Certificate
import java.io.*


class CertificatesManager() {


    fun addCertificate(certificate: Certificate, certificatesFile: File, position: Int){
        certificatesFile.createNewFile()
        val certificates = getExistingCertificates(certificatesFile)
        certificates.add(position, certificate)
        saveCertificates(certificatesFile, certificates)
    }

    fun removeCertificate(certificateToRemove: Certificate, certificatesFile: File){
        certificatesFile.createNewFile()
        val certificates = getExistingCertificates(certificatesFile)

        var index = 0
        while (index < certificates.size &&
            certificates[index].pdfPath != certificateToRemove.pdfPath){
            index++
        }

        certificates.removeAt(index)

        saveCertificates(certificatesFile, certificates)
    }

    private fun saveCertificates(certificatesFile: File, certificates: ArrayList<Certificate>){
        val gson = Gson()
        val fileOutputStream = FileOutputStream(certificatesFile)
        fileOutputStream.write(gson.toJson(certificates).toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    fun getExistingCertificates(certificatesFile: File): ArrayList<Certificate> {
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
}