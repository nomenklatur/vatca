package com.example.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.PreferencesManager
import com.example.data.model.AppThemeMode
import com.example.data.model.LibraryLayout
import com.example.data.model.LibrarySort
import com.example.data.model.PageMargin
import com.example.data.model.PageSpacing
import com.example.data.model.ReaderTheme
import com.example.data.model.ReadingMode
import com.example.data.model.ScreenOrientationPref
import com.example.data.model.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preferences by preferencesManager.userPreferencesFlow.collectAsStateWithLifecycle(initialValue = UserPreferences())
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeader(icon = Icons.Default.Palette, title = "Appearance")
            }

            item {
                SettingsCard {
                    Text(
                        text = "Application Theme",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.values().forEach { mode ->
                            FilterChip(
                                selected = preferences.appTheme == mode,
                                onClick = {
                                    coroutineScope.launch { preferencesManager.setAppTheme(mode) }
                                },
                                label = { Text(mode.title) }
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader(icon = Icons.Default.ViewList, title = "Library Defaults")
            }

            item {
                SettingsCard {
                    Text(
                        text = "Default View Layout",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LibraryLayout.values().forEach { layout ->
                            FilterChip(
                                selected = preferences.libraryLayout == layout,
                                onClick = {
                                    coroutineScope.launch { preferencesManager.setLibraryLayout(layout) }
                                },
                                label = { Text(layout.title) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default Sorting Order",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        LibrarySort.values().forEach { sort ->
                            FilterChip(
                                selected = preferences.librarySort == sort,
                                onClick = {
                                    coroutineScope.launch { preferencesManager.setLibrarySort(sort) }
                                },
                                label = { Text(sort.title) }
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader(icon = Icons.Default.Palette, title = "Reader Defaults")
            }

            item {
                SettingsCard {
                    Text(
                        text = "Default Reader Backdrop",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReaderTheme.values().forEach { theme ->
                            FilterChip(
                                selected = preferences.readerTheme == theme,
                                onClick = {
                                    coroutineScope.launch { preferencesManager.setReaderTheme(theme) }
                                },
                                label = { Text(theme.title) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default Reading Mode",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReadingMode.values().forEach { mode ->
                            FilterChip(
                                selected = preferences.readingMode == mode,
                                onClick = {
                                    coroutineScope.launch { preferencesManager.setReadingMode(mode) }
                                },
                                label = { Text(mode.title) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default Page Spacing",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PageSpacing.values().forEach { spacing ->
                            FilterChip(
                                selected = preferences.pageSpacing == spacing,
                                onClick = {
                                    coroutineScope.launch { preferencesManager.setPageSpacing(spacing) }
                                },
                                label = { Text(spacing.title) }
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader(icon = Icons.Default.Info, title = "About Vatca")
            }

            item {
                SettingsCard {
                    Text(
                        text = "Vatca v1.0",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Minimal, calm, and offline-first native Android PDF reader.\n" +
                                "• 100% private: no tracking, accounts, or analytics\n" +
                                "• Zero ads and completely free\n" +
                                "• High performance native Android PdfRenderer\n" +
                                "• Reading progress stored locally on your device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}
