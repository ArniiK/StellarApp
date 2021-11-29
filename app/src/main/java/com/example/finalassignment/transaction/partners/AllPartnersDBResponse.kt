package com.example.finalassignment.transaction.partners

import com.example.finalassignment.transaction.ValidationResponse

class AllPartnersDBResponse :ValidationResponse() {

    var partnerList: MutableList<Partner> = mutableListOf<Partner>()
        get() = field
        set(value) {field = value}


    init{
        partnerList = mutableListOf<Partner>()
    }
}