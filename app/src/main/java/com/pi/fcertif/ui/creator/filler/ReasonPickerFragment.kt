package com.pi.fcertif.ui.creator.filler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.pi.fcertif.R
import com.pi.fcertif.objects.Reason

/**
 * [Fragment] used to show [com.pi.fcertif.objects.Reasons] from which the user should choose.
 */
@Suppress("unused")
class ReasonPickerFragment : Fragment(), ReasonListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reasons_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.reasonsRecyclerView)
        (recyclerView.itemAnimator as SimpleItemAnimator?)?.supportsChangeAnimations = false
        recyclerView.adapter = ReasonsAdapter(resources, this)
        recyclerView.setHasFixedSize(true)
    }

    override fun pick(reason: Reason) {
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, DateTimeFragment.newInstance(reason))
        transaction.commit()
    }

    override fun onDetailsOpened(position: Int) {
        val view = view
        if (view != null) {
            val reasonsRecyclerView = view.findViewById<RecyclerView>(R.id.reasonsRecyclerView)
            reasonsRecyclerView.smoothScrollToPosition(position)
        }
    }
}