package com.example.finalassignment.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentPinBinding
import com.example.finalassignment.databinding.FragmentPinTransactionBinding
import java.security.PrivateKey
class TransactionPinFragment: DialogFragment(), View.OnClickListener {


    private lateinit var binding: FragmentPinTransactionBinding
    private lateinit var listener: OnTransactionConfirmedListener
    private var privateKey: String? = null
    private lateinit var viewModel :TransactionViewModel


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

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_pin_transaction,container, false)

        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        binding.transactionViewModel = viewModel


        //binding.pinConfirmButton.setOnClickListener(this)
        binding.pinDismissButton.setOnClickListener(this)


//        viewModel.eventPinConfirmed.observe(viewLifecycleOwner, Observer <Boolean> { pinCorrect ->
//
//            if (pinCorrect){
//                confirmTransaction()
//            }     // ak kontrola pozitivna, vypni dialog, preved transakciu
//            else{
//
//                Toast.makeText(activity,"Incorrect pin, insert again", Toast.LENGTH_LONG).show()
//            }
//
//        })
        viewModel.eventPinControlResponse.observe(viewLifecycleOwner, Observer<ValidationResponse>{ response ->

            if (response.isSuccess){

                Toast.makeText(activity,response.message, Toast.LENGTH_LONG).show()
                confirmTransaction()        // dialog close, send transaction, success remove field values - WM
            }
            else{

                Toast.makeText(activity, response.message, Toast.LENGTH_LONG).show()
            }
        })

        Log.d("privateKeyPin" ,"private key is: " + privateKey)

        return binding.root
    }

    override fun onClick(v: View?) {

        when(v){
            //binding.pinConfirmButton -> confirmTransaction()
            binding.pinDismissButton -> closeTransaction()
        }
    }

    private fun confirmTransaction(){

        Log.d("pin" ,"confirmed")
        viewModel.performTransaction()  //vykonaj transakciu
        dialog?.cancel()
//            val parent = getChildFragmentManager()  //getParentFragment()
//            //val mview = parent?.view
//            val action = PinFragmentDirections.actionPinFragmentToWrappingFragment()
//            if (parent != null) {
//                NavHostFragment.findNavController(this).navigate(action)  // prejdi na transaction fragment
        // }

    }



    private fun closeTransaction(){
        //Toast.makeText(activity,"Authorization closed", Toast.LENGTH_LONG).show()
        dialog?.cancel()
    }

    interface OnTransactionConfirmedListener{

        fun onTransactionConfirmed();
    }

    fun setOnTransactionConfirmedListener(listener: OnTransactionConfirmedListener){

        this.listener = listener
    }



}