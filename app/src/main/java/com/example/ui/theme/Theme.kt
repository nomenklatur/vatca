package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EditorialPrimaryDark,
    onPrimary = EditorialOnPrimaryDark,
    primaryContainer = EditorialPrimaryContainerDark,
    onPrimaryContainer = EditorialPrimaryDark,
    secondary = EditorialSage,
    onSecondary = EditorialOnPrimaryDark,
    secondaryContainer = EditorialSurfaceVariantDark,
    onSecondaryContainer = EditorialSage,
    tertiary = EditorialGold,
    onTertiary = EditorialOnGold,
    background = EditorialCanvasDark,
    onBackground = EditorialTextPrimaryDark,
    surface = EditorialSurfaceDark,
    onSurface = EditorialTextPrimaryDark,
    surfaceVariant = EditorialSurfaceVariantDark,
    onSurfaceVariant = EditorialTextSecondaryDark,
    outline = EditorialHairlineDark,
    outlineVariant = EditorialHairlineDark,
    error = EditorialError,
    onError = EditorialOnError
)

private val LightColorScheme = lightColorScheme(
    primary = EditorialPrimaryNavy,
    onPrimary = EditorialOnPrimary,
    primaryContainer = EditorialPrimaryContainer,
    onPrimaryContainer = EditorialOnPrimaryContainer,
    secondary = EditorialSage,
    onSecondary = EditorialOnPrimary,
    secondaryContainer = EditorialSageContainer,
    onSecondaryContainer = EditorialOnSageContainer,
    tertiary = EditorialGold,
    onTertiary = EditorialOnGold,
    background = EditorialCanvasLight,
    onBackground = EditorialTextPrimaryLight,
    surface = EditorialSurfaceLight,
    onSurface = EditorialTextPrimaryLight,
    surfaceVariant = EditorialSurfaceVariantLight,
    onSurfaceVariant = EditorialTextSecondaryLight,
    outline = EditorialHairlineLight,
    outlineVariant = EditorialHairlineLight,
    error = EditorialError,
    onError = EditorialOnError
)

@Composable
fun VatcaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent Serene Editorial styling
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VatcaTypography,
        content = content
    )
}
