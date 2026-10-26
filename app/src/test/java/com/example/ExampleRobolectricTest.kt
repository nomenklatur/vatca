package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PdfDocumentDao
import com.example.data.local.PdfDocumentEntity
import com.example.data.local.VatcaDatabase
import com.example.data.model.LibrarySort
import com.example.data.model.PdfDocument
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: VatcaDatabase
    private lateinit var dao: PdfDocumentDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VatcaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.pdfDocumentDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun appNameStringResourceIsVatca() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Vatca", appName)
    }

    @Test
    fun pdfDocumentProgressCalculation() {
        val docUnread = PdfDocument(
            id = "1",
            uri = "content://test/1.pdf",
            title = "Test PDF",
            pageCount = 100,
            lastReadPage = 0,
            lastReadOffset = 0f,
            lastOpenedAt = null,
            dateAdded = 1000L,
            thumbnailPath = null,
            fileSize = 1024L
        )
        // Page 0 (which is page 1 of 100) -> 1%
        assertEquals(1, docUnread.progressPercentage)

        val docHalfRead = docUnread.copy(lastReadPage = 49) // Page 50 of 100 -> 50%
        assertEquals(50, docHalfRead.progressPercentage)

        val docCompleted = docUnread.copy(lastReadPage = 99) // Page 100 of 100 -> 100%
        assertEquals(100, docCompleted.progressPercentage)
    }

    @Test
    fun insertAndQueryPdfDocumentInDatabase() = runBlocking {
        val entity = PdfDocumentEntity(
            id = "doc-uuid-1",
            uri = "content://media/external/file/101",
            title = "Clean Architecture",
            pageCount = 350,
            lastReadPage = 12,
            lastReadOffset = 0.5f,
            lastOpenedAt = 5000L,
            dateAdded = 4000L,
            thumbnailPath = "/data/user/0/com.example/files/thumbnails/doc-uuid-1.jpg",
            fileSize = 512000L
        )

        dao.insertDocument(entity)

        val loaded = dao.getDocumentByIdDirect("doc-uuid-1")
        assertNotNull(loaded)
        assertEquals("Clean Architecture", loaded?.title)
        assertEquals(350, loaded?.pageCount)
        assertEquals(12, loaded?.lastReadPage)

        // Update progress
        dao.updateReadingProgress("doc-uuid-1", page = 45, offset = 0.2f, openedAt = 6000L)
        val updated = dao.getDocumentByIdDirect("doc-uuid-1")
        assertEquals(45, updated?.lastReadPage)
        assertEquals(6000L, updated?.lastOpenedAt)

        // Delete
        dao.deleteDocumentById("doc-uuid-1")
        assertNull(dao.getDocumentByIdDirect("doc-uuid-1"))
    }

    @Test
    fun librarySortingLogic() {
        val docA = PdfDocument("1", "uri1", "Android Internals", 100, 10, 0f, lastOpenedAt = 1000L, dateAdded = 100L, null, null)
        val docB = PdfDocument("2", "uri2", "Kotlin in Action", 200, 150, 0f, lastOpenedAt = 5000L, dateAdded = 200L, null, null)
        val docC = PdfDocument("3", "uri3", "Algorithms", 300, 5, 0f, lastOpenedAt = 2000L, dateAdded = 300L, null, null)

        val list = listOf(docA, docB, docC)

        // Sort by Recently Read
        val sortedRecent = list.sortedWith(
            compareByDescending<PdfDocument> { it.lastOpenedAt ?: 0L }
                .thenByDescending { it.dateAdded }
        )
        assertEquals(listOf("Kotlin in Action", "Algorithms", "Android Internals"), sortedRecent.map { it.title })

        // Sort by Title A-Z
        val sortedTitle = list.sortedBy { it.title.lowercase() }
        assertEquals(listOf("Algorithms", "Android Internals", "Kotlin in Action"), sortedTitle.map { it.title })

        // Sort by Progress
        val sortedProgress = list.sortedByDescending { it.progressPercentage }
        assertEquals(listOf("Kotlin in Action", "Android Internals", "Algorithms"), sortedProgress.map { it.title })
    }
}
