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
import androidx.viewpager2.widget.ViewPager2
import com.example.finalassignment.databinding.FragmentWrappingBinding
import com.example.finalassignment.singleton.ActiveUserSingleton
import com.example.finalassignment.transaction.PinFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WrappingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WrappingFragment : Fragment(), TabLayout.OnTabSelectedListener, LogoutFragment.OnLogoutConfirmedListener {
    // TODO: Rename and change types of parameters
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
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WrappingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WrappingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
        val action = WrappingFragmentDirections.actionWrappingFragmentToLoginFragment()
        view?.findNavController()?.navigate(action)
        Toast.makeText(activity,"User successfully logged out", Toast.LENGTH_LONG).show()
    }


}
