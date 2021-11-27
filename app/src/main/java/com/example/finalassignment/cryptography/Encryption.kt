package com.example.finalassignment.cryptography

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class Encryption {

    companion object{
        private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    }

    fun hashPinRegistration(pin: String): HashedPinEncryptedData {

        val salt = ByteArray(256)
        SecureRandom().nextBytes(salt)

        val spec = PBEKeySpec(pin.toCharArray(), salt, 1000, 256)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        val secretKeyHashedPin : SecretKey = SecretKeySpec(hash, ALGORITHM)

        return HashedPinEncryptedData(secretKeyHashedPin, salt)
    }

    fun hashPinLogin(salt: ByteArray, pin: String): SecretKey {
        val spec = PBEKeySpec(pin.toCharArray(), salt, 1000, 256)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        val secretKeyHashedPin : SecretKey = SecretKeySpec(hash, ALGORITHM)

        return secretKeyHashedPin
    }

//    @Throws(
//        NoSuchPaddingException::class,
//        NoSuchAlgorithmException::class,
//        InvalidAlgorithmParameterException::class,
//        InvalidKeyException::class,
//        BadPaddingException::class,
//        IllegalBlockSizeException::class
//    )
    fun encrypt( input: String, key: SecretKey?, iv: ByteArray, data: HashedPinEncryptedData): HashedPinEncryptedData? {
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val encryptedtext = Base64.encodeToString(cipher.doFinal(input.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
            data.encryptedText = encryptedtext

            return data
        }
        catch (e:Exception){
            println("Error while encrypting: $e")
        }
        return null
    }

//    @Throws(
//        NoSuchPaddingException::class,
//        NoSuchAlgorithmException::class,
//        InvalidAlgorithmParameterException::class,
//        InvalidKeyException::class,
//        BadPaddingException::class,
//        IllegalBlockSizeException::class
//    )
    fun decrypt(data: HashedPinEncryptedData, iv: ByteArray): String? {
        try {
            val cipher = Cipher.getInstance(ALGORITHM)

            val cipherText = data.encryptedText
            val key = data.hashedPin
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

            return  String(cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT)))
        }
        catch (e : Exception){
            println("Error while decrypting: $e")
        }
        return null
    }

    fun generateIv(): ByteArray {
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        return iv
    }
}