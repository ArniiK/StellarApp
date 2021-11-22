package com.example.finalassignment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.finalassignment.databinding.FragmentRegistrationBinding

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
        binding = DataBindingUtil.inflate<FragmentRegistrationBinding>(inflater,R.layout.fragment_registration,container, false)
        val view = binding.root

        registerBtn = binding.registrationBtn
        registerBtn.setOnClickListener(this)

        loginClickable = binding.registrationClickableLoginText //klikatelny text na presun na login
        loginClickable.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {

        when(v){

            binding.registrationBtn -> register()//registruj a potom sa presun sa na hlavnu stranku
            binding.registrationClickableLoginText -> moveToLogin()
        }

    }

    fun register(){ //TODO: mala by tu byt stelar funkcionalita - registracia na testnet

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