package com.example.finalassignment.transaction.partners

import com.example.finalassignment.roomdb.PartnerDB
import com.example.finalassignment.singleton.ActiveUserSingleton
import com.example.finalassignment.transaction.ValidationResponse

class PartnerDBResponse ( partner :PartnerDB = PartnerDB(0,"","",ActiveUserSingleton.publicKey)) : ValidationResponse() {

    var partner: PartnerDB =partner
        get() = field
    set(value) {field = value}

    var position: Int = -1
        get() = field
        set(value) {field = value}

}