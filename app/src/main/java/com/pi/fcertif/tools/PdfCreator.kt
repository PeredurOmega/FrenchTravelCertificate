package com.pi.fcertif.tools

import com.itextpdf.text.BaseColor
import com.itextpdf.text.ExceptionConverter
import com.itextpdf.text.PageSize
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.qrcode.EncodeHintType
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel
import com.pi.fcertif.objects.Certificate
import java.io.File
import java.io.FileOutputStream


class PdfCreator(private val cacheDir: File, private val originalCertificate: File) {

    internal fun generatePdf(certificate: Certificate): String? {
        val userInfo = certificate.userInfo
        var k = 0
        var fileNew: File
        if (certificate.pdfFileName.isBlank()) {
            do {
                val fileNewName =
                    "french_certificate_".plus(certificate.creationDateTime.toString())
                        .plus("_").plus(k).plus(".pdf")
                certificate.pdfFileName = fileNewName
                fileNew = File(cacheDir, fileNewName)
                k++
            } while (fileNew.exists())
        } else fileNew = File(cacheDir, certificate.pdfFileName)

        val pdfReader = PdfReader(originalCertificate.path)
        val pdfStamper = PdfStamper(pdfReader, FileOutputStream(fileNew.path))

        val form = pdfStamper.acroFields
        form.isGenerateAppearances = true
        form.setField("Nom Prénom", userInfo.lastName!!.plus(" ").plus(userInfo.firstName))
        form.setField("Date de naissance", userInfo.birthDate!!)
        form.setField("Lieu de naissance", userInfo.birthPlace!!)
        form.setField(
            "Adresse du domicile",
            userInfo.address!!.plus(" ").plus(userInfo.postalCode!!).plus(" ").plus(userInfo.city!!)
        )

        form.setField("Motif ${certificate.reason.id + 1}", "Yes")

        form.setField("Lieu d'établissement du justificatif", userInfo.city)
        form.setField("Date", certificate.exitDateTime.date)
        form.setField(
            "Heure",
            certificate.exitDateTime.getHours().plus(":")
                .plus(certificate.exitDateTime.getMinutes())
        )
        form.setField("Signature", userInfo.lastName.plus(" ").plus(userInfo.firstName))

        pdfStamper.setFormFlattening(true)

        val layout = Rectangle(PageSize.A4)
        layout.backgroundColor = BaseColor.PINK
        pdfStamper.insertPage(
            pdfReader.numberOfPages + 1,
            layout
        )

        val qrParam: MutableMap<EncodeHintType, Any> = HashMap()
        qrParam[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M

        val pageSize = pdfReader.getPageSize(1)
        val width = pageSize.width
        val height = pageSize.height
        val barcodeQRCode = BarcodeQRCode(
            certificate.buildData(), width.toInt(),
            width.toInt(), qrParam
        )
        val image = barcodeQRCode.image ?: return null
        image.scalePercent((100f / width) * 100f)
        image.setAbsolutePosition(width - 160f, 87f)

        val overContent = pdfStamper.getOverContent(1)
        overContent.addImage(image)
        overContent.beginText()
        overContent.setFontAndSize(BaseFont.createFont(), 7.5f)
        overContent.setTextMatrix(width - 150f, 85f)
        overContent.showText("Date de création:")
        overContent.setTextMatrix(width - 150f, 77f)
        overContent.showText(
            certificate.creationDateTime.date + " à "
                    + certificate.creationDateTime.time.replace(":", "h")
        )
        overContent.endText()

        image.scalePercent(100f)
        image.setAbsolutePosition(0f, (height / 2) - (width / 2) + 100f)
        pdfStamper.getOverContent(2).addImage(image)

        try {
            pdfStamper.close()
            pdfReader.close()
        } catch (e: ExceptionConverter) {
            e.printStackTrace()
        }

        return fileNew.name
    }
}