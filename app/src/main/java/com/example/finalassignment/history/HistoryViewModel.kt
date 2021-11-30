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

    fun getTransactionsForId(userId: Int): LiveData<List<Transaction>> {
        transactionsById = repository.getTransactionsForUserId(userId)
        return transactionsById!!
    }
}