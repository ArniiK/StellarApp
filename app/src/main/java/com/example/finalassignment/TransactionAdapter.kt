package com.example.finalassignment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalassignment.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat

class TransactionAdapter (
    private val transactions: MutableList<Transaction>
    ) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(private val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(transaction: Transaction) {
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
            itemBinding.tvDate.text = formatter.format(transaction.date)
            itemBinding.tvAmount.text = transaction.amount.toString() + " " + transaction.currency
            itemBinding.tvName.text = transaction.name
            itemBinding.tvSign.text = transaction.sign

            if (transaction.sign == "-") {
                itemBinding.tvSign.setTextColor(Color.RED)
                itemBinding.tvAmount.setTextColor(Color.RED)
            }
            if (transaction.sign == "+") {
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
        val curTransaction = transactions[position]
        holder.bind(curTransaction)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}