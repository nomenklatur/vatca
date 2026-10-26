package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppThemeMode
import com.example.data.model.FitMode
import com.example.data.model.LibraryLayout
import com.example.data.model.LibrarySort
import com.example.data.model.PageMargin
import com.example.data.model.PageSpacing
import com.example.data.model.ReaderTheme
import com.example.data.model.ReadingMode
import com.example.data.model.ScreenOrientationPref
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vatca_preferences")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val LIBRARY_LAYOUT = stringPreferencesKey("library_layout")
        val LIBRARY_SORT = stringPreferencesKey("library_sort")
        val READER_THEME = stringPreferencesKey("reader_theme")
        val READING_MODE = stringPreferencesKey("reading_mode")
        val PAGE_SPACING = stringPreferencesKey("page_spacing")
        val PAGE_MARGIN = stringPreferencesKey("page_margin")
        val FIT_MODE = stringPreferencesKey("fit_mode")
        val SCREEN_ORIENTATION = stringPreferencesKey("screen_orientation")
        val IN_APP_BRIGHTNESS = floatPreferencesKey("in_app_brightness")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val appThemeStr = prefs[Keys.APP_THEME] ?: AppThemeMode.SYSTEM.name
        val libraryLayoutStr = prefs[Keys.LIBRARY_LAYOUT] ?: LibraryLayout.GRID.name
        val librarySortStr = prefs[Keys.LIBRARY_SORT] ?: LibrarySort.RECENTLY_OPENED.name
        val readerThemeStr = prefs[Keys.READER_THEME] ?: ReaderTheme.SEPIA.name
        val readingModeStr = prefs[Keys.READING_MODE] ?: ReadingMode.CONTINUOUS.name
        val pageSpacingStr = prefs[Keys.PAGE_SPACING] ?: PageSpacing.NORMAL.name
        val pageMarginStr = prefs[Keys.PAGE_MARGIN] ?: PageMargin.COMPACT.name
        val fitModeStr = prefs[Keys.FIT_MODE] ?: FitMode.FIT_TO_WIDTH.name
        val orientationStr = prefs[Keys.SCREEN_ORIENTATION] ?: ScreenOrientationPref.SENSOR.name
        val brightness = prefs[Keys.IN_APP_BRIGHTNESS] ?: -1f

        UserPreferences(
            appTheme = safeValueOf(appThemeStr, AppThemeMode.SYSTEM),
            libraryLayout = safeValueOf(libraryLayoutStr, LibraryLayout.GRID),
            librarySort = safeValueOf(librarySortStr, LibrarySort.RECENTLY_OPENED),
            readerTheme = safeValueOf(readerThemeStr, ReaderTheme.SEPIA),
            readingMode = safeValueOf(readingModeStr, ReadingMode.CONTINUOUS),
            pageSpacing = safeValueOf(pageSpacingStr, PageSpacing.NORMAL),
            pageMargin = safeValueOf(pageMarginStr, PageMargin.COMPACT),
            fitMode = safeValueOf(fitModeStr, FitMode.FIT_TO_WIDTH),
            screenOrientation = safeValueOf(orientationStr, ScreenOrientationPref.SENSOR),
            inAppBrightness = brightness
        )
    }

    suspend fun setAppTheme(theme: AppThemeMode) {
        context.dataStore.edit { it[Keys.APP_THEME] = theme.name }
    }

    suspend fun setLibraryLayout(layout: LibraryLayout) {
        context.dataStore.edit { it[Keys.LIBRARY_LAYOUT] = layout.name }
    }

    suspend fun setLibrarySort(sort: LibrarySort) {
        context.dataStore.edit { it[Keys.LIBRARY_SORT] = sort.name }
    }

    suspend fun setReaderTheme(theme: ReaderTheme) {
        context.dataStore.edit { it[Keys.READER_THEME] = theme.name }
    }

    suspend fun setReadingMode(mode: ReadingMode) {
        context.dataStore.edit { it[Keys.READING_MODE] = mode.name }
    }

    suspend fun setPageSpacing(spacing: PageSpacing) {
        context.dataStore.edit { it[Keys.PAGE_SPACING] = spacing.name }
    }

    suspend fun setPageMargin(margin: PageMargin) {
        context.dataStore.edit { it[Keys.PAGE_MARGIN] = margin.name }
    }

    suspend fun setFitMode(fitMode: FitMode) {
        context.dataStore.edit { it[Keys.FIT_MODE] = fitMode.name }
    }

    suspend fun setScreenOrientation(orientation: ScreenOrientationPref) {
        context.dataStore.edit { it[Keys.SCREEN_ORIENTATION] = orientation.name }
    }

    suspend fun setInAppBrightness(brightness: Float) {
        context.dataStore.edit { it[Keys.IN_APP_BRIGHTNESS] = brightness }
    }

    private inline fun <reified T : Enum<T>> safeValueOf(value: String, default: T): T {
        return try {
            java.lang.Enum.valueOf(T::class.java, value)
        } catch (_: Exception) {
            default
        }
    }
}
