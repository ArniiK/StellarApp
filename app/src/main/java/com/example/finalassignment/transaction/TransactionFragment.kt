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
import com.example.finalassignment.databinding.FragmentTransactionBinding
import com.example.finalassignment.partners.BeneficiariesFragment


class TransactionFragment : Fragment(), View.OnClickListener, BeneficiariesFragment.OnPartnerPKfetchedListener {
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
            binding.sendTransactionButton -> performTransaction()
        }

    }

    private fun performTransaction(){

        val dialog = PinFragment()
        dialog.show(activity?.supportFragmentManager!!, "PinDialog")
        Toast.makeText(activity,"Sent",Toast.LENGTH_LONG).show()

    }

    override fun onPartnerFetched(publicKey: String) {
        binding.PKInputField.beneficiaryPKInputText.setText(publicKey)
    }

    private fun openPartnersDialog(){

        val dialog = BeneficiariesFragment()
        dialog.setOnPartnerPKfetchedListener(this)
        dialog.show(activity?.supportFragmentManager!!, "PartnersDialog")

    }
}