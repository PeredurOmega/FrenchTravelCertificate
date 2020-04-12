package com.pi.attestation.ui.profile

import android.text.TextUtils
import androidx.core.util.Pair
import com.google.android.material.textfield.TextInputEditText
import com.pi.attestation.objects.UserInfo

/**
 * Toolkit class used for displaying user's info.
 */
internal class UserInfoBuilder(
    private var firstNameEditText: Pair<TextInputEditText?, String>,
    private var lastNameEditText: Pair<TextInputEditText?, String>,
    private var birthDateEditText: Pair<TextInputEditText?, String>,
    private var birthPlaceEditText: Pair<TextInputEditText?, String>,
    private var addressEditText: Pair<TextInputEditText?, String>,
    private var cityEditText: Pair<TextInputEditText?, String>,
    private var postalCodeEditText: Pair<TextInputEditText?, String>) {

    private fun setUpTextInfo() {
        setOriginalText(firstNameEditText)
        setOriginalText(lastNameEditText)
        setOriginalText(birthDateEditText)
        setOriginalText(birthPlaceEditText)
        setOriginalText(addressEditText)
        setOriginalText(cityEditText)
        setOriginalText(postalCodeEditText)
    }

    private fun setOriginalText(pair: Pair<TextInputEditText?, String>) {
        if (pair.first != null) pair.first!!.setText(pair.second)
    }

    private fun fieldEdited(pair: Pair<TextInputEditText?, String>): Boolean {
        if (pair.first != null) {
            val editable = pair.first!!.text
            return if (editable != null) {
                if (TextUtils.isEmpty(pair.second) && TextUtils.isEmpty(editable.toString())) false else editable.toString() != pair.second
            } else !TextUtils.isEmpty(pair.second)
        }
        return false
    }

    /**
     * Returns a [UserInfo] containing only edited info. Other fields are null.
     * @return [UserInfo] containing only edited info. Other fields are left null.
     */
    fun buildUserInfoInput(): UserInfo {
        return UserInfo(firstNameEditText.getText(), lastNameEditText.getText(),
            birthDateEditText.getText(), birthPlaceEditText.getText(), addressEditText.getText(),
            cityEditText.getText(), postalCodeEditText.getText())
    }

    private fun Pair<TextInputEditText?, String>.getText() : String{
        return this.first?.text.toString()
    }

    /**
     * Checks whether or not the current info has been updated / edited.
     * @return True if one of the fields has been edited, false otherwise.
     */
    fun hasBeenEdited(): Boolean {
        return fieldEdited(firstNameEditText) || fieldEdited(lastNameEditText) ||
                fieldEdited(birthDateEditText) || fieldEdited(birthPlaceEditText) ||
                fieldEdited(addressEditText) || fieldEdited(cityEditText)
                || fieldEdited(postalCodeEditText)
    }

    /**
     * Registers all current changes. All the current fields will be registered and if you call [.hasBeenEdited] just after this method,
     * the result should be false.
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


    private fun registerEdition(_pair: Pair<TextInputEditText?, String>): Pair<TextInputEditText?, String> {
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

    init {
        setUpTextInfo()
    }
}
