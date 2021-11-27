package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTransaction(transaction: Transaction)

    @Query("Select * from transaction_table order by id ASC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("Select * from transaction_table where userRegistrationId == :userId")
    fun getTransactionsForUserId(userId: Int): LiveData<Transaction>
}