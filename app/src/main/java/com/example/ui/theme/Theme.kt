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

import androidx.compose.ui.graphics.Color

import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF4FC3F7),      // Bright Radiant Sky Blue for dark mode
    secondary = Color(0xFF80DEEA),    // Waterfall Turquoise/Aqua accent
    tertiary = Color(0xFF29B6F6),     // Vivid Sea Blue
    background = Color(0xFF0B132B),   // Dark abyssal/midnight ocean background
    surface = Color(0xFF1C2541),      // Deep navy oceanic card surface
    onPrimary = Color(0xFF01579B),    // Deepest sea contrast text
    onSecondary = Color(0xFF004D40),  // Deep forest/waterfall teal text
    onBackground = Color(0xFFE0F7FA), // Soft water-spray white
    onSurface = Color(0xFFE0F7FA),    // Soft water-spray white
    primaryContainer = Color(0xFF0288D1),
    onPrimaryContainer = Color(0xFFE0F7FA)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF0288D1),       // Signature Deep Sea Blue
    secondary = Color(0xFF00ACC1),     // Refreshing Waterfall Turquoise
    tertiary = Color(0xFF29B6F6),      // Vivid Sky Blue
    background = Color(0xFFF0F8FF),    // Alice Blue - extremely soft flowing water-colored background
    surface = Color(0xFFFFFFFF),       // Clean sparkling foam surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF01579B),  // Dark sea-blue text
    onSurface = Color(0xFF0D1B2A),     // Navy-black for high legibility
    primaryContainer = Color(0xFFE0F7FA), // Refreshing cool mist container
    onPrimaryContainer = Color(0xFF006064)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = Color.Transparent.toArgb()
      window.navigationBarColor = Color.Transparent.toArgb()
      val insetsController = WindowCompat.getInsetsController(window, view)
      insetsController.isAppearanceLightStatusBars = !darkTheme
      insetsController.isAppearanceLightNavigationBars = !darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
