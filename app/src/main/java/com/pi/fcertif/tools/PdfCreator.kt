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
        form.setField(
            "fc468c8b-002d-4f3a-8aa2-681a504cab56", userInfo.lastName!!.plus(" ")
                .plus(userInfo.firstName)
        )
        form.setField("5f6a2efc-d036-43ef-84d6-59c79dedc110", userInfo.birthDate!!)
        form.setField("c77ec808-bcd9-4e21-a6c2-cdc7255305ef", userInfo.birthPlace!!)
        form.setField(
            "dade0748-15a4-4598-ac45-fc9bd4bd5bc0", userInfo.address!!.plus(" ")
                .plus(userInfo.postalCode!!).plus(" ").plus(userInfo.city!!)
        )

        when (certificate.reason.id) {
            0 -> form.setField("2efb1ceb-ba8f-4403-a2a6-20a1560ae761", "Y")
            1 -> form.setField("bcd2467f-5c2b-4ef2-b981-74e12a662ed0", "Y")
            2 -> form.setField("7211a7e3-e3c4-4c47-ab1c-9f442d2d9f87", "Y")
            3 -> form.setField("c3bf563d-9e28-49ab-a1e5-6003cb721b93", "Y")
            4 -> form.setField("92211d47-8435-4c54-ae4b-4c607f3b9c15", "Y")
            5 -> form.setField("9b314247-7d8e-4838-b16e-2058610b7af3", "Y")
            6 -> form.setField("d379e155-67a3-487f-88e5-1165871737a9", "Y")
            7 -> form.setField("d1cbd8f2-6879-4427-867f-6718421c9d45", "Y")
            8 -> form.setField("7eb5e59d-67ca-485c-b7ac-d71a94dbc680", "Y")
        }

        form.setField("447c063f-1d0f-426d-84f3-678bb516d646", userInfo.city)
        form.setField("e263b23c-9cc5-4b41-9cfe-8227c2d4efe7", certificate.exitDateTime.date)
        form.setField(
            "9d8d4538-307e-4113-9f70-8352982f7e3d",
            certificate.exitDateTime.getHours().plus(":")
                .plus(certificate.exitDateTime.getMinutes())
        )
        form.setField(
            "b95d3b5b-8f65-42c4-b54d-f2c316e74623", userInfo.lastName.plus(" ")
                .plus(userInfo.firstName)
        )

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
        image.setAbsolutePosition(width - 160f, 40f)

        val overContent = pdfStamper.getOverContent(1)
        overContent.addImage(image)
        overContent.beginText()
        overContent.setFontAndSize(BaseFont.createFont(), 7.5f)
        overContent.setTextMatrix(width - 150f, 38f)
        overContent.showText("Date de création:")
        overContent.setTextMatrix(width - 150f, 30f)
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