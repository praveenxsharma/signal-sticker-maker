package com.signalsticker.maker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
  val canvas: Color,
  val primary: Color,
  val primaryActive: Color,
  val surface: Color,
  val dark: Color,
  val ink: Color,
  val body: Color,
  val muted: Color,
  val mutedSoft: Color,
  val hairline: Color,
  val onPrimary: Color,
  val onDark: Color,
  val error: Color,
  val success: Color,
)

val LocalColors = staticCompositionLocalOf<AppColors> { error("AppColors not provided") }

val C: AppColors
  @Composable get() = LocalColors.current

private val Light = AppColors(
  canvas = Color(0xFFF8F8F8),
  primary = Color(0xFFCC785C),
  primaryActive = Color(0xFFA9583E),
  surface = Color(0xFFFFFFFF),
  dark = Color(0xFF1C1B1F),
  ink = Color(0xFF1C1B1F),
  body = Color(0xFF49454F),
  muted = Color(0xFF7A7682),
  mutedSoft = Color(0xFF9E9BA6),
  hairline = Color(0xFFE7E0EC),
  onPrimary = Color(0xFFFFFFFF),
  onDark = Color(0xFFF8F8F8),
  error = Color(0xFFC64545),
  success = Color(0xFF5DB872),
)

private val Dark = AppColors(
  canvas = Color(0xFF181715),       // surface-dark
  primary = Color(0xFFCC785C),       // primary (unchanged)
  primaryActive = Color(0xFFD9917A), // lighter coral for dark bg
  surface = Color(0xFF252320),       // surface-dark-elevated
  dark = Color(0xFF000000),          // true black top bar
  ink = Color(0xFFFAF9F5),          // on-dark cream text
  body = Color(0xFFC8C3B8),         // slightly muted cream
  muted = Color(0xFFA09D96),        // on-dark-soft
  mutedSoft = Color(0xFF7A7670),    // further muted
  hairline = Color(0xFF353330),     // subtle dark divider
  onPrimary = Color(0xFFFFFFFF),
  onDark = Color(0xFFFAF9F5),       // on-dark
  error = Color(0xFFE87474),        // lighter red for dark bg
  success = Color(0xFF5DB872),
)

@Composable
fun StickerPackTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colors = if (darkTheme) Dark else Light
  CompositionLocalProvider(LocalColors provides colors) {
    content()
  }
}
