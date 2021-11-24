package com.example.finalassignment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.finalassignment.databinding.FragmentRegistrationBinding
import com.example.finalassignment.databinding.FragmentTransactionBinding
import com.example.finalassignment.partners.BeneficiariesFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var partnersBtn: ImageButton
    private lateinit var spendBtn: Button
    private lateinit var binding: FragmentTransactionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction,container, false)
        val view = binding.root

        partnersBtn = view.findViewById(R.id.enterPartnersButton)  //binding.PKInputButton.enterPartnersButton
        spendBtn = binding.sendTransactionButton
        partnersBtn.setOnClickListener(this)
        spendBtn.setOnClickListener(this)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {

        when(v){
            binding.PKInputButton.enterPartnersButton -> openPartnersDialog()
            binding.sendTransactionButton -> Toast.makeText(activity,"Sent",Toast.LENGTH_LONG).show()
        }

    }

    private fun openPartnersDialog(){
        Toast.makeText(activity,"dialog",Toast.LENGTH_LONG).show()
        Log.i("clicked", "dialog")
        var dialog = BeneficiariesFragment()
        dialog.show(activity?.supportFragmentManager!!, "PartnersDialog")

    }
}