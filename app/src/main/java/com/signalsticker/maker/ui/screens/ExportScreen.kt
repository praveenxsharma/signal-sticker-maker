package com.signalsticker.maker.ui.screens

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.signalsticker.maker.ui.theme.C
import com.signalsticker.maker.viewmodel.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(vm: MainViewModel, onBack: () -> Unit) {
  val st by vm.s.collectAsState()
  val ctx = LocalContext.current

  Scaffold(
    containerColor = C.canvas,
    topBar = {
      TopAppBar(
        title = { Text("Preview & Export", fontWeight = FontWeight(400)) },
        navigationIcon = { TextButton(onClick = onBack) { Text("Back", color = C.onDark) } },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = C.dark,
          titleContentColor = C.onDark,
        ),
      )
    }
  ) { pad ->
    Column(
      modifier = Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(st.title, fontWeight = FontWeight(400), fontSize = 22.sp, color = C.ink)
      Spacer(Modifier.height(2.dp))
      Text(st.author, color = C.muted, fontSize = 14.sp)
      Spacer(Modifier.height(16.dp))

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(st.stickers) { i, s ->
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
              model = Uri.parse(s.uri),
              contentDescription = "sticker $i",
              modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(C.surface).border(1.dp, C.hairline, RoundedCornerShape(12.dp)),
            )
            Text(s.emoji, fontSize = 18.sp)
          }
        }
      }

      Spacer(Modifier.height(20.dp))
      HorizontalDivider(color = C.hairline)
      Spacer(Modifier.height(12.dp))

      Text("Emoji", fontWeight = FontWeight(500), fontSize = 14.sp, color = C.ink)
      Spacer(Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        itemsIndexed(st.stickers) { i, s ->
          var editing by remember { mutableStateOf(false) }
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { editing = true },
          ) {
            Text("#${i + 1}", fontSize = 10.sp, color = C.mutedSoft)
            Text(s.emoji, fontSize = 22.sp)
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

      Spacer(Modifier.height(24.dp))

      Button(
        onClick = {
          val zip = st.exportZip ?: return@Button
          val name = "sticker-pack.signal.zip"
          File(ctx.cacheDir, name).writeBytes(zip)
          if (Build.VERSION.SDK_INT >= 29) {
            val vals = ContentValues().apply {
              put(MediaStore.Downloads.DISPLAY_NAME, name)
              put(MediaStore.Downloads.MIME_TYPE, "application/zip")
              put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/StickerPacks")
            }
            runCatching {
              val dlUri = ctx.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, vals)!!
              ctx.contentResolver.openOutputStream(dlUri)?.use { it.write(zip) }
            }
          }
          val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", File(ctx.cacheDir, name))
          runCatching {
            ctx.startActivity(Intent(Intent.ACTION_VIEW).apply {
              setDataAndType(uri, "application/zip")
              addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            })
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = C.primary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(44.dp),
      ) { Text("Export to Signal", fontWeight = FontWeight(500)) }

      Spacer(Modifier.height(8.dp))

      Text(
        "Saved to Downloads/StickerPacks/sticker-pack.signal.zip\nSignal should open automatically to install the pack.",
        textAlign = TextAlign.Center, color = C.muted, fontSize = 12.sp,
      )

      Spacer(Modifier.height(24.dp))

      OutlinedButton(
        onClick = { vm.reset(); onBack() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
      ) { Text("Start Over") }

      st.packKey.takeIf { it.isNotBlank() }?.let { key ->
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = C.surface), shape = RoundedCornerShape(8.dp)) {
          Column(Modifier.padding(16.dp)) {
            Text("Pack Key", fontWeight = FontWeight(500), fontSize = 12.sp, color = C.ink)
            SelectionContainer { Text(key, fontSize = 10.sp, color = C.mutedSoft) }
          }
        }
      }
    }
  }
}
