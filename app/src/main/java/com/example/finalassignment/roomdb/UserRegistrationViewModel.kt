package com.example.finalassignment.roomdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRegistrationViewModel(application: Application): AndroidViewModel(application) {
    private val getAllUsers: LiveData<List<UserRegistration>>
    private val repository: UserRegistrationRepository
    init {
        val userRegistrationDAO = UserRegistrationDatabase.getDatabase(application).userRegistrationDAO()
        repository = UserRegistrationRepository(userRegistrationDAO)
        getAllUsers = repository.getAllUsers
    }

    fun addUser(userRegistration: UserRegistration){
        viewModelScope.launch (Dispatchers.IO){
            repository.addUser(userRegistration)
        }
    }
}