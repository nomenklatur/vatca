package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.data.model.PageSpacing
import com.example.data.model.ReaderTheme
import com.example.data.model.ReadingMode
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
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
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingOptionSelector(
                        options = AppThemeMode.entries,
                        selectedOption = preferences.appTheme,
                        onOptionSelected = { mode ->
                            coroutineScope.launch { preferencesManager.setAppTheme(mode) }
                        },
                        labelProvider = { it.title }
                    )
                }
            }

            item {
                SectionHeader(icon = Icons.AutoMirrored.Filled.ViewList, title = "Library Defaults")
            }

            item {
                SettingsCard {
                    Text(
                        text = "Default View Layout",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingOptionSelector(
                        options = LibraryLayout.entries,
                        selectedOption = preferences.libraryLayout,
                        onOptionSelected = { layout ->
                            coroutineScope.launch { preferencesManager.setLibraryLayout(layout) }
                        },
                        labelProvider = { it.title }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default Sorting Order",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingOptionSelector(
                        options = LibrarySort.entries,
                        selectedOption = preferences.librarySort,
                        onOptionSelected = { sort ->
                            coroutineScope.launch { preferencesManager.setLibrarySort(sort) }
                        },
                        labelProvider = { it.title }
                    )
                }
            }

            item {
                SectionHeader(icon = Icons.Default.Palette, title = "Reader Defaults")
            }

            item {
                SettingsCard {
                    Text(
                        text = "Default Reader Backdrop",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingOptionSelector(
                        options = ReaderTheme.entries,
                        selectedOption = preferences.readerTheme,
                        onOptionSelected = { theme ->
                            coroutineScope.launch { preferencesManager.setReaderTheme(theme) }
                        },
                        labelProvider = { it.title }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default Reading Mode",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingOptionSelector(
                        options = ReadingMode.entries,
                        selectedOption = preferences.readingMode,
                        onOptionSelected = { mode ->
                            coroutineScope.launch { preferencesManager.setReadingMode(mode) }
                        },
                        labelProvider = { it.title }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default Page Switch Animation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingOptionSelector(
                        options = com.example.data.model.PageTransition.entries,
                        selectedOption = preferences.pageTransition,
                        onOptionSelected = { transition ->
                            coroutineScope.launch { preferencesManager.setPageTransition(transition) }
                        },
                        labelProvider = { it.title }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default Page Spacing",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingOptionSelector(
                        options = PageSpacing.entries,
                        selectedOption = preferences.pageSpacing,
                        onOptionSelected = { spacing ->
                            coroutineScope.launch { preferencesManager.setPageSpacing(spacing) }
                        },
                        labelProvider = { it.title }
                    )
                }
            }

            item {
                SectionHeader(icon = Icons.Default.Info, title = "About Vatca")
            }

            item {
                SettingsCard {
                    Text(
                        text = "Vatca • Serene Editorial Reader",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A paper-first digital sanctuary for long-form study, research, and reading.\n\n" +
                                "• Archival paper-inspired palette and Inter typography\n" +
                                "• 100% private: no tracking, accounts, or analytics\n" +
                                "• High-performance native Android PdfRenderer\n" +
                                "• Reading progress and library stored locally on your device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> SettingOptionSelector(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            Surface(
                onClick = { onOptionSelected(option) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Text(
                    text = labelProvider(option),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
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
            modifier = Modifier
                .size(18.dp)
                .padding(end = 4.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}
