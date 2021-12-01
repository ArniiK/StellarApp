package com.example.finalassignment.transaction

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import com.example.finalassignment.DbUpdateService
import com.example.finalassignment.StellarService
import com.example.finalassignment.cryptography.Encryption
import com.example.finalassignment.cryptography.HashedPinEncryptedData
import com.example.finalassignment.roomdb.*
import com.example.finalassignment.transaction.partners.AllPartnersDBResponse
import com.example.finalassignment.transaction.partners.PartnerDBResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.finalassignment.singleton.ActiveUserSingleton
import com.example.finalassignment.transaction.partners.Partner
import com.example.finalassignment.transaction.partners.PinValidationResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.stellar.sdk.KeyPair
import javax.crypto.SecretKey

class TransactionViewModel(application: Application) : AndroidViewModel(application) {      //view model pre transakciu a pin

    var currentUser: LiveData<UserRegistration>? = null
    var getAllPartners: LiveData<List<PartnerDB>>
    var getPartnerByPK = MutableLiveData<PartnerDB>()
      //emptyList()
    private val partnerRepository: PartnerRepository
    private val userRegistrationRepository: UserRegistrationRepository

    var getAllPartnersNew :MutableLiveData<List<PartnerDB>> = MutableLiveData<List<PartnerDB>>()

    init {
        val partnerDAO =  UserRegistrationDatabase.getDatabase(application).partnerDAO()
        partnerRepository = PartnerRepository(partnerDAO)
        val userRegistrationDao = UserRegistrationDatabase.getDatabase(application).userRegistrationDAO()
        userRegistrationRepository = UserRegistrationRepository(userRegistrationDao)
        getAllPartners = partnerRepository.getAllPartners
    }

    fun addPartner(partnerDB: PartnerDB ){
        viewModelScope.launch (Dispatchers.IO){
            partnerRepository.addPartner(partnerDB)
        }
    }

    fun deletePartner(partnerDB: PartnerDB){
        viewModelScope.launch(Dispatchers.IO){
            partnerRepository.deletePartner(partnerDB)
        }
    }

    fun getPartnerByPK(pK: String){
        val result = MutableLiveData<PartnerDB>()
        viewModelScope.launch(Dispatchers.IO){
            getPartnerByPK = partnerRepository.getPartnerByPK(pK) as MutableLiveData<PartnerDB>
        }
    }

    fun getCurrentUser(userId: Int): LiveData<UserRegistration> {
        currentUser = userRegistrationRepository.getUserById(userId)
        return currentUser!!
    }

     fun getAllPartnersByActiveUser(pK: String){

        // var getAllPartnersByPk: MutableLiveData<List<PartnerDB>> = MutableLiveData<List<PartnerDB>>()
         // var result = MutableLiveData<List<PartnerDB>>()

        viewModelScope.launch(){
            //result = partnerRepository.getAllPartnersByActiveUser(pK)
            getAllPartnersNew.value = partnerRepository.getAllPartnersByActiveUser(pK)
            //getAllPartnersByPk = (partnerRepository.getAllPartnersByActiveUser(pK)) as MutableLiveData<List<PartnerDB>>
            //getAllPartnersByPk = result.postValue()


        }
       // return  getAllPartnersByPk
        //result.observe(lifeC)
    }

    var _publicKey = MutableLiveData<String>()
    val getpublicKey: LiveData<String> = _publicKey


    private val _balance = MutableLiveData<String>()
    val balance: LiveData<String>
        get() = _balance

    var _amount = MutableLiveData<String>()
    val getamount: LiveData<String> = _amount

    //pin validation

   var _pin = MutableLiveData<String>()
    val getpin: LiveData<String> = _pin

    //partners
    var _partner = MutableLiveData<Partner>()
    val partner: LiveData<Partner> = _partner

    var _partnerKey = MutableLiveData<String>()
    val getPartnerKey: LiveData<String> = _partnerKey


    var _partnerList = MutableLiveData<MutableList<PartnerDB>>()      //list of objects

    private val _eventInputsVerified = MutableLiveData<Boolean>()
    val eventInputsVerified: LiveData<Boolean>
        get() = _eventInputsVerified


    private val _eventPinConfirmed = MutableLiveData<Boolean>()
    val eventPinConfirmed: LiveData<Boolean>
        get() = _eventPinConfirmed

    private val _eventTransactionSuccess= MutableLiveData<Boolean>()
    val eventTransactionSuccess: LiveData<Boolean>
        get() = _eventTransactionSuccess


    private var _eventPinControlResponse = MutableLiveData<PinValidationResponse>()
    val eventPinControlResponse: LiveData<PinValidationResponse>
        get() = _eventPinControlResponse

    private var _eventValidationResponse = MutableLiveData<ValidationResponse>()
    val eventValidationResponse: LiveData<ValidationResponse>
        get() = _eventValidationResponse


    private var _eventPartnerFromDBremoval = MutableLiveData<PartnerDBResponse>()
    val eventUserFromDBremoval: LiveData<PartnerDBResponse>
        get() = _eventPartnerFromDBremoval

    private var _eventPartnersFetched = MutableLiveData<AllPartnersDBResponse>()
    val eventPartnersFetched: LiveData<AllPartnersDBResponse>
        get() = _eventPartnersFetched

    // viemodel livedata for new partner adding

    var _partnerAddingKey = MutableLiveData<String>()
    val getPartnerAddingKey: LiveData<String> = _partnerAddingKey

    var _partnerAddingNickname = MutableLiveData<String>()
    val getPartnerAddingNickname: LiveData<String> = _partnerAddingNickname


    private var _eventDBPartnerAdded = MutableLiveData<PartnerDBResponse>()
    val eventDBPartnerAdded: LiveData<PartnerDBResponse>
        get() = _eventDBPartnerAdded

    private var _eventDBPartnerDeleted = MutableLiveData<PartnerDBResponse>()
    val eventDBPartnerDeleted: LiveData<PartnerDBResponse>
        get() = _eventDBPartnerAdded


    //event, kedy vznikne potreba ulozit partnera do recycler view
    private var _eventPartnerToRecycler = MutableLiveData<Partner>()
    val eventPartnerToRecycler: LiveData<Partner>
        get() = _eventPartnerToRecycler

    private var _eventPartnerFieldValidation= MutableLiveData<ValidationResponse>()
    val eventPartnerFieldValidation: LiveData<ValidationResponse>
        get() = _eventPartnerFieldValidation



    init {

        _publicKey.value = ""
        _balance.value = "1000"
        _amount.value = ""
        _pin.value = ""
        //_partnerKey.value =""
        //_eventTransactionSuccess.value = false
        //_eventInputsVerified.value = false

        //partner adding variables
        _partnerAddingKey.value = ""
        _partnerAddingNickname.value = ""
    }


    fun validateTransaction(){
        viewModelScope.launch (Dispatchers.IO){
            _eventValidationResponse.postValue(performValidation()!!)
        }
        //_eventInputsVerified.value = perform_validation()


    }

    suspend fun performValidation(): ValidationResponse{
        val response = ValidationResponse()

        //skontroluj zostatok, skontroluj, ci existuje moj ucet, jeho ucet, otvor zadanie pinu
        var waitFor = viewModelScope.async(Dispatchers.IO) {
            if((balance.value?.toDoubleOrNull() != null)  || (getamount.value?.toDoubleOrNull() != null) ) {

                if ((!getamount.value.toString().isEmpty()) && (!balance.value.toString().isEmpty())) {    //ci je balance double cislo
                    //amount field not empty
                    Log.i("input validation", "amount field not empty " + balance.value.toString() )

                    val amountDecimal = getamount.value.toString().toDouble()
                    val balanceDecimal = StellarService.getBalanceByPublicKey(ActiveUserSingleton.publicKey)

                    if (balanceDecimal < amountDecimal) {
                        // insufficient balance
                        Log.i("input validation", "insufficient balance")
                        //Toast.makeText(activity, "Insufficient balance", Toast.LENGTH_LONG).show()
                        response.isSuccess = false
                        response.message = "Insufficient balance"
                        return@async response

                    } else {

                        //balance sufficient, perform account existance check
                        Log.i("input validation", "balance sufficient, perform account existance check")


                        if (getpublicKey.value.toString().isNotEmpty()){
                            //publickey field not empty
                            Log.i("input validation","publickey not field empty")

                            var accountExists = StellarService.checkAccountExists(getpublicKey.value.toString())
                            if (accountExists) {

                                // state of sufficient balance and existing recipient account
                                // PIN verification
                                Log.i("input validation","state of sufficient balance and existing recipient account, check pin")
                                response.message = "Validatation succesfull"
                                response.isSuccess = true
                                return@async response

                            }
                            //not existing account
                            else {
                                Log.i("input validation","public key not on stelar network")
                                // Toast.makeText(activity,"Recipient account not on stellar network", Toast.LENGTH_LONG).show()
                                response.message = "Recipient account not on stellar network"
                                response.isSuccess = false
                                return@async response
                            }

                        }
                        else{
                            //Toast.makeText(activity,"Recipient account field empty", Toast.LENGTH_LONG).show()
                            Log.i("input validation","publickey field empty")
                            response.message = "Recipient Public key empty"
                            response.isSuccess = false
                            return@async response
                        }




                    }
                } else {
                    // ammount field empty
                    Log.i("input validation","ammount, balance empty")
                    //Toast.makeText(activity, "Amount field empty", Toast.LENGTH_LONG).show()
                    response.message = "Amount field empty"
                    response.isSuccess = false
                    return@async response

                }
            }
            else{
                Log.i("input validation","balance not a number decimal")
                // Toast.makeText(activity, "Your balance is not double number", Toast.LENGTH_LONG).show()
                response.message = "Balance not a number dec"
                response.isSuccess = false
                return@async response
            }
        }
        waitFor.await()
        return response
    }



    fun onPinConfirm(){

        val pinResponse = PinValidationResponse()
        try{
            if((getpin.value.toString().length == 4) && getpin.value.toString().isDigitsOnly()){    // field of 4 numbers

                val pinCode = getpin.value

                var isPinCorrect = false

                //vytiahnem prihlaseneho usera z dbs ...
                val user: ActiveUserSingleton = ActiveUserSingleton

                //z daneho usera ziskam data
                //salt z dbs
                val salt: ByteArray? = user.salt
                //iv z dbs
                val inicializationVector: ByteArray? = user.iv
                //toto je privateKey(zasifrovany) z dbs
                val encryptedSenderPrivateKeyFromDB: String? = user.privateKey

                val senderPublicKeyFromDB: String? = user.publicKey


                val e = Encryption()
                //zahashujem novozadany pin pomocou saltu z dbs
                val secretKey: SecretKey = e.hashPinLogin(salt, pinCode)

                // prazdny object
                var hped = HashedPinEncryptedData()
                hped.encryptedText = encryptedSenderPrivateKeyFromDB
                hped.hashedPin = secretKey

                val decryptedPrivateKey = e.decrypt(hped, inicializationVector)

                val source = KeyPair.fromSecretSeed(decryptedPrivateKey)
                val senderPublicKeyFromDecryption = source.accountId

                //skontrolujem ci sa novy publicKey ziskany z desifrovaneho(pomocou zadaneho pinu) privateKey, rovna public Key z databazy pre prihlaseneho usera
                if(senderPublicKeyFromDecryption.equals(senderPublicKeyFromDB)) {
                    isPinCorrect = true

                }

                if (isPinCorrect == true){

                    pinResponse.isSuccess = true
                    pinResponse.message = "Pin correct, transaction"
                    pinResponse.decryptedPrivateKey = decryptedPrivateKey.toString() //treba ku transakcii
                    _eventPinControlResponse.value = pinResponse
                }
                else{

                    pinResponse.isSuccess = false
                                pinResponse.message = "Pin incorrect, try again"
                    _eventPinControlResponse.value = pinResponse
                }
            }
            else{

                pinResponse.isSuccess = false
                pinResponse.message = "Pin incorrect, try again"
                _eventPinControlResponse.value = pinResponse
            }
        }catch (e: Exception){
            println("pin is not correct" + e.stackTrace)
            pinResponse.isSuccess = false
            pinResponse.message = "Pin incorrect, try again"
            _eventPinControlResponse.value = pinResponse

        }
    }

    fun performTransaction(decryptedPrivateKey: String){

        //po uspesnom overeni pinu sa z transaction fragment zavola funkcia na vykonanie transakcie
        //natiahnute su polia public key a amount, pomocou 2way databinding, live data
        var amount = getamount.value.toString().toDouble()
        var recipientPublicKey = getpublicKey.value.toString()

        Log.d("transaction state", "PERFORMED")
        Log.d("dec private ", decryptedPrivateKey)
        Log.d("recipient public ", getpublicKey.value.toString())
        Log.d("amount to send ", getamount.value.toString())


        GlobalScope.launch(Dispatchers.IO) {
            val keyPair: KeyPair = KeyPair.fromSecretSeed(decryptedPrivateKey)
            var balance: Double = StellarService.getBalanceByPublicKey(keyPair.accountId)

            if (balance >= amount ) {
                var accountExists: Boolean = StellarService.checkAccountExists(recipientPublicKey)
                if (accountExists) {
                    var successful = false
                    if (amount != null && recipientPublicKey != null && decryptedPrivateKey != null) {
                        successful = StellarService.sendTransaction(
                            decryptedPrivateKey,
                            recipientPublicKey,
                            amount,
                        )
                    }
                    if (successful) {
                        Log.d("transaction state", "SUCCESSFUL")
                    }
                    else {
                        Log.d("transaction state", "FAILED")
                    }

                }
            }
            GlobalScope.launch(Dispatchers.IO) {
                DbUpdateService.updateBalance(ActiveUserSingleton.id, ActiveUserSingleton.publicKey)
            }
            GlobalScope.launch(Dispatchers.IO) {
                DbUpdateService.updateTransactions(ActiveUserSingleton.id, ActiveUserSingleton.publicKey)
            }
        }
    }


    fun onPartnerChosen(pkey: String){

        Log.i("partner chosen", pkey)
        _partnerKey.value = pkey
    }



    fun onPartnerRemoval(partner: PartnerDB){


        var ondelResponse = PartnerDBResponse()

        viewModelScope.launch (Dispatchers.IO) {

            try {   //pokus sa zmazat partnera
                partnerRepository.deletePartner(partner)
                ondelResponse.isSuccess = true
                ondelResponse.message = "partner " + partner.nickName+ " deleted"
            }
            catch (e: Exception){

                ondelResponse.isSuccess = true
                ondelResponse.message = "partner deleting not successful"
            }

            _eventDBPartnerDeleted.postValue(ondelResponse)
        }


    }

    private fun removePartnerFromDb(partner: PartnerDB, position: Int) :PartnerDBResponse{   //vymaz z db

        var dbRemoved = true
        val dbRemResponse = PartnerDBResponse()
        dbRemResponse.message = "Removal success"
        dbRemResponse.isSuccess = true
        dbRemResponse.partner = partner
        dbRemResponse.position = position

        return dbRemResponse
    }


    //partner adding functions

    fun onPartnerAdd(){

        val validResponse = ValidationResponse()

        //validuj data
        if (getPartnerAddingKey.value!!.isNotEmpty()){     //nickname nemusi byt vyplneny

            validResponse.isSuccess = true
            validResponse.message = "fields valid"
        }
        else{

            validResponse.isSuccess = false
            Log.i("public key partner add","public key empty")
            validResponse.message = "Public key field empty"
        }

        _eventPartnerFieldValidation.value = validResponse

    }

    fun persistPartner(){   //save partner to db, on success set event and update recycler by new partner

        viewModelScope.launch (Dispatchers.IO) {
            _eventDBPartnerAdded.postValue(persistToDb())
        }
    }

    private suspend fun persistToDb() :PartnerDBResponse{


        val newPartner = PartnerDB(0,getPartnerAddingKey.value.toString(),ActiveUserSingleton.publicKey, getPartnerAddingNickname.value.toString())

        //val persisted = true        //TODO: save to DB - provizorna premenna

        val response = PartnerDBResponse()

        try{
            partnerRepository.addPartner(newPartner)
            response.message = "Partner added - success"
            response.isSuccess = true
            response.partner = newPartner // partnerov novy index

           // _partnerList.value?.add(response.partner)     //  on succesfull add to db, add also to livedata partners
            return  response
        }catch (e: Exception){
            response.isSuccess = false
            response.message = "DB partner save failed"
            return response     //ked neulozi do db - response s chybou
        }

    }


//NEPOUZIVA sauz je to jednoduchsie
    fun signalRecycler(partner: PartnerDB, position: Int){       //vyvola event ze treba updatenut recycler, Partnerom ktory sa vlozil do db

        val newlist = _partnerList.value

        newlist?.add(partner)
        _partnerList.value = newlist!!

        val recyclerPartner: Partner = Partner()
        recyclerPartner.nickName = partner.nickName
        recyclerPartner.publicKey = partner.publicKey
        recyclerPartner.position = newlist.size -1      //partner zrovna pridany na koniec listu

        _eventPartnerToRecycler.value = recyclerPartner  //Partner()

        Log.i("partner in viewmodel" ,partner.publicKey)

    }

}

