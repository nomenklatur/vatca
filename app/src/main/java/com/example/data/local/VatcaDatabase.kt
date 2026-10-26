package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PdfDocumentEntity::class], version = 1, exportSchema = false)
abstract class VatcaDatabase : RoomDatabase() {
    abstract fun pdfDocumentDao(): PdfDocumentDao

    companion object {
        @Volatile
        private var INSTANCE: VatcaDatabase? = null

        fun getInstance(context: Context): VatcaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VatcaDatabase::class.java,
                    "vatca_library.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
