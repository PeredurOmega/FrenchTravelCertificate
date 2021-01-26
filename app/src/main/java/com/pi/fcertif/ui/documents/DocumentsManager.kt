package com.pi.fcertif.ui.documents

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import com.itextpdf.text.exceptions.InvalidPdfException
import com.itextpdf.text.pdf.PdfReader
import com.pi.fcertif.R
import com.pi.fcertif.objects.Document
import com.pi.fcertif.objects.UserInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Utility class used to manage custom documents.
 * @param context [Context] to use to retrieve [android.content.SharedPreferences].
 * @see [android.content.SharedPreferences]
 */
class DocumentsManager(private val context: Context) {

    /**
     * Returns the currently saved [UserInfo] in [android.content.SharedPreferences].
     * @return [UserInfo] currently save in [android.content.SharedPreferences].
     * @see [android.content.SharedPreferences]
     */
    fun retrieveDocuments(): ArrayList<Document> {
        val sharedPref = retrieveSharedPreferences()

        val serializedDocuments =
            sharedPref.getStringSet(context.getString(R.string.documents_key), null)

        val documents = ArrayList<Document>()

        if (serializedDocuments != null) {
            for (serializedDocument in serializedDocuments) {
                documents.add(Json.decodeFromString(serializedDocument))
            }
        }

        documents.sortedBy {
            it.timestamp
        }
        return documents
    }

    private fun retrieveSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
    }

    /**
     * Saves the provided [Document] in the [android.content.SharedPreferences].
     * @param document [Document] to save into the [android.content.SharedPreferences].
     * @see [android.content.SharedPreferences]
     */
    fun saveDocument(document: Document) {
        val sharedPref = retrieveSharedPreferences()
        val documents = sharedPref.getStringSet(context.getString(R.string.documents_key), null)
            ?: emptySet()
        val editor = sharedPref.edit()
        editor.putStringSet(
            context.getString(R.string.documents_key),
            documents.plusElement(Json.encodeToString(document))
        )
        editor.apply()
    }

    /**
     * Removes the provided document.
     * @param document [Document] to remove.
     */
    fun removeDocument(document: Document) {
        val sharedPref = retrieveSharedPreferences()
        val documents = sharedPref.getStringSet(context.getString(R.string.documents_key), null)
            ?: emptySet()
        val editor = sharedPref.edit()
        editor.putStringSet(
            context.getString(R.string.documents_key),
            documents.minusElement(Json.encodeToString(document))
        )
        editor.apply()
    }

    /**
     * Copies a uri reference to a temporary file.
     * @param uri The uri used as the input stream.
     * @return The input tempFile for convenience.
     */
    fun createDocument(uri: Uri) {
        val dirFile = context.filesDir
        val timeInMillis = Calendar.getInstance().timeInMillis
        val fileName = getFileName(uri)
        if (fileName != null) {
            val copiedFile = File(dirFile, "${timeInMillis}_${fileName}")
            val inStream = context.contentResolver.openInputStream(uri)
            val outStream = FileOutputStream(copiedFile)

            if (inStream != null) {
                inStream.copyTo(outStream)
                inStream.close()
                outStream.close()
                var pagesCount = 1
                try {
                    val pdfReader = PdfReader(copiedFile.inputStream())
                    pagesCount = pdfReader.numberOfPages
                } catch (e: InvalidPdfException) {
                }
                saveDocument(Document(fileName, pagesCount, timeInMillis))
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val metaCursor = context.contentResolver.query(uri, projection, null, null, null)
        var fileName: String? = null
        metaCursor?.use { mc ->
            if (mc.moveToFirst()) {
                fileName = mc.getString(0)
            }
        }
        return fileName
    }
}