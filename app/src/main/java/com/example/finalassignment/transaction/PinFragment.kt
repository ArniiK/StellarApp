package com.example.finalassignment.transaction

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
import androidx.navigation.findNavController
import com.example.finalassignment.LoginFragmentDirections
import com.example.finalassignment.R
import com.example.finalassignment.cryptography.Encryption
import com.example.finalassignment.cryptography.HashedPinEncryptedData
import com.example.finalassignment.databinding.FragmentPinBinding
import com.example.finalassignment.roomdb.ActiveUser
import com.example.finalassignment.roomdb.ActiveUserViewModel
import com.example.finalassignment.roomdb.UserRegistration
import com.example.finalassignment.roomdb.UserRegistrationViewModel
import com.example.finalassignment.singleton.ActiveUserSingleton
import org.stellar.sdk.KeyPair
import java.lang.Exception
import javax.crypto.SecretKey
import kotlin.jvm.Throws


class PinFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding:FragmentPinBinding
    private lateinit var listener: OnTransactionConfirmedListener
    private var privateKey: String? = null
    private var recipientPublicKey: String? = null
    private var amount: String? = null
    private lateinit var mUserRegistrationViewModel: UserRegistrationViewModel
    private lateinit var mActiveUserViewModel: ActiveUserViewModel
    private var test: List<UserRegistration> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedArgs = getArguments()
        if (receivedArgs != null) {
            if(receivedArgs.getString("privateKey")!=null){
                privateKey = receivedArgs.getString("privateKey")
            }else if (receivedArgs.getString("publicKey")!=null){
                recipientPublicKey = receivedArgs.getString("publicKey")
                amount = receivedArgs.getString("amount")
            }
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
        if (privateKey!=null){
            when(v){
                binding.pinConfirmButton -> confirmTransactionLogin(privateKey)
                binding.pinDismissButton -> closeTransaction()
            }
            privateKey = null

        }else if(recipientPublicKey != null){
            when(v){
                binding.pinConfirmButton -> confirmTransactionSend(recipientPublicKey, amount)
                binding.pinDismissButton -> closeTransaction()
            }
            recipientPublicKey = null
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

    @Throws(IllegalArgumentException::class)
    private fun confirmTransactionLogin(privateKey: String?){
        val pinCode = binding.editTextNumberPassword.text.toString()


        // funkcia tryToLogin ma vratit ci je spravny pin pre dany privateKey
//        val isPinCorrect = mUserRegistrationViewModel.tryToLogin(privateKey, pinCode)
        var isPinCorrect = false


        try {
            val source = KeyPair.fromSecretSeed(privateKey)
            val publicKey = source.accountId

            var user: UserRegistration? = null
            for (item in test) {
                if (item.publicKey.equals(publicKey)) {
                    user = item

                    Log.d("useri obtainuty", "useri obtainuty")
                    break
                }

            }

            if (user != null) {
                Log.d("user null? ", "user null? ")
                //            //salt z dbs
                val salt: ByteArray? = user.salt
                Log.d("salt generated ", "salt generated  ")
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
                val hpedNew: HashedPinEncryptedData? =
                    e.encrypt(privateKey, secretKey, inicializationVector, hped)
                //skontrolujem ci sa novy encryptedText(zasifrovany privateKey) rovna tomu v dbs, ak ano prihlasim
                if (hpedNew?.encryptedText.equals(encryptedTextFromDB)) {
                    isPinCorrect = true

                }
            }
            if (isPinCorrect == true){
                val activeUser = ActiveUser(user!!.id, "Y")
                mActiveUserViewModel.addActiveUser(activeUser)
                initActiveUserToSingleton(user)
                Toast.makeText(requireContext(),"Succesfully logged in",Toast.LENGTH_LONG).show()

                dialog?.cancel()        //zavriem dialog
                //previest tranzakciu v transaction fragment, ak sa podari, toast ze sa podarila, ak nie tak ze sa nepodarila, znovunacitanie zostatku a jeho refresh
                listener.onTransactionConfirmed()
            }
            else {
                Toast.makeText(activity,"Incorrect pin or private key, try again", Toast.LENGTH_LONG).show()
                dialog?.cancel()
            }

        }
        catch (e: Exception){
            println("private Key is not correct" + e.stackTrace)
            Toast.makeText(activity,"Incorrect pin or private key, try again", Toast.LENGTH_LONG).show()
            dialog?.cancel()

        }
    }

    private fun initActiveUserToSingleton(user: UserRegistration) {
        ActiveUserSingleton.id = user.id
        ActiveUserSingleton.iv = user.iv
        ActiveUserSingleton.privateKey = user.privateKey
        ActiveUserSingleton.publicKey = user.publicKey
        ActiveUserSingleton.salt = user.salt
    }

    private fun confirmTransactionSend(recipientPublicKey: String?, amount: String?){
        val pinCode = binding.editTextNumberPassword.text.toString()

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

            Toast.makeText(requireContext(),"Succesfully confirmed",Toast.LENGTH_LONG).show()

            //vykonaj tranzakciu




            dialog?.cancel()        //zavriem dialog
            //previest tranzakciu v transaction fragment, ak sa podari, toast ze sa podarila, ak nie tak ze sa nepodarila, znovunacitanie zostatku a jeho refresh
            listener.onTransactionConfirmed()
        }
        else Toast.makeText(activity,"Incorrect pin, try again", Toast.LENGTH_LONG).show()

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