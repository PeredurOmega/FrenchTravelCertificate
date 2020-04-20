package com.pi.attestation.tools

import com.itextpdf.text.pdf.BarcodeQRCode
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.qrcode.EncodeHintType
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel
import com.pi.attestation.objects.Certificate
import java.io.File
import java.io.FileOutputStream

class PdfCreator(private val cacheDir: File, private val originalCertificate: File) {

    internal fun generatePdf(certificate: Certificate)
            : String? {
        val userInfo = certificate.userInfo
        var k = 0
        var fileNew : File
        if(certificate.pdfFileName.isBlank()){
            do {
                val fileNewName = "french_certificate_".plus(certificate.creationDateTime.toString())
                    .plus("_").plus(k).plus(".pdf")
                certificate.pdfFileName = fileNewName
                fileNew = File(cacheDir, fileNewName)
                k++
            }while (fileNew.exists())
        }else{
            fileNew = File(cacheDir, certificate.pdfFileName)
        }

        val pdfReader = PdfReader(originalCertificate.path)
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

        val overContent = pdfStamper.getOverContent(1)
        overContent.addImage(image)
        overContent.beginText()
        overContent.setFontAndSize(BaseFont.createFont(), 7.5f)
        overContent.setTextMatrix(width - 160f, 153f)
        overContent.showText("Date de création:")
        overContent.setTextMatrix(width - 160f, 145f)
        overContent.showText(certificate.creationDateTime.date + " à "
                + certificate.creationDateTime.time.replace(":", "h"))
        overContent.endText()

        image.scalePercent(100f)
        image.setAbsolutePosition(0f, (height / 2) - (width / 2) + 100f)
        pdfStamper.getOverContent(2).addImage(image)

        pdfStamper.close()
        pdfReader.close()

        return fileNew.name
    }
}