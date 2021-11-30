package com.example.finalassignment.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalassignment.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat

class TransactionAdapter (
    private val transactionData: MutableList<TransactionData>
    ) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(private val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(transactionData: TransactionData) {
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
            itemBinding.tvDate.text = formatter.format(transactionData.date)
            itemBinding.tvAmount.text = transactionData.amount.toString() + " " + transactionData.currency
            itemBinding.tvName.text = transactionData.name
            itemBinding.tvSign.text = transactionData.sign

            if (transactionData.sign == "-") {
                itemBinding.tvSign.setTextColor(Color.RED)
                itemBinding.tvAmount.setTextColor(Color.RED)
            }
            if (transactionData.sign == "+") {
                itemBinding.tvSign.setTextColor(Color.GREEN)
                itemBinding.tvAmount.setTextColor(Color.GREEN)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemBinding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val curTransaction = transactionData[position]
        holder.bind(curTransaction)
    }

    override fun getItemCount(): Int {
        return transactionData.size
    }
}