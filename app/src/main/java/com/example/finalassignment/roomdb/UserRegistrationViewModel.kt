package com.example.finalassignment.roomdb

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.finalassignment.StellarService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
import java.security.PublicKey
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserRegistrationViewModel(application: Application): AndroidViewModel(application) {
    val getAllUsers: LiveData<List<UserRegistration>>
    private val repository: UserRegistrationRepository
    lateinit var userData: LiveData<UserRegistration>
    var bool: Boolean? = null
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

    fun tryToLogin(privateKey: String?, pinCode: String ) : Boolean? {
        viewModelScope.launch (Dispatchers.IO) {
            val source = KeyPair.fromSecretSeed(privateKey)
            val publicKey = source.accountId
            userData = repository.getUserByPublicId(publicKey)
            Log.d("userData", "user data " + userData)

        }
        return bool

    }
}