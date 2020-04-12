package com.pi.attestation.ui.profile

import android.text.format.DateFormat
import android.view.View
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pi.attestation.R
import com.pi.attestation.objects.UserInfo
import com.pi.attestation.ui.tools.EditedListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Toolkit class which provides useful tools to monitor the state of the user's information edition.
 */
class InfoEditionWatcher internal constructor(private val fragmentActivity: FragmentActivity) :
    EditedListener {

    private var userInfoBuilder: UserInfoBuilder? = null

    /**
     * Builds the [InfoEditionWatcher] by binding the view to the info.
     * @param view [View] currently displayed in the [androidx.fragment.app.Fragment].
     * @param myInfo [UserInfo] to bind to the view. Contains all the user's info.
     */
    fun build(view: View, myInfo: UserInfo) {
        val firstNameEditText: TextInputEditText = view.findViewById(R.id.firstNameEditText)
        val lastNameEditText: TextInputEditText = view.findViewById(R.id.lastNameEditText)
        val birthDateEditText: TextInputEditText = view.findViewById(R.id.birthDateEditText)
        val birthPlaceEditText: TextInputEditText = view.findViewById(R.id.birthPlaceEditText)
        val addressEditText: TextInputEditText = view.findViewById(R.id.addressEditText)
        val cityEditText: TextInputEditText = view.findViewById(R.id.cityEditText)
        val postalCodeEditText: TextInputEditText = view.findViewById(R.id.postalCodeEditText)

        val birthDateField: TextInputLayout = view.findViewById(R.id.birthDateField)
        val originalBirthDate = myInfo.birthDate

        val birthDate = setUpBirthDate(birthDateEditText, birthDateField, originalBirthDate)

        userInfoBuilder = UserInfoBuilder(
            Pair(firstNameEditText, myInfo.firstName),
            Pair(lastNameEditText, myInfo.lastName),
            Pair(birthDateEditText, birthDate),
            Pair(birthPlaceEditText, myInfo.birthPlace),
            Pair(addressEditText, myInfo.address),
            Pair(cityEditText, myInfo.city),
            Pair(postalCodeEditText, myInfo.postalCode))
    }

    /**
     * Returns the info of the user that have been edited.
     * @return [UserInfo] containing all the new info of the user (only the ones that have been edited by the user).
     */
    val info: UserInfo?
        get() {
            return userInfoBuilder?.buildUserInfoInput()
        }

    override fun hasBeenEdited(): Boolean {
        return userInfoBuilder?.hasBeenEdited() == true
    }

    override fun registerEdition() {
        userInfoBuilder?.registerEdition()
    }

    private fun setUpBirthDate(birthDateEditText: TextInputEditText,
                               birthDateField: TextInputLayout,
                               birthDate: String?): String? {
        var originalDate = birthDate
        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault())
        val birthCalendar = Calendar.getInstance()

        if(birthDate != null){
            try {
                val date = dateFormat.parse(birthDate)
                if (date != null) {
                    birthCalendar.time = date
                    originalDate = dateFormat.format(date)
                    birthDateEditText.setText(originalDate)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        birthDateField.setEndIconOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val constraintsBuilder = CalendarConstraints.Builder()
            val nowSelection = birthCalendar.time.time
            constraintsBuilder.setEnd(Date().time)
            constraintsBuilder.setOpenAt(nowSelection)
            builder.setCalendarConstraints(constraintsBuilder.build())
            builder.setSelection(nowSelection)
            builder.setTitleText(R.string.select_your_birthdate)
            val picker = builder.build()
            picker.addOnPositiveButtonClickListener { selection: Long? ->
                birthCalendar.timeInMillis = selection!!
                birthDateEditText.setText(dateFormat.format(birthCalendar.time))
            }
            picker.show(fragmentActivity.supportFragmentManager, picker.toString())
        }
        return originalDate
    }
}