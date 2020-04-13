package com.pi.attestation.ui.contribute

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.pi.attestation.R


class ContributeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contribute, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val helpToCode = view.findViewById<MaterialButton>(R.id.helpToCode)
        helpToCode.setOnClickListener {
            val url = "https://github.com/PeredurOmega/FrenchTravelCertificate/" +
                    "blob/master/HELP_TO_CODE.md"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        val helpToDesign = view.findViewById<MaterialButton>(R.id.helpToDesign)
        helpToDesign.setOnClickListener {
            val url = "https://github.com/PeredurOmega/FrenchTravelCertificate/" +
                    "blob/master/HELP_TO_DESIGN.md"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        val helpToTranslate = view.findViewById<MaterialButton>(R.id.helpToTranslate)
        helpToTranslate.setOnClickListener {
            val url = "https://github.com/PeredurOmega/FrenchTravelCertificate/" +
                    "blob/master/HELP_TO_TRANSLATE.md"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }
}
