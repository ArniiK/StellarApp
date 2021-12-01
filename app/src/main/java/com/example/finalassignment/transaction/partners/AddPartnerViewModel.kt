package com.example.finalassignment.transaction.partners

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalassignment.transaction.ValidationResponse

class AddPartnerViewModel : ViewModel(){

    var _partnerAddingKey = MutableLiveData<String>()
    val getPartnerAddingKey: LiveData<String> = _partnerAddingKey

    var _partnerAddingNickname = MutableLiveData<String>()
    val getPartnerAddingNickname: LiveData<String> = _partnerAddingNickname


    private var _eventDBPartnerAdded = MutableLiveData<PartnerDBResponse>()
    val eventDBPartnerAdded: LiveData<PartnerDBResponse>
        get() = _eventDBPartnerAdded

    private var _eventPartnerFieldValidation= MutableLiveData<ValidationResponse>()
    val eventPartnerFieldValidation: LiveData<ValidationResponse>
        get() = _eventPartnerFieldValidation

    init {
        _partnerAddingKey.value = ""
        _partnerAddingNickname.value = ""
    }

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

        _eventDBPartnerAdded.value = persistToDb()

   }

    private fun persistToDb() :PartnerDBResponse{

        val newPartner = Partner()
        newPartner.nickName = getPartnerAddingNickname.value.toString()
        newPartner.publicKey = getPartnerAddingKey.value.toString()
        
        val response = PartnerDBResponse()
        
        response.message = "Partner added - success"
        response.isSuccess = true
        

        return response
    }


}