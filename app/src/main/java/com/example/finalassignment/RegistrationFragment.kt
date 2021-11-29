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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var binding: FragmentRegistrationBinding


/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var registerBtn : Button
    private lateinit var loginClickable: TextView

    private lateinit var mUserRegistrationViewModel: UserRegistrationViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_registration,container, false)
        mUserRegistrationViewModel = ViewModelProvider(this).get(UserRegistrationViewModel::class.java)
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
                Toast.makeText(requireContext(),"Pin codes must be same.\nLength must be 4 or 6 digits.",Toast.LENGTH_LONG).show()
            if (returnState.equals(2))
                Toast.makeText(requireContext(),"User with this private key is already registered,\ntry another private key or log in",Toast.LENGTH_LONG).show()
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

        if(!pin.equals(pinAgain)||!validatePIN(pin))
        {
            return 1
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
        //TODO: mala by tu byt stelar funkcionalita - registracia na testnet

        //DATABASE


        Log.v("register function", "Register function launched")
        //todo: dopln registraciu
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
        return !(pin.length!=4 && pin.length!=6)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}