package com.example.finalassignment.roomdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActiveUserViewModel(application: Application):AndroidViewModel(application) {
    private val activeUserRepository: ActiveUserRepository
    init {
        val activeUserDAO = UserRegistrationDatabase.getDatabase(application).activeUserDAO()
        activeUserRepository = ActiveUserRepository(activeUserDAO)
    }

    fun getActiveUser(){
        viewModelScope.launch(Dispatchers.IO){
            activeUserRepository.getActiveUser()
        }
    }

    fun addActiveUser(activeUser: ActiveUser){
        viewModelScope.launch(Dispatchers.IO){
            activeUserRepository.addActiveUser(activeUser)
        }
    }

    fun deleteActiveUser(activeUser: ActiveUser){
        viewModelScope.launch(Dispatchers.IO){
            activeUserRepository.deleteActiveUser(activeUser)
        }
    }

}