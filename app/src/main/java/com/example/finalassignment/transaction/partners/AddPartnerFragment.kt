package com.example.finalassignment.transaction.partners

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentAddPartnerBinding
import com.example.finalassignment.partners.PartnersRecyclerAdapter
import com.example.finalassignment.roomdb.PartnerDB
import com.example.finalassignment.transaction.TransactionViewModel
import com.example.finalassignment.transaction.ValidationResponse

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
    private lateinit var addingViewModel: TransactionViewModel

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

        addingViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        binding.addingViewmodel = addingViewModel


        addingViewModel.eventPartnerFieldValidation.observe(viewLifecycleOwner, Observer <ValidationResponse> {

            response ->  if (response.isSuccess){   // public key field filled, add partner to db
                addPartner()
            }
            else Toast.makeText(activity,response.message, Toast.LENGTH_LONG).show()    //show error message

        })

        addingViewModel.eventDBPartnerAdded.observe(viewLifecycleOwner, Observer <PartnerDBResponse> {

                response ->  if (response.isSuccess){   // public key field filled, add partner to db
            onPersisted(response.partner, response.position)   //ulozeny do db, teraz update recyclera
        }
        else Toast.makeText(activity,response.message, Toast.LENGTH_LONG).show()    //show error message

        })


        binding.addPartnerButton.setOnClickListener(this)
        binding.dismissButton.setOnClickListener(this)

        return binding.root

    }

    override fun onClick(v: View?) {

        when(v){
           // binding.addPartnerButton -> addPartner()
            binding.dismissButton -> dialog?.cancel()
        }

    }


    fun addPartner(){
//        var isSuccessful = true
//        //TODO implement adding to the db
//
//        if (isSuccessful){
//
//            val nickName = binding.addPartnerNicknameEdittext.text.toString()
//            val publicKey = binding.addPartnerPkEdittext.text.toString()
//
//            Toast.makeText(activity,"Partner added", Toast.LENGTH_LONG).show()
//            listener.onAccountAdded(nickName, publicKey)
//            dialog?.cancel()
//        }
//        else Toast.makeText(activity,"Adding not succesful", Toast.LENGTH_LONG).show()

        addingViewModel.persistPartner()    //zalovaj ulozenie do db

    }

    fun onPersisted(newPartner: PartnerDB, position: Int){
        //komunikuj s fragmentom partners fragment, update recyclerview, pomocou viewmodelu, livedata
        listener.onAccountAdded(newPartner, position)
        //addingViewModel.signalRecycler(newPartner)
        dialog?.cancel()
    }




    // listenery na eventy - komunikacia medzi parent a child fragmentom -- este si nie som isty ci potrebne

    interface OnAccountAddedListener{

        fun onAccountAdded(newPartner: PartnerDB, position: Int)
    }

    fun setOnAccountAddedListener(listener: AddPartnerFragment.OnAccountAddedListener) {
        this.listener = listener
    }


}