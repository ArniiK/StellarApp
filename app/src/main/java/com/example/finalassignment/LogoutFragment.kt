package com.example.finalassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.finalassignment.databinding.FragmentLogoutBinding
import com.example.finalassignment.singleton.ActiveUserSingleton


class LogoutFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding:FragmentLogoutBinding
    private lateinit var listener: OnLogoutConfirmedListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_logout,container, false)
        binding.logoutConfirmButton.setOnClickListener(this)
        binding.logoutDismissButton.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
            when(v){
                binding.logoutConfirmButton -> confirmLogout()
                binding.logoutDismissButton -> closeLogout()
            }

    }

    private fun confirmLogout(){
        ActiveUserSingleton.id = -1
        ActiveUserSingleton.publicKey = ""
        ActiveUserSingleton.salt = null
        ActiveUserSingleton.privateKey = ""
        ActiveUserSingleton.iv = null

        dialog?.cancel()
        listener.onLogoutConfirmed()
    }

    private fun closeLogout(){
        dialog?.cancel()
    }

    interface OnLogoutConfirmedListener{

        fun onLogoutConfirmed();
    }

    fun setOnLogoutConfirmedListener(listener: OnLogoutConfirmedListener){

        this.listener = listener
    }

}