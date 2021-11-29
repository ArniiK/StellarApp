package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData


class UserRegistrationRepository(
    private val userRegistrationDAO: UserRegistrationDAO,
    private val transactionDAO: TransactionDAO
) {

    val getAllUsers: LiveData<List<UserRegistration>> = userRegistrationDAO.getAllUsers()

    suspend fun addUser(userRegistration: UserRegistration){
        userRegistrationDAO.addUser(userRegistration)
    }
    suspend fun getUserByPublicId(publicId: String): LiveData<UserRegistration> {
        return userRegistrationDAO.getUserByPublicId(publicId)
    }
    suspend fun getUserById(id: Int): LiveData<UserRegistration> {
        return userRegistrationDAO.getUserById(id)
    }
    suspend fun updateBalanceByUserId(id: Int, balance: Double) {
        return userRegistrationDAO.updateBalanceByUserById(id, balance)
    }

    val getAllTransactions: LiveData<List<Transaction>> = transactionDAO.getAllTransactions()

    suspend fun addTransaction(transaction: Transaction){
        transactionDAO.addTransaction(transaction)
    }
    suspend fun getTransactionsForUserId(userId: Int): LiveData<List<Transaction>> {
        return transactionDAO.getTransactionsForUserId(userId)
    }
}