package com.svbackend.natai.android.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class FileManagerService() {
    // throws java.io.FileNotFoundException: someFile.jpg: open failed: EROFS (Read-only file system)
    suspend fun inputStreamToFile(inputStream: java.io.InputStream, filename: String): File =
        withContext(Dispatchers.IO) {
            val file = File(filename)
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var len = 0
            while (inputStream.read(buffer).also { len = it } != -1) {
                outputStream.write(buffer, 0, len)
            }
            outputStream.close()
            inputStream.close()

            return@withContext file
        }
}