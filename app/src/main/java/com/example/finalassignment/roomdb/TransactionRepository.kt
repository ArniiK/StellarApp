package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData

class TransactionRepository (
    private val transactionDAO: TransactionDAO
    ) {
    val getAllTransactions: LiveData<List<Transaction>> = transactionDAO.getAllTransactions()

    suspend fun addTransaction(transaction: Transaction){
        transactionDAO.addTransaction(transaction)
    }
    fun getTransactionsForUserId(userId: Int): LiveData<List<Transaction>> {
        return transactionDAO.getTransactionsForUserId(userId)
    }
}