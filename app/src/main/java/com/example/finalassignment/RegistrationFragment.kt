package com.example.finalassignment

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



        registerBtn = binding.registrationBtn
        view.registrationBtn.setOnClickListener{
            val returnState = insertDataToDatabase()

            if (returnState.equals(0)) {
                Toast.makeText(requireContext(), "Successfully registered!", Toast.LENGTH_LONG).show()
                val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
                view?.findNavController()?.navigate(action)

            }
            if (returnState.equals(1))
                Toast.makeText(requireContext(),"Passwords are not the same!",Toast.LENGTH_LONG).show()

        }

        loginClickable = binding.registrationClickableLoginText //klikatelny text na presun na login
        loginClickable.setOnClickListener(this)

        return view
    }

    /** Funkcia na pridanie do databazy
     * @return 0 -> pridalo uspnesne
     * @return 1 -> passwordy sa nezhoduju
     * @return 2 TODO */
    private fun insertDataToDatabase():Int
    {

        val userName = view?.privateKeyEditText?.text.toString()            //prerobte to pls na databinding
        val pinAgain = view?.registrationPinAgainPassword?.text.toString()
        val pin = view?.registrationPinPassword?.text.toString()


        val pair: org.stellar.sdk.KeyPair? = org.stellar.sdk.KeyPair.random()





        if(!pin.equals(pinAgain))
        {
            return 1
        }



        val userRegistration = UserRegistration(0,userName,pin, pair?.accountId.toString(),pair?.secretSeed.toString())
        mUserRegistrationViewModel.addUser(userRegistration)

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