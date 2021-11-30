package com.example.finalassignment

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.example.finalassignment.DbUpdateService.transactionRepository
import com.example.finalassignment.roomdb.*
import java.time.Instant


object DbUpdateService {
    private val userRegistrationRepository: UserRegistrationRepository
    private val transactionRepository: TransactionRepository

    init {
        val userRegistrationDAO = UserRegistrationDatabase.getDatabase(FinalAssignmentApp.context).userRegistrationDAO()
        userRegistrationRepository = UserRegistrationRepository(userRegistrationDAO)

        val transactionDAO = UserRegistrationDatabase.getDatabase(FinalAssignmentApp.context).transactionDAO()
        transactionRepository = TransactionRepository(transactionDAO)
    }

    suspend fun updateBalance(userId: Int, publicKey: String) {
        val balance = StellarService.getBalanceByPublicKey(publicKey)
        userRegistrationRepository.updateBalanceByUserId(userId, balance)
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

                    transactionRepository.addTransaction(newTransaction)
                }
            }
        }
    }

}