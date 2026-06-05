package com.signalsticker.maker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.signalsticker.maker.model.StickerItem
import com.signalsticker.maker.ui.theme.C
import com.signalsticker.maker.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: MainViewModel, onNavExport: () -> Unit) {
  val st by vm.s.collectAsState()
  val ctx = LocalContext.current

  val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
    if (uris.isNotEmpty()) vm.addImages(ctx.contentResolver, uris)
  }

  Scaffold(
    containerColor = C.canvas,
    topBar = {
      TopAppBar(
        title = { Text("Sticker Pack Maker", fontWeight = FontWeight(400)) },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = C.dark,
          titleContentColor = C.onDark,
        )
      )
    }
  ) { pad ->
    Column(
      modifier = Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      if (st.stickers.isEmpty() && !st.processing) {
        Spacer(Modifier.height(64.dp))
        Text(
          "Pick images to create a\nSignal sticker pack.",
          textAlign = TextAlign.Center, color = C.muted, fontSize = 16.sp,
        )
        Spacer(Modifier.height(24.dp))
        Button(
          onClick = { picker.launch("image/*") },
          colors = ButtonDefaults.buttonColors(containerColor = C.primary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(44.dp).widthIn(max = 220.dp).fillMaxWidth(),
        ) { Text("Pick Images", fontSize = 15.sp, fontWeight = FontWeight(500)) }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { picker.launch("image/gif") }) {
          Text("or pick GIFs / animations", color = C.muted)
        }
      } else {
        Column(
          modifier = Modifier.padding(24.dp).fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text("${st.stickers.size}/200 stickers", fontWeight = FontWeight(500), color = C.ink, fontSize = 14.sp)
          Spacer(Modifier.height(12.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(st.stickers) { i, s ->
              Thumb(s, i, onRemove = { vm.removeSticker(s.id) })
            }
          }
          Spacer(Modifier.height(24.dp))
          OutlinedTextField(
            value = st.title,
            onValueChange = { vm.setTitle(it) },
            label = { Text("Pack Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = C.ink,
              unfocusedTextColor = C.ink,
              cursorColor = C.primary,
              focusedBorderColor = C.primary,
              unfocusedBorderColor = C.hairline,
              focusedLabelColor = C.primary,
              unfocusedLabelColor = C.muted,
            ),
          )
          Spacer(Modifier.height(12.dp))
          OutlinedTextField(
            value = st.author,
            onValueChange = { vm.setAuthor(it) },
            label = { Text("Author") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = C.ink,
              unfocusedTextColor = C.ink,
              cursorColor = C.primary,
              focusedBorderColor = C.primary,
              unfocusedBorderColor = C.hairline,
              focusedLabelColor = C.primary,
              unfocusedLabelColor = C.muted,
            ),
          )
          Spacer(Modifier.height(16.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
              onClick = { picker.launch("image/*") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
            ) { Text("+ Add More") }
            Button(
              onClick = { vm.export(); onNavExport() },
              enabled = st.stickers.isNotEmpty() && st.title.isNotBlank(),
              colors = ButtonDefaults.buttonColors(containerColor = C.primary),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
            ) { Text("Preview & Export") }
          }
        }
      }

      st.error?.let { err ->
        Spacer(Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = C.error.copy(alpha = 0.08f)), shape = RoundedCornerShape(8.dp)) {
          Text(err, color = C.error, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
        }
      }

      if (st.processing) {
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = C.primary)
      }
    }
  }
}

@Composable
private fun Thumb(s: StickerItem, i: Int, onRemove: () -> Unit) {
  Box {
    AsyncImage(
      model = Uri.parse(s.uri),
      contentDescription = "sticker ${i + 1}",
      modifier = Modifier
        .size(96.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(C.surface)
        .border(1.dp, C.hairline, RoundedCornerShape(12.dp)),
    )
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)
        .size(24.dp).background(C.error, RoundedCornerShape(12.dp))
        .clickable(onClick = onRemove),
      contentAlignment = Alignment.Center,
    ) { Text("×", color = C.onPrimary, fontSize = 14.sp, fontWeight = FontWeight(500)) }
    if (s.emoji.isNotBlank()) {
      Text(s.emoji, fontSize = 16.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-4).dp, y = (-4).dp))
    }
  }
}
