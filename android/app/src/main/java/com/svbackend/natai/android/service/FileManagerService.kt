package com.svbackend.natai.android.service

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.svbackend.natai.android.coil.CropTransformation
import com.svbackend.natai.android.entity.ExistingAttachmentDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.http.ApiClient
import java.io.File

data class AttachmentUris(
    val uri: Uri,
    val previewUri: Uri? = null,
)

const val PREVIEW_SIZE = 192

class FileManagerService(
    private val apiClient: ApiClient,
    private val contentResolver: ContentResolver,
    val filesDir: File,
    public val cacheDir: File,
    private val cropTransformation: CropTransformation = CropTransformation(),
) {
    // copy file from uri to internal storage, generate preview and return new uris
    fun processNewAttachment(uri: Uri, originalFilename: String): AttachmentUris {
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            throw IllegalArgumentException("$uri - not found")
        }

        val file = File(filesDir, originalFilename)
        val internalUri = file.toUri()
        val isImage = contentResolver.getType(uri)?.startsWith("image") ?: false

        inputStream.use { input ->
            contentResolver.openOutputStream(internalUri, "w").use { output ->
                if (output == null) {
                    throw IllegalArgumentException("$internalUri - not created")
                }

                input.copyTo(output)
            }
        }

        val previewUri = if (isImage) {
            generatePreview(internalUri)
        } else {
            null
        }

        return AttachmentUris(internalUri, previewUri)
    }

    /**
     * if image - generate small preview 128x128 cropped preview
     */
    private fun generatePreview(internalFileUri: Uri): Uri? {
        try {
            val inputStream = contentResolver.openInputStream(internalFileUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val croppedBitmap = cropTransformation.transform(bitmap, PREVIEW_SIZE, PREVIEW_SIZE)

            val previewFile = File(filesDir, "preview_${internalFileUri.lastPathSegment}")

            // use webp only on android 11+
            val format = if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.JPEG
            }

            previewFile.outputStream().use { output ->
                croppedBitmap.compress(format, 90, output)
            }

            return previewFile.toUri()
        } catch (e: Exception) {
            println("------- GENERATE PREVIEW ERROR $internalFileUri")
            e.printStackTrace()
        }

        return null
    }

    fun deleteFile(uri: Uri) {
        try {
            uri.toFile().delete()
        } catch (e: Exception) {
            println("------- DELETE FILE ERROR $uri")
            e.printStackTrace()
        }
    }

    fun isFileExists(uri: Uri): Boolean {
        return try {
            uri.toFile().exists()
        } catch (e: Exception) {
            println("------- FILE EXISTS ERROR $uri")
            e.printStackTrace()
            false
        }
    }

    suspend fun loadAttachments(note: LocalNote): List<ExistingAttachmentDto> {

    }
}