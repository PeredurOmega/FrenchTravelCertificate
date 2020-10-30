package com.pi.fcertif.ui.viewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.PhotoView
import com.pi.fcertif.R
import com.pi.fcertif.tools.CertificateGenerator
import com.pi.fcertif.tools.CertificatesManager
import com.pi.fcertif.ui.viewer.CertificateViewerActivity.Companion.FILE_PATH
import java.io.File
import java.io.FileNotFoundException


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

        val context = context ?: return
        val dirFile = context.cacheDir
        val file = File(dirFile, filePath)

        if(page != null){
            try {
                val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).also {
                    val pdfRenderer = PdfRenderer(it)
                    val page = pdfRenderer.openPage(page!!)
                    val pdfImageView = view.findViewById<PhotoView>(R.id.pdfView)
                    val density = resources.displayMetrics.density
                    val bitmap = Bitmap.createBitmap((page.width * density).toInt(),
                        (page.height * density).toInt(), Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    pdfImageView.setImageBitmap(bitmap)
                    pdfImageView.post {
                        pdfImageView.scaleType = ImageView.ScaleType.FIT_START
                    }
                    if(this.page == 1) pdfImageView.setZoomable(false)
                }
                fileDescriptor.close()
            }catch (e: FileNotFoundException){
                val certificate = CertificatesManager(context.filesDir).getCertificate(filePath)
                if(certificate != null) CertificateGenerator(context, certificate, false).execute()
                else{
                    Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }
            }
        }
    }
}