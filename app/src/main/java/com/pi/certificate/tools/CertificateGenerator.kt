package com.pi.certificate.tools

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.Toast
import com.pi.certificate.R
import com.pi.certificate.objects.Certificate
import com.pi.certificate.ui.creator.filler.LoadingDialog
import com.pi.certificate.ui.viewer.CertificateViewerActivity
import java.io.File
import java.lang.ref.WeakReference

/**
 * [AsyncTask] Generating a certificate in a background thread. On pre execution this class shows a
 * [LoadingDialog] and on post execution this class hides its [LoadingDialog] and launches the
 * [CertificateViewerActivity]. The generated certificate is stored in a pdf file that contains two
 * filled pages with the second one containing a "big" QR Code. When the pdf file is generated the
 * certificate is auto added to the json file containing all the certificates.
 * @param mContext [Context] used to display the [LoadingDialog] and retrieve the cache dir.
 * @param certificate [Certificate] to use to generate its bound pdf.
 * @param addCertificate [Boolean] True if we want to add the certificate in the json file,
 * false otherwise.
 * @see PdfCreator
 */
class CertificateGenerator(mContext: Context, private val certificate: Certificate,
                           private val addCertificate: Boolean) :
    AsyncTask<Void, Void, String?>() {

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

    override fun doInBackground(vararg params: Void): String? {
        val context = context.get() ?: return null

        val originalCertificateName = "french_certificate.pdf"
        val cacheDir = context.cacheDir
        val originalCertificate = File(cacheDir, originalCertificateName)

        if(!originalCertificate.exists()){
            context.assets.open(originalCertificateName).use { asset ->
                originalCertificate.writeBytes(asset.readBytes())
            }
        }

        val newFileName = PdfCreator(cacheDir, originalCertificate).generatePdf(certificate)

        if(addCertificate){
            CertificatesManager(context.filesDir).addCertificate(certificate, 0)
        }

        return newFileName
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        val context = context.get()

        if(context != null && result != null){
            val intent = Intent(context, CertificateViewerActivity::class.java)
            intent.putExtra(CertificateViewerActivity.FILE_PATH, result)
            context.startActivity(intent)
            /**
             * The [LoadingDialog] is auto dismissed when a new activity is launched.
             */
        }else{
            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
            loadingDialog?.dismiss()
        }
    }
}