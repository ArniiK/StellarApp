package com.example.finalassignment.transaction.partners

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentBeneficiariesBinding
import com.example.finalassignment.databinding.PartnerAccountItemBinding
import com.example.finalassignment.partners.PartnersRecyclerAdapter
import com.example.finalassignment.roomdb.PartnerDB
import com.example.finalassignment.transaction.TransactionViewModel
import com.example.finalassignment.transaction.ValidationResponse


class BeneficiariesFragment() : DialogFragment(), View.OnClickListener,
PartnersRecyclerAdapter.OnDeleteItemListener,  AddPartnerFragment.OnAccountAddedListener,
    PartnersRecyclerAdapter.OnPartnerPickedListener
{

    private lateinit var binding: FragmentBeneficiariesBinding
    private lateinit var itemlist: List<PartnerDB>
    private lateinit var pKfetchedListener: OnPartnerPKfetchedListener
    private var adapter: PartnersRecyclerAdapter? = null
    private lateinit var transactionViewModel : TransactionViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_beneficiaries,container, false)

        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        binding.transactionViewModel = transactionViewModel

//        transactionViewModel.eventPartnersFetched.observe(viewLifecycleOwner,{ dbPartnerListFetched ->
//
//            if (dbPartnerListFetched.isSuccess){
//
//                //ak sa podarilo nacitat partnerov z db, notifikuj adapter na zmenu obsahu recyclera
//                changeItemList()
//                //else chybova hlaska pre usera
//            }else Toast.makeText(activity,dbPartnerListFetched.message,Toast.LENGTH_LONG).show()
//
//        })


        transactionViewModel.getAllPartners.observe(viewLifecycleOwner, {

            it ->
            itemlist = it       //zmeni sa obsah partner db, zmen recycler
            changeItemList()

        } )

        transactionViewModel._partnerKey.observe(viewLifecycleOwner, Observer<String> { pkPicked ->

            onPartnerPicked(pkPicked)

        })

//        transactionViewModel.eventUserFromDBremoval.observe( viewLifecycleOwner, Observer<PartnerDBResponse>{
//
//              response -> if (response.isSuccess){
//
//                        //zmazany z db, zmaz z recyclera
//                        adapter?.notifyItemRemoved(response.position)
//              }
//            else Toast.makeText(activity,response.message,Toast.LENGTH_LONG).show() //ukaz chybovu spravu
//
//        }
//        )

//        transactionViewModel.eventPartnerToRecycler.observe(viewLifecycleOwner, Observer <Partner>{
//
//            response ->
//            Log.i("partner added", "recycler updating")
//            adapter?.notifyItemInserted(response.position)
//            changeItemList()
//
//        })

        setupRecycler()
        binding.dismissButtonPartnersList.setOnClickListener(this)
        binding.addPartnerButtonPartnersList.setOnClickListener(this)

        //transactionViewModel.updatePartnerList()  prerobene

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onClick(v: View?) {

        when(v){

            binding.dismissButtonPartnersList -> dialog?.cancel()
            binding.addPartnerButtonPartnersList -> openAddingFragment()//Toast.makeText(activity,"Add new partner",Toast.LENGTH_LONG).show()

        }
    }


   private fun openAddingFragment(){

        val dialog = AddPartnerFragment()
        dialog.setOnAccountAddedListener(this)
        dialog.show(activity?.supportFragmentManager!!, "addPartnerDialog")
    }

    fun fillItemList(){

        //TODO natiahnut items z DB

        //provizorne data
//        var partnerViewItem = P("Public key", "Partner nickname")
//        var partnerViewItem1 = PartnerViewItem("Ox000000000000000000", "Petrik")
//
//        itemlist = mutableListOf<Partner>()
//        itemlist.add(Partner)
//        itemlist.add(partnerViewItem)

    }

    private fun setupRecycler(){        //??????ak bude na db async call, do callbacku ked budu itemy moze ist vytvorenie recyclera

        val recycler = binding.partnerRecycler
        val layoutManager = LinearLayoutManager(activity)

        adapter = PartnersRecyclerAdapter()
        adapter!!.setOnDeleteListener(this)
        adapter!!.setOnPartnerPickedListener(this)

        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
        //binding.transactionViewModel?.updatePartnerList()     //prerobene
    }

    private fun changeItemList(){        //adapter zmeni data na tie dovedene do livedata viewmodelu

        //adapter?.changePartnerList(binding.transactionViewModel?._partnerList?.value!!)
        //adapter?.changePartnerList(binding.transactionViewModel?.getAllPartners?.value!!)
        adapter?.changePartnerList(itemlist)
        adapter?.notifyDataSetChanged()
    }

   override fun onPartnerPicked(publicKey: String) {
        Log.i("partner picked ",publicKey)
        pKfetchedListener.onPartnerFetched(publicKey)
        dialog?.cancel()
    }



    override fun onDelete(position: Int) {

        //TODO removal function (DB)

        // voviewmodeli sa osetri vymazanie partnera, observuje sa response,
        // on success sa zmaze aj v recycler view + partner zozname
        binding.transactionViewModel?.onPartnerRemoval(position)


//        val wasRemoved = true
//
//        if (wasRemoved){
//
//            itemlist.removeAt(position)
//            adapter?.notifyItemRemoved(position)
//            Toast.makeText(activity,"Deleted",Toast.LENGTH_LONG).show()
//
//        }
//        else Toast.makeText(activity,"DB removal error",Toast.LENGTH_LONG).show()

    }



    override fun onAccountAdded(newPartner: PartnerDB, position: Int) {

        changeItemList()

        //transactionViewModel.signalRecycler(newPartner, position)
        //viewmodel - notifikuj recycler


//        val newPartner = Partner()
//        newPartner.nickName = accountNickname
//        newPartner.publicKey = publicKey
//        itemlist.add(newPartner)
//        adapter?.notifyItemInserted(itemlist.lastIndex)

    }

    interface OnPartnerPKfetchedListener{

        fun onPartnerFetched(publicKey: String)
    }

    fun setOnPartnerPKfetchedListener(listener: OnPartnerPKfetchedListener) {
        this.pKfetchedListener = listener
    }


}