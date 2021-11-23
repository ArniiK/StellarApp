package com.example.finalassignment

import java.util.*

data class Transaction(
    val sign: String,
    val name: String,
    val amount: Double,
    val date: Date,
    val currency: String,
)
