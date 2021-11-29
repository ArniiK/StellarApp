package com.example.finalassignment.transaction.partners

import com.example.finalassignment.transaction.ValidationResponse

class PartnerDBResponse : ValidationResponse() {

    var partner: Partner = Partner()
        get() = field
    set(value) {field = value}

    var position: Int = -1
        get() = field
        set(value) {field = value}

    init{
        partner = Partner()
        position = -1

    }


}