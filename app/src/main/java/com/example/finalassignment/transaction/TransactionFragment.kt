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
import com.example.finalassignment.R
import com.example.finalassignment.StellarService
import com.example.finalassignment.databinding.FragmentTransactionBinding
import com.example.finalassignment.transaction.partners.BeneficiariesFragment
import java.security.PublicKey


class TransactionFragment : Fragment(), View.OnClickListener, BeneficiariesFragment.OnPartnerPKfetchedListener, TransactionPinFragment.OnTransactionConfirmedListener {
    // TODO: Rename and change types of parameters
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

                Toast.makeText(activity,response.message, Toast.LENGTH_LONG).show()
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

    //TODO do live data pridat amount a publickey
//    private fun performTransaction(){
//
//        if (verifyTransaction()){
//
//            val dialog = PinFragment()
//            var args: Bundle? = Bundle()
//
//            args?.putString("publicKey", publicKey)
//            args?.putString("amount", amount)
//            dialog.setArguments(args)
//
//            dialog.setOnTransactionConfirmedListener(this)
//            dialog.show(activity?.supportFragmentManager!!, "PinDialog")
//    }




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


//    override fun onTransactionConfirmed() {
//
////        //TODO tranzakcia
////        val transactionExecuted = true
////
////        if (transactionExecuted) {
////            //TODO refresh balance from stellar network
////            Toast.makeText(activity, "Your transaction was performed", Toast.LENGTH_LONG).show()
////        }
////        else  Toast.makeText(activity, "Transaction error - not executed", Toast.LENGTH_LONG).show()
//    }

    fun getActiveUserBalance(): Double {
        return 1000.toDouble()
    }

    override fun onTransactionConfirmed() {
        //        //TODO tranzakcia
//        val transactionExecuted = true
//
//        if (transactionExecuted) {
//            //TODO refresh balance from stellar network
//            Toast.makeText(activity, "Your transaction was performed", Toast.LENGTH_LONG).show()
//        }
//        else  Toast.makeText(activity, "Transaction error - not executed", Toast.LENGTH_LONG).show()
    }

}