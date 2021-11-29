package com.example.finalassignment.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_registration_table",indices = [Index(value =["publicKey"],unique = true)])
data class UserRegistration(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "publicKey")
    val publicKey: String,
    val salt: ByteArray?,
    val privateKey: String?,
    val iv: ByteArray?
)