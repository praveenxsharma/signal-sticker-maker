package com.signalsticker.maker.signal

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object StickerCrypto {

  private const val INFO = "Sticker Pack"
  private const val KEY_LENGTH = 512
  private const val IV_LENGTH = 16
  private const val HMAC_LENGTH = 32

  fun deriveKeys(key: String): Pair<SecretKeySpec, SecretKeySpec> {
    val hkdf = HkdfSha256()
    val raw = hkdf.deriveKey(key.hexToBytes(), INFO.toByteArray(), KEY_LENGTH / 8)
    val aesKey = SecretKeySpec(raw.copyOfRange(0, 32), "AES")
    val hmacKey = SecretKeySpec(raw.copyOfRange(32, 64), "HmacSHA256")
    return aesKey to hmacKey
  }

  fun encrypt(data: ByteArray, key: String): ByteArray {
    val (aesKey, hmacKey) = deriveKeys(key)
    val iv = ByteArray(IV_LENGTH).apply { SecureRandom().nextBytes(this) }

    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, aesKey, IvParameterSpec(iv))
    val encrypted = cipher.doFinal(data)

    val mac = Mac.getInstance("HmacSHA256")
    mac.init(hmacKey)
    mac.update(iv)
    mac.update(encrypted)
    val hmac = mac.doFinal()

    return iv + encrypted + hmac
  }

  fun generateKey(): String {
    val bytes = ByteArray(32)
    SecureRandom().nextBytes(bytes)
    return bytes.toHex()
  }
}

private class HkdfSha256 {
  fun deriveKey(ikm: ByteArray, info: ByteArray, length: Int): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    val salt = ByteArray(32)
    mac.init(SecretKeySpec(salt, "HmacSHA256"))
    val prk = mac.doFinal(ikm)

    val result = ByteArray(length)
    var t = ByteArray(0)
    val block = ByteArray(1)
    for (i in 1..(length + 31) / 32) {
      mac.init(SecretKeySpec(prk, "HmacSHA256"))
      mac.update(t)
      mac.update(info)
      block[0] = i.toByte()
      t = mac.doFinal(block)
      System.arraycopy(t, 0, result, (i - 1) * 32, minOf(32, length - (i - 1) * 32))
    }
    return result
  }
}

private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
private fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
