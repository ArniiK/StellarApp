package com.example.finalassignment.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_registration_table")
data class UserRegistration(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userName: String,
    val password: String,
    val publicKey: String,
    val privateKey: String


)