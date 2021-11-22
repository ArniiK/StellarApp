package com.example.finalassignment.roomdb

import androidx.lifecycle.LiveData


class UserRegistrationRepository(private val userRegistrationDAO: UserRegistrationDAO) {

    val getAllUsers: LiveData<List<UserRegistration>> = userRegistrationDAO.getAllUsers()

    suspend fun addUser(userRegistration: UserRegistration){
        userRegistrationDAO.addUser(userRegistration)
    }
}