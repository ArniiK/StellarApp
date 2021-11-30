package com.example.finalassignment.transaction

import android.app.Application
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import com.example.finalassignment.cryptography.Encryption
import com.example.finalassignment.cryptography.HashedPinEncryptedData
import com.example.finalassignment.roomdb.PartnerRepository
import com.example.finalassignment.roomdb.UserRegistrationDatabase
import com.example.finalassignment.transaction.partners.AllPartnersDBResponse
import com.example.finalassignment.transaction.partners.PartnerDBResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.finalassignment.roomdb.PartnerDB
import com.example.finalassignment.singleton.ActiveUserSingleton
import com.example.finalassignment.transaction.partners.Partner
import com.example.finalassignment.transaction.partners.PinValidationResponse
import org.stellar.sdk.KeyPair
import javax.crypto.SecretKey

class TransactionViewModel(application: Application) : AndroidViewModel(application) {      //view model pre transakciu a pin

    var getAllPartners: LiveData<List<PartnerDB>>
    var getPartnerByPK = MutableLiveData<PartnerDB>()
    private val partnerRepository: PartnerRepository

    init {
        val partnerDAO =  UserRegistrationDatabase.getDatabase(application).partnerDAO()
        partnerRepository = PartnerRepository(partnerDAO)
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


    //event, kedy vznikne potreba ulozit partnera do recycler view
    private var _eventPartnerToRecycler = MutableLiveData<Partner>()
    val eventPartnerToRecycler: LiveData<Partner>
        get() = _eventPartnerToRecycler

    private var _eventPartnerFieldValidation= MutableLiveData<ValidationResponse>()
    val eventPartnerFieldValidation: LiveData<ValidationResponse>
        get() = _eventPartnerFieldValidation



    init {

        _publicKey.value = ""
        _balance.value = "100"
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

        //_eventInputsVerified.value = perform_validation()
        _eventValidationResponse.value = perform_validation()

    }

        fun perform_validation(): ValidationResponse{     //skontroluj zostatok, skontroluj, ci existuje moj ucet, jeho ucet, otvor zadanie pinu

            val response = ValidationResponse()

        //TODO nepouzivat zobrazeny balance, treba zo stellaru natiahnut, ten je aktualny
        if((balance.value?.toDoubleOrNull() != null)  || (getamount.value?.toDoubleOrNull() != null) ) {

            if ((!getamount.value.toString().isEmpty()) && (!balance.value.toString().isEmpty())) {    //ci je balance double cislo
                //amount field not empty
                    Log.i("input validation", "amount field not empty " + balance.value.toString() )

                val amount_decimal = getamount.value.toString().toDouble()
                val balance_decimal = balance.value.toString().toDouble()

                if (balance_decimal < amount_decimal) {
                    // insufficient balance
                    Log.i("input validation", "insufficient balance")
                    //Toast.makeText(activity, "Insufficient balance", Toast.LENGTH_LONG).show()
                    response.isSuccess = false
                    response.message = "Insufficient balance"
                    return response

                } else {

                    //balance sufficient, perform account existance check
                    Log.i("input validation", "balance sufficient, perform account existance check")


                    if (getpublicKey.value.toString().isNotEmpty()){
                        //publickey field not empty
                        Log.i("input validation","publickey not field empty")

                        if (checkAccountExist(getpublicKey.value.toString())) {

                            // state of sufficient balance and existing recipient account
                            //PIN verification
                            Log.i("input validation","state of sufficient balance and existing recipient account, check pin")
                            response.message = "Validatation succesfull"
                            response.isSuccess = true
                            return response

                        }
                        //not existing account
                        else {
                            Log.i("input validation","public key not on stelar network")
                           // Toast.makeText(activity,"Recipient account not on stellar network", Toast.LENGTH_LONG).show()
                            response.message = "Recipient account not on stellar network"
                            response.isSuccess = false
                            return response
                        }

                    }
                    else{
                        //Toast.makeText(activity,"Recipient account field empty", Toast.LENGTH_LONG).show()
                        Log.i("input validation","publickey field empty")
                        response.message = "Recipient Public key empty"
                        response.isSuccess = false
                        return response
                    }




                }
            } else {
                // ammount field empty
                Log.i("input validation","ammount, balance empty")
                //Toast.makeText(activity, "Amount field empty", Toast.LENGTH_LONG).show()
                response.message = "Amount field empty"
                response.isSuccess = false
                return response

            }
        }
        else{
            Log.i("input validation","balance not a number decimal")
           // Toast.makeText(activity, "Your balance is not double number", Toast.LENGTH_LONG).show()
            response.message = "Balance not a number dec"
            response.isSuccess = false
            return response
        }
    }


    private fun checkAccountExist(publicKey: String) :Boolean{

        //TODO check if account exists on stellar network

        return true
    }

//    private fun checkAccountNotEmpty(publicKey: String) :Boolean{
//
//        if( binding.PKInputField.beneficiaryPKInputText.text.toString().isEmpty()){
//
//            return false
//        }
//
//        return true
//    }
//
//
//    override fun onTransactionConfirmed() {
//
//        //TODO tranzakcia
//        val transactionExecuted = true
//
//        if (transactionExecuted) {
//            //TODO refresh balance from stellar network
//            Toast.makeText(activity, "Your transaction was performed", Toast.LENGTH_LONG).show()
//        }
//        else  Toast.makeText(activity, "Transaction error - not executed", Toast.LENGTH_LONG).show()
//    }
//
//    fun getBalance(publicKey: PublicKey): Double{
//
//        return 1000.toDouble()
//    }


    fun onPinConfirm(){

        val pinResponse = PinValidationResponse()

        if((getpin.value.toString().length == 4) && getpin.value.toString().isDigitsOnly()){    // field of 4 numbers

//            val publicKey = getpublicKey.value
//            val amount = getamount.value

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
                pinResponse.message = "Pin icorrect, try again"
                _eventPinControlResponse.value = pinResponse
            }
        }
        else{

            pinResponse.isSuccess = false
            pinResponse.message = "Pin icorrect, try again"
            _eventPinControlResponse.value = pinResponse
        }

    }

    fun performTransaction(decryptedPrivateKey: String){

        Log.d("transaction state", "PERFORMED")
        Log.d("dec private ", decryptedPrivateKey)
        Log.d("recipient public ", getpublicKey.value.toString())
        Log.d("amount to send ", getamount.value.toString())
        //po uspesnom overeni pinu sa z transaction fragment zavola funkcia na vykonanie transakcie
        //natiahnute su polia public key a amount, pomocou 2way databinding, live data



    }


    fun checkPin(){


    }

    fun onTransactionSuccessful(){

        Log.d("Transaction succes", "Transaction successful")
        //  todo: refresh balance from network
    }

    fun onPartnerChosen(pkey: String){

        Log.i("partner chosen", pkey)
        _partnerKey.value = pkey
    }


//    fun updatePartnerList(){    //zavolaj funkciu na natiahnutie partnerov z db, live data items update
//                                // na response prebehne vo fragmente notifydatasetchanged v adapteri recyclere view
//        viewModelScope.launch (Dispatchers.IO) {
//            val newPartnersResponse: AllPartnersDBResponse = fetchPartnerList()
//            _partnerList.postValue(newPartnersResponse.partnerList)
//                    //aktualizuj zoznam partnerov pre recycler
//            _eventPartnersFetched.postValue(newPartnersResponse)
//                       //event je observovany, nan sa zmeni obsah recyclera podla live dat
//        }
//    }

//    fun fetchPartnerList() :AllPartnersDBResponse{
//
//        //provizorne data
//        //val p1 = PartnerDB("petrik", "XOOXOXOXOXOXO")
//        //val p2 = PartnerDB("jozko", "JOJJOJOJOOJJOOJJOOJOJOJ")
//        //newPartners.add(p1)
//        //newPartners.add(p2)
//        val res : AllPartnersDBResponse = AllPartnersDBResponse()
//
//        try{
//
//            var partnerss = partnerRepository.getAllPartners.value
//            var mutablePartners = partnerss
//
//            var newPartners = mutableListOf<PartnerDB>()
//
//            for (item in mutablePartners!!){      // prekopirujem si list do mutabl listu
//
//                newPartners.add(item)
//            }
//
//            res.partnerList = newPartners
//            res.message = "success"
//            res.isSuccess = true
//
//            return res      // DB response obsahujuca zozanm partnerov
//        }
//        catch (e: java.lang.Exception){
//
//            res.isSuccess = false
//            res.message = "Partner DB loading not successful"
//            return res
//        }
//    }


    fun onPartnerRemoval(position: Int){

        //val toBeRemoved: PartnerDB? = _partnerList.value?.get(position)
        //val removalResponse = removePartnerFromDb(toBeRemoved!!, position)

//        if (removalResponse.isSuccess){
//
//            _partnerList.value?.removeAt(position)      //zmaz partnera v liste
//        }
//        _eventPartnerFromDBremoval.value =   removalResponse





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

    fun resetInputs(){



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


//        newPartner.nickName = getPartnerAddingNickname.value.toString()
//        newPartner.publicKey = getPartnerAddingKey.value.toString()

        val newPartner = PartnerDB(getPartnerAddingNickname.value.toString(), getPartnerAddingKey.value.toString())

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

