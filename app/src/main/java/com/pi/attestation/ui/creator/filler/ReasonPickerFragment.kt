package com.pi.attestation.ui.creator.filler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.objects.Reason

@Suppress("unused")
class ReasonPickerFragment : Fragment(), ReasonListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reasons_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view as RecyclerView
        recyclerView.adapter = ReasonsAdapter(resources, this)
        recyclerView.setHasFixedSize(true)
    }

    override fun pick(reason: Reason) {
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, DateTimeFragment.newInstance(reason))
        transaction.commit()
    }
}