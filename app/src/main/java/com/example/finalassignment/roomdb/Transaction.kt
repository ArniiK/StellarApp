package com.example.finalassignment.roomdb

import androidx.room.Entity
import androidx.room.TypeConverters
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "transaction_table", primaryKeys = ["transactionHash", "userRegistrationId"])
data class Transaction(

    val transactionHash: String,
    val userRegistrationId: Int,
    val type: String,
    val amount: Double,
    val partnerHash: String,
    @TypeConverters(Converters::class)
    val date: Instant?,
)