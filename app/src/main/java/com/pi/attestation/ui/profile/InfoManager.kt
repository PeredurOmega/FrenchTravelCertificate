package com.pi.attestation.ui.profile

import android.content.Context
import com.pi.attestation.R
import com.pi.attestation.objects.UserInfo

class InfoManager(private val context: Context) {

    fun retrieveUserInfo(): UserInfo {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref),
            Context.MODE_PRIVATE)
        val firstName = sharedPref.getString(context.getString(R.string.first_name_key), "")
        val lastName = sharedPref.getString(context.getString(R.string.last_name_key), "")
        val birthDate = sharedPref.getString(context.getString(R.string.birth_date_key),"")
        val birthPlace = sharedPref.getString(context.getString(R.string.birth_place_key), "")
        val address = sharedPref.getString(context.getString(R.string.address_key), "")
        val city = sharedPref.getString(context.getString(R.string.city_key), "")
        val postalCode = sharedPref.getString(context.getString(R.string.postal_code_key), "")
        return UserInfo(firstName, lastName, birthDate, birthPlace, address, city, postalCode)
    }

    fun hasBeenFilled(userInfo: UserInfo): Boolean{
        return !userInfo.firstName.isNullOrBlank() && !userInfo.lastName.isNullOrBlank() &&
                !userInfo.birthDate.isNullOrBlank() && !userInfo.birthPlace.isNullOrBlank() &&
                !userInfo.address.isNullOrBlank() && !userInfo.city.isNullOrBlank() &&
                !userInfo.postalCode.isNullOrBlank()
    }

    fun saveUserInfo(userInfo: UserInfo) {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref),
            Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(context.getString(R.string.first_name_key), userInfo.firstName)
        editor.putString(context.getString(R.string.last_name_key), userInfo.lastName)
        editor.putString(context.getString(R.string.birth_date_key), userInfo.birthDate)
        editor.putString(context.getString(R.string.birth_place_key), userInfo.birthPlace)
        editor.putString(context.getString(R.string.address_key), userInfo.address)
        editor.putString(context.getString(R.string.city_key), userInfo.city)
        editor.putString(context.getString(R.string.postal_code_key), userInfo.postalCode)
        editor.apply()
    }
}