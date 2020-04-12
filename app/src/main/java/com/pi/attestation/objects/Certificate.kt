package com.pi.attestation.objects

import java.lang.StringBuilder

class Certificate(val creationDateTime: DateTime, val userInfo: UserInfo,
                  val exitDateTime: DateTime, val reason: Reason){

    var pdfPath : String = ""

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