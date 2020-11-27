package com.pi.fcertif.ui.contribute

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.pi.fcertif.R
import java.lang.Exception


/**
 * [Fragment] used to invite people to contribute to this project by helping for translating, for
 * designing and for coding.
 */
class ContributeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contribute, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val helpDir = "https://github.com/PeredurOmega/FrenchTravelCertificate/blob/master/info/"

        val helpToCode = view.findViewById<MaterialButton>(R.id.helpToCode)
        helpToCode.setOnClickListener {
            openURL(helpDir + "HELP_TO_CODE.md")
        }

        val helpToDesign = view.findViewById<MaterialButton>(R.id.helpToDesign)
        helpToDesign.setOnClickListener {
            openURL(helpDir + "HELP_TO_DESIGN.md")
        }

        val helpToTranslate = view.findViewById<MaterialButton>(R.id.helpToTranslate)
        helpToTranslate.setOnClickListener {
            openURL(helpDir + "HELP_TO_TRANSLATE.md")
        }

        val githubLabMILink = view.findViewById<TextView>(R.id.githubLabMILink)
        val linkName = getString(R.string.github_lab_mi_link)
        val spannableString = SpannableString(linkName)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                openURL("https://www.tiny.cc/prjcnz")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(
            clickableSpan,
            0,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        githubLabMILink.text = spannableString
    }

    /**
     * Opens the provided link into a web browser or into a dedicated app.
     * @param link [String] to open into a web browser.
     */
    private fun openURL(link: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        try {
            activity?.startActivity(i)
        } catch (e: Exception) {
            activity?.let {
                Toast.makeText(it, it.getString(R.string.no_app_to_open_the_link), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
