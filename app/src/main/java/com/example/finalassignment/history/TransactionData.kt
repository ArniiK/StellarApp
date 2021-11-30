package com.example.finalassignment.history

import java.util.*

data class TransactionData(
    val sign: String,
    val name: String,
    val amount: Double,
    val date: Date,
    val currency: String,
)
