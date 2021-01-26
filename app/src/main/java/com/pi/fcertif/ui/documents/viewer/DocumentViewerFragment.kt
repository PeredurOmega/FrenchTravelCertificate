package com.pi.fcertif.ui.documents.viewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
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
import com.pi.fcertif.ui.viewer.CertificateViewerActivity.Companion.FILE_PATH
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


/**
 * [Fragment] used to display one page of a pdf.
 */
class DocumentViewerFragment : Fragment() {

    /**
     * [String] File path of the pdf to display.
     */
    private lateinit var filePath: String

    /**
     * [Int] Representing the number of the page to display (starting from 0).
     */
    private var page: Int? = null

    companion object {

        /**
         * Key value for retrieving [PdfViewerFragment#page]
         */
        private const val PAGE = "PAGE"

        /**
         * Creates a new instance of [DocumentViewerFragment] with the file path of the pdf to display
         * and the number of the page to display (starting from 0). Those parameters will be "saved"
         * as arguments (enabling to retain them even in case of screen rotation).
         * @param filePath [String] File path of the pdf to display.
         * @param page [Int] Number of the page to display (starting from 0).
         * @return
         */
        fun newInstance(filePath: String, page: Int): DocumentViewerFragment {
            val args = Bundle()
            args.putString(FILE_PATH, filePath)
            args.putInt(PAGE, page)
            val fragment = DocumentViewerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePath = arguments?.getString(FILE_PATH).toString()
        page = arguments?.getInt(PAGE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pdf_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return
        val dirFile = context.filesDir
        val file = File(dirFile, filePath)

        if (page != null) {
            try {
                val pdfImageView = view.findViewById<PhotoView>(R.id.pdfView)
                if (file.extension == "pdf") {
                    val fileDescriptor =
                        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).also {
                            val pdfRenderer = PdfRenderer(it)
                            val page = pdfRenderer.openPage(page!!)
                            val density = resources.displayMetrics.density
                            val bitmap = Bitmap.createBitmap(
                                (page.width * density).toInt(),
                                (page.height * density).toInt(), Bitmap.Config.ARGB_8888
                            )
                            page.render(
                                bitmap,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )
                            val newBitmap = Bitmap.createBitmap(
                                bitmap.width,
                                bitmap.height, bitmap.config
                            )
                            val canvas = Canvas(newBitmap)
                            canvas.drawColor(Color.WHITE)
                            canvas.drawBitmap(bitmap, 0f, 0f, null)
                            pdfImageView.setImageBitmap(newBitmap)
                        }
                    fileDescriptor.close()
                } else pdfImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))


                pdfImageView.post {
                    pdfImageView.scaleType = ImageView.ScaleType.FIT_START
                }
            } catch (e: FileNotFoundException) {
                raiseError(context)
            } catch (e: IOException) {
                raiseError(context)
            }
        }
    }

    private fun raiseError(context: Context) {
        Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
        activity?.onBackPressed()
    }
}