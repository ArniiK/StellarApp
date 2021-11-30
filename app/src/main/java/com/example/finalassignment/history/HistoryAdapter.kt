package com.example.finalassignment.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalassignment.databinding.ItemTransactionBinding
import com.example.finalassignment.roomdb.Transaction
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class HistoryAdapter (
    private val transactionData: List<Transaction>
    ) : RecyclerView.Adapter<HistoryAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(private val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(transaction: Transaction) {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                    .withLocale(Locale.UK)
                    .withZone(ZoneId.systemDefault())
            itemBinding.tvDate.text = formatter.format(transaction.date)
            itemBinding.tvAmount.text = transaction.amount.toString()
            itemBinding.tvName.text = transaction.partnerHash
            itemBinding.tvSign.text = transaction.type

            if (transaction.type == "-") {
                itemBinding.tvSign.setTextColor(Color.RED)
                itemBinding.tvAmount.setTextColor(Color.RED)
            }
            if (transaction.type == "+") {
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