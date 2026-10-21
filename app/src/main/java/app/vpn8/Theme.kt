@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package com.pixvpn.gram

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Extra brand colors that Material's color scheme has no slot for. */
@Immutable
class Extra(val a: Color, val b: Color, val busy: Color) {
    val gradient: Brush get() = Brush.linearGradient(listOf(a, b))
}

val LocalExtra = staticCompositionLocalOf { Extra(Color(0xFF3DDBC4), Color(0xFF7C8CFF), Color(0xFFFFB84D)) }

// Botanical lime + lilac: bright actions against quiet, plum-tinted surfaces.
// Every accent has an explicit on-color; dynamic wallpaper palettes remain supported.
private val Dark = darkColorScheme(
    primary = Color(0xFFD5ED9B), onPrimary = Color(0xFF263500),
    primaryContainer = Color(0xFF3D5017), onPrimaryContainer = Color(0xFFE0F8AD),
    secondary = Color(0xFFD3BEF4), onSecondary = Color(0xFF392751),
    secondaryContainer = Color(0xFF514068), onSecondaryContainer = Color(0xFFEEDDFF),
    tertiary = Color(0xFFFFB3C1), onTertiary = Color(0xFF521D2C),
    tertiaryContainer = Color(0xFF703342), onTertiaryContainer = Color(0xFFFFD9E0),
    background = Color(0xFF141318), onBackground = Color(0xFFE8E0E9),
    surface = Color(0xFF141318), onSurface = Color(0xFFE8E0E9),
    surfaceVariant = Color(0xFF49454F), onSurfaceVariant = Color(0xFFCBC4D0),
    surfaceContainerLowest = Color(0xFF0F0D12), surfaceContainerLow = Color(0xFF1D1B21),
    surfaceContainer = Color(0xFF211F26), surfaceContainerHigh = Color(0xFF2C2931),
    surfaceContainerHighest = Color(0xFF37343C),
    outline = Color(0xFF958E9A), outlineVariant = Color(0xFF49454F),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
)

private val Light = lightColorScheme(
    primary = Color(0xFF486318), onPrimary = Color.White,
    primaryContainer = Color(0xFFD5ED9B), onPrimaryContainer = Color(0xFF293F00),
    secondary = Color(0xFF695382), onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEDDFF), onSecondaryContainer = Color(0xFF3F2C57),
    tertiary = Color(0xFF8D495A), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E0), onTertiaryContainer = Color(0xFF713343),
    background = Color(0xFFFCF8FD), onBackground = Color(0xFF1D1B21),
    surface = Color(0xFFFCF8FD), onSurface = Color(0xFF1D1B21),
    surfaceVariant = Color(0xFFE8E0EB), onSurfaceVariant = Color(0xFF49454F),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFF6F2F7),
    surfaceContainer = Color(0xFFF0ECF1), surfaceContainerHigh = Color(0xFFEAE6EB),
    surfaceContainerHighest = Color(0xFFE4E0E5),
    outline = Color(0xFF7A747F), outlineVariant = Color(0xFFCBC4D0),
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF93000A),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

private val ExpressiveType = Typography(
    displaySmall = TextStyle(fontSize = 40.sp, lineHeight = 46.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontSize = 34.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, letterSpacing = (-.6).sp),
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
)

@Composable
fun AppTheme(s: Settings, dark: Boolean, content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val dynamic = s.dynamicColor && Build.VERSION.SDK_INT >= 31
    val scheme = when {
        dynamic -> if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        dark -> Dark
        else -> Light
    }
    val extra = if (dynamic) Extra(scheme.primary, scheme.tertiary, scheme.tertiary)
    else Extra(scheme.secondary, scheme.primary, scheme.tertiary)
    CompositionLocalProvider(LocalExtra provides extra) {
        MaterialExpressiveTheme(colorScheme = scheme, shapes = AppShapes,
            motionScheme = MotionScheme.expressive(), typography = ExpressiveType, content = content)
    }
}

@Composable
fun isDark(s: Settings): Boolean = when (s.theme) {
    "dark" -> true
    "light" -> false
    else -> isSystemInDarkTheme()
}
