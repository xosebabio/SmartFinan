package com.xbs.smartfinan.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.xbs.smartfinan.R
import com.xbs.smartfinan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mActiveFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupBottomNav()
    }

    private fun setupBottomNav() {
        mFragmentManager = supportFragmentManager

        val mainScreenFragment = MainScreenFragment()
        val monthSpendFragment = MonthSpendFragment()
        val profileFragment = ProfileFragment()

        mActiveFragment = mainScreenFragment

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, monthSpendFragment, MonthSpendFragment::class.java.name)
            .hide(monthSpendFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, mainScreenFragment, MainScreenFragment::class.java.name)
            .commit()

        mBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment)
                        .show(mainScreenFragment).commit()
                    mActiveFragment = mainScreenFragment
                    true
                }
                    R.id.action_month_spend -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment)
                        .show(monthSpendFragment).commit()
                    mActiveFragment = monthSpendFragment
                    true
                }
                    R.id.action_profile -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment)
                        .show(profileFragment).commit()
                    mActiveFragment = profileFragment
                        true
                }

                else -> false
            }
        }
    }
}