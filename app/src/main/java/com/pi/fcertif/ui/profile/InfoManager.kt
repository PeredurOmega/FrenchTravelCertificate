package com.pi.fcertif.ui.profile

import android.content.Context
import android.content.SharedPreferences
import com.pi.fcertif.R
import com.pi.fcertif.objects.UserInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Utility class used to manage user's information.
 * @param context [Context] to use to retrieve [android.content.SharedPreferences].
 * @see [android.content.SharedPreferences]
 */
class InfoManager(private val context: Context) {

    /**
     * Returns the currently saved [UserInfo] in [android.content.SharedPreferences].
     * @return [UserInfo] currently save in [android.content.SharedPreferences].
     * @see [android.content.SharedPreferences]
     */
    fun retrieveUsersInfo(): ArrayList<UserInfo> {
        val sharedPref = retrieveSharedPreferences()

        val ids = sharedPref.getStringSet(context.getString(R.string.profiles_ids_key), null)

        val users = ArrayList<UserInfo>()
        if (ids == null || ids.size == 0) {
            val legacyProfile = retrieveLegacyUserInfo(sharedPref)
            if (legacyProfile != null) {
                users.add(legacyProfile)
                saveUserInfo(legacyProfile)
            }
        } else {
            val sortedIds = ids.sorted()
            for (id in sortedIds) users.add(retrieveUserInfo(sharedPref, id))
        }
        val shortNames = ArrayList<String>()
        users.forEach {
            while (shortNames.contains(it.shortName())) {
                it.uniqueNameID++
            }
            shortNames.add(it.shortName())
        }
        return users
    }

    private fun retrieveSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
    }

    /**
     * Retrieves the number of profiles.
     * @return [Int] the number of profiles.
     */
    fun profileCount(): Int {
        val sharedPref = retrieveSharedPreferences()
        val ids = sharedPref.getStringSet(context.getString(R.string.profiles_ids_key), null)
        return ids?.size ?: 0
    }

    /**
     * Retrieve a specific [UserInfo] according to the provided id.
     * @param sharedPref [SharedPreferences] to use.
     * @param id [String] of the [UserInfo] to retrieve in the [SharedPreferences].
     * @return [UserInfo] corresponding to the provided id in the [SharedPreferences].
     */
    fun retrieveUserInfo(
        sharedPref: SharedPreferences = retrieveSharedPreferences(),
        id: String
    ): UserInfo {
        val serializedInfo = sharedPref.getString(context.getString(R.string.profile_key, id), null)
        return if (serializedInfo != null) Json.decodeFromString(serializedInfo)
        else UserInfo("", "", "", "", "", "", "", id)
    }

    /**
     * Retrieves main profile from previous versions.
     * @param sharedPref [SharedPreferences] used to retrieve a profile from previous versions.
     * @return [UserInfo] if a main profile from previous versions exists, null otherwise.
     */
    private fun retrieveLegacyUserInfo(sharedPref: SharedPreferences): UserInfo? {
        if (!sharedPref.contains(context.getString(R.string.first_name_key)) || !sharedPref.contains(
                context.getString(R.string.last_name_key)
            )
        ) return null
        val firstName = sharedPref.getString(context.getString(R.string.first_name_key), "")
        val lastName = sharedPref.getString(context.getString(R.string.last_name_key), "")
        val birthDate = sharedPref.getString(context.getString(R.string.birth_date_key), "")
        val birthPlace = sharedPref.getString(context.getString(R.string.birth_place_key), "")
        val address = sharedPref.getString(context.getString(R.string.address_key), "")
        val city = sharedPref.getString(context.getString(R.string.city_key), "")
        val postalCode = sharedPref.getString(context.getString(R.string.postal_code_key), "")
        return UserInfo(firstName, lastName, birthDate, birthPlace, address, city, postalCode, "0")
    }

    /**
     * Checks whether or not a provided [UserInfo] has been fully filled.
     * @param userInfo [UserInfo] to check.
     * @return [Boolean] True if the provided [UserInfo] is fully filled, false otherwise.
     */
    fun hasBeenFilled(userInfo: UserInfo): Boolean {
        return !userInfo.firstName.isNullOrBlank() && !userInfo.lastName.isNullOrBlank() &&
                !userInfo.birthDate.isNullOrBlank() && !userInfo.birthPlace.isNullOrBlank() &&
                !userInfo.address.isNullOrBlank() && !userInfo.city.isNullOrBlank() &&
                !userInfo.postalCode.isNullOrBlank()
    }

    /**
     * Checks if there is at least one valid profile to use to fill a certificate.
     * @return [Boolean] True if there is at least one valid profile and false otherwise.
     */
    fun hasOneValidProfile(): Boolean {
        val sharedPref = retrieveSharedPreferences()

        val ids = sharedPref.getStringSet(context.getString(R.string.profiles_ids_key), null)

        if (ids == null || ids.size == 0) {
            val legacyProfile = retrieveLegacyUserInfo(sharedPref)
            return legacyProfile != null
        } else {
            var i = 0
            var profileId = ids.elementAtOrNull(i)
            while (profileId != null) {
                if (hasBeenFilled(retrieveUserInfo(sharedPref, profileId))) return true
                i++
                profileId = ids.elementAtOrNull(i)
            }
            return false
        }
    }

    /**
     * Moves the current main profile to the provided "new" id. If there is no profile at 0, the
     * provided id will be removed from the ids.
     * @param new [Int] id of the new position of the main profile.
     * @param sharedPref [SharedPreferences] to use.
     * @param editor [SharedPreferences.Editor] to use.
     */
    private fun moveMainProfile(
        new: Int,
        sharedPref: SharedPreferences,
        editor: SharedPreferences.Editor
    ) {
        val serializedInfo =
            sharedPref.getString(context.getString(R.string.profile_key, "0"), null)
        if (serializedInfo != null) {
            val userInfo = Json.decodeFromString<UserInfo>(serializedInfo)
            userInfo.id = new.toString()
            editor.putString(
                context.getString(R.string.profile_key, new.toString()),
                Json.encodeToString(userInfo)
            )
        } else editor.remove(context.getString(R.string.profile_key, new.toString()))
        editor.apply()
    }

    /**
     * Saves the provided [UserInfo] in the [android.content.SharedPreferences].
     * @param userInfo [UserInfo] to save into the [android.content.SharedPreferences].
     * @see [android.content.SharedPreferences]
     */
    fun saveUserInfo(userInfo: UserInfo) {
        val sharedPref = retrieveSharedPreferences()
        val ids = sharedPref.getStringSet(context.getString(R.string.profiles_ids_key), null)
            ?: emptySet()
        val newId = if (ids.isEmpty()) 0 else ids.sortedDescending()[0].toInt() + 1
        val editor = sharedPref.edit()
        val profileId =
            if (userInfo.id?.length ?: 0 > 3 && userInfo.id?.substring(0, 4) == "new0") {
                val previousId = userInfo.id?.substring(4)?.toIntOrNull() ?: newId
                if (previousId == newId) editor.putStringSet(
                    context.getString(R.string.profiles_ids_key),
                    ids.plusElement(previousId.toString())
                )
                moveMainProfile(previousId, sharedPref, editor)
                "0"
            } else (userInfo.id?.toIntOrNull() ?: newId).toString()
        userInfo.id = profileId
        editor.putStringSet(
            context.getString(R.string.profiles_ids_key),
            ids.plusElement(profileId)
        )
        editor.putString(
            context.getString(R.string.profile_key, profileId),
            Json.encodeToString(userInfo)
        )
        editor.apply()
    }

    /**
     * Removes the profile with the provided id.
     * @param id [String] id of the profile to remove.
     */
    fun removeProfile(id: String?) {
        val sharedPref = retrieveSharedPreferences()
        val ids = sharedPref.getStringSet(context.getString(R.string.profiles_ids_key), null)
            ?: emptySet()
        val editor = sharedPref.edit()
        editor.putStringSet(
            context.getString(R.string.profiles_ids_key),
            ids.minusElement(id)
        )
        editor.remove(context.getString(R.string.profile_key, id))
        editor.apply()
    }
}