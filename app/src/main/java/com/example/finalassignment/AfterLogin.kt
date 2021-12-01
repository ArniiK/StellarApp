package com.example.finalassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.finalassignment.databinding.FragmentAfterLoginBinding
import com.example.finalassignment.singleton.ActiveUserSingleton


class AfterLogin : Fragment() {

    private lateinit var binding: FragmentAfterLoginBinding // LATEINT - kompilatoru povieme ze prem inicializujeme pred pouzitim
    private lateinit var setAccountsBtn :Button

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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_after_login, container,false)
        val view = binding.root

        binding.afterLoginPKTextView.setText(ActiveUserSingleton.publicKey.toString())
        return view
        //return inflater.inflate(R.layout.fragment_after_login, container, false)
    }

}