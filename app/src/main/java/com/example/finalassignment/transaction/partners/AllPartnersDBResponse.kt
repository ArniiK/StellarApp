package com.example.finalassignment.transaction.partners

import com.example.finalassignment.roomdb.PartnerDB
import com.example.finalassignment.transaction.ValidationResponse

class AllPartnersDBResponse :ValidationResponse() {

    var partnerList: MutableList<PartnerDB> = mutableListOf<PartnerDB>()
        get() = field
        set(value) {field = value}


    init{
        partnerList = mutableListOf<PartnerDB>()
    }
}