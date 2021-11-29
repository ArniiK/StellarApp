package com.example.finalassignment.transaction.partners

class Partner {

    var nickName:String = ""
        get() = field
        set(value) {field = value}

    var publicKey: String = ""
        get() = field
        set(value) {field = value}

    var position: Int = -1
        get() = field
        set(value) {field = value}


    init {
        nickName = ""
        publicKey = ""
        position = -1
    }
}