package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PartnerDAO {

    @Query("Select * from partner_table order by publicKey ASC")
    fun getAllPartners(): LiveData<List<PartnerDB>>

    @Query("Select * from partner_table where publicKey == :pk")
    fun getPartnerByPK(pk: String): LiveData<PartnerDB>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addPartner(partnerDB: PartnerDB)

    @Delete()
    fun deletePartner(partnerDB: PartnerDB)

}