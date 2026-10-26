package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PdfDocument

@Entity(tableName = "pdf_documents")
data class PdfDocumentEntity(
    @PrimaryKey
    val id: String,
    val uri: String,
    val title: String,
    val pageCount: Int,
    val lastReadPage: Int,
    val lastReadOffset: Float,
    val lastOpenedAt: Long?,
    val dateAdded: Long,
    val thumbnailPath: String?,
    val fileSize: Long?
) {
    fun toDomainModel(): PdfDocument = PdfDocument(
        id = id,
        uri = uri,
        title = title,
        pageCount = pageCount,
        lastReadPage = lastReadPage,
        lastReadOffset = lastReadOffset,
        lastOpenedAt = lastOpenedAt,
        dateAdded = dateAdded,
        thumbnailPath = thumbnailPath,
        fileSize = fileSize
    )

    companion object {
        fun fromDomainModel(model: PdfDocument): PdfDocumentEntity = PdfDocumentEntity(
            id = model.id,
            uri = model.uri,
            title = model.title,
            pageCount = model.pageCount,
            lastReadPage = model.lastReadPage,
            lastReadOffset = model.lastReadOffset,
            lastOpenedAt = model.lastOpenedAt,
            dateAdded = model.dateAdded,
            thumbnailPath = model.thumbnailPath,
            fileSize = model.fileSize
        )
    }
}
