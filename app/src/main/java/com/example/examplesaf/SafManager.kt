package com.example.examplesaf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object SafManager {

    const val REQUEST_CODE_CREATE_SAF = 100
    const val REQUEST_CODE_OPEN_SAF = 101

    @JvmStatic
    fun createFileSAF(activity: Activity, mimeType: String, fileName: String) {

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        activity.startActivityForResult(intent, REQUEST_CODE_CREATE_SAF)
    }

    @JvmStatic
    fun openFileSAF(activity: Activity, mimeType: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
        }
        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_SAF)
    }

    @JvmStatic
    fun getFileFromSAF(context: Context, uri: Uri): File {

        val file = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { inputStream -> createFileFromStream(inputStream, file) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    @JvmStatic
    fun createFileFromStream(inputStream: InputStream?, file: File?) {
        try {
            if (inputStream == null) return

            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    private fun queryName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()

            val name = cursor.getString(nameIndex)
            cursor.close()

            return name
        }
        return null
    }

    @JvmStatic
    fun getTextFromSAF(context: Context, uri: Uri): String {

        val byteArrayOutputStream = ByteArrayOutputStream()
        var readLength = 0
        try {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                while (inputStream?.read()?.also { readLength = it } != -1) {
                    byteArrayOutputStream.write(readLength)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return byteArrayOutputStream.toString()
    }

    @JvmStatic
    fun getImageFromSAF(context: Context, uri: Uri): Bitmap? {

        var bitmap: Bitmap? = null
        try {
            context.contentResolver.openInputStream(uri).use { inputStream -> bitmap = BitmapFactory.decodeStream(inputStream) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
}