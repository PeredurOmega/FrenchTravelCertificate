package com.pi.fcertif.ui.documents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.MainActivity.Companion.REQUEST_FILE
import com.pi.fcertif.R

/**
 * [RecyclerView.ViewHolder] used to create a basic import custom document "card" for the [RecyclerView]
 * in [DocumentsFragment].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 */
class ImportCustomDocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * [TextView] where we set the text for creating a new profile.
     */
    private val createText: TextView = itemView.findViewById(R.id.createText)

    init {
        createText.setText(R.string.import_a_new_document)
        itemView.setOnClickListener {
            selectImage(it.context)
        }
    }

    private fun selectImage(context: Context) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
        }
        val mimeTypes = arrayOf("image/*", "application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        if (context is Activity) {
            startActivityForResult(context, intent, REQUEST_FILE, null)
        }
    }
}