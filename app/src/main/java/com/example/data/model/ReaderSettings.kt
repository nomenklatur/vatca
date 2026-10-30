package com.example.data.model

enum class ReaderTheme(val title: String, val bgHex: Long, val textHex: Long) {
    SYSTEM("Follow App", 0L, 0L),
    WHITE("Daylight", 0xFFFFFFFF, 0xFF1E293B),
    SEPIA("Parchment", 0xFFFBF0D9, 0xFF3D3222),
    SLATE("Calm Slate", 0xFF1E222B, 0xFFE2E8F0),
    BLACK("OLED Black", 0xFF000000, 0xFFE2E8F0)
}

enum class ReadingMode(val title: String) {
    CONTINUOUS("Continuous Scroll"),
    PAGE_BY_PAGE("Page by Page")
}

enum class PageTransition(val title: String) {
    CURL_FLIP("Book Page Flip"),
    SLIDE("Slide"),
    FADE("Fade")
}

enum class PageSpacing(val title: String, val dpValue: Int) {
    NONE("None", 0),
    COMPACT("Compact (8dp)", 8),
    NORMAL("Standard (16dp)", 16),
    SPACIOUS("Spacious (24dp)", 24)
}

enum class PageMargin(val title: String, val dpValue: Int) {
    NONE("Full Bleed", 0),
    COMPACT("Compact (8dp)", 8),
    NORMAL("Standard (16dp)", 16)
}

enum class FitMode(val title: String) {
    FIT_TO_WIDTH("Fit Width"),
    FIT_TO_PAGE("Fit Page")
}

enum class ScreenOrientationPref(val title: String) {
    SENSOR("Auto / Sensor"),
    PORTRAIT("Portrait"),
    LANDSCAPE("Landscape")
}

enum class LibraryLayout(val title: String) {
    GRID("Grid"),
    LIST("List")
}

enum class LibrarySort(val title: String) {
    RECENTLY_OPENED("Recently Read"),
    DATE_ADDED("Date Added"),
    TITLE("Title (A-Z)"),
    PROGRESS("Reading Progress")
}

enum class AppThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

data class UserPreferences(
    val appTheme: AppThemeMode = AppThemeMode.SYSTEM,
    val libraryLayout: LibraryLayout = LibraryLayout.GRID,
    val librarySort: LibrarySort = LibrarySort.RECENTLY_OPENED,
    val readerTheme: ReaderTheme = ReaderTheme.SEPIA,
    val readingMode: ReadingMode = ReadingMode.CONTINUOUS,
    val pageTransition: PageTransition = PageTransition.CURL_FLIP,
    val pageSpacing: PageSpacing = PageSpacing.NORMAL,
    val pageMargin: PageMargin = PageMargin.COMPACT,
    val fitMode: FitMode = FitMode.FIT_TO_WIDTH,
    val screenOrientation: ScreenOrientationPref = ScreenOrientationPref.SENSOR,
    val inAppBrightness: Float = -1f // -1f means system default brightness
)
