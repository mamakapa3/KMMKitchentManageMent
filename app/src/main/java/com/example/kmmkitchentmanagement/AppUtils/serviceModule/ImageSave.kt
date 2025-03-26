package com.example.kmmkitchentmanagement.AppUtils.serviceModule

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class ImageSave {
    fun saveImageToLocalStorage(context: Context, uri: Uri, fileName: String): String? {
        val parentDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) // Pictures folder
        val directory = File(parentDir, "YourAppImages") // Ensure "YourAppImages" exists

        if (!directory.exists()) {
            val created = directory.mkdirs()
            Log.e("ImageSave", "Creating directory: ${directory.absolutePath} - Success: $created")
        }

        val file = File(directory, "$fileName.jpg")
        Log.d("ImageSave", "✅ Đường dẫn file: ${file.absolutePath}")

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.i("ImageSave", "Image saved successfully at ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e("ImageSave", "Failed to save image: ${e.message}")
            null
        }
    }
}