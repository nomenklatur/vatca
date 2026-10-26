package com.example.data.model

data class PdfDocument(
    val id: String,
    val uri: String,
    val title: String,
    val pageCount: Int,
    val lastReadPage: Int, // 0-indexed internally
    val lastReadOffset: Float, // 0f to 1f vertical offset
    val lastOpenedAt: Long?,
    val dateAdded: Long,
    val thumbnailPath: String?,
    val fileSize: Long?
) {
    val progressPercentage: Int
        get() = if (pageCount > 0) {
            (((lastReadPage + 1).toFloat() / pageCount.toFloat()) * 100f)
                .toInt()
                .coerceIn(0, 100)
        } else {
            0
        }

    val displayPageText: String
        get() = if (pageCount > 0) {
            "Page ${lastReadPage + 1} of $pageCount"
        } else {
            "0 pages"
        }
}
