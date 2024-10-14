package com.example.demo_5000.data

import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

object Crypto {
    private const val AES = "AES"
    private const val CIPHER = "AES/CBC/PKCS5Padding"
    private const val KEY = "jaF5UNFMkcpwfB5+Cv9k0J/4YjjzbbNcQCtcIjmm400="

    private val charset = Charset.defaultCharset()
    private val secretKey = KEY.toSecretKey()

    @OptIn(ExperimentalEncodingApi::class)
    private fun String.toSecretKey() = Base64.Mime.decode(this).run {
        SecretKeySpec(this, 0, size, AES)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun String.encode(saveIV: (String)-> Unit) =
        aesEncrypt(toByteArray(charset), null.toIV(saveIV))
            .let(Base64.Mime::encode)

    @OptIn(ExperimentalEncodingApi::class)
    fun String.decode(iv:ByteArray) =
        aesDecrypt(Base64.Mime.decode(this), iv).toString(charset)

    @OptIn(ExperimentalEncodingApi::class)
    fun String?.toIV(saveIV: ((String)-> Unit)? = null) = this
        ?.let(Base64.Mime::decode) ?: Random.nextBytes(16).also {
            saveIV?.invoke(Base64.Mime.encode(it))
        }

    private fun aesEncrypt(data: ByteArray, iv: ByteArray/*16*/): ByteArray {
        val cipher = Cipher.getInstance(CIPHER)
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(data)
    }

    private fun aesDecrypt(encryptedData: ByteArray, iv: ByteArray/*16*/): ByteArray {
        val cipher = Cipher.getInstance(CIPHER)
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(encryptedData)
    }

    private fun generateAESKey(keySize: Int = 256): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(AES)
        keyGenerator.init(keySize)
        return keyGenerator.generateKey()
    }
}