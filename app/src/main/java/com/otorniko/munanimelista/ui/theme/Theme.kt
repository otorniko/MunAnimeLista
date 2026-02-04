package com.otorniko.munanimelista.ui.theme

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
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80
                                             )

/*private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

     Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),

)*/
private val LightColorScheme = lightColorScheme(
        // 1. Main Bars (TopBar, Buttons)
        primary = BrandDarkBlue,
        onPrimary = White,
        // 2. The Canvas (The App Background)
        background = White,
        onBackground = Color.Black,
        // 3. Cards & Sheets
        surface = White,
        onSurface = Color.Black,
        // 4. Drawer / BottomSheet specific
        surfaceContainer = BrandLightBlue,
        onSurfaceVariant = LightGrey, // Icons/Subtext color
        surfaceContainerLow = BrandLightBlue,
        // 5. Container? (Chips)
        primaryContainer = BrandDarkBlue.copy(alpha = 0.1f),
        onPrimaryContainer = BrandDarkBlue,
                                               )

@Composable
fun MunAnimeListaTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        dynamicColor: Boolean = false,
        content: @Composable () -> Unit
                      ) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        //darkTheme -> DarkColorScheme
        darkTheme -> LightColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
                 )
}