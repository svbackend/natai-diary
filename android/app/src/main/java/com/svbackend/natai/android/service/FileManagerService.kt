package com.svbackend.natai.android.service

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.svbackend.natai.android.coil.CropTransformation
import com.svbackend.natai.android.entity.AttachmentEntityDto
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
    private val TAG = "FileManagerService"

    fun processExistingAttachment(uri: Uri): AttachmentUris {
        return AttachmentUris(
            uri,
            generatePreview(uri), // todo preview only if image
        )
    }

    // copy file from uri to internal storage, generate preview and return new uris
    fun processNewAttachment(uri: Uri, originalFilename: String): AttachmentUris {
        val file = File(cacheDir, originalFilename)
        val internalUri = file.toUri()

        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            throw IllegalArgumentException("$uri - not found")
        }

        inputStream.use { input ->
            contentResolver.openOutputStream(internalUri, "w").use { output ->
                if (output == null) {
                    throw IllegalArgumentException("$internalUri - not created")
                }

                input.copyTo(output)
            }
        }

        val contentType = contentResolver.getType(internalUri) ?: contentResolver.getType(uri)
        val isImage = contentType?.startsWith("image") ?: false

        Log.v(TAG, "CONTENT TYPE: $contentType")

        val previewUri = if (isImage) {
            Log.v(TAG, "generating preview for $internalUri")
            generatePreview(internalUri)
        } else {
            null
        }

        val newUris = AttachmentUris(internalUri, previewUri)
        Log.v(TAG, "new uris: $newUris")
        return newUris
    }

    /**
     * if image - generate small preview 128x128 cropped preview
     */
    private fun generatePreview(internalFileUri: Uri): Uri? {
        try {
            val inputStream = contentResolver.openInputStream(internalFileUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val croppedBitmap = cropTransformation.transform(bitmap, PREVIEW_SIZE, PREVIEW_SIZE)

            val finalBitmap = try {
                orientate(internalFileUri, croppedBitmap)
            } catch (e: Throwable) {
                Log.v(TAG, "orientate failed, using original bitmap", e)
                croppedBitmap
            }

            val previewFile = File(cacheDir, "preview_${internalFileUri.lastPathSegment}")

            // use webp only on android 11+
            val format = if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.JPEG
            }

            previewFile.outputStream().use { output ->
                finalBitmap.compress(format, 90, output)
            }

            return previewFile.toUri()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun orientate(internalFileUri: Uri, croppedBitmap: Bitmap): Bitmap {
        // fix orientation if needed (based on exif data)
        val exif = ExifInterface(internalFileUri.toFile())
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val finalBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                Bitmap.createBitmap(
                    croppedBitmap,
                    0,
                    0,
                    croppedBitmap.width,
                    croppedBitmap.height,
                    Matrix().apply { postRotate(90f) },
                    true
                )
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                Bitmap.createBitmap(
                    croppedBitmap,
                    0,
                    0,
                    croppedBitmap.width,
                    croppedBitmap.height,
                    Matrix().apply { postRotate(180f) },
                    true
                )
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                Bitmap.createBitmap(
                    croppedBitmap,
                    0,
                    0,
                    croppedBitmap.width,
                    croppedBitmap.height,
                    Matrix().apply { postRotate(270f) },
                    true
                )
            }
            else -> {
                croppedBitmap
            }
        }

        return finalBitmap
    }

    fun deleteFile(uri: Uri) {
        try {
            uri.toFile().delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isFileExists(uri: Uri): Boolean {
        return try {
            val f = uri.toFile()
            Log.v(TAG, "isFileExists $uri ${f.exists()} ${f.isFile} ${f.length()}")
            f.exists() && f.isFile && f.length() > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun savePreview(uri: Uri, originalFilename: String): Uri {
        val originalFile = uri.toFile()
        val ext = originalFilename.substringAfterLast(".")
        val previewFilename = originalFilename.replace(".$ext", "_preview.$ext")
        val previewFile = File(cacheDir, previewFilename) // todo cacheDir vs filesDir, what to use?
        val previewUri = previewFile.toUri()

        originalFile.copyTo(previewFile, overwrite = true)

        // delete original file
        originalFile.delete()

        return previewUri
    }

    suspend fun uploadAttachments(
        attachments: List<AttachmentEntityDto>
    ): List<AttachmentEntityDto> {
        val uploadedAttachments = attachments.map { attachment ->
            if (attachment.uri == null || attachment.cloudAttachmentId != null) {
                return@map attachment
            }

            val resolvedFileInput = contentResolver.openInputStream(attachment.uri)

            if (resolvedFileInput == null) {
                Log.v(TAG, "uploadAttachments - not found ${attachment.uri}")
                return@map attachment
            }

            val pendingAttachment = apiClient.getAttachmentSignedUrl(attachment.filename)
            val contentType = contentResolver.getType(attachment.uri) ?: "application/octet-stream"

            try {
                resolvedFileInput.use { inputStream ->
                    apiClient.uploadFileSync(
                        inputStream = inputStream,
                        contentType = contentType,
                        pendingAttachment.uploadUrl
                    )
                }
            } catch (e: Exception) {
                Log.v(TAG, "uploadAttachments - upload failed ${attachment.uri}", e)
                return@map attachment
            }

            attachment.copy(
                cloudAttachmentId = pendingAttachment.attachmentId,
            )
        }

        return uploadedAttachments
    }

    companion object {
        fun isImageBasedOnFilename(filename: String): Boolean {
            val ext = filename.substringAfterLast(".")
            return ext in listOf("jpg", "jpeg", "png", "gif", "webp")
        }
    }
}