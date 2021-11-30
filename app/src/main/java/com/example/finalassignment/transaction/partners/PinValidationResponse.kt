package com.example.finalassignment.transaction.partners

import com.example.finalassignment.transaction.ValidationResponse

class PinValidationResponse: ValidationResponse() {

   var decryptedPrivateKey: String = ""
       get() = field
       set(value) {field = value}

    init {
        isSuccess = false
        message = ""
        decryptedPrivateKey = ""

    }

}