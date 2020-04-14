package com.pi.attestation.objects

import java.io.Serializable
import java.lang.StringBuilder

/**
 * Object that contains all the info needed for an official certificate. This object is able to
 * auto-build its data for the QR Code.
 * @param creationDateTime [DateTime] of creation.
 * @param userInfo [UserInfo] contained in this certificate.
 * @param exitDateTime [DateTime] of the exit mentioned int this certificate.
 * @param reason [Reason] of the exit.
 */
class Certificate(val creationDateTime: DateTime, val userInfo: UserInfo,
                  val exitDateTime: DateTime, val reason: Reason) : Serializable{

    /**
     * [String] Path of the pdf that has been generated once the certificate has been created.
     */
    var pdfPath : String = ""

    /**
     * Builds the data to put in the QR Code content. This data can be used by the police to check
     * whether or not the certificate is valid.
     * @return [String] containing all the information needed by the police in the right format.
     */
    fun buildData(): String{
        val dataBuilder = StringBuilder()
        dataBuilder.append("Cree le: ")
            .append(creationDateTime.date)
            .append(" a ")
            .append(creationDateTime.time)
            .append("; Nom: ")
            .append(userInfo.lastName)
            .append("; Prenom: ")
            .append(userInfo.firstName)
            .append("; Naissance: ")
            .append(userInfo.birthDate)
            .append(" a ")
            .append(userInfo.birthPlace)
            .append("; Adresse: ")
            .append(userInfo.address)
            .append(" ")
            .append(userInfo.postalCode)
            .append(" ")
            .append(userInfo.city)
            .append("; Sortie: ")
            .append(exitDateTime.date)
            .append(" a ")
            .append(exitDateTime.time)
            .append("; Motifs: ")
            .append(reason.shortName)
        return dataBuilder.toString()
    }
}