package com.example.finalassignment.singleton

 object ActiveUserSingleton {

     var id: Int = -1
     var publicKey: String = ""
     var salt: ByteArray? = null
     var privateKey: String? = ""
     var iv: ByteArray? = null

 }
