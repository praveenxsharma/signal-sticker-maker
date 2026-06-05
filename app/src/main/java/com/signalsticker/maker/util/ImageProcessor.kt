package com.signalsticker.maker.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImageProcessor {

  private const val TARGET_SIZE = 512

  suspend fun processImage(
    resolver: ContentResolver,
    uri: Uri,
  ): Result<ByteArray> = withContext(Dispatchers.Default) {
    runCatching {
      val input = resolver.openInputStream(uri) ?: error("Cannot open $uri")
      val opts = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
      }
      BitmapFactory.decodeStream(input, null, opts)
      input.close()

      val scale = maxOf(opts.outWidth, opts.outHeight) / TARGET_SIZE
      val sampleSize = if (scale > 0) Integer.highestOneBit(scale) else 1

      val decodeOpts = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
      }
      val input2 = resolver.openInputStream(uri) ?: error("Cannot open $uri")
      val src = BitmapFactory.decodeStream(input2, null, decodeOpts) ?: error("Failed to decode image")
      input2.close()

      val bmp = resizeAndCrop(src, TARGET_SIZE)
      val out = ByteArrayOutputStream()
      bmp.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, out)
      bmp.recycle()
      src.recycle()
      out.toByteArray()
    }
  }

  private fun resizeAndCrop(src: Bitmap, size: Int): Bitmap {
    val scale = maxOf(
      size.toFloat() / src.width,
      size.toFloat() / src.height
    )
    val w = (src.width * scale).toInt()
    val h = (src.height * scale).toInt()
    val scaled = Bitmap.createScaledBitmap(src, w, h, true)
    val x = (w - size) / 2
    val y = (h - size) / 2
    val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val c = Canvas(result)
    c.drawBitmap(scaled, -x.toFloat(), -y.toFloat(), null)
    if (scaled != src) scaled.recycle()
    return result
  }
}
