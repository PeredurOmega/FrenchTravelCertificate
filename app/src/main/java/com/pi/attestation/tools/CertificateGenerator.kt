package com.pi.attestation.tools

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.Toast
import com.itextpdf.text.pdf.BarcodeQRCode
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.qrcode.EncodeHintType
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.ui.creator.filler.LoadingDialog
import com.pi.attestation.ui.viewer.CertificateViewerActivity
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference


/**
 * [AsyncTask] Generating a certificate in a background thread. On pre execution this class shows a
 * [LoadingDialog] and on post execution this class hides its [LoadingDialog] and launches the
 * [CertificateViewerActivity]. The generated certificate is stored in a pdf file that contains two
 * filled pages with the second one containing a "big" QR Code. When the pdf file is generated the
 * certificate is auto added to the json file containing all the certificates.
 */
class CertificateGenerator(mContext: Context, private val certificate: Certificate) :
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

        val userInfo = certificate.userInfo
        val fileName = "french_certificate.pdf"
        val file = File(context.cacheDir, fileName)

        var k = 0
        var fileNew : File
        do {
            val fileNewName = "french_certificate_".plus(certificate.creationDateTime.toString())
                .plus("_").plus(k).plus(".pdf")
            certificate.pdfPath = fileNewName
            fileNew = File(context.cacheDir, fileNewName)
            k++
        }while (fileNew.exists())

        if(!file.exists()){
            context.assets.open(fileName).use { asset ->
                file.writeBytes(asset.readBytes())
            }
        }

        val pdfReader = PdfReader(file.path)
        val pdfStamper = PdfStamper(pdfReader, FileOutputStream(fileNew.path))

        val form = pdfStamper.acroFields
        form.isGenerateAppearances = true
        form.setField("Nom et prénom", userInfo.lastName!!.plus(" ")
            .plus(userInfo.firstName))
        form.setField("Date de naissance", userInfo.birthDate!!)
        form.setField("Lieu de naissance", userInfo.birthPlace!!)
        form.setField("Adresse actuelle", userInfo.address!!.plus(" ")
            .plus(userInfo.postalCode!!).plus(" ").plus(userInfo.city!!))

        when(certificate.reason.id){
            0 -> form.setField("Déplacements entre domicile et travail", "Oui")
            1 -> form.setField("Déplacements achats nécéssaires", "Oui")
            2 -> form.setField("Déplacements brefs (activité physique et animaux)", "Oui")
            3 -> form.setField("Déplacements pour motif familial", "Oui")
            4 -> form.setField("Consultations et soins", "Oui")
            5 -> form.setField("Convcation judiciaire ou administrative", "Oui")
            //TODO CHANGE TO -> JUDICIAL CONVOCATION FOLLOWING
            // (https://github.com/LAB-MI/deplacement-covid-19/issues/89)
            6 -> form.setField("Mission d'intérêt général", "Oui")
        }

        form.setField("Ville", userInfo.city)
        form.setField("Date", certificate.exitDateTime.date)
        form.setField("Heure", certificate.exitDateTime.getHours())
        form.setField("Minute", certificate.exitDateTime.getMinutes())
        form.setField("Signature", userInfo.lastName.plus(" ")
            .plus(userInfo.firstName))

        pdfStamper.setFormFlattening(true)

        pdfStamper.insertPage(pdfReader.numberOfPages + 1,
            pdfReader.getPageSizeWithRotation(1))

        val qrParam: MutableMap<EncodeHintType, Any> = HashMap()
        qrParam[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M

        val pageSize = pdfReader.getPageSize(1)
        val width = pageSize.width
        val height = pageSize.height
        val barcodeQRCode = BarcodeQRCode(certificate.buildData(), width.toInt(),
            width.toInt(), qrParam)
        val image = barcodeQRCode.image ?: return null
        image.scalePercent((100f / width) * 100f)
        image.setAbsolutePosition(width - 170f, 155f)
        pdfStamper.getOverContent(1).addImage(image)

        image.scalePercent(100f)
        image.setAbsolutePosition(0f, (height / 2) - (width / 2) + 100f)
        pdfStamper.getOverContent(2).addImage(image)

        pdfStamper.close()
        pdfReader.close()

        CertificatesManager(context.filesDir).addCertificate(certificate, 0)
        return fileNew.name
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