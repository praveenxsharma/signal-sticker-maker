package com.signalsticker.maker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.signalsticker.maker.ui.screens.HomeScreen
import com.signalsticker.maker.ui.screens.ExportScreen
import com.signalsticker.maker.ui.theme.StickerPackTheme
import com.signalsticker.maker.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      StickerPackTheme {
        val vm: MainViewModel = viewModel()
        val nav = rememberNavController()
        NavHost(nav, startDestination = "home") {
          composable("home") {
            HomeScreen(vm, onNavExport = { nav.navigate("export") })
          }
          composable("export") {
            ExportScreen(vm, onBack = { nav.popBackStack() })
          }
        }
      }
    }
  }
}
