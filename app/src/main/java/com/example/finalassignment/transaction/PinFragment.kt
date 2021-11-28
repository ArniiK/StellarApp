package com.example.finalassignment.transaction

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.finalassignment.R
import com.example.finalassignment.cryptography.Encryption
import com.example.finalassignment.cryptography.HashedPinEncryptedData
import com.example.finalassignment.databinding.FragmentPinBinding
import com.example.finalassignment.roomdb.ActiveUser
import com.example.finalassignment.roomdb.ActiveUserViewModel
import com.example.finalassignment.roomdb.UserRegistration
import com.example.finalassignment.roomdb.UserRegistrationViewModel
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import org.stellar.sdk.responses.AccountResponse
import java.io.InputStream
import java.net.URL
import java.util.*
import javax.crypto.SecretKey


class PinFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding:FragmentPinBinding
    private lateinit var listener: OnTransactionConfirmedListener
    private var privateKey: String? = null
    private lateinit var mUserRegistrationViewModel: UserRegistrationViewModel
    private lateinit var mActiveUserViewModel: ActiveUserViewModel
    private var test: List<UserRegistration> = emptyList()

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
        mActiveUserViewModel = ViewModelProvider(this).get(ActiveUserViewModel::class.java)
        mUserRegistrationViewModel = ViewModelProvider(this).get(UserRegistrationViewModel::class.java)
        mUserRegistrationViewModel.getAllUsers.observe(viewLifecycleOwner, Observer { it ->
            test = it
        })
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
//
//    class CreateAcc(): AsyncTask<Unit, Unit, Unit>(), ViewModelStoreOwner, LifecycleOwner {
//        private lateinit var mUserRegistrationViewModel: UserRegistrationViewModel
//
//        private var test: List<UserRegistration> = emptyList()
//        @SuppressLint("WrongThread")
//        override fun doInBackground(vararg params: Unit?) {
//            mUserRegistrationViewModel = ViewModelProvider(this).get(UserRegistrationViewModel::class.java)
//            mUserRegistrationViewModel.getAllUsers.observe(this) { it ->
//                test = it
//            }
//
//
//
//        }
//
//        override fun getViewModelStore(): ViewModelStore {
//            TODO("Not yet implemented")
//        }
//
//        override fun getLifecycle(): Lifecycle {
//            TODO("Not yet implemented")
//        }
//
//    }


    private fun confirmTransaction(privateKey: String?){
        val pinCode = binding.editTextNumberPassword.text.toString()


        // funkcia tryToLogin ma vratit ci je spravny pin pre dany privateKey
//        val isPinCorrect = mUserRegistrationViewModel.tryToLogin(privateKey, pinCode)
        var isPinCorrect = false


        Log.d("source " ,"source ")
        val source = KeyPair.fromSecretSeed(privateKey)

        val publicKey = source.accountId
        Log.d("PK " ,"PK ")

        var user: UserRegistration? = null
        for (item in test)
        {
            if (item.publicKey.equals(publicKey))
            {
                user = item

                Log.d("useri obtainuty" ,"useri obtainuty")
                break
            }

        }

        if (user!= null)
        {
            Log.d("user null? " ,"user null? ")
            //            //salt z dbs
            val salt: ByteArray? = user.salt
            Log.d("salt generated " ,"salt generated  ")
            //iv z dbs
            val inicializationVector: ByteArray? = user.iv

            //toto je privateKey(zasifrovany) z dbs
            val encryptedTextFromDB: String? = user.privateKey

            val e = Encryption()
            //zahashujem novozadany pin pomocou saltu z dbs
            val secretKey: SecretKey = e.hashPinLogin(salt, pinCode)

            // prazdny object
            var hped = HashedPinEncryptedData()

            //hpedNew bude obsahovat po zbehnuti iba encryptedText->novozasifrovany text(privateKey) podla zahashovaneho pinu, ostatne atributy tu mas uz
            val hpedNew: HashedPinEncryptedData? = e.encrypt(privateKey, secretKey, inicializationVector, hped)
            if(hpedNew?.encryptedText.equals(encryptedTextFromDB)) {
                isPinCorrect = true

            }
        }



        //skontrolujem ci sa novy encryptedText(zasifrovany privateKey) rovna tomu v dbs, ak ano prihlasim



        if (isPinCorrect == true){
            val activeUser = ActiveUser(user!!.id, "Y")
            mActiveUserViewModel.addActiveUser(activeUser)
            Toast.makeText(requireContext(),"ASABACHALA SABACHALA SAUNDSKA ARABIA",Toast.LENGTH_LONG).show()

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