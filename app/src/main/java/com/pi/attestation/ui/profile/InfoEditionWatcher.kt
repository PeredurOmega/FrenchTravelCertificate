package com.pi.attestation.ui.profile

import android.os.Parcel
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
import com.pi.attestation.ui.tools.DateEditTextFormatter
import com.pi.attestation.ui.tools.DateValidator
import com.pi.attestation.ui.tools.EditedListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Toolkit class which provides useful tools to monitor the state of the user's information edition
 * and implements [EditedListener].
 */
class InfoEditionWatcher internal constructor(private val fragmentActivity: FragmentActivity) :
    EditedListener {

    /**
     * @see UserInfoBuilder
     */
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
     * [MaterialDatePicker].
     * @param birthDateEditText [TextInputEditText] where is written the birth date of the user.
     * @param birthDateField [TextInputLayout] containing the [TextInputEditText] where is written
     * the birth date of the user.
     * @param birthDate [String?] of the currently registered birth date of the user.
     */
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

        val tomorrow = Calendar.getInstance()
        tomorrow.set(Calendar.HOUR_OF_DAY, 0)
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)

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
            val builder = MaterialDatePicker.Builder.datePicker()
            val constraintsBuilder = CalendarConstraints.Builder()
            val nowSelection = birthCalendar.time.time
            constraintsBuilder.setEnd(tomorrow.timeInMillis)
            constraintsBuilder.setOpenAt(nowSelection)
            constraintsBuilder.setValidator(object : CalendarConstraints.DateValidator{

                override fun writeToParcel(dest: Parcel?, flags: Int) {}

                override fun isValid(date: Long): Boolean {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = date
                    return dateValidator.isValid(calendar) == null
                }

                override fun describeContents(): Int {
                    return 0
                }
            })
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


        DateEditTextFormatter(birthDateEditText, originalDate ?: "", birthDateField,
            dateValidator)
        return originalDate
    }
}