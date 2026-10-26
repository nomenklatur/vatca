package com.example

import android.app.Application
import com.example.data.local.PreferencesManager
import com.example.data.local.VatcaDatabase
import com.example.data.repository.PdfRepository
import com.example.data.repository.PdfRepositoryImpl

class VatcaApplication : Application() {

    lateinit var database: VatcaDatabase
        private set

    lateinit var repository: PdfRepository
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = VatcaDatabase.getInstance(this)
        repository = PdfRepositoryImpl(this, database.pdfDocumentDao())
        preferencesManager = PreferencesManager(this)
    }

    companion object {
        lateinit var instance: VatcaApplication
            private set
    }
}
