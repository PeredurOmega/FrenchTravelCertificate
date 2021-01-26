package com.pi.fcertif.ui.profiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pi.fcertif.objects.UserInfo
import com.pi.fcertif.ui.profile.InfoManager
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList

/**
 * [ViewModel] used to provide content for the [ProfilesFragment] (retains data even in case of
 * screen rotation).
 */
class ProfilesViewModel(private val infoManager: InfoManager) : ViewModel() {

    /**
     * [MutableLiveData] with an [ArrayList] of all the currently available [UserInfo].
     */
    private val _profiles = MutableLiveData<ArrayList<UserInfo>>().apply {
        val profiles = infoManager.retrieveUsersInfo()
        value = profiles
    }

    /**
     * Returns [ProfilesViewModel#_profiles]. To be used by an observer.
     */
    val profiles: LiveData<ArrayList<UserInfo>> = _profiles

    /**
     * Adds a [UserInfo] to the [HomeViewModel#_certificates]. Any observer will be notified of
     * this change.
     * @param profile [UserInfo] to add.
     * @param position [Int] Position where to add the provided [UserInfo] in the [ArrayList].
     */
    fun addItem(profile: UserInfo, position: Int) {
        val profiles = _profiles.value
        if (profiles != null) {
            profiles.add(position, profile)
            _profiles.postValue(profiles)
        } else _profiles.postValue(ArrayList(Collections.singleton(profile)))
        infoManager.saveUserInfo(profile)
    }

    /**
     * Removes a [UserInfo] to the [ProfilesViewModel#_profiles]. Any observer will be notified of
     * this change.
     * @param position [Int] Position of the [UserInfo] to remove.
     */
    fun removeItem(position: Int) {
        val profiles = _profiles.value
        if (profiles != null) {
            infoManager.removeProfile(profiles[position].id)
            profiles.removeAt(position)
            _profiles.postValue(profiles)
        }
    }

    /**
     * Returns a [UserInfo] according its position [Int].
     * @param position [Int] Position of the [UserInfo] to retrieve.
     * @return [UserInfo] at the given position.
     */
    fun getProfile(position: Int): UserInfo? {
        return _profiles.value?.get(position)
    }

    /**
     * Returns an [ArrayList] of [UserInfo] according their positions in the adapter.
     * @param adapterPositions [ArrayList] containing each position in the adapter of the elements
     * of the [ArrayList] of [UserInfo] to retrieve.
     * @return [ArrayList] of [UserInfo] of the elements found at the given positions.
     */
    fun getProfiles(adapterPositions: ArrayList<Int>): ArrayList<UserInfo>? {
        val profiles = _profiles.value ?: return null
        val profilesToRemove = ArrayList<UserInfo>()
        try {
            for (adapterPosition in adapterPositions) {
                profilesToRemove.add(profiles[adapterPosition - 1])
            }
        } catch (e: IndexOutOfBoundsException) {
            return null
        }
        return profilesToRemove
    }

    /**
     * Removes an [ArrayList] of [UserInfo] to the [ProfilesViewModel#_profiles]. Any observer
     * will be notified of this change.
     * @param adapterPositions [UserInfo] of [Int] containing positions in the adapter of each
     * [UserInfo] to remove.
     */
    fun removeItems(adapterPositions: ArrayList<Int>) {
        val profiles = _profiles.value ?: return
        var corrector = 1
        for (adapterPosition in adapterPositions) {
            infoManager.removeProfile(profiles[adapterPosition - corrector].id)
            profiles.removeAt(adapterPosition - corrector)
            corrector++
        }
        _profiles.postValue(profiles)
    }

    /**
     * Adds an [ArrayList] of [UserInfo] to the [ProfilesViewModel#_profiles]. Any observer will
     * be notified of this change.
     * @param profilesToAdd [ArrayList] of [UserInfo] to add.
     * @param adapterPositions [ArrayList] of [Int] containing the positions where to add each
     * provided [UserInfo] in the [ArrayList].
     */
    fun addItems(profilesToAdd: ArrayList<UserInfo>, adapterPositions: ArrayList<Int>) {
        val profiles = _profiles.value
        if (profiles != null && profilesToAdd.size == adapterPositions.size) {
            for (i in 0 until profilesToAdd.size) {
                profiles.add(adapterPositions[i] - 1, profilesToAdd[i])
                infoManager.saveUserInfo(profilesToAdd[i])
            }
            _profiles.postValue(profiles)
        } else {
            for (i in 0 until profilesToAdd.size) infoManager.saveUserInfo(profilesToAdd[i])
            _profiles.postValue(ArrayList(profilesToAdd))
        }
    }
}