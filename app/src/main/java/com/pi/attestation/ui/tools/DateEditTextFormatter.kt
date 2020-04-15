package com.pi.attestation.ui.tools

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pi.attestation.R
import java.util.*

/**
 * Utility class used to simplify inputs for dates.
 * @param editText [TextInputEditText] to which we have to set the [TextWatcher].
 * @param original [String] Text that was in this edit text in the first place.
 * @param textInputLayout [TextInputLayout] to use to throw malformation errors.
 * @param canNotBeAfterToday [Boolean] True if the date in the provided edit text can not be after
 * today, false otherwise.
 */
class DateEditTextFormatter(private val editText: TextInputEditText, original: String,
                            private val textInputLayout : TextInputLayout,
                            private val canNotBeAfterToday : Boolean) : TextWatcher{
    //TODO CHANGE canNotBeAfterToday TO CONSTRAINTS VALIDATOR

    /**
     * Current text in the edit text.
     */
    private var current = original

    /**
     * Current date provided by the edit text.
     */
    private val cal = Calendar.getInstance()

    /**
     * Format to use for the date.
     */
    private var format = editText.resources.getString(R.string.date_format)

    init {
        editText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString() != current) {
            var clean: String = s.toString().replace("[^\\d.]|\\.".toRegex(), "")
            val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")
            val cl = clean.length
            var sel = cl
            var i = 2
            while (i <= cl && i < 6) {
                sel++
                i += 2
            }
            if (clean == cleanC) sel--

            var malformed = 0

            if(clean.length >= 2){
                val day = clean.substring(0, 2).toInt()
                val sDay = if (day > 31 || day < 1){
                    malformed = R.string.day_between_1_31
                    format.substring(0, 2)
                } else clean.substring(0, 2)
                clean = sDay + clean.substring(2)
            }

            if(clean.length >= 4){
                val month = clean.substring(2, 4).toInt()
                val sMonth = if (month < 1 || month > 12){
                    malformed = R.string.month_between_1_12
                    format.substring(2, 4)
                } else clean.substring(2, 4)
                cal[Calendar.MONTH] = month - 1
                clean = clean.substring(0, 2) + sMonth + clean.substring(4)
            }

            if(clean.length == 8){
                val year = clean.substring(4, 8).toInt()
                val sYear = when {
                    year < 1900 -> {
                        malformed = R.string.date_can_not_be_before_1900
                        format.substring(4, 8)
                    }
                    year > 2025 ->{
                        malformed = if(canNotBeAfterToday) R.string.date_after_today
                                    else R.string.date_can_not_be_after_2025
                        format.substring(4, 8)
                    }
                    else -> {
                        cal[Calendar.YEAR] = year
                        clean.substring(4, 8)
                    }
                }

                clean = clean.substring(0, 4) + sYear
                if(malformed == 0){
                    val day = clean.substring(0, 2).toInt()
                    val sDay = if (day > cal.getActualMaximum(Calendar.DATE) || day < 1){
                        malformed = R.string.nonexistent_date
                        format.substring(0, 2)
                    } else {
                        cal[Calendar.DAY_OF_MONTH] = day
                        day.toString()
                    }
                    if(canNotBeAfterToday && cal.after(Calendar.getInstance())){
                        malformed = R.string.date_after_today
                    }
                    clean = sDay + clean.substring(2)
                }
            }else if (clean.length < 8) {
                clean += format.substring(clean.length)
            }

            clean = String.format("%s/%s/%s", clean.substring(0, 2), clean.substring(2, 4),
                clean.substring(4, 8))
            sel = if (sel < 0) 0 else sel
            current = clean
            editText.setText(current)
            editText.setSelection(if (sel < current.length) sel else current.length)
            if(malformed != 0) textInputLayout.error = textInputLayout.resources.getString(malformed)
            else textInputLayout.error = ""
        }
    }
}