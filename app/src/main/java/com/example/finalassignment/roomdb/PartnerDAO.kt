package com.example.finalassignment.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PartnerDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addPartner(partnerDB: PartnerDB)

}