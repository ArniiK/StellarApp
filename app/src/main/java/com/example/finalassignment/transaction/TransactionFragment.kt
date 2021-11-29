package com.example.finalassignment.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.finalassignment.R
import com.example.finalassignment.StellarService
import com.example.finalassignment.databinding.FragmentTransactionBinding
import com.example.finalassignment.partners.BeneficiariesFragment
import kotlinx.coroutines.*
import org.stellar.sdk.*
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import java.lang.Exception
import java.security.PublicKey


class TransactionFragment : Fragment(), View.OnClickListener, BeneficiariesFragment.OnPartnerPKfetchedListener, PinFragment.OnTransactionConfirmedListener {
    // TODO: Rename and change types of parameters
    private lateinit var partnersBtn: ImageButton
    private lateinit var spendBtn: Button
    private lateinit var binding: FragmentTransactionBinding

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
        val view = binding.root

        partnersBtn = view.findViewById(R.id.enterPartnersButton)  //binding.PKInputField.enterPartnersButton
        spendBtn = binding.sendTransactionButton
        partnersBtn.setOnClickListener(this)
        spendBtn.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {

        when(v){
            binding.PKInputField.enterPartnersButton -> openPartnersDialog()
            binding.sendTransactionButton -> performTransactionScope()
        }

    }

    private fun performTransactionScope(){
        GlobalScope.launch(Dispatchers.Main) {
            performTransaction()
        }
    }

    private suspend fun performTransaction(){
        if (verifyTransaction()){


            val publicKey = binding.PKInputField.beneficiaryPKInputText.text.toString()
            val amount = binding.sumToPayNumberDec.text.toString()

            val dialog = PinFragment()
            var args: Bundle? = Bundle()

            args?.putString("publicKey", publicKey)
            args?.putString("amount", amount)
            dialog.setArguments(args)

            dialog.setOnTransactionConfirmedListener(this)
            dialog.show(activity?.supportFragmentManager!!, "PinDialog")
            Toast.makeText(activity,"Sent",Toast.LENGTH_LONG).show()

        }
    }

    override fun onPartnerFetched(publicKey: String) {
        binding.PKInputField.beneficiaryPKInputText.setText(publicKey)
    }

    private fun openPartnersDialog(){

        val dialog = BeneficiariesFragment()
        dialog.setOnPartnerPKfetchedListener(this)
        dialog.show(activity?.supportFragmentManager!!, "PartnersDialog")

    }

    private suspend fun verifyTransaction(): Boolean{     //skontroluj zostatok, skontroluj, ci existuje moj ucet, jeho ucet, otvor zadanie pinu

        //TODO nepouzivat zobrazeny balance, treba zo stellaru natiahnut, ten je aktualny
        if(binding.walletBalanceTextView.text.toString().toDoubleOrNull() != null) {

            if (!binding.sumToPayNumberDec.text.toString().isEmpty() && !binding.walletBalanceTextView.text.toString().isEmpty()) {    //ci je balance double cislo
                //amount field not empty

                val amount = binding.sumToPayNumberDec.text.toString().toDouble()

                if (binding.walletBalanceTextView.text.toString()
                        .toDouble() < binding.sumToPayNumberDec.text.toString().toDouble()
                ) {
                    // insufficient balance
                    Toast.makeText(activity, "Insufficient balance", Toast.LENGTH_LONG).show()
                    return false

                } else {

                    //balance sufficient, perform account existance check

                        if (!binding.PKInputField.beneficiaryPKInputText.text.toString().isEmpty()){
                            if (checkAccountExist(binding.PKInputField.beneficiaryPKInputText.text.toString())) {

                                // state of sufficient balance and existing recipient account
                                //PIN verification
                                return true

                            }
                            //not existing account
                            else {

                                Toast.makeText(activity,"Recipient account not on stellar network", Toast.LENGTH_LONG).show()
                                return false
                            }

                        }
                    else{
                            Toast.makeText(activity,"Recipient account field empty", Toast.LENGTH_LONG).show()
                        return false
                    }




                }
            } else {
                // ammount field empty
                Toast.makeText(activity, "Amount field empty", Toast.LENGTH_LONG).show()
                return false

            }
        }
        else{

            Toast.makeText(activity, "Your balance is not double number", Toast.LENGTH_LONG).show()
            return false
        }
    }

    private suspend fun checkAccountExist(publicKey: String) :Boolean{
        return StellarService.checkAccountExists(publicKey)
    }

    private fun checkAccountNotEmpty(publicKey: String) :Boolean{

        if( binding.PKInputField.beneficiaryPKInputText.text.toString().isEmpty()){

            return false
        }

        return true
    }


    override fun onTransactionConfirmed() {

        //TODO tranzakcia
        val transactionExecuted = true

        if (transactionExecuted) {
            //TODO refresh balance from stellar network
            Toast.makeText(activity, "Your transaction was performed", Toast.LENGTH_LONG).show()
        }
        else  Toast.makeText(activity, "Transaction error - not executed", Toast.LENGTH_LONG).show()
    }

    fun getActiveUserBalance(): Double {
        return 1000.toDouble()
    }

}