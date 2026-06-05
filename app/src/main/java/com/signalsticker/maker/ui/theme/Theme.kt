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
  canvas = Color(0xFFFAF9F5),
  primary = Color(0xFFCC785C),
  primaryActive = Color(0xFFA9583E),
  surface = Color(0xFFEFE9DE),
  dark = Color(0xFF181715),
  ink = Color(0xFF141413),
  body = Color(0xFF3D3D3A),
  muted = Color(0xFF6C6A64),
  mutedSoft = Color(0xFF8E8B82),
  hairline = Color(0xFFE6DFD8),
  onPrimary = Color(0xFFFFFFFF),
  onDark = Color(0xFFFAF9F5),
  error = Color(0xFFC64545),
  success = Color(0xFF5DB872),
)

private val Dark = AppColors(
  canvas = Color(0xFF181715),
  primary = Color(0xFFCC785C),
  primaryActive = Color(0xFFA9583E),
  surface = Color(0xFF252320),
  dark = Color(0xFF000000),
  ink = Color(0xFFFAF9F5),
  body = Color(0xFFA09D96),
  muted = Color(0xFF8E8B82),
  mutedSoft = Color(0xFF6C6A64),
  hairline = Color(0xFF353330),
  onPrimary = Color(0xFFFFFFFF),
  onDark = Color(0xFFFAF9F5),
  error = Color(0xFFC64545),
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
