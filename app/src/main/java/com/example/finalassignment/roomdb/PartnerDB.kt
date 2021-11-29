package com.example.finalassignment.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "partner_table")
data class PartnerDB(

    @PrimaryKey
    val publicKey: String,
    val nickName: String
)
