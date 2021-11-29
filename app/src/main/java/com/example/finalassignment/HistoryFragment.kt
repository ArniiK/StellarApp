package com.example.finalassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalassignment.databinding.FragmentHistoryBinding
import org.stellar.sdk.*
import org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE
import java.util.*
import org.stellar.sdk.responses.SubmitTransactionResponse

import org.stellar.sdk.responses.AccountResponse
import java.lang.Exception


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


        val transactionList: MutableList<TransactionData>
           = mutableListOf(
                TransactionData(
                    "+",
                    "Jozo",
                    23.45,
                    Date(2020,
                        10,
                        10),
                    "EUR"
                ),
                TransactionData(
                    "-",
                    "Peto",
                    43.25,
                    Date(2021,
                        12,
                        10),
                    "USD"
                ),
                TransactionData(
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