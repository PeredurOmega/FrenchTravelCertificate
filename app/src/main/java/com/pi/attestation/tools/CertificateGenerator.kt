package com.pi.attestation.tools

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.Toast
import com.itextpdf.barcodes.BarcodeQRCode
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.ui.creator.filler.LoadingDialog
import com.pi.attestation.ui.viewer.CertificateViewerActivity
import java.io.*
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

        val pdfReader = PdfReader(file)
        val pdfWriter = PdfWriter(fileNew)
        val pdfDoc = PdfDocument(pdfReader, pdfWriter)

        val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)

        val form = PdfAcroForm.getAcroForm(pdfDoc, true)
        form.isGenerateAppearance = true
        form.getField("Nom et prénom").setValue(userInfo.lastName!!.plus(" ")
            .plus(userInfo.firstName), font, 11f)
        form.getField("Date de naissance").setValue(userInfo.birthDate!!, font, 11f)
        form.getField("Lieu de naissance").setValue(userInfo.birthPlace!!, font, 11f)
        form.getField("Adresse actuelle").setValue(userInfo.address!!.plus(" ")
            .plus(userInfo.postalCode!!).plus(" ").plus(userInfo.city!!), font, 11f)

        when(certificate.reason.id){
            0 -> form.getField("Déplacements entre domicile et travail")
                .setValue("X", font, 11f)
            1 -> form.getField("Déplacements achats nécéssaires")
                .setValue("X", font, 11f)
            2 -> form.getField("Consultations et soins")
                .setValue("X", font, 11f)
            3 -> form.getField("Déplacements pour motif familial")
                .setValue("X", font, 11f)
            4 -> form.getField("Déplacements brefs (activité physique et animaux)")
                .setValue("X", font, 11f)
            5 -> form.getField("Convcation judiciaire ou administrative")
                .setValue("X", font, 11f)
            //TODO CHANGE TO -> JUDICIAL CONVOCATION FOLLOWING
            // (https://github.com/LAB-MI/deplacement-covid-19/issues/89)
            6 -> form.getField("Mission d'intérêt général")
                .setValue("X", font, 11f)
        }

        form.getField("Ville").setValue(userInfo.city, font, 11f)
        form.getField("Date").setValue(certificate.exitDateTime.date, font, 11f)
        form.getField("Heure").setValue(certificate.exitDateTime.getHours(), font, 11f)
        form.getField("Minute").setValue(certificate.exitDateTime.getMinutes(), font, 11f)
        form.getField("Signature").setValue(userInfo.lastName.plus(" ")
            .plus(userInfo.firstName), font, 11f)

        form.flattenFields()

        val canvas = PdfCanvas(pdfDoc.firstPage)

        val barcodeQRCode = BarcodeQRCode(certificate.buildData())
        val codeQrImage = barcodeQRCode.createFormXObject(pdfDoc)
        canvas.addXObject(codeQrImage, pdfDoc.firstPage.pageSize.width - 170f,140f,
            100f / codeQrImage.width)

        val newPage = pdfDoc.addNewPage()
        val newCanvas = PdfCanvas(newPage)
        val fullPageWidth = newPage.pageSize.width / codeQrImage.width
        newCanvas.addXObject(codeQrImage, 0f,
            (newPage.pageSize.height / 2) - (newPage.pageSize.width / 2) + 100f, fullPageWidth)

        pdfDoc.close()

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