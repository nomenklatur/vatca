package com.example.core.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors

private const val TAG = "PdfRendererEngine"

data class PageDimension(
    val width: Int,
    val height: Int,
    val aspectRatio: Float = if (height > 0) width.toFloat() / height.toFloat() else 0.75f
)

/**
 * Encapsulates an active PDF rendering session for a single document.
 * Thread-safe: All native PdfRenderer operations are strictly serialized on a single-thread dispatcher.
 */
class PdfDocumentSession(
    private val pfd: ParcelFileDescriptor,
    private val renderer: PdfRenderer,
    val pageCount: Int,
    val pageDimensions: List<PageDimension>,
    private val dispatcher: CoroutineDispatcher
) : AutoCloseable {

    // LruCache bounded by memory footprint (default 48MB)
    private val maxCacheBytes = 48 * 1024 * 1024
    private val bitmapCache = object : LruCache<Int, Bitmap>(maxCacheBytes) {
        override fun sizeOf(key: Int, value: Bitmap): Int {
            return value.byteCount
        }

        override fun entryRemoved(
            evicted: Boolean,
            key: Int?,
            oldValue: Bitmap?,
            newValue: Bitmap?
        ) {
            // Note: Let GC reclaim bitmap; do not recycle immediately as it might still be in composition
        }
    }

    private var isClosed = false

    suspend fun renderPage(pageIndex: Int, targetWidth: Int): Bitmap? = withContext(dispatcher) {
        if (isClosed || pageIndex < 0 || pageIndex >= pageCount) return@withContext null

        // Check in-memory cache
        bitmapCache.get(pageIndex)?.let { cached ->
            if (!cached.isRecycled) {
                return@withContext cached
            }
        }

        var page: PdfRenderer.Page? = null
        try {
            page = renderer.openPage(pageIndex)
            val originalWidth = page.width
            val originalHeight = page.height

            // Calculate scaled dimension (enforcing minimum and maximum limits for crisp rendering)
            val effectiveWidth = targetWidth.coerceIn(300, 2048)
            val scale = effectiveWidth.toFloat() / originalWidth.toFloat()
            val effectiveHeight = (originalHeight * scale).toInt().coerceAtLeast(100)

            val bitmap = Bitmap.createBitmap(effectiveWidth, effectiveHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            bitmapCache.put(pageIndex, bitmap)
            return@withContext bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Failed to render page $pageIndex", e)
            return@withContext null
        } finally {
            try {
                page?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing page $pageIndex", e)
            }
        }
    }

    suspend fun getCachedPage(pageIndex: Int): Bitmap? {
        val bitmap = bitmapCache.get(pageIndex)
        return if (bitmap != null && !bitmap.isRecycled) bitmap else null
    }

    override fun close() {
        if (isClosed) return
        isClosed = true
        bitmapCache.evictAll()
        try {
            renderer.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing PdfRenderer", e)
        }
        try {
            pfd.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing ParcelFileDescriptor", e)
        }
    }
}

object PdfRendererEngine {

    /**
     * Opens a PDF document session using ContentResolver.
     */
    suspend fun openSession(context: Context, uri: Uri): Result<PdfDocumentSession> = withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext Result.failure(IOException("Could not open file descriptor for URI: $uri"))

            val renderer = PdfRenderer(pfd)
            val count = renderer.pageCount
            val dimensions = mutableListOf<PageDimension>()

            // Read dimensions of pages
            for (i in 0 until count) {
                val page = renderer.openPage(i)
                dimensions.add(PageDimension(page.width, page.height))
                page.close()
            }

            val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
            Result.success(PdfDocumentSession(pfd, renderer, count, dimensions, dispatcher))
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for URI: $uri", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening PDF: $uri", e)
            Result.failure(e)
        }
    }

    /**
     * Inspects a PDF and generates a thumbnail for page 0 saved to destination file.
     */
    suspend fun generateThumbnailAndInspect(
        context: Context,
        uri: Uri,
        destFile: File
    ): Result<Pair<Int, String?>> = withContext(kotlinx.coroutines.Dispatchers.IO) {
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var firstPage: PdfRenderer.Page? = null
        try {
            pfd = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext Result.failure(IOException("Could not open file descriptor for URI: $uri"))

            renderer = PdfRenderer(pfd)
            val pageCount = renderer.pageCount
            if (pageCount == 0) {
                return@withContext Result.success(Pair(0, null))
            }

            firstPage = renderer.openPage(0)
            val origWidth = firstPage.width
            val origHeight = firstPage.height

            // Thumbnail size: width ~360px
            val targetThumbWidth = 360
            val scale = targetThumbWidth.toFloat() / origWidth.toFloat()
            val targetThumbHeight = (origHeight * scale).toInt().coerceAtLeast(100)

            val bitmap = Bitmap.createBitmap(targetThumbWidth, targetThumbHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)

            firstPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            destFile.parentFile?.mkdirs()
            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            bitmap.recycle()

            Result.success(Pair(pageCount, destFile.absolutePath))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate thumbnail for $uri", e)
            Result.failure(e)
        } finally {
            try { firstPage?.close() } catch (_: Exception) {}
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
        }
    }
}
