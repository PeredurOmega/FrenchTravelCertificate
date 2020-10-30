package com.pi.fcertif.ui.tools

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pi.fcertif.R

/**
 * Utility class used to simplify inputs for times.
 * @param editText [TextInputEditText] to which we have to set the [TextWatcher].
 * @param original [String] Text that was in this edit text in the first place.
 * @param textInputLayout [TextInputLayout] to use to throw malformation errors.
 */
class TimeEditTextFormatter(private val editText: TextInputEditText, original: String,
                            private val textInputLayout : TextInputLayout) : TextWatcher{

    /**
     * Current text in the edit text.
     */
    private var current = original

    /**
     * Format to use for the date.
     */
    private var format = editText.resources.getString(R.string.time_format)

    init {
        editText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString() != current) {
            var clean: String = s.toString().replace("[^\\d.]|:.".toRegex(), "")
            val cleanC = current.replace("[^\\d.]|:.".toRegex(), "")
            val cl = clean.length
            var sel = cl
            var i = 2
            while (i <= cl && i < 3) {
                sel++
                i += 2
            }
            if (clean == cleanC) sel--

            var malformed = 0

            if(clean.length >= 2){
                val hours = clean.substring(0, 2).toInt()
                val sHours = if (hours >= 24 || hours < 0){
                    malformed = R.string.hours_between_0_23
                    format.substring(0, 2)
                } else clean.substring(0, 2)
                clean = sHours + clean.substring(2)
            }

            if(clean.length == 4){
                val minutes = clean.substring(2, 4).toInt()
                val sMinutes = if (minutes < 0 || minutes >= 60){
                    malformed = R.string.minutes_between_0_59
                    format.substring(2, 4)
                } else clean.substring(2, 4)
                clean = clean.substring(0, 2) + sMinutes
            }else if(clean.length < 4){
                clean += format.substring(clean.length)
            }

            clean = String.format("%s:%s", clean.substring(0, 2), clean.substring(2, 4))
            sel = if (sel < 0) 0 else sel
            current = clean
            editText.setText(current)
            editText.setSelection(if (sel < current.length) sel else current.length)
            if(malformed != 0) textInputLayout.error = textInputLayout.resources.getString(malformed)
            else textInputLayout.error = ""
        }
    }
}