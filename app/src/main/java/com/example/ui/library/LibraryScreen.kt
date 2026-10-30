package com.example.ui.library

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.LibraryLayout
import com.example.data.model.LibrarySort
import com.example.data.model.PdfDocument
import com.example.ui.theme.EditorialGold
import com.example.ui.theme.EditorialSage
import com.example.ui.theme.EditorialSageText
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onOpenDocument: (documentId: String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importPdf(it) }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.dismissError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LibraryTopBar(
                isSearchActive = uiState.isSearchActive,
                searchQuery = uiState.searchQuery,
                totalCount = uiState.totalDocumentCount,
                layoutMode = uiState.layoutMode,
                sortOrder = uiState.sortOrder,
                onSearchToggle = { viewModel.toggleSearch() },
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onLayoutChange = { viewModel.setLayoutMode(it) },
                onSortChange = { viewModel.setSortOrder(it) },
                onOpenSettings = onOpenSettings,
                onImportClick = { filePickerLauncher.launch(arrayOf("application/pdf")) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isImporting -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Importing volume & generating cover...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.documents.isEmpty() && !uiState.isSearchActive -> {
                    EmptyLibraryView(
                        onImportClick = { filePickerLauncher.launch(arrayOf("application/pdf")) }
                    )
                }

                uiState.documents.isEmpty() && uiState.isSearchActive -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No volumes found matching \"${uiState.searchQuery}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.layoutMode == LibraryLayout.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.documents, key = { it.id }) { document ->
                            PdfGridCard(
                                document = document,
                                onClick = { onOpenDocument(document.id) },
                                onRemoveClick = { viewModel.requestRemoveDocument(document) }
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.documents, key = { it.id }) { document ->
                            PdfListRow(
                                document = document,
                                onClick = { onOpenDocument(document.id) },
                                onRemoveClick = { viewModel.requestRemoveDocument(document) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Removal Confirmation Dialog - Styled for Serene Editorial Reader
    uiState.documentPendingRemoval?.let { doc ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissRemoveDocument() },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Remove Volume?",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            },
            text = {
                Text(
                    text = "Remove \"${doc.title}\" and your reading progress from Vatca?\n\n" +
                           "Your original PDF file will remain untouched on your device storage.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmRemoveDocument() },
                    modifier = Modifier.testTag("confirm_remove_button")
                ) {
                    Text(
                        text = "Remove",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRemoveDocument() }) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryTopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    totalCount: Int,
    layoutMode: LibraryLayout,
    sortOrder: LibrarySort,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLayoutChange: (LibraryLayout) -> Unit,
    onSortChange: (LibrarySort) -> Unit,
    onOpenSettings: () -> Unit,
    onImportClick: () -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar: Only contains "Library" text, Search, and Settings (using non-gear Tune icon)
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            ),
            title = {
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                "Search volumes by title...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("search_text_field"),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                } else {
                    Text(
                        text = "Library",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onSearchToggle,
                    modifier = Modifier.testTag("search_button")
                ) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.Clear else Icons.Default.Search,
                        contentDescription = if (isSearchActive) "Close search" else "Search library",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune, // Non-gear settings icon
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        // Header Section below App Bar:
        // Left: Logo/Icon next to Vatca text
        // Right: Sort menu | Grid/List toggle | "+ Books" import button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon next to Vatca text
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_vatca_logo),
                    contentDescription = "Vatca Logo",
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Vatca",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (totalCount > 0) {
                        Text(
                            text = "$totalCount ${if (totalCount == 1) "volume" else "volumes"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Actions: Sort | Grid/List Toggle | "+ Books" Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Sort Menu Button
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("sort_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort library",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        shape = RoundedCornerShape(12.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        LibrarySort.entries.forEach { sort ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sort.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (sort == sortOrder) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (sort == sortOrder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                },
                                onClick = {
                                    onSortChange(sort)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }

                // Grid/List Layout Toggle
                IconButton(
                    onClick = {
                        val nextLayout = if (layoutMode == LibraryLayout.GRID) LibraryLayout.LIST else LibraryLayout.GRID
                        onLayoutChange(nextLayout)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("layout_toggle_button")
                ) {
                    Icon(
                        imageVector = if (layoutMode == LibraryLayout.GRID) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                        contentDescription = "Toggle view mode",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // "+ Books" Button in Header
                Button(
                    onClick = onImportClick,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("import_pdf_fab"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add book",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Books",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // Hairline divider beneath header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
private fun EmptyLibraryView(onImportClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Stacked Archival Paper Composition per Stitch design system
        Box(
            modifier = Modifier.size(width = 140.dp, height = 170.dp),
            contentAlignment = Alignment.Center
        ) {
            // Back Tilted Paper Sheet
            Surface(
                modifier = Modifier
                    .size(width = 110.dp, height = 145.dp)
                    .graphicsLayer {
                        rotationZ = -6f
                        translationX = -12f
                    },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {}

            // Foreground Main Paper Sheet
            Surface(
                modifier = Modifier.size(width = 115.dp, height = 150.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Simulated Left Book Crease
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterStart)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0x14000000), Color.Transparent)
                                )
                            )
                    )

                    // Warm Gilding Gold Bookmark Ribbon extending from top
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(36.dp)
                            .align(Alignment.TopEnd)
                            .padding(end = 12.dp)
                            .background(EditorialGold, shape = RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                    )

                    // Central Architectural PDF Icon
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF Volume",
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Your Library is Quiet",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "A paper-first digital sanctuary for long-form study, research, and reading. Import your PDF documents to begin.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onImportClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .height(48.dp)
                .testTag("empty_state_import_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Choose PDF File",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun PdfGridCard(
    document: PdfDocument,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("document_card_${document.id}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Thumbnail Area with 3:4 aspect ratio & simulated paper crease
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (document.thumbnailPath != null && File(document.thumbnailPath).exists()) {
                    AsyncImage(
                        model = File(document.thumbnailPath),
                        contentDescription = "Thumbnail for ${document.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF Icon",
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }

                // Simulated paper crease overlay across 8dp on the left edge
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0x14000000), // subtle tactile paper crease
                                    Color(0x05000000),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Progress Badge if started reading
                if (document.progressPercentage > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EditorialGold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${document.progressPercentage}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Custom Editorial Progress Track
            EditorialProgressBar(
                progress = document.progressPercentage / 100f
            )

            // Info Area
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Document options",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Open volume", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    showMenu = false
                                    onClick()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Remove from Library",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onRemoveClick()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${document.pageCount} pages",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (document.progressPercentage >= 100) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EditorialSage.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Read",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = EditorialSageText
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfListRow(
    document: PdfDocument,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    // Borderless list row (72dp spec from Serene Editorial Reader design system) with hairline divider
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("document_row_${document.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with simulated crease
            Box(
                modifier = Modifier
                    .size(width = 48.dp, height = 64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (document.thumbnailPath != null && File(document.thumbnailPath).exists()) {
                    AsyncImage(
                        model = File(document.thumbnailPath),
                        contentDescription = "Thumbnail for ${document.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Crease overlay
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0x14000000), Color.Transparent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${document.pageCount} pages",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (document.progressPercentage > 0) {
                        Text(
                            text = " • ${document.progressPercentage}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = EditorialGold
                        )
                    }
                    if (document.progressPercentage >= 100) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EditorialSage.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Read",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = EditorialSageText
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                EditorialProgressBar(
                    progress = document.progressPercentage / 100f
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Open volume", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Remove from Library",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            showMenu = false
                            onRemoveClick()
                        }
                    )
                }
            }
        }

        // Hairline divider inset by 64dp (16dp margin + 48dp thumbnail)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 64.dp)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
fun EditorialProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    ) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        if (clampedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = clampedProgress)
                    .fillMaxHeight()
                    .background(EditorialGold)
            )
        }
    }
}
