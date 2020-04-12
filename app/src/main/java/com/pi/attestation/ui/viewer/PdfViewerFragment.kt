package com.pi.attestation.ui.viewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.pi.attestation.R
import com.pi.attestation.ui.viewer.CertificateViewerActivity.Companion.FILE_NAME
import java.io.File

class PdfViewerFragment : Fragment(){

    private lateinit var fileName: String
    private var page: Int? = null

    companion object{
        private const val PAGE = "PAGE"

        fun newInstance(fileName: String, page: Int): PdfViewerFragment{
            val args = Bundle()
            args.putString(FILE_NAME, fileName)
            args.putInt(PAGE, page)
            val fragment = PdfViewerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName = arguments?.getString(FILE_NAME).toString()
        page = arguments?.getInt(PAGE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pdf_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = File(context?.cacheDir, fileName)

        if(page != null){
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).also {
                val pdfRenderer = PdfRenderer(it)
                val page = pdfRenderer.openPage(page!!)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                val pdfImageView : ImageView = view.findViewById(R.id.pdfView)
                pdfImageView.setImageBitmap(bitmap)
            }
            fileDescriptor.close()
        }
    }
}