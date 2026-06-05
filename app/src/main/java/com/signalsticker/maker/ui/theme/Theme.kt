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

val C: AppColors get() = LocalColors.current

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
  canvas = Color(0xFF1C1B1F),
  primary = Color(0xFFCC785C),
  primaryActive = Color(0xFFD9917A),
  surface = Color(0xFF2B2930),
  dark = Color(0xFF000000),
  ink = Color(0xFFE6E1E5),
  body = Color(0xFFCAC4D0),
  muted = Color(0xFF9E9BA6),
  mutedSoft = Color(0xFF7A7682),
  hairline = Color(0xFF49454F),
  onPrimary = Color(0xFFFFFFFF),
  onDark = Color(0xFFE6E1E5),
  error = Color(0xFFF2B8B8),
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
