package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfDocumentDao {
    @Query("SELECT * FROM pdf_documents ORDER BY CASE WHEN lastOpenedAt IS NOT NULL THEN lastOpenedAt ELSE dateAdded END DESC")
    fun getAllDocuments(): Flow<List<PdfDocumentEntity>>

    @Query("SELECT * FROM pdf_documents WHERE id = :id")
    fun getDocumentById(id: String): Flow<PdfDocumentEntity?>

    @Query("SELECT * FROM pdf_documents WHERE id = :id")
    suspend fun getDocumentByIdDirect(id: String): PdfDocumentEntity?

    @Query("SELECT * FROM pdf_documents WHERE uri = :uri LIMIT 1")
    suspend fun getDocumentByUri(uri: String): PdfDocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: PdfDocumentEntity)

    @Query("UPDATE pdf_documents SET lastReadPage = :page, lastReadOffset = :offset, lastOpenedAt = :openedAt WHERE id = :id")
    suspend fun updateReadingProgress(id: String, page: Int, offset: Float, openedAt: Long)

    @Query("UPDATE pdf_documents SET title = :title, pageCount = :pageCount, thumbnailPath = :thumbnailPath WHERE id = :id")
    suspend fun updateMetadata(id: String, title: String, pageCount: Int, thumbnailPath: String?)

    @Query("DELETE FROM pdf_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: String)
}
