package com.signalsticker.maker.ui.screens

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.signalsticker.maker.ui.theme.C
import com.signalsticker.maker.viewmodel.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(vm: MainViewModel, onBack: () -> Unit) {
  val st by vm.s.collectAsState()
  val ctx = LocalContext.current
  var saved by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = C.canvas,
    topBar = {
      TopAppBar(
        title = { Text("Preview & Export", fontWeight = FontWeight(400)) },
        navigationIcon = { TextButton(onClick = onBack) { Text("Back", color = C.onDark) } },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = C.dark,
          titleContentColor = C.onDark,
        )
      )
    }
  ) { pad ->
    Column(
      modifier = Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text("Preview", fontWeight = FontWeight(400), fontSize = 28.sp, color = C.ink)
      Spacer(Modifier.height(4.dp))
      Text("${st.title} — ${st.author}", color = C.muted, fontSize = 14.sp)
      Text("${st.stickers.size} stickers", color = C.mutedSoft, fontSize = 13.sp)
      Spacer(Modifier.height(20.dp))

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(st.stickers) { i, s ->
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
              model = Uri.parse(s.uri),
              contentDescription = "sticker $i",
              modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(C.surface).border(1.dp, C.hairline, RoundedCornerShape(12.dp)),
            )
            Text(s.emoji, fontSize = 20.sp)
          }
        }
      }

      Spacer(Modifier.height(24.dp))
      HorizontalDivider(color = C.hairline)
      Spacer(Modifier.height(16.dp))

      Text("Emoji", fontWeight = FontWeight(500), fontSize = 15.sp, color = C.ink)
      Spacer(Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        itemsIndexed(st.stickers) { i, s ->
          var editing by remember { mutableStateOf(false) }
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { editing = true },
          ) {
            Text("#${i + 1}", fontSize = 10.sp, color = C.mutedSoft)
            Text(s.emoji, fontSize = 24.sp)
          }
          if (editing) {
            var e by remember { mutableStateOf(s.emoji) }
            AlertDialog(
              onDismissRequest = { editing = false },
              title = { Text("Emoji for #${i + 1}", fontWeight = FontWeight(500)) },
              text = { OutlinedTextField(value = e, onValueChange = { e = it.takeLast(4) }, singleLine = true, shape = RoundedCornerShape(8.dp)) },
              confirmButton = { TextButton(onClick = { vm.updateEmoji(s.id, e); editing = false }) { Text("Done") } },
            )
          }
        }
      }

      Spacer(Modifier.height(28.dp))

      if (!saved) {
        Button(
          onClick = {
            val zip = st.exportZip ?: return@Button
            File(ctx.cacheDir, "sticker-pack.signal.zip").writeBytes(zip)
            saved = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = C.primary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().height(44.dp),
        ) { Text("Save Pack", fontWeight = FontWeight(500)) }
      }

      if (saved) {
        Card(
          colors = CardDefaults.cardColors(containerColor = C.primary.copy(alpha = 0.08f)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Saved!", fontWeight = FontWeight(500), color = C.primary, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(
              "Open Signal → sticker icon → +\nthen install the .signal.zip",
              textAlign = TextAlign.Center, color = C.muted, fontSize = 13.sp,
            )
          }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { vm.reset(); onBack() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
          Text("Create New Pack")
        }
      }

      st.packKey.takeIf { it.isNotBlank() }?.let { key ->
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = C.surface), shape = RoundedCornerShape(8.dp)) {
          Column(Modifier.padding(16.dp)) {
            Text("Pack Key", fontWeight = FontWeight(500), fontSize = 13.sp, color = C.ink)
            Text(key, fontSize = 11.sp, color = C.mutedSoft)
          }
        }
      }
    }
  }
}
