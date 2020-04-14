package com.pi.attestation.ui.creator.filler

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.pi.attestation.R

/**
 * [Dialog] that should be displayed while we are loading content.
 */
class LoadingDialog(context: Context, cancelable: Boolean,
                    cancelListener: DialogInterface.OnCancelListener?)
    : Dialog(context, cancelable, cancelListener) {

    init {
        val titleLL = LinearLayout(context)
        val titleLLParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        titleLL.layoutParams = titleLLParams
        titleLL.setPadding(20, 20, 20, 20)
        titleLL.orientation = LinearLayout.VERTICAL
        val tv = TextView(context)
        tv.text = context.getString(R.string.loading_please_wait)
        tv.textSize = 20f
        titleLL.addView(tv)
        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(20, 20, 20, 20)
        titleLL.addView(progressBar)
        this.setContentView(titleLL)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(this.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        this.window!!.attributes = lp
    }
}
