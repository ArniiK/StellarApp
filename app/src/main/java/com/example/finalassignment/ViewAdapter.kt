package com.example.finalassignment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.finalassignment.partners.BeneficiariesFragment

class ViewAdapter (fragment: Fragment) : FragmentStateAdapter(fragment){

    val NUM_PAGES = 3     // 3 karty - transakcia, platobni partneri, historia transakcii

    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {

        when(position){

            0 -> return TransactionFragment()
            1 -> return HistoryFragment()
            2 -> return AfterLogin()        //ide sa zrusit, budu len 2 polozky v menu
            else -> return TransactionFragment()    // this will never happen
        }
    }
}