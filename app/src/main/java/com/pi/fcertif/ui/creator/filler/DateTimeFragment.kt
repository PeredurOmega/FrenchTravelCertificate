package com.pi.fcertif.ui.creator.filler

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pi.fcertif.R
import com.pi.fcertif.objects.Certificate
import com.pi.fcertif.objects.DateTime
import com.pi.fcertif.objects.Reason
import com.pi.fcertif.tools.CertificateGenerator
import com.pi.fcertif.ui.profile.InfoManager
import com.pi.fcertif.ui.tools.DateEditTextFormatter
import com.pi.fcertif.ui.tools.DateValidator
import com.pi.fcertif.ui.tools.TimeEditTextFormatter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * [Fragment] used to indicate the date and the time of the exit considered with the current reason.
 * By default the date will be today. By default the time will be now.
 */
class DateTimeFragment : Fragment() {

    /**
     * Previously selected [Reason].
     */
    private lateinit var reason: Reason

    /**
     * Currently selected date.
     * @see Calendar
     */
    private val date = Calendar.getInstance(Locale.FRANCE)

    companion object {

        /**
         * Key value for retrieving [DateTimeFragment#reason].
         */
        private const val REASON = "REASON"

        /**
         * Creates a new instance of [DateTimeFragment] with the previously selected [Reason] as an
         * argument (enabling to retain this argument even in case of screen rotation).
         * @param reason [Reason] previously selected.
         * @return New [DateTimeFragment].
         */
        fun newInstance(reason: Reason): DateTimeFragment {
            val args = Bundle()
            args.putSerializable(REASON, reason)
            val fragment = DateTimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reason = arguments?.get(REASON) as Reason
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_date_time_filler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDateField(view)
        setUpTimeField(view)
        setUpCreation(view)
    }

    /**
     * Sets up the create button.
     * @param view [View] where to find needed components for creation.
     */
    private fun setUpCreation(view: View) {
        val useProfile = view.findViewById<SwitchMaterial>(R.id.useProfile)
        val createButton = view.findViewById<MaterialButton>(R.id.createButton)
        useProfile.setOnCheckedChangeListener { _, isChecked ->
            run {
                createButton.setText(
                    if (isChecked) R.string.create_certificate
                    else R.string.fill_certificate
                )
            }
        }

        createButton.setOnClickListener { tryToCreateCertificate(useProfile.isChecked, view) }
    }

    /**
     * Tries to create a certificate. If information are malformed or missing a [Toast] will be
     * displayed to the user.
     * @param useProfile [Boolean] True if we should use the user's profile (registered information)
     * or false if we should create a new temporary profile just for this certificate.
     * @param view [View] where to find needed components for creation.
     */
    private fun tryToCreateCertificate(useProfile: Boolean, view: View) {
        val exitDateEditText = view.findViewById<TextInputEditText>(R.id.exitDateEditText)
        val exitTimeEditText = view.findViewById<TextInputEditText>(R.id.exitTimeEditText)

        val exitDate: String? = exitDateEditText.text.toString()
        val exitTime: String? = exitTimeEditText.text.toString()

        if (!exitDate.isNullOrEmpty() && !exitTime.isNullOrEmpty()) {
            val exitDateTime = DateTime(exitDate, exitTime)
            if (exitDateTime.isMalformed()) {
                Toast.makeText(context, R.string.date_time_match_error, Toast.LENGTH_SHORT).show()
            } else {
                if (useProfile) createCertificate(exitDateTime, view.context)
                else goFillCertificate(exitDateTime)
            }
        } else Toast.makeText(context, R.string.please_fill_date_time, Toast.LENGTH_SHORT).show()
    }

    /**
     * Navigates to [InfoFragment] in order to ask the user for the info related to this
     * certificate. This method is only called when the user doesn't want to use its registered
     * profile's info.
     * @param exitDateTime [DateTime] representing the date and the time of the exit considered in
     * this certificate.
     */
    private fun goFillCertificate(exitDateTime: DateTime) {
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, InfoFragment.newInstance(reason, exitDateTime))
        transaction.commit()
    }

    /**
     * Creates a certificate. This method is only called when the user wants to use its registered
     * profile's info.
     * @param exitDateTime [DateTime] representing the date and the time of the exit considered in
     * this certificate.
     * @param context [Context] used to generate a certificate with [CertificateGenerator].
     * @see [CertificateGenerator]
     */
    private fun createCertificate(exitDateTime: DateTime, context: Context) {
        val timeFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "HH mm"),
            Locale.getDefault()
        )

        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault()
        )

        val certificate = Certificate(
            DateTime(dateFormat.format(Date()), timeFormat.format(Date())),
            InfoManager(context).retrieveUserInfo(), exitDateTime, reason
        )

        CertificateGenerator(context, certificate, true).execute()
    }

    /**
     * Sets up the time [TextInputEditText] and [TextInputLayout] with their [TimePickerDialog].
     * @param view [View] where to find the time [TextInputEditText] and the time [TextInputLayout].
     * @see DateTimeFragment.setUpDateField
     */
    private fun setUpTimeField(view: View) {
        val exitTimeEditText = view.findViewById<TextInputEditText>(R.id.exitTimeEditText)
        val exitTimeField = view.findViewById<TextInputLayout>(R.id.exitTimeField)

        val timeFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "HH mm"),
            Locale.getDefault()
        )

        val now = timeFormat.format(date.time)
        exitTimeEditText.setText(now)

        exitTimeField.setEndIconOnClickListener {
            val timePicker = TimePickerDialog(
                context, R.style.TimePickerTheme,
                { _, selectedHour, selectedMinute ->
                    date.set(Calendar.HOUR_OF_DAY, selectedHour)
                    date.set(Calendar.MINUTE, selectedMinute)
                    exitTimeEditText.setText(timeFormat.format(date.time))
                }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }

        TimeEditTextFormatter(exitTimeEditText, now, exitTimeField)
    }

    /**
     * Sets up the date [TextInputEditText] and [TextInputLayout] with their [DatePickerDialog] in
     * case of a click on the end icon.
     * @param view [View] where to find the date [TextInputEditText] and the date [TextInputLayout].
     * @see DateTimeFragment.setUpDateField
     */
    private fun setUpDateField(view: View) {
        val exitDateEditText = view.findViewById<TextInputEditText>(R.id.exitDateEditText)
        val exitDateField = view.findViewById<TextInputLayout>(R.id.exitDateField)

        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault()
        )

        val now = dateFormat.format(date.time)
        exitDateEditText.setText(now)

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val inOneYear = Calendar.getInstance()
        inOneYear.add(Calendar.YEAR, 1)

        val dateValidator = object : DateValidator {
            override fun isValid(calendar: Calendar): Int? {
                return when {
                    calendar < today -> R.string.date_before_today
                    calendar > inOneYear -> R.string.date_can_not_be_in_more_than_one_year
                    else -> null
                }
            }
        }

        exitDateField.setEndIconOnClickListener {
            val currentWrittenDate = exitDateEditText.text
            if (currentWrittenDate != null) {
                try {
                    val newDate = dateFormat.parse(currentWrittenDate.toString())
                    if (newDate != null) {
                        date.timeInMillis = newDate.time
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            val dateListener = OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int,
                                                   dayOfMonth: Int ->
                date.set(Calendar.YEAR, year)
                date.set(Calendar.MONTH, monthOfYear)
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                exitDateEditText.setText(dateFormat.format(date.time))
            }

            val context = it.context
            if (context != null) {
                val datePickerDialog = DatePickerDialog(
                    context, R.style.TimePickerTheme,
                    dateListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
                val datePicker = datePickerDialog.datePicker
                datePicker.firstDayOfWeek = Calendar.MONDAY
                datePicker.maxDate = inOneYear.timeInMillis
                datePicker.minDate = Calendar.getInstance(Locale.FRANCE).timeInMillis
            }
        }

        DateEditTextFormatter(exitDateEditText, now ?: "", exitDateField, dateValidator)
    }
}