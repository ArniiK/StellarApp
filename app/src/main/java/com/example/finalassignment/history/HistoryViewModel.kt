package com.example.finalassignment.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.finalassignment.StellarService
import com.example.finalassignment.roomdb.Transaction
import com.example.finalassignment.roomdb.TransactionRepository
import com.example.finalassignment.roomdb.UserRegistrationDatabase
import com.example.finalassignment.roomdb.UserRegistrationRepository
import kotlinx.coroutines.*
import java.time.Instant

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository
    var transactionsById: LiveData<List<Transaction>>? = null
    init {
        val transactionDAO = UserRegistrationDatabase.getDatabase(application).transactionDAO()
        repository = TransactionRepository(transactionDAO)
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch (Dispatchers.IO){
            repository.addTransaction(transaction)
        }
    }

    fun getTransactionsForId(userId: Int): LiveData<List<Transaction>> {
        transactionsById = repository.getTransactionsForUserId(userId)
        return transactionsById!!
    }

    suspend fun updateTransactions(userId: Int, publicKey: String) {
        val transactions = StellarService.getTransactionsByPublicKey(publicKey)
        if (transactions != null && publicKey != null && userId != null) {
            for (transaction in transactions) {
                if (transaction.isTransactionSuccessful == true) {
                    val date = Instant.parse(transaction.createdAt)
                    var transactionType : String
                    var partnerHash : String
                    if (transaction.from == publicKey) {
                        transactionType = "-"
                        partnerHash = transaction.to
                    }
                    else {
                        transactionType = "+"
                        partnerHash = transaction.from
                    }

                    val newTransaction = Transaction(
                        transactionHash = transaction.transactionHash,
                        userRegistrationId = userId,
                        type = transactionType,
                        amount = transaction.amount.toDouble(),
                        partnerHash = partnerHash,
                        date = date,
                    )

                    addTransaction(newTransaction)
                }
            }
        }
    }
}