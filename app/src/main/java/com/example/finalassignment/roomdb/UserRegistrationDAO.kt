package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserRegistrationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(userRegistration: UserRegistration)

    @Query("Select * from user_registration_table order by id ASC")
    fun getAllUsers(): LiveData<List<UserRegistration>>

}