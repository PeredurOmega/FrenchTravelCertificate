package com.pi.attestation.ui.creator.filler

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.objects.DateTime
import com.pi.attestation.objects.Reason
import com.pi.attestation.tools.CertificateGenerator
import com.pi.attestation.ui.profile.InfoManager
import java.text.SimpleDateFormat
import java.util.*


class DateTimeFragment : Fragment() {

    private lateinit var reason: Reason
    private val date = Calendar.getInstance(Locale.getDefault())

    companion object{
        private const val REASON = "REASON"

        fun newInstance(reason: Reason): DateTimeFragment{
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_date_time_filler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDateField(view)
        setUpTimeField(view)
        setUpCreation(view)
    }

    private fun setUpCreation(view: View){
        val useProfile = view.findViewById<SwitchMaterial>(R.id.useProfile)
        val createButton = view.findViewById<MaterialButton>(R.id.createButton)
        useProfile.setOnCheckedChangeListener { _, isChecked ->
            run {
                createButton.setText(if (isChecked) R.string.create_certificate
                                    else R.string.fill_certificate)
            }
        }

        createButton.setOnClickListener { tryToCreateCertificate(useProfile.isChecked, view) }
    }

    private fun tryToCreateCertificate(useProfile: Boolean, view: View){
        val exitDateEditText = view.findViewById<TextInputEditText>(R.id.exitDateEditText)
        val exitTimeEditText = view.findViewById<TextInputEditText>(R.id.exitTimeEditText)

        val exitDate: String? = exitDateEditText.text.toString()
        val exitTime: String? = exitTimeEditText.text.toString()

        if(!exitDate.isNullOrEmpty() && ! exitTime.isNullOrEmpty()){
            val exitDateTime = DateTime(exitDate, exitTime)
            if(exitDateTime.isMalformed()){
                Toast.makeText(context, R.string.date_time_match_error, Toast.LENGTH_SHORT).show()
            }else{
                if(useProfile) createCertificate(exitDateTime, view.context)
                else goFillCertificate(exitDateTime)
            }
        }else Toast.makeText(context, R.string.please_fill_date_time, Toast.LENGTH_SHORT).show()
    }

    private fun goFillCertificate(exitDateTime: DateTime){
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, InfoFragment.newInstance(reason, exitDateTime))
        transaction.commit()
    }

    private fun createCertificate(exitDateTime: DateTime, context: Context){

        val timeFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "HH mm"),
            Locale.getDefault())

        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault())

        val certificate = Certificate(DateTime(dateFormat.format(Date()), timeFormat.format(Date())),
            InfoManager(context).retrieveUserInfo(), exitDateTime, reason)

        CertificateGenerator(context, certificate).execute()
    }

    private fun setUpTimeField(view: View){
        val exitTimeEditText = view.findViewById<TextInputEditText>(R.id.exitTimeEditText)
        val exitTimeField = view.findViewById<TextInputLayout>(R.id.exitTimeField)

        val timeFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "HH mm"),
            Locale.getDefault())
        exitTimeEditText.setText(timeFormat.format(date.time))

        exitTimeField.setEndIconOnClickListener {
            val timePicker: TimePickerDialog
            timePicker = TimePickerDialog(context,
                OnTimeSetListener { _, selectedHour, selectedMinute ->
                    date.set(Calendar.HOUR_OF_DAY, selectedHour)
                    date.set(Calendar.MINUTE, selectedMinute)
                    exitTimeEditText.setText(timeFormat.format(date.time))
                }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }
    }

    private fun setUpDateField(view: View){
        val exitDateEditText = view.findViewById<TextInputEditText>(R.id.exitDateEditText)
        val exitDateField = view.findViewById<TextInputLayout>(R.id.exitDateField)

        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault())
        exitDateEditText.setText(dateFormat.format(date.time))

        exitDateField.setEndIconOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val constraintsBuilder = CalendarConstraints.Builder()
            val nowSelection = date.time.time
            constraintsBuilder.setOpenAt(nowSelection)
            builder.setCalendarConstraints(constraintsBuilder.build())
            builder.setSelection(nowSelection)
            builder.setTitleText(R.string.select_exit_date)
            val picker = builder.build()
            picker.addOnPositiveButtonClickListener { selection: Long? ->
                date.timeInMillis = selection!!
                exitDateEditText.setText(dateFormat.format(date.time))
            }
            picker.show(parentFragmentManager, picker.toString())
        }
    }
}