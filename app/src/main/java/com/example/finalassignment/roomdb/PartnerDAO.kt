package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface PartnerDAO {

    @Query("Select * from partner_table")
    fun getAllPartners(): LiveData<List<PartnerDB>>

    @Query("Select * from partner_table WHERE owner_publicKey == :pK")
    fun getAllPartnersByActiveUser(pK: String): List<PartnerDB>

    @Query("Select * from partner_table where publicKey == :pk")
    fun getPartnerByPK(pk: String): LiveData<PartnerDB>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addPartner(partnerDB: PartnerDB)

    @Delete()
    fun deletePartner(partnerDB: PartnerDB)

}