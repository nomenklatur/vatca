package com.example.ui.reader

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.pdf.PdfDocumentSession
import com.example.data.model.FitMode
import com.example.data.model.PageMargin
import com.example.data.model.PageSpacing
import com.example.data.model.ReaderTheme
import com.example.data.model.ReadingMode
import com.example.data.model.ScreenOrientationPref
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    BackHandler {
        onBackToLibrary()
    }

    when (val state = uiState) {
        is ReaderUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Opening document...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        is ReaderUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cannot Open Document",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = onBackToLibrary) {
                                Text("Back to Library")
                            }
                            if (state.canRetry) {
                                Button(onClick = { viewModel.retryLoading() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }

        is ReaderUiState.Ready -> {
            ReaderContent(
                state = state,
                viewModel = viewModel,
                activity = activity,
                onBack = onBackToLibrary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderContent(
    state: ReaderUiState.Ready,
    viewModel: ReaderViewModel,
    activity: Activity?,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Handle Orientation preference
    DisposableEffect(state.preferences.screenOrientation, activity) {
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.let { act ->
            when (state.preferences.screenOrientation) {
                ScreenOrientationPref.PORTRAIT -> act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                ScreenOrientationPref.LANDSCAPE -> act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                ScreenOrientationPref.SENSOR -> act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    // Handle in-app brightness
    DisposableEffect(state.preferences.inAppBrightness, activity) {
        val window = activity?.window
        val lp = window?.attributes
        val originalBrightness = lp?.screenBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

        if (window != null && lp != null) {
            lp.screenBrightness = if (state.preferences.inAppBrightness < 0f) {
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            } else {
                state.preferences.inAppBrightness.coerceIn(0.05f, 1.0f)
            }
            window.attributes = lp
        }

        onDispose {
            if (window != null && lp != null) {
                lp.screenBrightness = originalBrightness
                window.attributes = lp
            }
        }
    }

    // Handle Immersive Fullscreen
    DisposableEffect(state.isFullscreen, activity) {
        val window = activity?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            if (state.isFullscreen) {
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Resolve Reader Backdrop Color
    val readerBgColor = when (state.preferences.readerTheme) {
        ReaderTheme.SYSTEM -> MaterialTheme.colorScheme.background
        ReaderTheme.WHITE -> Color(0xFFFFFFFF)
        ReaderTheme.SEPIA -> Color(0xFFFBF0D9)
        ReaderTheme.SLATE -> Color(0xFF1E222B)
        ReaderTheme.BLACK -> Color(0xFF000000)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBgColor)
    ) {
        // Document Page Viewport
        if (state.preferences.readingMode == ReadingMode.CONTINUOUS) {
            ContinuousPdfView(
                session = state.session,
                initialPage = state.document.lastReadPage,
                pageSpacing = state.preferences.pageSpacing,
                pageMargin = state.preferences.pageMargin,
                fitMode = state.preferences.fitMode,
                onPageChange = { page, offset -> viewModel.onPageChanged(page, offset) },
                onToggleControls = { viewModel.toggleControls() }
            )
        } else {
            SinglePagePdfView(
                session = state.session,
                currentPage = state.currentPage,
                pageMargin = state.preferences.pageMargin,
                fitMode = state.preferences.fitMode,
                onPageChange = { page -> viewModel.onPageChanged(page, 0f) },
                onToggleControls = { viewModel.toggleControls() }
            )
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = state.controlsVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ReaderTopControls(
                title = state.document.title,
                currentPage = state.currentPage + 1,
                totalPages = state.pageCount,
                isFullscreen = state.isFullscreen,
                onBack = onBack,
                onToggleFullscreen = { viewModel.toggleFullscreen() },
                onOpenSettings = { showSettingsSheet = true }
            )
        }

        AnimatedVisibility(
            visible = state.controlsVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ReaderBottomControls(
                currentPage = state.currentPage,
                pageCount = state.pageCount,
                readingMode = state.preferences.readingMode,
                readerTheme = state.preferences.readerTheme,
                onJumpToPage = { viewModel.jumpToPage(it) },
                onToggleMode = {
                    val nextMode = if (state.preferences.readingMode == ReadingMode.CONTINUOUS) {
                        ReadingMode.PAGE_BY_PAGE
                    } else {
                        ReadingMode.CONTINUOUS
                    }
                    viewModel.setReadingMode(nextMode)
                },
                onSelectTheme = { viewModel.setReaderTheme(it) }
            )
        }
    }

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        ReaderSettingsBottomSheet(
            preferences = state.preferences,
            onDismiss = { showSettingsSheet = false },
            onUpdateTheme = { viewModel.setReaderTheme(it) },
            onUpdateMode = { viewModel.setReadingMode(it) },
            onUpdateSpacing = { viewModel.setPageSpacing(it) },
            onUpdateMargin = { viewModel.setPageMargin(it) },
            onUpdateFitMode = { viewModel.setFitMode(it) },
            onUpdateOrientation = { viewModel.setScreenOrientation(it) },
            onUpdateBrightness = { viewModel.setInAppBrightness(it) }
        )
    }
}

@Composable
private fun ContinuousPdfView(
    session: PdfDocumentSession,
    initialPage: Int,
    pageSpacing: PageSpacing,
    pageMargin: PageMargin,
    fitMode: FitMode,
    onPageChange: (Int, Float) -> Unit,
    onToggleControls: () -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage.coerceIn(0, session.pageCount - 1))

    // Monitor visible page index
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { (page, offset) ->
                onPageChange(page, offset.toFloat())
            }
    }

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onToggleControls() },
                    onDoubleTap = {
                        scale = if (scale > 1.2f) 1f else 2f
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 3.5f)
                    if (scale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX
                translationY = offsetY
            }
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = pageMargin.dpValue.dp,
                end = pageMargin.dpValue.dp,
                top = 56.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(pageSpacing.dpValue.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(session.pageCount, key = { it }) { index ->
                PdfPageItem(
                    session = session,
                    pageIndex = index,
                    fitMode = fitMode
                )
            }
        }
    }
}

@Composable
private fun SinglePagePdfView(
    session: PdfDocumentSession,
    currentPage: Int,
    pageMargin: PageMargin,
    fitMode: FitMode,
    onPageChange: (Int) -> Unit,
    onToggleControls: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(currentPage) {
                detectTapGestures(
                    onTap = { offset ->
                        val width = size.width
                        when {
                            offset.x < width * 0.25f -> {
                                if (currentPage > 0) onPageChange(currentPage - 1)
                            }
                            offset.x > width * 0.75f -> {
                                if (currentPage < session.pageCount - 1) onPageChange(currentPage + 1)
                            }
                            else -> onToggleControls()
                        }
                    },
                    onDoubleTap = {
                        scale = if (scale > 1.2f) 1f else 2f
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
            .pointerInput(currentPage) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 3.5f)
                    if (scale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX
                translationY = offsetY
            }
            .padding(
                start = pageMargin.dpValue.dp,
                end = pageMargin.dpValue.dp,
                top = 64.dp,
                bottom = 100.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        PdfPageItem(
            session = session,
            pageIndex = currentPage,
            fitMode = fitMode
        )
    }
}

@Composable
private fun PdfPageItem(
    session: PdfDocumentSession,
    pageIndex: Int,
    fitMode: FitMode
) {
    val density = LocalDensity.current
    val dimension = session.pageDimensions.getOrNull(pageIndex)
    val aspectRatio = dimension?.aspectRatio ?: 0.75f

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(2.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val targetWidthPx = with(density) { maxWidth.roundToPx() }

        val pageBitmap by produceState<Bitmap?>(initialValue = null, key1 = pageIndex, key2 = targetWidthPx) {
            value = session.renderPage(pageIndex, targetWidthPx)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = pageBitmap
            if (bitmap != null && !bitmap.isRecycled) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Page ${pageIndex + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = if (fitMode == FitMode.FIT_TO_WIDTH) ContentScale.FillWidth else ContentScale.Fit
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderTopControls(
    title: String,
    currentPage: Int,
    totalPages: Int,
    isFullscreen: Boolean,
    onBack: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 4.dp
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("reader_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Library"
                    )
                }
            },
            title = {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Page $currentPage of $totalPages",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onToggleFullscreen,
                    modifier = Modifier.testTag("fullscreen_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isFullscreen) "Exit fullscreen" else "Fullscreen reading"
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("reader_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Reading settings"
                    )
                }
            }
        )
    }
}

@Composable
private fun ReaderBottomControls(
    currentPage: Int,
    pageCount: Int,
    readingMode: ReadingMode,
    readerTheme: ReaderTheme,
    onJumpToPage: (Int) -> Unit,
    onToggleMode: () -> Unit,
    onSelectTheme: (ReaderTheme) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Scrubber Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (currentPage > 0) onJumpToPage(currentPage - 1) },
                    enabled = currentPage > 0,
                    modifier = Modifier.testTag("prev_page_button")
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous page")
                }

                Slider(
                    value = currentPage.toFloat(),
                    onValueChange = { onJumpToPage(it.toInt()) },
                    valueRange = 0f..(pageCount - 1).coerceAtLeast(1).toFloat(),
                    steps = (pageCount - 2).coerceAtLeast(0),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("page_scrubber_slider")
                )

                IconButton(
                    onClick = { if (currentPage < pageCount - 1) onJumpToPage(currentPage + 1) },
                    enabled = currentPage < pageCount - 1,
                    modifier = Modifier.testTag("next_page_button")
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next page")
                }
            }

            // Quick Theme & Mode Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Theme Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val themes = listOf(ReaderTheme.SEPIA, ReaderTheme.WHITE, ReaderTheme.SLATE, ReaderTheme.BLACK)
                    themes.forEach { theme ->
                        val isSelected = readerTheme == theme
                        val swatchColor = when (theme) {
                            ReaderTheme.SEPIA -> Color(0xFFFBF0D9)
                            ReaderTheme.WHITE -> Color(0xFFFFFFFF)
                            ReaderTheme.SLATE -> Color(0xFF232833)
                            ReaderTheme.BLACK -> Color(0xFF000000)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(swatchColor)
                                .clickable { onSelectTheme(theme) }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (theme == ReaderTheme.SEPIA || theme == ReaderTheme.WHITE) Color.Black else Color.White)
                                )
                            }
                        }
                    }
                }

                // Quick Mode Switch Button
                OutlinedButton(
                    onClick = onToggleMode,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (readingMode == ReadingMode.CONTINUOUS) "Scroll" else "Pages",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderSettingsBottomSheet(
    preferences: UserPreferences,
    onDismiss: () -> Unit,
    onUpdateTheme: (ReaderTheme) -> Unit,
    onUpdateMode: (ReadingMode) -> Unit,
    onUpdateSpacing: (PageSpacing) -> Unit,
    onUpdateMargin: (PageMargin) -> Unit,
    onUpdateFitMode: (FitMode) -> Unit,
    onUpdateOrientation: (ScreenOrientationPref) -> Unit,
    onUpdateBrightness: (Float) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Reader Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reading Mode
            Text(
                text = "Navigation Mode",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReadingMode.values().forEach { mode ->
                    FilterChip(
                        selected = preferences.readingMode == mode,
                        onClick = { onUpdateMode(mode) },
                        label = { Text(mode.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Backdrop Theme
            Text(
                text = "Reader Backdrop",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReaderTheme.values().forEach { theme ->
                    FilterChip(
                        selected = preferences.readerTheme == theme,
                        onClick = { onUpdateTheme(theme) },
                        label = { Text(theme.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Page Spacing
            Text(
                text = "Vertical Page Spacing",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PageSpacing.values().forEach { spacing ->
                    FilterChip(
                        selected = preferences.pageSpacing == spacing,
                        onClick = { onUpdateSpacing(spacing) },
                        label = { Text(spacing.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Margins
            Text(
                text = "Horizontal Reading Margins",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PageMargin.values().forEach { margin ->
                    FilterChip(
                        selected = preferences.pageMargin == margin,
                        onClick = { onUpdateMargin(margin) },
                        label = { Text(margin.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Screen Orientation
            Text(
                text = "Orientation",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScreenOrientationPref.values().forEach { orientation ->
                    FilterChip(
                        selected = preferences.screenOrientation == orientation,
                        onClick = { onUpdateOrientation(orientation) },
                        label = { Text(orientation.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // In-App Brightness
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "In-App Brightness",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (preferences.inAppBrightness < 0f) "System" else "Custom",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = preferences.inAppBrightness >= 0f,
                        onCheckedChange = { checked ->
                            onUpdateBrightness(if (checked) 0.7f else -1f)
                        }
                    )
                }
            }
            if (preferences.inAppBrightness >= 0f) {
                Slider(
                    value = preferences.inAppBrightness.coerceIn(0.05f, 1.0f),
                    onValueChange = { onUpdateBrightness(it) },
                    valueRange = 0.05f..1.0f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
