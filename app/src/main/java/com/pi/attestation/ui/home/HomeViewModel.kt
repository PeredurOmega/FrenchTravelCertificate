package com.pi.attestation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pi.attestation.objects.Certificate
import java.io.*

class HomeViewModel(private val file: File) : ViewModel() {

    private val _certificates = MutableLiveData<List<Certificate>>().apply {
        file.createNewFile()
        val certificates = getExistingCertificates(file)
        certificates.reverse()
        value = certificates
    }

    private fun getExistingCertificates(file: File): ArrayList<Certificate> {
        val gson = Gson()
        var text = ""
        try {
            val inputStream: InputStream = FileInputStream(file)
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

    val certificates: LiveData<List<Certificate>> = _certificates
}