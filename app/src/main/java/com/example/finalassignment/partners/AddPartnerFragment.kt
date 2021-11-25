package com.example.finalassignment.partners

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentAddPartnerBinding
import com.example.finalassignment.databinding.FragmentBeneficiariesBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddPartnerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddPartnerFragment : DialogFragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAddPartnerBinding
    private var adapter: PartnersRecyclerAdapter? = null
    private var addBtn: Button? = null
    private var dismissBtn: Button? = null

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
        var isSuccesful = true
        //TODO implement adding to the db and also updating recyclerview

        if (isSuccesful){
            Toast.makeText(activity,"Partner added", Toast.LENGTH_LONG).show()
            dialog?.cancel()
        }
        else Toast.makeText(activity,"Adding not succesful", Toast.LENGTH_LONG).show()

    }
}