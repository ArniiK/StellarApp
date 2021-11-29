package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*
import org.stellar.sdk.responses.AccountResponse

@Dao
interface UserRegistrationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(userRegistration: UserRegistration)

    @Query("Select * from user_registration_table order by id ASC")
    fun getAllUsers(): LiveData<List<UserRegistration>>

    @Query("Select * from user_registration_table where publicKey == :publicId")
    fun getUserByPublicId(publicId: String): LiveData<UserRegistration>

    @Query("Select * from user_registration_table where id == :id")
    fun getUserById(id: Int): LiveData<UserRegistration>

    @Query("update user_registration_table set balance = :balance where id == :id")
    fun updateBalanceByUserById(id: Int, balance: Double)
}