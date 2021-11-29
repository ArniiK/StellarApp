package com.example.finalassignment.transaction

open class ValidationResponse {


     var message:String = ""
        get() = field
        set(value) {field = value}

     var isSuccess: Boolean = false
        get() = field
        set(value) {field = value}


    init {
        message = ""
        isSuccess = false
    }




}