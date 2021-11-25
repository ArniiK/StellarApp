package com.example.finalassignment.partners

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalassignment.R
import com.example.finalassignment.databinding.FragmentBeneficiariesBinding
import com.example.finalassignment.databinding.FragmentRegistrationBinding


class BeneficiariesFragment() : DialogFragment(), View.OnClickListener,
PartnersRecyclerAdapter.OnDeleteItemListener, PartnersRecyclerAdapter.OnPartnerPickedListener, AddPartnerFragment.OnAccountAdded
{

    private lateinit var binding: FragmentBeneficiariesBinding
    private lateinit var itemlist: MutableList<PartnerViewItem>
    private lateinit var pKfetchedListener: OnPartnerPKfetchedListener
    private var adapter: PartnersRecyclerAdapter? = null


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
        fillItemList()      //itemy z db
        setupRecycler()
        binding.dismissButtonPartnersList.setOnClickListener(this)
        binding.addPartnerButtonPartnersList.setOnClickListener(this)

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


    fun openAddingFragment(){

        var dialog = AddPartnerFragment()
        dialog.setOnAccountAddedListener(this)
        dialog.show(activity?.supportFragmentManager!!, "addPartnerDialog")
    }

    fun fillItemList(){

        //TODO natiahnut items z DB

        //provizorne data
        var partnerViewItem = PartnerViewItem("Public key", "Partner nickname")
        var partnerViewItem1 = PartnerViewItem("Ox000000000000000000", "Petrik")

        itemlist = mutableListOf<PartnerViewItem>()
        itemlist.add(partnerViewItem)
        itemlist.add(partnerViewItem1)

    }

    fun setupRecycler(){        //??????ak bude na db async call, do callbacku ked budu itemy moze ist vytvorenie recyclera

        var recycler = binding.partnerRecycler
        var layoutManager = LinearLayoutManager(activity)

        adapter = PartnersRecyclerAdapter(itemlist)
        adapter!!.setOnDeleteListener(this)
        adapter!!.setOnPartnerPickedListener(this)

        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
    }


    override fun onPartnerPicked(publicKey: String) {
        pKfetchedListener.onPartnerFetched(publicKey)
        dialog?.cancel()
    }


    override fun onDelete(position: Int) {

        //TODO removal function (DB) returning success or error

        val wasRemoved = true

        if (wasRemoved){

            itemlist.removeAt(position)
            adapter?.notifyItemRemoved(position)
            Toast.makeText(activity,"Deleted",Toast.LENGTH_LONG).show()

        }
        else Toast.makeText(activity,"DB removal error",Toast.LENGTH_LONG).show()

    }



    override fun onAccountAdded(accountNickname: String, publicKey: String) {

        val newPartner = PartnerViewItem(publicKey, accountNickname)
        itemlist.add(newPartner)
        adapter?.notifyItemInserted(itemlist.lastIndex)

    }

    interface OnPartnerPKfetchedListener{

        fun onPartnerFetched(publicKey: String)
    }

    fun setOnPartnerPKfetchedListener(listener: OnPartnerPKfetchedListener) {
        this.pKfetchedListener = listener
    }


}