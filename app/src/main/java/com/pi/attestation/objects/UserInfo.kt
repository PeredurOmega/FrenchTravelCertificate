package com.pi.attestation.objects

import android.text.format.DateFormat
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class UserInfo(val firstName: String?, val lastName: String?, val birthDate: String?,
               val birthPlace: String?, val address: String?, val city: String?,
               val postalCode: String?) : Serializable {

    fun hasMalformedBirthdate(): Boolean {
        if(birthDate.isNullOrBlank()) return false
        if(!birthDate.contains("/")) return true
        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault())
        try {
            dateFormat.parse(birthDate)
        }catch (e: Exception){
            return true
        }
        val splitDate = birthDate.split("/")
        if(splitDate.size != 3) return true

        val day = splitDate[0].toIntOrNull()
        val month = splitDate[1].toIntOrNull()

        if(day == null || day < 1 || day > 31) return true
        if(month == null || month < 1 || month > 12) return true
        return false
    }

    fun incomplete(): Boolean {
        return firstName.isNullOrBlank() || lastName.isNullOrBlank() || birthDate.isNullOrBlank()
                || birthPlace.isNullOrBlank() || address.isNullOrBlank() || city.isNullOrBlank()
                || postalCode.isNullOrBlank()
    }
}