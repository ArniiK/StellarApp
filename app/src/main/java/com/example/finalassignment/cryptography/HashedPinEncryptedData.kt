package com.example.finalassignment.cryptography

import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class HashedPinEncryptedData{
    var hashedPin: SecretKey? = null
    var salt: ByteArray? = null
    var encryptedText: String? = null

    constructor()
    constructor(hashedPin: SecretKey?, salt: ByteArray?) {
        this.hashedPin = hashedPin
        this.salt = salt
    }


}