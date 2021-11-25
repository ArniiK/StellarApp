package com.example.finalassignment.partners

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalassignment.databinding.PartnerAccountItemBinding

class PartnersRecyclerAdapter(private val itemList: MutableList<PartnerViewItem>):
    RecyclerView.Adapter<PartnersRecyclerAdapter.PartnersViewHolder>() {

    //ondeletelistener
    private lateinit var delListener: OnDeleteItemListener
    private lateinit var partnerPickedListener: OnPartnerPickedListener

    private var _binding:PartnerAccountItemBinding ?= null
    private val binding get() = _binding!!

//TODO doimplementovat itemlistener na mazanie
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnersViewHolder {
       // val view = LayoutInflater.from(parent.context).inflate(R.layout.partner_account_item, parent, false)
        _binding = PartnerAccountItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = binding.root

        return PartnersViewHolder(view, delListener, partnerPickedListener)      //returnujem holder
    }

    override fun onBindViewHolder(holder: PartnersViewHolder, position: Int) {
        val item = itemList[position]
        holder.pkTextView.text = item.publicKey
    }

    override fun getItemCount(): Int {
       return itemList.size
    }

  inner  class PartnersViewHolder(itemView: View, delListener: OnDeleteItemListener, pickedListener: OnPartnerPickedListener) : RecyclerView.ViewHolder(itemView){


        val pkTextView: TextView        // klikatelny text publik key partnera
        val removeBtn: ImageButton

       init {
           pkTextView =  binding.partnerAccountTextView       //itemView.findViewById(R.id.partnerAccountTextView)
           removeBtn =  binding.removeAccButton        //itemView.findViewById(R.id.removeAccButton)


           removeBtn.setOnClickListener(object : View.OnClickListener{

               override fun onClick(v: View?) {

                   if (adapterPosition != RecyclerView.NO_POSITION){

                       delListener.onDelete(adapterPosition)
                   }

               }
           })

           pkTextView.setOnClickListener(object :View.OnClickListener{

               override fun onClick(v: View?) {
                   if (adapterPosition != RecyclerView.NO_POSITION){

                       partnerPickedListener.onPartnerPicked(adapterPosition)
                   }

               }
           })
       }


   }

    interface OnDeleteItemListener{

        fun onDelete(position: Int)
    }

    fun setOnDeleteListener(listener: OnDeleteItemListener) {
        this.delListener = listener
    }

    interface OnPartnerPickedListener{

        fun onPartnerPicked(position: Int)
    }

    fun setOnPartnerPickedListener(listener: OnPartnerPickedListener) {
        this.partnerPickedListener = listener
    }



}