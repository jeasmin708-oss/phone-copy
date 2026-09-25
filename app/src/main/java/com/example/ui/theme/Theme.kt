package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CalcAccentAmber,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF422100),
    onPrimaryContainer = Color(0xFFFFDCC1),
    secondary = CalcAccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D68),
    onSecondaryContainer = Color(0xFFBCE9FF),
    background = ObsidianBg,
    onBackground = Color(0xFFE3E2E6),
    surface = SurfaceDark,
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC4C6D0),
    error = CalcErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CalcAccentAmber,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCC1),
    onPrimaryContainer = Color(0xFF422100),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBCE9FF),
    onSecondaryContainer = Color(0xFF001F2B),
    background = TitaniumBg,
    onBackground = Color(0xFF191C1E),
    surface = SurfaceLight,
    onSurface = Color(0xFF191C1E),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF44474E),
    error = CalcErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set default to false for dynamic color to preserve custom rich calculator amber/obsidian theme
    dynamicColor: Boolean = false,
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
        typography = Typography,
        content = content
    )
}
