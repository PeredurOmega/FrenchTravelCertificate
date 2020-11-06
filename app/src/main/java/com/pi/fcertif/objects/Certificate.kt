package com.pi.fcertif.objects

import android.content.Context
import android.text.format.DateFormat
import com.pi.fcertif.R
import java.io.Serializable
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Object that contains all the info needed for an official certificate. This object is able to
 * auto-build its data for the QR Code.
 * @param context [Context] Used to retrieve shared preferences.
 * @param userInfo [UserInfo] contained in this certificate.
 * @param exitDateTime [DateTime] of the exit mentioned int this certificate.
 * @param reason [Reason] of the exit.
 */
class Certificate(
    context: Context,
    val userInfo: UserInfo,
    val exitDateTime: DateTime,
    val reason: Reason
) : Serializable {

    /**
     * [DateTime] of creation of the PDF
     */
    val creationDateTime: DateTime

    init {
        val timeFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "HH mm"),
            Locale.getDefault()
        )

        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault()
        )

        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
        val creationTimeAhead = sharedPref.getBoolean(
            context.resources.getString(R.string.creation_time_ahead),
            false
        )

        val now = Date()
        if (creationTimeAhead) now.time -= 900000

        creationDateTime = DateTime(dateFormat.format(now), timeFormat.format(now))
    }

    /**
     * [String] Name of the pdf that has been generated once the certificate has been created.
     */
    var pdfFileName: String = ""

    /**
     * Builds the data to put in the QR Code content. This data can be used by the police to check
     * whether or not the certificate is valid.
     * @return [String] containing all the information needed by the police in the right format.
     */
    fun buildData(): String {
        val dataBuilder = StringBuilder()
        dataBuilder.append("Cree le: ")
            .append(creationDateTime.date)
            .append(" a ")
            .append(creationDateTime.time.replace(":", "h"))
            .append(";\n Nom: ")
            .append(userInfo.lastName)
            .append(";\n Prenom: ")
            .append(userInfo.firstName)
            .append(";\n Naissance: ")
            .append(userInfo.birthDate)
            .append(" a ")
            .append(userInfo.birthPlace)
            .append(";\n Adresse: ")
            .append(userInfo.address)
            .append(" ")
            .append(userInfo.postalCode)
            .append(" ")
            .append(userInfo.city)
            .append(";\n Sortie: ")
            .append(exitDateTime.date)
            .append(" a ")
            .append(exitDateTime.time.replace(":", "h"))
            .append(";\n Motifs: ")
            .append(reason.value)
        return dataBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Certificate) {
            other.pdfFileName == pdfFileName
        } else false
    }

    override fun hashCode(): Int {
        var result = creationDateTime.hashCode()
        result = 31 * result + userInfo.hashCode()
        result = 31 * result + exitDateTime.hashCode()
        result = 31 * result + reason.hashCode()
        result = 31 * result + pdfFileName.hashCode()
        return result
    }
}