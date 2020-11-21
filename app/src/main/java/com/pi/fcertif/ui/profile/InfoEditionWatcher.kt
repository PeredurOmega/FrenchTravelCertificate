package com.pi.fcertif.ui.profile

import android.app.DatePickerDialog
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import androidx.core.util.Pair
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pi.fcertif.R
import com.pi.fcertif.objects.UserInfo
import com.pi.fcertif.ui.tools.DateEditTextFormatter
import com.pi.fcertif.ui.tools.DateValidator
import com.pi.fcertif.ui.tools.EditedListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Toolkit class which provides useful tools to monitor the state of the user's information edition
 * and implements [EditedListener].
 */
class InfoEditionWatcher : EditedListener {

    /**
     * @see UserInfoBuilder
     */
    private var userInfoBuilder: UserInfoBuilder? = null

    /**
     * Builds the [InfoEditionWatcher] by binding the view to the info.
     * @param view [View] currently displayed in the [androidx.fragment.app.Fragment].
     * @param myInfo [UserInfo] to bind to the view. Contains all the user's info.
     * @param multipleProfiles [Boolean] True if we should display the feature to set this profile
     * as a main profile and false otherwise.
     */
    fun build(view: View, myInfo: UserInfo, multipleProfiles: Boolean) {
        val mainProfile: SwitchMaterial = view.findViewById(R.id.mainProfile)
        if (!multipleProfiles) mainProfile.visibility = View.GONE
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

        mainProfile.isChecked = myInfo.id == "0"

        userInfoBuilder = UserInfoBuilder(
            Pair(mainProfile, myInfo.id),
            Pair(firstNameEditText, myInfo.firstName),
            Pair(lastNameEditText, myInfo.lastName),
            Pair(birthDateEditText, birthDate),
            Pair(birthPlaceEditText, myInfo.birthPlace),
            Pair(addressEditText, myInfo.address),
            Pair(cityEditText, myInfo.city),
            Pair(postalCodeEditText, myInfo.postalCode),
        )
    }

    /**
     * Returns the info of the user that have been edited. If a field is incomplete an error will
     * be displayed on it.
     * @return [UserInfo] containing all the new info of the user.
     */
    val info: UserInfo?
        get() {
            return userInfoBuilder?.buildUserInfo()
        }

    override fun hasBeenEdited(): Boolean {
        return userInfoBuilder?.hasBeenEdited() == true
    }

    override fun registerEdition() {
        userInfoBuilder?.registerEdition()
    }

    /**
     * Sets up the birth date [TextInputEditText] and [TextInputLayout] and enables input with a
     * [DatePickerDialog] in case of a click from the user on the end icon.
     * @param birthDateEditText [TextInputEditText] where is written the birth date of the user.
     * @param birthDateField [TextInputLayout] containing the [TextInputEditText] where is written
     * the birth date of the user.
     * @param birthDate [String?] of the currently registered birth date of the user.
     */
    private fun setUpBirthDate(
        birthDateEditText: TextInputEditText,
        birthDateField: TextInputLayout,
        birthDate: String?
    ): String? {
        var originalDate = birthDate
        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault()
        )
        val birthCalendar = Calendar.getInstance(Locale.FRANCE)

        if (birthDate != null) {
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

        val tomorrow = Calendar.getInstance(Locale.FRANCE)
        tomorrow.set(Calendar.HOUR_OF_DAY, 23)
        tomorrow.set(Calendar.MINUTE, 59)
        tomorrow.set(Calendar.SECOND, 59)
        tomorrow.set(Calendar.MILLISECOND, 99)

        val dateValidator = object : DateValidator {
            override fun isValid(calendar: Calendar): Int? {
                return when {
                    calendar > tomorrow -> R.string.date_after_today
                    calendar.get(Calendar.YEAR) < 1900 -> R.string.date_can_not_be_before_1900
                    else -> null
                }
            }
        }

        birthDateField.setEndIconOnClickListener {
            val currentWrittenDate = birthDateEditText.text
            if (currentWrittenDate != null) {
                try {
                    val newDate = dateFormat.parse(currentWrittenDate.toString())
                    if (newDate != null) {
                        birthCalendar.timeInMillis = newDate.time
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            val dateListener =
                DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int,
                                                     dayOfMonth: Int ->
                    birthCalendar.set(Calendar.YEAR, year)
                    birthCalendar.set(Calendar.MONTH, monthOfYear)
                    birthCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    birthDateEditText.setText(dateFormat.format(birthCalendar.time))
                }

            val context = it.context
            if (context != null) {
                val datePickerDialog = DatePickerDialog(
                    context, R.style.TimePickerTheme,
                    dateListener, birthCalendar.get(Calendar.YEAR),
                    birthCalendar.get(Calendar.MONTH), birthCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
                val datePicker = datePickerDialog.datePicker
                datePicker.firstDayOfWeek = Calendar.MONDAY
                val in1900 = Calendar.getInstance(Locale.FRANCE)
                in1900.set(Calendar.YEAR, 1900)
                in1900.set(Calendar.MONTH, 0)
                in1900.set(Calendar.DAY_OF_MONTH, 1)
                in1900.set(Calendar.HOUR_OF_DAY, 0)
                in1900.set(Calendar.MINUTE, 0)
                in1900.set(Calendar.SECOND, 0)
                in1900.set(Calendar.MILLISECOND, 0)
                datePicker.maxDate = tomorrow.timeInMillis
                datePicker.minDate = in1900.timeInMillis
            }
        }

        DateEditTextFormatter(
            birthDateEditText, originalDate ?: "", birthDateField,
            dateValidator
        )
        return originalDate
    }
}