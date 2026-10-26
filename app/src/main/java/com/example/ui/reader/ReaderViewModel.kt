package com.example.ui.reader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.pdf.PdfDocumentSession
import com.example.core.pdf.PdfRendererEngine
import com.example.data.local.PreferencesManager
import com.example.data.model.FitMode
import com.example.data.model.PageMargin
import com.example.data.model.PageSpacing
import com.example.data.model.PdfDocument
import com.example.data.model.ReaderTheme
import com.example.data.model.ReadingMode
import com.example.data.model.ScreenOrientationPref
import com.example.data.model.UserPreferences
import com.example.data.repository.PdfRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "ReaderViewModel"

sealed interface ReaderUiState {
    object Loading : ReaderUiState
    data class Error(val message: String, val canRetry: Boolean = true) : ReaderUiState
    data class Ready(
        val document: PdfDocument,
        val pageCount: Int,
        val currentPage: Int, // 0-based
        val session: PdfDocumentSession,
        val preferences: UserPreferences,
        val isFullscreen: Boolean = false,
        val controlsVisible: Boolean = true
    ) : ReaderUiState
}

private data class ReaderLocalState(
    val currentPage: Int = 0,
    val currentOffset: Float = 0f,
    val isFullscreen: Boolean = false,
    val controlsVisible: Boolean = true,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

class ReaderViewModel(
    private val documentId: String,
    private val context: Context,
    private val repository: PdfRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _documentFlow = repository.getDocument(documentId)
    private val _localState = MutableStateFlow(ReaderLocalState())

    private var activeSession: PdfDocumentSession? = null
    private var progressSaveJob: Job? = null

    val uiState: StateFlow<ReaderUiState> = combine(
        _documentFlow,
        preferencesManager.userPreferencesFlow,
        _localState
    ) { doc, prefs, local ->
        when {
            local.errorMessage != null -> ReaderUiState.Error(local.errorMessage)
            local.isLoading || doc == null || activeSession == null -> ReaderUiState.Loading
            else -> ReaderUiState.Ready(
                document = doc,
                pageCount = activeSession?.pageCount ?: doc.pageCount,
                currentPage = local.currentPage,
                session = activeSession!!,
                preferences = prefs,
                isFullscreen = local.isFullscreen,
                controlsVisible = local.controlsVisible
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReaderUiState.Loading
    )

    init {
        loadDocument()
    }

    private fun loadDocument() {
        viewModelScope.launch {
            _localState.value = _localState.value.copy(isLoading = true, errorMessage = null)

            val doc = repository.getDocumentDirect(documentId)
            if (doc == null) {
                _localState.value = _localState.value.copy(
                    errorMessage = "Document not found in library.",
                    isLoading = false
                )
                return@launch
            }

            try {
                val uri = Uri.parse(doc.uri)
                val sessionResult = PdfRendererEngine.openSession(context, uri)
                if (sessionResult.isSuccess) {
                    activeSession = sessionResult.getOrThrow()
                    val initialPage = doc.lastReadPage.coerceIn(0, (activeSession!!.pageCount - 1).coerceAtLeast(0))
                    _localState.value = _localState.value.copy(
                        currentPage = initialPage,
                        currentOffset = doc.lastReadOffset,
                        isLoading = false
                    )
                } else {
                    val ex = sessionResult.exceptionOrNull()
                    Log.e(TAG, "Failed to open PDF session", ex)
                    _localState.value = _localState.value.copy(
                        errorMessage = "Cannot open document. The file may have been moved, renamed, or permission was revoked.",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception opening PDF session", e)
                _localState.value = _localState.value.copy(
                    errorMessage = "Error opening document: ${e.localizedMessage ?: "Unknown error"}",
                    isLoading = false
                )
            }
        }
    }

    fun onPageChanged(pageIndex: Int, offset: Float = 0f) {
        val safeIndex = pageIndex.coerceAtLeast(0)
        _localState.value = _localState.value.copy(currentPage = safeIndex, currentOffset = offset)

        // Debounce database writing so scrolling does not cause excessive DB transactions
        progressSaveJob?.cancel()
        progressSaveJob = viewModelScope.launch {
            delay(1200)
            repository.updateReadingProgress(documentId, safeIndex, offset)
        }
    }

    fun jumpToPage(pageIndex: Int) {
        val session = activeSession ?: return
        val clamped = pageIndex.coerceIn(0, session.pageCount - 1)
        onPageChanged(clamped)
    }

    fun toggleControls() {
        _localState.value = _localState.value.copy(controlsVisible = !_localState.value.controlsVisible)
    }

    fun setControlsVisible(visible: Boolean) {
        _localState.value = _localState.value.copy(controlsVisible = visible)
    }

    fun toggleFullscreen() {
        _localState.value = _localState.value.copy(isFullscreen = !_localState.value.isFullscreen)
    }

    fun setReaderTheme(theme: ReaderTheme) {
        viewModelScope.launch { preferencesManager.setReaderTheme(theme) }
    }

    fun setReadingMode(mode: ReadingMode) {
        viewModelScope.launch { preferencesManager.setReadingMode(mode) }
    }

    fun setPageSpacing(spacing: PageSpacing) {
        viewModelScope.launch { preferencesManager.setPageSpacing(spacing) }
    }

    fun setPageMargin(margin: PageMargin) {
        viewModelScope.launch { preferencesManager.setPageMargin(margin) }
    }

    fun setFitMode(fitMode: FitMode) {
        viewModelScope.launch { preferencesManager.setFitMode(fitMode) }
    }

    fun setScreenOrientation(orientation: ScreenOrientationPref) {
        viewModelScope.launch { preferencesManager.setScreenOrientation(orientation) }
    }

    fun setInAppBrightness(brightness: Float) {
        viewModelScope.launch { preferencesManager.setInAppBrightness(brightness) }
    }

    fun retryLoading() {
        loadDocument()
    }

    override fun onCleared() {
        super.onCleared()
        // Save final progress immediately upon leaving reader
        val finalPage = _localState.value.currentPage
        val finalOffset = _localState.value.currentOffset
        progressSaveJob?.cancel()
        kotlinx.coroutines.runBlocking {
            try {
                repository.updateReadingProgress(documentId, finalPage, finalOffset)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to save final reading progress", e)
            }
        }
        activeSession?.close()
        activeSession = null
    }

    class Factory(
        private val documentId: String,
        private val context: Context,
        private val repository: PdfRepository,
        private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReaderViewModel(documentId, context, repository, preferencesManager) as T
        }
    }
}
