package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PartnerRepository(private val partnerDAO: PartnerDAO) {

    val getAllPartners: LiveData<List<PartnerDB>> = partnerDAO.getAllPartners()

    suspend fun addPartner(partnerDB: PartnerDB){
        partnerDAO.addPartner(partnerDB)
    }

    suspend fun getAllPartnersByActiveUser(pK: String): List<PartnerDB> {
        return partnerDAO.getAllPartnersByActiveUser(pK)
    }

    suspend fun getPartnerByPK(pk: String):LiveData<PartnerDB>{
        return partnerDAO.getPartnerByPK(pk)
    }

    suspend fun deletePartner(partnerDB: PartnerDB){
        partnerDAO.deletePartner(partnerDB)
    }


}