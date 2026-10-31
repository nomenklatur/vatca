package com.example.ui.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.PreferencesManager
import com.example.data.model.LibraryLayout
import com.example.data.model.LibrarySort
import com.example.data.model.PdfDocument
import com.example.data.repository.PdfRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LibraryUiState(
    val isLoading: Boolean = true,
    val documents: List<PdfDocument> = emptyList(),
    val totalDocumentCount: Int = 0,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val layoutMode: LibraryLayout = LibraryLayout.GRID,
    val sortOrder: LibrarySort = LibrarySort.RECENTLY_OPENED,
    val isImporting: Boolean = false,
    val documentPendingRemoval: PdfDocument? = null,
    val errorMessage: String? = null
)

private data class FilterState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isImporting: Boolean = false,
    val pendingRemoval: PdfDocument? = null,
    val errorMessage: String? = null
)

class LibraryViewModel(
    private val repository: PdfRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _filterState = MutableStateFlow(FilterState())

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.documents,
        preferencesManager.userPreferencesFlow,
        _filterState
    ) { docs, prefs, filter ->
        val filtered = docs.filter { doc ->
            filter.query.isBlank() || doc.title.contains(filter.query.trim(), ignoreCase = true)
        }

        val sorted = when (prefs.librarySort) {
            LibrarySort.RECENTLY_OPENED -> filtered.sortedWith(
                compareByDescending<PdfDocument> { it.lastOpenedAt ?: 0L }
                    .thenByDescending { it.dateAdded }
            )
            LibrarySort.DATE_ADDED -> filtered.sortedByDescending { it.dateAdded }
            LibrarySort.TITLE -> filtered.sortedBy { it.title.lowercase() }
            LibrarySort.PROGRESS -> filtered.sortedByDescending { it.progressPercentage }
        }

        LibraryUiState(
            isLoading = false,
            documents = sorted,
            totalDocumentCount = docs.size,
            searchQuery = filter.query,
            isSearchActive = filter.isSearchActive,
            layoutMode = prefs.libraryLayout,
            sortOrder = prefs.librarySort,
            isImporting = filter.isImporting,
            documentPendingRemoval = filter.pendingRemoval,
            errorMessage = filter.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun onSearchQueryChange(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun toggleSearch() {
        val current = _filterState.value
        val nextActive = !current.isSearchActive
        _filterState.value = current.copy(
            isSearchActive = nextActive,
            query = if (!nextActive) "" else current.query
        )
    }

    fun setLayoutMode(layout: LibraryLayout) {
        viewModelScope.launch {
            preferencesManager.setLibraryLayout(layout)
        }
    }

    fun setSortOrder(sort: LibrarySort) {
        viewModelScope.launch {
            preferencesManager.setLibrarySort(sort)
        }
    }

    fun importPdf(uri: Uri) {
        viewModelScope.launch {
            _filterState.value = _filterState.value.copy(isImporting = true)
            val result = repository.importPdf(uri)
            _filterState.value = _filterState.value.copy(
                isImporting = false,
                errorMessage = if (result.isFailure) {
                    "Failed to import PDF: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown error"}"
                } else null
            )
        }
    }

    fun requestRemoveDocument(document: PdfDocument) {
        _filterState.value = _filterState.value.copy(pendingRemoval = document)
    }

    fun confirmRemoveDocument() {
        val doc = _filterState.value.pendingRemoval ?: return
        viewModelScope.launch {
            repository.deleteDocumentFromLibrary(doc.id)
            _filterState.value = _filterState.value.copy(pendingRemoval = null)
        }
    }

    fun dismissRemoveDocument() {
        _filterState.value = _filterState.value.copy(pendingRemoval = null)
    }

    fun dismissError() {
        _filterState.value = _filterState.value.copy(errorMessage = null)
    }

    class Factory(
        private val repository: PdfRepository,
        private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LibraryViewModel(repository, preferencesManager) as T
        }
    }
}
