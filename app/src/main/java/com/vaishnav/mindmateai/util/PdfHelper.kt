package com.vaishnav.mindmateai.util

import android.content.Context
import android.net.Uri
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor

object PdfHelper {
    fun extractTextFromUri(context: Context, uri: Uri): String {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val reader = PdfReader(inputStream)
            val stringBuilder = java.lang.StringBuilder()
            
            for (i in 1..reader.numberOfPages) {
                stringBuilder.append(PdfTextExtractor.getTextFromPage(reader, i))
                stringBuilder.append("\n")
            }
            reader.close()
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "Error extracting text from PDF: ${e.message}"
        }
    }
}
