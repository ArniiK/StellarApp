package com.example.finalassignment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentPinBinding
import com.example.finalassignment.roomdb.UserRegistrationViewModel


class PinFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding:FragmentPinBinding
    private lateinit var listener: OnTransactionConfirmedListener
    private var privateKey: String? = null
    private lateinit var mUserRegistrationViewModel: UserRegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            val receivedArgs = getArguments()
            if (receivedArgs != null) {
                privateKey = receivedArgs.getString("privateKey")
            }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUserRegistrationViewModel = ViewModelProvider(this).get(UserRegistrationViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_pin,container, false)
        binding.pinConfirmButton.setOnClickListener(this)
        binding.pinDismissButton.setOnClickListener(this)

//        Log.d("privateKeyPin" ,"private key is: " + privateKey)

        return binding.root
    }

    override fun onClick(v: View?) {

        when(v){

            binding.pinConfirmButton -> confirmTransaction(privateKey)
            binding.pinDismissButton -> closeTransaction()
        }
    }

    private fun confirmTransaction(privateKey: String?){
        val pinCode = binding.editTextNumberPassword.text.toString()

        // funkcia tryToLogin ma vratit ci je spravny pin pre dany privateKey
        val isPinCorrect = mUserRegistrationViewModel.tryToLogin(privateKey, pinCode)

        if (isPinCorrect == true){

            dialog?.cancel()        //zavriem dialog
            //previest tranzakciu v transaction fragment, ak sa podari, toast ze sa podarila, ak nie tak ze sa nepodarila, znovunacitanie zostatku a jeho refresh
            listener.onTransactionConfirmed()
        }
        else Toast.makeText(activity,"Incorrect pin, insert again", Toast.LENGTH_LONG).show()

    }

    private fun closeTransaction(){
        Toast.makeText(activity,"Authorization closed", Toast.LENGTH_LONG).show()
        dialog?.cancel()
    }

    interface OnTransactionConfirmedListener{

        fun onTransactionConfirmed();
    }

    fun setOnTransactionConfirmedListener(listener: OnTransactionConfirmedListener){

        this.listener = listener
    }

}