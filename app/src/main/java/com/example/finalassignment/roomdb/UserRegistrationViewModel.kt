package com.example.finalassignment.roomdb

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.finalassignment.StellarService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
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
        val transactionDAO = UserRegistrationDatabase.getDatabase(application).transactionDAO()
        repository = UserRegistrationRepository(userRegistrationDAO, transactionDAO)
        getAllUsers = repository.getAllUsers

    }
    fun addUser(userRegistration: UserRegistration){
        viewModelScope.launch (Dispatchers.IO){
            repository.addUser(userRegistration)
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch (Dispatchers.IO){
            repository.addTransaction(transaction)
        }
    }
    fun tryToLogin(privateKey: String?, pinCode: String ) : Boolean? {
        viewModelScope.launch (Dispatchers.IO) {
            val source = KeyPair.fromSecretSeed(privateKey)
            val publicKey = source.accountId
            userData = repository.getUserByPublicId(publicKey)
            Log.d("userData", "user data " + userData)

            //TODO: ziskat z dbs zaznam podla publicKey, ziskat salt, iv, encryptedText
            //odkomentuj :D
            
//            //salt z dbs
//            val salt: ByteArray = ???
//            //iv z dbs
//            val inicializationVector: ByteArray = ???
//
//            //toto je privateKey(zasifrovany) z dbs
//            val encryptedTextFromDB: String = ???
//
//            val e = Encryption()
//            //zahashujem novozadany pin pomocou saltu z dbs
//            val secretKey: SecretKey = e.hashPinLogin(salt, pinCode)
//
//            // prazdny object
//            var hped = HashedPinEncryptedData()
//
//            //hpedNew bude obsahovat po zbehnuti iba encryptedText->novozasifrovany text(privateKey) podla zahashovaneho pinu, ostatne atributy tu mas uz
//            val hpedNew: HashedPinEncryptedData? = e.encrypt(privateKey, secretKey, inicializationVector, hped)
//
//            //skontrolujem ci sa novy encryptedText(zasifrovany privateKey) rovna tomu v dbs, ak ano prihlasim
//            if(hpedNew.encryptedText.equals(encryptedTextFromDB)) {
//                bool = true
//            }
        }
        return bool

    }

    fun updateBalance(user: LiveData<UserRegistration>) {
        viewModelScope.launch (Dispatchers.IO) {
            val userId = user.value?.id
            val balance = user.value?.let { StellarService.getBalanceByPublicKey(it.publicKey) }
            if (balance != null && userId != null) {
                repository.updateBalanceByUserId(userId, balance)
            }
        }
    }

    fun updateTransactions(user: LiveData<UserRegistration>) {
        viewModelScope.launch (Dispatchers.IO) {
            val userId = user.value?.id
            val transactions = user.value?.let { StellarService.getTransactionsByPublicKey(it.publicKey) }
            val publicKey = user.value?.publicKey
            if (transactions != null && publicKey != null && userId != null) {
                for (transaction in transactions) {
                    if (transaction.isTransactionSuccessful == true) {
                        val date = Instant.parse(transaction.createdAt)
                        var transactionType : String
                        var partnerHash : String
                        if (transaction.from == publicKey) {
                            transactionType = "debit"
                            partnerHash = transaction.to
                        }
                        else {
                            transactionType = "credit"
                            partnerHash = transaction.from
                        }

                        val newTransaction = Transaction(
                            transactionHash = transaction.transactionHash,
                            userRegistrationId = userId,
                            type = transactionType,
                            amount = transaction.amount.toDouble(),
                            partnerHash = partnerHash,
                            date = date,
                        )

                        repository.addTransaction(newTransaction)
                    }
                }
            }
        }
    }

    fun updateUserById(userId: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            val user = repository.getUserById(userId)
            updateBalance(user)
            updateTransactions(user)
        }
    }

    fun updateUserByPublicKey(publicKey: String) {
        viewModelScope.launch (Dispatchers.IO) {
            val user = repository.getUserByPublicId(publicKey)
            updateBalance(user)
            updateTransactions(user)
        }
    }
}