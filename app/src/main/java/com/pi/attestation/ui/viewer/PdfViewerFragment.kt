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
import com.pi.attestation.ui.viewer.CertificateViewerActivity.Companion.FILE_PATH
import java.io.File

/**
 * [Fragment] used to display one page of a pdf.
 */
class PdfViewerFragment : Fragment(){

    /**
     * [String] File path of the pdf to display.
     */
    private lateinit var filePath: String

    /**
     * [Int] Representing the number of the page to display (starting from 0).
     */
    private var page: Int? = null

    companion object{

        /**
         * Key value for retrieving [PdfViewerFragment#page]
         */
        private const val PAGE = "PAGE"

        /**
         * Creates a new instance of [PdfViewerFragment] with the file path of the pdf to display
         * and the number of the page to display (starting from 0). Those parameters will be "saved"
         * as arguments (enabling to retain them even in case of screen rotation).
         * @param filePath [String] File path of the pdf to display.
         * @param page [Int] Number of the page to display (starting from 0).
         * @return
         */
        fun newInstance(filePath: String, page: Int): PdfViewerFragment{
            val args = Bundle()
            args.putString(FILE_PATH, filePath)
            args.putInt(PAGE, page)
            val fragment = PdfViewerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePath = arguments?.getString(FILE_PATH).toString()
        page = arguments?.getInt(PAGE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pdf_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = File(context?.cacheDir, filePath)

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