package com.example.finalassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.finalassignment.databinding.FragmentAfterLoginBinding
import com.example.finalassignment.databinding.FragmentLoginBinding
import com.example.finalassignment.roomdb.ActiveUser
import com.example.finalassignment.roomdb.ActiveUserViewModel
import com.example.finalassignment.roomdb.UserRegistration
import com.example.finalassignment.roomdb.UserRegistrationViewModel
import com.example.finalassignment.transaction.PinFragment
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : Fragment(), View.OnClickListener, PinFragment.OnTransactionConfirmedListener {

    private lateinit var loginBtn : Button
    private lateinit var loginClickable: TextView


    private lateinit var mUserRegistrationViewModel: UserRegistrationViewModel
    private var test: List<UserRegistration> = emptyList()

    private lateinit var mActiveUserViewModel: ActiveUserViewModel

    private var activeUser: ActiveUser? = null


    //binding
    private lateinit var binding: FragmentLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container,false)
        val view = binding.root

        loginBtn = binding.loginSubmitBtn
        loginBtn.setOnClickListener(this)

        loginClickable = binding.registerTextClickableLogin //klikatelny text registrovat
        loginClickable.setOnClickListener(this)

        return view
    }


    override fun onClick(v: View?) {

        when(v){

            binding.loginSubmitBtn -> logIn()
            binding.registerTextClickableLogin -> moveToRegister()
        }
    }

    private fun logIn(){
        val privateKey = binding.loginPrivateKeyEditText.text.toString()

        if(privateKey.isNotEmpty()){
            val dialog = PinFragment()

            var args: Bundle? = Bundle()
            args?.putString("privateKey", privateKey)

            dialog.setArguments(args)
            dialog.setOnTransactionConfirmedListener(this)
            dialog.show(activity?.supportFragmentManager!!, "PinDialog")
//            Toast.makeText(activity,"Sent",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(activity,"Enter Private Key",Toast.LENGTH_LONG).show()
        }
    }

    fun moveToRegister(){


        val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
        view?.findNavController()?.navigate(action)
    }


    override fun onTransactionConfirmed() {

        val action = LoginFragmentDirections.actionLoginFragmentToWrappingFragment()
        view?.findNavController()?.navigate(action)
//        Toast.makeText(activity,"Pin succesfuly confirmed", Toast.LENGTH_LONG).show()
    }
}
