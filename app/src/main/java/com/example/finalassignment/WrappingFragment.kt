package com.example.finalassignment

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.finalassignment.databinding.FragmentWrappingBinding
import com.example.finalassignment.singleton.ActiveUserSingleton
import com.example.finalassignment.transaction.PinFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WrappingFragment : Fragment(), TabLayout.OnTabSelectedListener, LogoutFragment.OnLogoutConfirmedListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewpager: ViewPager2
    private lateinit var viewAdapter: ViewAdapter
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var tabLayout: TabLayout

    //binding
    private lateinit var binding: FragmentWrappingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wrapping, container,false)
        binding.logoutItem.setOnClickListener(){
            logOutUser()
        }
        binding.refreshItem.setOnClickListener() {
            refreshDb()
        }
        val view = binding.root
        addToolbar()
        initViewPager()
        return view
    }


    private fun addToolbar(){

        toolBar = binding.pageToolbar
        tabLayout = binding.menuBarTabLayout
        tabLayout.addOnTabSelectedListener(this)
        tabLayout.getTabAt(0)?.icon!!.setTint(Color.BLACK)   // este preskusat
        tabLayout.getTabAt(1)?.icon!!.setTint(Color.WHITE)
    }

    private fun initViewPager(){

        viewAdapter = ViewAdapter(this)     //skusit, ci pojde inak getSupportFragmentManager a zmenit constructor v adapteri

        viewpager = binding.pager
        viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewpager.adapter = viewAdapter
        //viewpager.setCurrentItem(0, false) nastavi sa na prvu polozku

        viewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
                super.onPageSelected(position)
            }
        });
    }

    private fun logOutUser(){
        val dialog = LogoutFragment()

        dialog.setOnLogoutConfirmedListener(this)
        dialog.show(activity?.supportFragmentManager!!, "LogoutDialog")
    }

    private fun refreshDb() {
        GlobalScope.launch(Dispatchers.IO){
            DbUpdateService.updateBalance(ActiveUserSingleton.id, ActiveUserSingleton.publicKey)
            DbUpdateService.updateTransactions(ActiveUserSingleton.id, ActiveUserSingleton.publicKey)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            tab.icon!!.setTint(Color.BLACK)
            viewpager.setCurrentItem(tab.position)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        if (tab != null) {
            tab.icon!!.setTint(Color.WHITE)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onLogoutConfirmed() {

        val directions = WrappingFragmentDirections

        val action = WrappingFragmentDirections.actionWrappingFragmentToLoginFragment()

        view?.findNavController()?.navigate(action)
        Toast.makeText(activity,"User successfully logged out", Toast.LENGTH_LONG).show()
    }




}
