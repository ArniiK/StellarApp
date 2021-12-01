package com.example.finalassignment.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalassignment.databinding.FragmentHistoryBinding
import com.example.finalassignment.roomdb.Transaction
import com.example.finalassignment.singleton.ActiveUserSingleton
import java.util.*


class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        binding = FragmentHistoryBinding.inflate(inflater)
        val view = binding.root

        viewModel.getTransactionsForId(ActiveUserSingleton.id).observe(viewLifecycleOwner,
        Observer<List<Transaction>> { transactions ->
            historyAdapter = HistoryAdapter(transactions.reversed())
            binding.rvTransactionHistory.adapter = historyAdapter
            binding.rvTransactionHistory.layoutManager = LinearLayoutManager(activity)
        }
        )

        return view
    }
}