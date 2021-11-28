package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData

class ActiveUserRepository(
    private val activeUserDAO: ActiveUserDAO
) {

    fun getActiveUser(){
        activeUserDAO.getActiveUser()
    }

    fun addActiveUser(activeUser: ActiveUser){
        activeUserDAO.addActiveUser(activeUser)
    }

    fun deleteActiveUser(activeUser: ActiveUser){
        activeUserDAO.deleleActiveUser(activeUser)
    }


}