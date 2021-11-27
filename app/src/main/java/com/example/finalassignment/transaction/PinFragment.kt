package com.example.finalassignment.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentPinBinding


class PinFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding:FragmentPinBinding
    private lateinit var listener: OnTransactionConfirmedListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_pin,container, false)
        binding.pinConfirmButton.setOnClickListener(this)
        binding.pinDismissButton.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {

        when(v){

            binding.pinConfirmButton -> confirmTransaction()
            binding.pinDismissButton -> closeTransaction()
        }
    }

    private fun confirmTransaction(){

        val isPinCorrect = true
        //TODO PIN check if correct
        if (isPinCorrect){

            dialog?.cancel()        //zavriem dialog
            //previest tranzakciu v transaction fragment, ak sa podari, toast ze sa podarila, ak nie tak ze sa nepodarila, znovunacitanie zostatku a jeho refresh
            listener.onTransactionConfirmed()
        }
        else Toast.makeText(activity,"Incorrect pin, insert again", Toast.LENGTH_LONG).show()

    }

    private fun closeTransaction(){
        Toast.makeText(activity,"Authorization closed", Toast.LENGTH_LONG).show()
        dialog?.cancel()
    }

    interface OnTransactionConfirmedListener{

        fun onTransactionConfirmed();
    }

    fun setOnTransactionConfirmedListener(listener: OnTransactionConfirmedListener){

        this.listener = listener
    }

}