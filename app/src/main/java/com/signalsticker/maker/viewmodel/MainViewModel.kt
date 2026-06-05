package com.signalsticker.maker.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signalsticker.maker.model.StickerPack
import com.signalsticker.maker.model.StickerItem
import com.signalsticker.maker.signal.StickerCrypto
import com.signalsticker.maker.signal.StickerPackEncoder
import com.signalsticker.maker.util.ImageProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class State(
  val title: String = "",
  val author: String = "",
  val stickers: List<StickerItem> = emptyList(),
  val processing: Boolean = false,
  val exportZip: ByteArray? = null,
  val packKey: String = "",
  val error: String? = null,
)

class MainViewModel : ViewModel() {
  private val _s = MutableStateFlow(State())
  val s: StateFlow<State> = _s.asStateFlow()

  private val dat = mutableMapOf<Int, ByteArray>()

  fun addImages(resolver: ContentResolver, uris: List<Uri>) {
    viewModelScope.launch {
      _s.value = _s.value.copy(processing = true, error = null)
      val cur = _s.value.stickers.toMutableList()
      var nid = cur.size

      for (uri in uris) {
        ImageProcessor.processImage(resolver, uri)
          .onSuccess { bytes ->
            dat[nid] = bytes
            cur.add(StickerItem(id = nid, uri = uri.toString()))
            nid++
          }
          .onFailure { e -> _s.value = _s.value.copy(error = e.message) }
      }
      _s.value = _s.value.copy(stickers = cur, processing = false)
    }
  }

  fun removeSticker(id: Int) {
    _s.value = _s.value.copy(stickers = _s.value.stickers.filter { it.id != id })
    dat.remove(id)
  }

  fun reorder(from: Int, to: Int) {
    val list = _s.value.stickers.toMutableList()
    if (from !in list.indices || to !in list.indices) return
    list.add(to, list.removeAt(from))
    _s.value = _s.value.copy(stickers = list)
  }

  fun updateEmoji(id: Int, e: String) {
    _s.value = _s.value.copy(
      stickers = _s.value.stickers.map { if (it.id == id) it.copy(emoji = e) else it }
    )
  }

  fun setTitle(t: String) { _s.value = _s.value.copy(title = t) }
  fun setAuthor(a: String) { _s.value = _s.value.copy(author = a) }

  fun export() {
    val st = _s.value
    if (st.stickers.isEmpty() || st.title.isBlank()) return
    val key = StickerCrypto.generatePackKey()
    val enc = StickerPackEncoder.encode(
      StickerPack(title = st.title, author = st.author.ifBlank { "Anonymous" }, stickers = st.stickers),
      dat, key,
    )
    _s.value = _s.value.copy(
      exportZip = StickerPackEncoder.toZip(enc.manifestBytes, enc.stickerFiles),
      packKey = key,
      error = null,
    )
  }

  fun reset() { _s.value = State(); dat.clear() }
}
