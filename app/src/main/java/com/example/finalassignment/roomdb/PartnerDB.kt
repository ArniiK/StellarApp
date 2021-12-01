package com.example.finalassignment.roomdb

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "partner_table", indices = [Index(value =["publicKey", "owner_publicKey"],unique = true)])
data class PartnerDB(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val publicKey: String,
    val owner_publicKey: String,
    val nickName: String
)
