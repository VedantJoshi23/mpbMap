package io.github.vedantjoshi23.kairos.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF0E6B6A), onPrimary = Color.White,
    secondary = Color(0xFFB4530A), background = Color(0xFFF4F6F3), surface = Color.White,
    onBackground = Color(0xFF13212B), onSurface = Color(0xFF13212B),
)
private val Dark = darkColorScheme(
    primary = Color(0xFF5CC2B8), onPrimary = Color(0xFF00201E),
    secondary = Color(0xFFF0A35E), background = Color(0xFF0E171D), surface = Color(0xFF14212A),
    onBackground = Color(0xFFE4ECEA), onSurface = Color(0xFFE4ECEA),
)

/** UI chrome uses these tokens, never skin colours (STD-ACCESSIBILITY rule 5). */
@Composable
fun KairosTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
