package com.example.finalassignment.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "transaction_table")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val transactionHash: String,
    val userRegistrationId: Int,
    val type: String,
    val amount: Double,
    val partnerHash: String,
    val date: Date,
)