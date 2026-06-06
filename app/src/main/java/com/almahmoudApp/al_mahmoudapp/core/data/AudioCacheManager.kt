package com.almahmoudApp.al_mahmoudapp.core.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * يدير تخزين الملفات الصوتية محلياً في الكاش.
 * عند أول تشغيل، يتم تحميل الملف من الرابط وحفظه.
 * في المرات التالية، يتم التشغيل مباشرة من الكاش.
 */
@Singleton
class AudioCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val cacheDir: File by lazy {
        File(context.cacheDir, AUDIO_CACHE_DIR).also { it.mkdirs() }
    }

    /**
     * يُرجع مسار الملف المحلي للصوت.
     * إذا كان الملف موجوداً في الكاش، يرجعه مباشرة.
     * وإلا يقوم بتحميله أولاً ثم يرجع المسار.
     */
    suspend fun getAudioFile(url: String): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val fileName = generateFileName(url)
            val cachedFile = File(cacheDir, fileName)

            if (cachedFile.exists() && cachedFile.length() > 0) {
                return@runCatching cachedFile
            }

            downloadFile(url, cachedFile)
            cachedFile
        }
    }

    /**
     * يتحقق ما إذا كان الملف الصوتي موجوداً في الكاش.
     */
    fun isCached(url: String): Boolean {
        val fileName = generateFileName(url)
        val cachedFile = File(cacheDir, fileName)
        return cachedFile.exists() && cachedFile.length() > 0
    }

    /**
     * يحذف جميع الملفات الصوتية المخزنة في الكاش.
     */
    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    /**
     * يرجع الحجم الكلي للكاش بالبايت.
     */
    fun getCacheSizeBytes(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    private fun downloadFile(url: String, destination: File) {
        val tempFile = File(cacheDir, "${destination.name}.tmp")
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("فشل التحميل: HTTP ${connection.responseCode}")
            }

            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                    output.flush()
                }
            }

            // نقل الملف المؤقت إلى الموقع النهائي بعد اكتمال التحميل
            if (!tempFile.renameTo(destination)) {
                tempFile.copyTo(destination, overwrite = true)
                tempFile.delete()
            }
        } catch (e: Exception) {
            tempFile.delete()
            throw e
        }
    }

    /**
     * يولد اسم ملف فريد بناءً على تجزئة MD5 للرابط.
     */
    private fun generateFileName(url: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(url.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return "$hash.mp3"
    }

    companion object {
        private const val AUDIO_CACHE_DIR = "audio_cache"
        private const val CONNECT_TIMEOUT_MS = 15_000
        private const val READ_TIMEOUT_MS = 30_000
        private const val BUFFER_SIZE = 8 * 1024
    }
}
