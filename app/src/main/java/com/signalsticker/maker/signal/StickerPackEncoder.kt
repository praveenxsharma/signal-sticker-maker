package com.signalsticker.maker.signal

import com.signalsticker.maker.model.StickerPack
import com.signalsticker.maker.signal.StickerPackProto.Pack
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object StickerPackEncoder {

  data class EncodedPack(
    val manifestBytes: ByteArray,
    val stickerFiles: Map<Int, ByteArray>,
  )

  fun encode(
    pack: StickerPack,
    stickerData: Map<Int, ByteArray>,
    packKey: String = StickerCrypto.generateKey(),
  ): EncodedPack {
    val protoPack = Pack.newBuilder().apply {
      title = pack.title
      author = pack.author
      cover = Pack.Sticker.newBuilder()
        .setId(pack.coverIndex)
        .setEmoji(pack.stickers.getOrNull(pack.coverIndex)?.emoji ?: "")
        .build()
      pack.stickers.forEachIndexed { i, s ->
        addStickers(Pack.Sticker.newBuilder().setId(i).setEmoji(s.emoji))
      }
    }.build()

    val manifestPlain = protoPack.toByteArray()
    val manifestEncrypted = StickerCrypto.encrypt(manifestPlain, packKey)

    val stickerEncrypted = stickerData.mapValues { (_, data) ->
      StickerCrypto.encrypt(data, packKey)
    }

    return EncodedPack(
      manifestBytes = manifestEncrypted,
      stickerFiles = stickerEncrypted,
    )
  }

  fun toZip(
    manifest: ByteArray,
    stickers: Map<Int, ByteArray>,
  ): ByteArray {
    val out = ByteArrayOutputStream()
    ZipOutputStream(out).use { zip ->
      zip.putNextEntry(ZipEntry("manifest"))
      zip.write(manifest)
      zip.closeEntry()
      stickers.forEach { (id, data) ->
        zip.putNextEntry(ZipEntry("stickers/$id"))
        zip.write(data)
        zip.closeEntry()
      }
    }
    return out.toByteArray()
  }
}
