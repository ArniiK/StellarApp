package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ActiveUserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActiveUser(activeUser: ActiveUser)

    @Query("Select * from active_user_table where active == 'Y'")
    fun getActiveUser():LiveData<ActiveUser>

    @Delete
    fun deleleActiveUser(activeUser: ActiveUser)
}