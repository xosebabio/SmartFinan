package com.xbs.smartfinan.ui

import ChartFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.xbs.smartfinan.R
import com.xbs.smartfinan.databinding.ActivityMainBinding
import com.xbs.smartfinan.ui.home.MainScreenFragment
import com.xbs.smartfinan.ui.monthincome.MonthIncomeFragment
import com.xbs.smartfinan.ui.monthspend.MonthSpendFragment
import com.xbs.smartfinan.ui.profile.ProfileFragment

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

        // Verifica si los fragmentos ya existen antes de agregarlos nuevamente.
        val mainScreenFragment = mFragmentManager.findFragmentByTag(MainScreenFragment::class.java.name)
            ?: MainScreenFragment()
        val monthSpendFragment = mFragmentManager.findFragmentByTag(MonthSpendFragment::class.java.name)
            ?: MonthSpendFragment()
        val profileFragment = mFragmentManager.findFragmentByTag(ProfileFragment::class.java.name)
            ?: ProfileFragment()
        val monthIncomeFragment = mFragmentManager.findFragmentByTag(MonthIncomeFragment::class.java.name)
            ?: MonthIncomeFragment()

        mActiveFragment = mainScreenFragment

        // Comprueba y agrega los fragmentos solo si no existen.
        if (mainScreenFragment.isAdded.not()) {
            mFragmentManager.beginTransaction()
                .add(R.id.hostFragment, mainScreenFragment, MainScreenFragment::class.java.name)
                .commit()
        }
        if (monthSpendFragment.isAdded.not()) {
            mFragmentManager.beginTransaction()
                .add(R.id.hostFragment, monthSpendFragment, MonthSpendFragment::class.java.name)
                .hide(monthSpendFragment)
                .commit()
        }
        if (profileFragment.isAdded.not()) {
            mFragmentManager.beginTransaction()
                .add(R.id.hostFragment, profileFragment, ChartFragment::class.java.name)
                .hide(profileFragment)
                .commit()
        }
        if (monthIncomeFragment.isAdded.not()) {
            mFragmentManager.beginTransaction()
                .add(R.id.hostFragment, monthIncomeFragment, MonthIncomeFragment::class.java.name)
                .hide(monthIncomeFragment)
                .commit()
        }

        mBinding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    showFragment(mainScreenFragment)
                    true
                }
                R.id.action_month_spend -> {
                    showFragment(monthSpendFragment)
                    true
                }
                R.id.action_profile -> { // Cambia el ID al correcto
                    showFragment(profileFragment)
                    true
                }
                R.id.action_month_income -> {
                    showFragment(monthIncomeFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        mFragmentManager.beginTransaction()
            .hide(mActiveFragment)
            .show(fragment)
            .commit()
        mActiveFragment = fragment
    }


}