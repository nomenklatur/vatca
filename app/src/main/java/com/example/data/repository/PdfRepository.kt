package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.core.pdf.PdfRendererEngine
import com.example.data.local.PdfDocumentDao
import com.example.data.local.PdfDocumentEntity
import com.example.data.model.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

private const val TAG = "PdfRepository"

interface PdfRepository {
    val documents: Flow<List<PdfDocument>>
    fun getDocument(id: String): Flow<PdfDocument?>
    suspend fun getDocumentDirect(id: String): PdfDocument?
    suspend fun importPdf(uri: Uri): Result<PdfDocument>
    suspend fun updateReadingProgress(id: String, page: Int, offset: Float)
    suspend fun deleteDocumentFromLibrary(id: String)
    suspend fun verifyUriAccessible(uriString: String): Boolean
}

class PdfRepositoryImpl(
    private val context: Context,
    private val dao: PdfDocumentDao
) : PdfRepository {

    override val documents: Flow<List<PdfDocument>> = dao.getAllDocuments().map { entities ->
        entities.map { it.toDomainModel() }
    }

    override fun getDocument(id: String): Flow<PdfDocument?> = dao.getDocumentById(id).map { it?.toDomainModel() }

    override suspend fun getDocumentDirect(id: String): PdfDocument? = withContext(Dispatchers.IO) {
        dao.getDocumentByIdDirect(id)?.toDomainModel()
    }

    override suspend fun importPdf(uri: Uri): Result<PdfDocument> = withContext(Dispatchers.IO) {
        try {
            // Take persistable permission if possible
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: SecurityException) {
                Log.w(TAG, "Could not take persistable URI permission for $uri", e)
            }

            // Check if already imported
            val existing = dao.getDocumentByUri(uri.toString())
            if (existing != null) {
                return@withContext Result.success(existing.toDomainModel())
            }

            // Extract file metadata (name and size)
            var displayName = "Document.pdf"
            var fileSize: Long? = null
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) {
                            cursor.getString(nameIndex)?.let { displayName = it }
                        }
                        if (sizeIndex != -1) {
                            fileSize = cursor.getLong(sizeIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to query OpenableColumns for $uri", e)
                uri.lastPathSegment?.let { displayName = it }
            }

            val docId = UUID.randomUUID().toString()
            val thumbnailFile = File(context.filesDir, "thumbnails/$docId.jpg")

            // Inspect PDF and generate thumbnail
            val inspectionResult = PdfRendererEngine.generateThumbnailAndInspect(
                context = context,
                uri = uri,
                destFile = thumbnailFile
            )

            val (pageCount, thumbnailPath) = inspectionResult.getOrElse {
                Log.e(TAG, "Inspection error for $uri", it)
                Pair(0, null)
            }

            val newEntity = PdfDocumentEntity(
                id = docId,
                uri = uri.toString(),
                title = cleanDocumentTitle(displayName),
                pageCount = pageCount,
                lastReadPage = 0,
                lastReadOffset = 0f,
                lastOpenedAt = null,
                dateAdded = System.currentTimeMillis(),
                thumbnailPath = thumbnailPath,
                fileSize = fileSize
            )

            dao.insertDocument(newEntity)
            Result.success(newEntity.toDomainModel())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import PDF $uri", e)
            Result.failure(e)
        }
    }

    override suspend fun updateReadingProgress(id: String, page: Int, offset: Float) = withContext(Dispatchers.IO) {
        dao.updateReadingProgress(
            id = id,
            page = page,
            offset = offset,
            openedAt = System.currentTimeMillis()
        )
    }

    override suspend fun deleteDocumentFromLibrary(id: String) = withContext(Dispatchers.IO) {
        val doc = dao.getDocumentByIdDirect(id)
        if (doc?.thumbnailPath != null) {
            try {
                File(doc.thumbnailPath).delete()
            } catch (e: Exception) {
                Log.w(TAG, "Error deleting thumbnail file: ${doc.thumbnailPath}", e)
            }
        }
        dao.deleteDocumentById(id)
    }

    override suspend fun verifyUriAccessible(uriString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(uriString)
            context.contentResolver.openFileDescriptor(uri, "r")?.use {
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun cleanDocumentTitle(rawName: String): String {
        return rawName.removeSuffix(".pdf").removeSuffix(".PDF")
    }
}
