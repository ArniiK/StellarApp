package com.example.finalassignment

import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.finalassignment.databinding.FragmentRegistrationBinding
import com.example.finalassignment.roomdb.UserRegistration
import com.example.finalassignment.roomdb.UserRegistrationViewModel
import kotlinx.android.synthetic.main.fragment_registration.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import org.stellar.sdk.responses.AccountResponse
import java.io.InputStream
import java.net.URL
import java.util.*
import com.example.finalassignment.cryptography.Encryption
import com.example.finalassignment.cryptography.HashedPinEncryptedData
import kotlin.jvm.Throws



private lateinit var binding: FragmentRegistrationBinding


class RegistrationFragment : Fragment(), View.OnClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var registerBtn : Button
    private lateinit var loginClickable: TextView

    private var test: List<UserRegistration> = emptyList()


    private lateinit var mUserRegistrationViewModel: UserRegistrationViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_registration,container, false)
        mUserRegistrationViewModel = ViewModelProvider(this).get(UserRegistrationViewModel::class.java)

        mUserRegistrationViewModel.getAllUsers.observe(viewLifecycleOwner, androidx.lifecycle.Observer{ it ->
            test = it
        })
        val view = binding.root

        binding.newAccountClickableText.setOnClickListener {
            GlobalScope.launch(context = Dispatchers.Main) {
                val pair = CreateAcc().execute()
                binding.privateKeyEditText.setText(pair.get().toString())
            }
        }

        registerBtn = binding.registrationBtn
        view.registrationBtn.setOnClickListener{
            val returnState = insertDataToDatabase()

            if (returnState.equals(0)) {
                Toast.makeText(requireContext(), "Successfully registered!", Toast.LENGTH_LONG).show()
                val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
                view?.findNavController()?.navigate(action)

            }
            if (returnState.equals(1))
                Toast.makeText(requireContext(),"Pin codes must be same.\nLength must be 4 digits.",Toast.LENGTH_LONG).show()
            if (returnState.equals(2))
                Toast.makeText(requireContext(),"User with this key already exists,\ntry another private key or log in",Toast.LENGTH_LONG).show()
        }

        loginClickable = binding.registrationClickableLoginText //klikatelny text na presun na login
        loginClickable.setOnClickListener(this)

        return view
    }

    /** Funkcia na pridanie do databazy
     * @return 0 -> pridalo uspnesne
     * @return 1 -> passwordy sa nezhoduju
     * @return 2 -> user s tymto privateKey uz je registrovany */
    @Throws(SQLiteException::class,SQLiteConstraintException::class, SQLiteAbortException::class)
    private fun insertDataToDatabase():Int
    {


        val privateKey = binding.privateKeyEditText.text.toString()
        val pinAgain = binding.registrationPinAgainPassword.text.toString()
        val pin = binding.registrationPinPassword.text.toString()

        val sourceCheck = KeyPair.fromSecretSeed(privateKey)
        val publicKeyCheck = sourceCheck.accountId

        //check ci uz nieje v db
        var user: UserRegistration? = null
        for (item in test) {
            if (item.publicKey.equals(publicKeyCheck)) {
                user = item
                break
            }
        }

        if(!pin.equals(pinAgain)||!validatePIN(pin))
        {
            return 1
        }

        if(user != null){
            return 2
        }

        val e = Encryption()
        val hped: HashedPinEncryptedData = e.hashPinRegistration(pin)
        val inicializationVector = e.generateIv()
        val hpedCompleted = e.encrypt(privateKey, hped.hashedPin, inicializationVector, hped)

        val source = KeyPair.fromSecretSeed(privateKey)
        val publicKey = source.accountId
        try{
            val userRegistration = UserRegistration(0, publicKey, hped.salt, hpedCompleted?.encryptedText, inicializationVector, 0.00)
            mUserRegistrationViewModel.addUser(userRegistration)
//            return 0

        }catch (e: Exception){
            println("Error while creating entry in dbs: $e")
            e.printStackTrace()
            return 2
        }
        return 0
    }

    override fun onClick(v: View?) {

        when(v){

            binding.registrationBtn -> register()//registruj a potom sa presun sa na hlavnu stranku
            binding.registrationClickableLoginText -> moveToLogin()
        }

    }

    fun register(){

        //DATABASE

        Log.v("register function", "Register function launched")

        val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
        view?.findNavController()?.navigate(action)

    }

    fun moveToLogin(){  //user chose to perform login, login page redirection

        val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
        view?.findNavController()?.navigate(action)
    }

    class CreateAcc(): AsyncTask<Unit, Unit, String>()
    {
        override fun doInBackground(vararg params: Unit?): String? {
            val pair: KeyPair? = KeyPair.random()

            val friendbotUrl = String.format(
                "https://friendbot.stellar.org/?addr=%s",
                pair!!.accountId
            )

            val response: InputStream = URL(friendbotUrl).openStream()
            val body: String = Scanner(response, "UTF-8").useDelimiter("\\A").next()
            println("SUCCESS! You have a new account :)\n$body")

            val server = Server("https://horizon-testnet.stellar.org")
            val account: AccountResponse = server.accounts().account(pair.accountId)
            println("Balances for account " + pair.accountId)
            for (balance in account.balances) {
                System.out.printf(
                    "Type: %s, Code: %s, Balance: %s%n",
                    balance.assetType,
                    balance.assetCode,
                    balance.balance
                )
            }
            return String(pair.secretSeed)


        }

    }

    fun validatePIN (pin: String): Boolean {
        return (pin.length==4)
    }

}