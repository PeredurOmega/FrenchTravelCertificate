package com.pi.fcertif.tools

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.pi.fcertif.R
import com.pi.fcertif.objects.Certificate
import com.pi.fcertif.ui.creator.filler.LoadingDialog
import com.pi.fcertif.ui.viewer.CertificateViewerActivity
import java.io.File
import java.lang.ref.WeakReference

/**
 * [AsyncTask] Generating certificates in a background thread. On pre execution this class shows a
 * [LoadingDialog] and on post execution this class hides its [LoadingDialog] and launches the
 * [CertificateViewerActivity]. Generated certificates are stored in a pdf file that contains two
 * filled pages with the second one containing a "big" QR Code. When pdf files are generated
 * certificates are not auto added to the json file containing all the certificates. Should only be
 * used when sharing multiple certificates.
 * @param mContext [Context] used to display the [LoadingDialog] and retrieve the cache dir.
 * @param certificates [ArrayList] of [Certificate] to use to generate their bound pdf.
 * @see PdfCreator
 */
class CertificatesGenerator(
    mContext: Context, private val certificates: ArrayList<Certificate>,
    private val generatorListener: GeneratorListener?
) :
    AsyncTask<Void, Void, ArrayList<String?>?>() {

    /**
     * [WeakReference] of the [Context] used to show the [LoadingDialog] and to start an intent.
     */
    private var context: WeakReference<Context> = WeakReference(mContext)

    /**
     * [LoadingDialog] that asks the user to wait while the certificate is generated.
     */
    private var loadingDialog: LoadingDialog? = null

    override fun onPreExecute() {
        super.onPreExecute()
        val context = context.get() ?: return
        loadingDialog = LoadingDialog(context, false, null)
        loadingDialog?.show()
    }

    override fun doInBackground(vararg params: Void): ArrayList<String?>? {
        val context = context.get() ?: return null

        val originalCertificateName = "french_certificate_3.pdf"
        val cacheDir = context.cacheDir
        val originalCertificate = File(cacheDir, originalCertificateName)

        if (!originalCertificate.exists()) {
            context.assets.open(originalCertificateName).use { asset ->
                originalCertificate.writeBytes(asset.readBytes())
            }
            Thread.sleep(500)
        }

        val newPdfFileNames = ArrayList<String?>()

        val pdfCreator = PdfCreator(cacheDir, originalCertificate)

        for (certificate in certificates) {
            val newFileName = pdfCreator.generatePdf(certificate)
            newPdfFileNames.add(newFileName)
        }

        return newPdfFileNames
    }

    override fun onPostExecute(result: ArrayList<String?>?) {
        super.onPostExecute(result)
        val context = context.get()

        loadingDialog?.dismiss()

        if (context != null && result != null) generatorListener?.onGenerated()
        else Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
    }
}