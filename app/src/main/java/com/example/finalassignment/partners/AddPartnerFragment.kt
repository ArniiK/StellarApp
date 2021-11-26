package com.example.finalassignment.partners

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentAddPartnerBinding

/**
 * A simple [Fragment] subclass.
 * Use the [AddPartnerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddPartnerFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentAddPartnerBinding
    private var adapter: PartnersRecyclerAdapter? = null
    private var addBtn: Button? = null
    private var dismissBtn: Button? = null
    private lateinit var listener: OnAccountAddedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_partner,container, false)
        binding.addPartnerButton.setOnClickListener(this)
        binding.dismissButton.setOnClickListener(this)

        return binding.root

    }

    override fun onClick(v: View?) {

        when(v){
            binding.addPartnerButton -> addPartner()
            binding.dismissButton -> dialog?.cancel()
        }

    }


    fun addPartner(){
        var isSuccessful = true
        //TODO implement adding to the db

        if (isSuccessful){

            val nickName = binding.addPartnerNicknameEdittext.text.toString()
            val publicKey = binding.addPartnerPkEdittext.text.toString()

            Toast.makeText(activity,"Partner added", Toast.LENGTH_LONG).show()
            listener.onAccountAdded(nickName, publicKey)
            dialog?.cancel()
        }
        else Toast.makeText(activity,"Adding not succesful", Toast.LENGTH_LONG).show()

    }


    // listenery na eventy - komunikacia medzi parent a child fragmentom -- este si nie som isty ci potrebne

    interface OnAccountAddedListener{

        fun onAccountAdded(accountNickname: String, publicKey: String)
    }
    fun setOnAccountAddedListener(listener: AddPartnerFragment.OnAccountAddedListener) {
        this.listener = listener
    }


}