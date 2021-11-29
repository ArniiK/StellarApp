package com.example.finalassignment.roomdb

class PartnerRepository(private val partnerDAO: PartnerDAO) {

    suspend fun addPartner(partnerDB: PartnerDB){

        partnerDAO.addPartner(partnerDB)



    }


}