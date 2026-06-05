package com.signalsticker.maker.model

data class StickerPack(
  val title: String = "",
  val author: String = "",
  val stickers: List<StickerItem> = emptyList(),
  val coverIndex: Int = 0,
)

data class StickerItem(
  val id: Int,
  val uri: String,
  val emoji: String = "\uD83D\uDE0A",
)
