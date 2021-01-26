package com.pi.fcertif.objects

import java.io.Serializable

/**
 * Object that contains all the info needed for a custom document.
 * @param fileName Name to the document.
 * @param pagesCount Number of pages in the document.
 * @param timestamp Date of importation of the document.
 */
@kotlinx.serialization.Serializable
data class Document(val fileName: String, val pagesCount: Int, val timestamp: Long) : Serializable {
    fun realFileName(): String? {
        return "${timestamp}_${fileName}"
    }
}