package com.example.finalassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalassignment.databinding.FragmentHistoryBinding
import java.util.*


class HistoryFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        val view = binding.root

        val transactionList: MutableList<Transaction> =
            mutableListOf(
                Transaction(
                    "+",
                    "Jozo",
                    23.45,
                    Date(2020,
                        10,
                        10),
                    "EUR"
                ),
                Transaction(
                    "-",
                    "Peto",
                    43.25,
                    Date(2021,
                        12,
                        10),
                    "USD"
                ),
                Transaction(
                    "-",
                    "Fero",
                    13.00,
                    Date(2021,
                        12,
                        24),
                    "EUR"
                )
            )

        transactionAdapter = TransactionAdapter(transactionList)

        binding.rvTransactionHistory.adapter = transactionAdapter
        binding.rvTransactionHistory.layoutManager = LinearLayoutManager(activity)
        return view
    }
}