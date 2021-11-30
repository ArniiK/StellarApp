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
import com.example.finalassignment.databinding.FragmentAfterLoginBinding
import com.example.finalassignment.databinding.FragmentLoginBinding
import com.example.finalassignment.roomdb.ActiveUser
import com.example.finalassignment.roomdb.ActiveUserViewModel
import com.example.finalassignment.roomdb.UserRegistration
import com.example.finalassignment.roomdb.UserRegistrationViewModel
import com.example.finalassignment.transaction.PinFragment
import kotlinx.android.synthetic.main.fragment_login.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(), View.OnClickListener, PinFragment.OnTransactionConfirmedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mActiveUserViewModel = ViewModelProvider(this).get(ActiveUserViewModel::class.java)
        //TODO: ARNE
//        mActiveUserViewModel.getActiveUser().

        // priklad observu treba este pridat do viewu premennu livedata a ju vyplnovat a potom to tuna observovat
//        mUserRegistrationViewModel.getAllUsers.observe(viewLifecycleOwner, Observer { it ->
//            test = it
//        })



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

        // Arnicek a marecek
        //kod ktory ste tu mali ate vo funkcii logIn dole
        //

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {

        when(v){

            binding.loginSubmitBtn -> logIn()
            binding.registerTextClickableLogin -> moveToRegister()
        }
    }

    fun logIn(){    //TODO: check the credentials and move to main wrapping fragment

        // pin a username uz nie su, ma byt iba private key

        //val pin = binding.PInfragmentLoginEditText.text.toString()
        //val userName = binding.usernameLoginEditText.text.toString()

        //TODO tieto funckie si presun do ontransaction confirmed
//        mUserRegistrationViewModel.getAllUsers
//        val allData:LiveData<List<UserRegistration>> =  mUserRegistrationViewModel.getAllUsers

        val privateKey = binding.loginPrivateKeyEditText.text.toString()

        if(privateKey.isNotEmpty()){
            val dialog = PinFragment()

            var args: Bundle? = Bundle()
            args?.putString("privateKey", privateKey)

            dialog.setArguments(args)
            dialog.setOnTransactionConfirmedListener(this)
            dialog.show(activity?.supportFragmentManager!!, "PinDialog")
            Toast.makeText(activity,"Sent",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(activity,"Enter Private Key",Toast.LENGTH_LONG).show()
        }




//        val action = LoginFragmentDirections.actionLoginFragmentToWrappingFragment()
//        view?.findNavController()?.navigate(action)
    }

    fun moveToRegister(){


        val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
        view?.findNavController()?.navigate(action)
    }


    override fun onTransactionConfirmed() {


        val action = LoginFragmentDirections.actionLoginFragmentToWrappingFragment()
        view?.findNavController()?.navigate(action)
        Toast.makeText(activity,"Pin succesfuly confirmed", Toast.LENGTH_LONG).show()
    }
}
