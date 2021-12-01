package com.example.finalassignment.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalassignment.R
import com.example.finalassignment.StellarService
import com.example.finalassignment.databinding.FragmentTransactionBinding
import com.example.finalassignment.history.HistoryAdapter
import com.example.finalassignment.roomdb.Transaction
import com.example.finalassignment.roomdb.UserRegistration
import com.example.finalassignment.singleton.ActiveUserSingleton
import com.example.finalassignment.transaction.partners.BeneficiariesFragment
import kotlinx.android.synthetic.main.fragment_transaction.*
import java.security.PublicKey


class TransactionFragment : Fragment(), View.OnClickListener, BeneficiariesFragment.OnPartnerPKfetchedListener, TransactionPinFragment.OnTransactionConfirmedListener {

    private lateinit var partnersBtn: ImageButton
    private lateinit var spendBtn: Button
    private lateinit var binding: FragmentTransactionBinding
    private lateinit var viewModel :TransactionViewModel

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction,container, false)

        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        viewModel.getCurrentUser(ActiveUserSingleton.id).observe(viewLifecycleOwner,
            Observer<UserRegistration> { user ->
                walletBalanceTextView.text = user.balance.toString()
        })

        binding.transactionViewModel = viewModel

//        viewModel.eventTransactionSuccess.observe(viewLifecycleOwner, Observer <Boolean> {  transactionSuccessful ->
//
//            if (transactionSuccessful){
//
//                Toast.makeText(activity,"Transaction successful", Toast.LENGTH_LONG).show()
//                viewModel
//            }     // ak kontrola pozitivna, vypni dialog, preved transakciu
//            else{
//
//                Toast.makeText(activity,"Incorrect pin, insert again", Toast.LENGTH_LONG).show()
//            }
//
//        })


        viewModel.eventValidationResponse.observe(viewLifecycleOwner, Observer<ValidationResponse>{ response ->

            if (response.isSuccess){

//                Toast.makeText(activity,response.message, Toast.LENGTH_LONG).show()
                moveToPin()
            }
            else{

             Toast.makeText(activity, response.message, Toast.LENGTH_LONG).show()
            }
        })

        viewModel._partnerKey.observe(viewLifecycleOwner, Observer <String>{ pkPicked ->

            binding.PKInputField.beneficiaryPKInputText.setText(pkPicked)

        })








        val view = binding.root

        partnersBtn = binding.PKInputField.enterPartnersButton      //otvranie dialogu platobnych partnerov
        partnersBtn.setOnClickListener(this)
        //spendBtn = binding.sendTransactionButton

       // spendBtn.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {

        when(v){
            binding.PKInputField.enterPartnersButton -> openPartnersDialog()
            //binding.sendTransactionButton -> performTransaction()
        }

    }

    private fun moveToPin(){
        Toast.makeText(activity,"Moving to pin",Toast.LENGTH_LONG).show()
//        val action = WrappingFragmentDirections.actionWrappingFragmentToPinFragment()
//        view?.findNavController()?.navigate(action)
        val dialog = TransactionPinFragment()
        dialog.setOnTransactionConfirmedListener(this)
        dialog.show(activity?.supportFragmentManager!!, "PinDialog")
    }


    override fun onPartnerFetched(publicKey: String) {
        binding.PKInputField.beneficiaryPKInputText.setText(publicKey)
    }

    private fun openPartnersDialog(){

        val dialog = BeneficiariesFragment()
        dialog.setOnPartnerPKfetchedListener(this)
        dialog.show(activity?.supportFragmentManager!!, "PartnersDialog")
        //viewModel.on

    }



    private suspend fun checkAccountExist(publicKey: String) :Boolean{
        return StellarService.checkAccountExists(publicKey)
    }

    private fun checkAccountNotEmpty(publicKey: String) :Boolean{

        if( binding.PKInputField.beneficiaryPKInputText.text.isEmpty()){

            return false
        }

        return true
    }



    fun getActiveUserBalance(): Double {
        return 1000.toDouble()
    }

    override fun onTransactionConfirmed(decryptedPrivateKey: String) {

        viewModel.performTransaction(decryptedPrivateKey)       //zavola sa vykonanie transakcie vo viewmodeli
    }

}