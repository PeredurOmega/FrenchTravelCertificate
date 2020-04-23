package com.pi.certificate.ui.profile

import android.text.TextUtils
import androidx.core.util.Pair
import com.google.android.material.textfield.TextInputEditText
import com.pi.certificate.objects.UserInfo

/**
 * Toolkit class used for displaying user's info.
 * @param firstNameEditText [Pair] containing as first parameter the [TextInputEditText] where the
 * user can write his first name and as second parameter the [String] of the previously saved first
 * name.
 * @param lastNameEditText [Pair] containing as first parameter the [TextInputEditText] where the
 * user can write his last name and as second parameter the [String] of the previously saved last
 * name.
 * @param birthDateEditText [Pair] containing as first parameter the [TextInputEditText] where the
 * user can write his birthdate and as second parameter the [String] of the previously saved
 * birthdate.
 * @param birthPlaceEditText [Pair] containing as first parameter the [TextInputEditText] where the
 * user can write his birth place and as second parameter the [String] of the previously saved
 * birth place.
 * @param addressEditText [Pair] containing as first parameter the [TextInputEditText] where the
 * user can write his current address and as second parameter the [String] of the previously saved
 * address.
 * @param cityEditText [Pair] containing as first parameter the [TextInputEditText] where the
 * user can write his current city and as second parameter the [String] of the previously saved
 * city.
 * @param postalCodeEditText [Pair] containing as first parameter the [TextInputEditText] where the
 * user can write his current postal code and as second parameter the [String] of the previously
 * saved postal code.
 */
internal class UserInfoBuilder(
    private var firstNameEditText: Pair<TextInputEditText, String>,
    private var lastNameEditText: Pair<TextInputEditText, String>,
    private var birthDateEditText: Pair<TextInputEditText, String>,
    private var birthPlaceEditText: Pair<TextInputEditText, String>,
    private var addressEditText: Pair<TextInputEditText, String>,
    private var cityEditText: Pair<TextInputEditText, String>,
    private var postalCodeEditText: Pair<TextInputEditText, String>) {

    init {
        setUpTextInfo()
    }

    /**
     * Sets the [String] previously saved in their bound [TextInputEditText] for all [Pair] passed
     * in the constructor.
     */
    private fun setUpTextInfo() {
        setOriginalText(firstNameEditText)
        setOriginalText(lastNameEditText)
        setOriginalText(birthDateEditText)
        setOriginalText(birthPlaceEditText)
        setOriginalText(addressEditText)
        setOriginalText(cityEditText)
        setOriginalText(postalCodeEditText)
    }

    /**
     * Sets the [String] previously saved in their bound [TextInputEditText] the provided [Pair].
     * @param pair [Pair] containing [TextInputEditText] as first argument and [String] as second
     * argument.
     */
    private fun setOriginalText(pair: Pair<TextInputEditText, String>) {
        if (pair.first != null) pair.first!!.setText(pair.second)
    }

    /**
     * Checks whether or not a [Pair] has been edited.
     * @param pair [Pair] to check.
     * @return [Boolean] True if the field has been edited. False otherwise.
     */
    private fun fieldEdited(pair: Pair<TextInputEditText, String>): Boolean {
        if (pair.first != null) {
            val editable = pair.first!!.text
            return if (editable != null) {
                if (TextUtils.isEmpty(pair.second) && TextUtils.isEmpty(editable.toString())) false
                else editable.toString() != pair.second
            } else !TextUtils.isEmpty(pair.second)
        }
        return false
    }

    /**
     * Checks whether or not a [Pair] dedicated to a date has been edited.
     * @param pair [Pair] to check.
     * @return [Boolean] True if the field has been edited. False otherwise.
     */
    private fun dateFieldEdited(pair: Pair<TextInputEditText, String>): Boolean {
        if (pair.first != null) {
            val editable = pair.first!!.text
            return if (editable != null) {
                if (TextUtils.isEmpty(pair.second) && TextUtils.isEmpty(editable.toString()
                        .replace("[^\\d.]|\\.".toRegex(), ""))) false
                else editable.toString() != pair.second
            } else !TextUtils.isEmpty(pair.second)
        }
        return false
    }

    /**
     * Builds [UserInfo] by retrieving all text contained in the first parameter of the [Pair]
     * provided through the constructor of [UserInfoBuilder].
     * @return [UserInfo] built according the user's input.
     */
    fun buildUserInfo(): UserInfo {
        return UserInfo(firstNameEditText.getText(), lastNameEditText.getText(),
            birthDateEditText.getText(), birthPlaceEditText.getText(), addressEditText.getText(),
            cityEditText.getText(), postalCodeEditText.getText())
    }

    /**
     * Extension returning the text content of a [Pair].
     * @return [String] Text content of the [TextInputEditText] in this [Pair].
     */
    private fun Pair<TextInputEditText, String>.getText() : String{
        return this.first?.text.toString()
    }

    /**
     * Checks whether or not the current info has been updated / edited.
     * @return [Boolean] True if one of the fields has been edited, false otherwise.
     */
    fun hasBeenEdited(): Boolean {
        return fieldEdited(firstNameEditText) || fieldEdited(lastNameEditText) ||
                dateFieldEdited(birthDateEditText) || fieldEdited(birthPlaceEditText) ||
                fieldEdited(addressEditText) || fieldEdited(cityEditText)
                || fieldEdited(postalCodeEditText)
    }

    /**
     * Registers all current changes. All the current fields will be registered and if you call
     * [UserInfoBuilder#hasBeenEdited] just after this method, the result should be false.
     */
    fun registerEdition() {
        firstNameEditText = registerEdition(firstNameEditText)
        lastNameEditText = registerEdition(lastNameEditText)
        birthDateEditText = registerEdition(birthDateEditText)
        birthPlaceEditText = registerEdition(birthPlaceEditText)
        addressEditText = registerEdition(addressEditText)
        cityEditText = registerEdition(cityEditText)
        postalCodeEditText = registerEdition(postalCodeEditText)
    }

    /**
     * Registers the current edition. Change will be now compared to this saved state.
     * @param _pair [Pair] to register edition.
     * @return [Pair] for which the change has been made.
     */
    private fun registerEdition(_pair: Pair<TextInputEditText, String>): Pair<TextInputEditText, String> {
        var pair = _pair
        if (pair.first != null) {
            val newEditable = pair.first!!.text
            if (newEditable != null) {
                val newText = newEditable.toString()
                pair = Pair(pair.first, newText)
            }
        }
        return pair
    }
}
