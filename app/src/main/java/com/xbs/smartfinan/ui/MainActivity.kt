package com.xbs.smartfinan.ui

import ChartFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.xbs.smartfinan.R
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.databinding.ActivityMainBinding
import com.xbs.smartfinan.domain.Regularity
import com.xbs.smartfinan.ui.home.MainScreenFragment
import com.xbs.smartfinan.ui.monthincome.MonthIncomeFragment
import com.xbs.smartfinan.ui.monthspend.MonthSpendFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mActiveFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager
    private lateinit var dateFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        checkRegularity()
        setupBottomNav()
    }

    private fun setupBottomNav() {
        mFragmentManager = supportFragmentManager

        // Verifica si los fragmentos ya existen antes de agregarlos nuevamente.
        val mainScreenFragment =
            mFragmentManager.findFragmentByTag(MainScreenFragment::class.java.name)
                ?: MainScreenFragment()
        val monthSpendFragment =
            mFragmentManager.findFragmentByTag(MonthSpendFragment::class.java.name)
                ?: MonthSpendFragment()
        val chartFragment = mFragmentManager.findFragmentByTag(ChartFragment::class.java.name)
            ?: ChartFragment()
        val monthIncomeFragment =
            mFragmentManager.findFragmentByTag(MonthIncomeFragment::class.java.name)
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
        if (chartFragment.isAdded.not()) {
            mFragmentManager.beginTransaction()
                .add(R.id.hostFragment, chartFragment, ChartFragment::class.java.name)
                .hide(chartFragment)
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
                    showFragment(chartFragment)
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


    private fun checkRegularity() {
        Log.i("RegularityChecker", "checkRegularity")

        // Utiliza una coroutine para realizar operaciones en segundo plano
        GlobalScope.launch(Dispatchers.IO) {
            val spends = SmartFinanApplication.database.spendDao().getAllSpends()
            val incomes = SmartFinanApplication.database.incomeDao().getAllIncomes()

            // Regresa al hilo principal para ejecutar checkYearly
            withContext(Dispatchers.Main) {
                checkYearly(spends, incomes)
            }
        }
    }


    private suspend fun checkYearly(spends: MutableList<Spend>, incomes: MutableList<Income>) {
        lateinit var newDate: Date
        for (i in incomes.indices) {
            val income = incomes[i]
            val date = dateFormat.parse(income.dateAt)
            newDate = if (incomes[i].regularity == Regularity.YEARLY) {
                Date(date.time + 31536000000)
            } else if (incomes[i].regularity == Regularity.MONTHLY) {
                Date(date.time + 2628000000)
            } else if (incomes[i].regularity == Regularity.WEEKLY) {
                Date(date.time + 604800000)
            } else {
                continue
            }
            if (newDate.before(Date()) && !income.checked) {
                val newIncome = Income(
                    0,
                    amount = income.amount,
                    description = income.description,
                    regularity = income.regularity,
                    dateAt = dateFormat.format(newDate)
                )
                // Utiliza coroutines para realizar las operaciones en segundo plano
                withContext(Dispatchers.IO) {
                    SmartFinanApplication.database.incomeDao().upsertIncome(newIncome)
                    income.checked = true
                    SmartFinanApplication.database.incomeDao().upsertIncome(income)
                }
            }
        }

        for (i in spends.indices) {
            val spend = spends[i]
            val date = dateFormat.parse(spend.dateAt)
            newDate = if (spends[i].regularity == Regularity.YEARLY) {
                Date(date.time + 31536000000)
            } else if (spends[i].regularity == Regularity.MONTHLY) {
                Date(date.time + 2628000000)
            } else if (spends[i].regularity == Regularity.WEEKLY) {
                Date(date.time + 604800000)
            } else {
                continue
            }
            if (newDate.before(Date()) && !spend.checked) {
                val newSpend = Spend(
                    0,
                    amount = spend.amount,
                    description = spend.description,
                    regularity = spend.regularity,
                    category = spend.category,
                    dateAt = dateFormat.format(newDate)
                )
                // Utiliza coroutines para realizar las operaciones en segundo plano
                withContext(Dispatchers.IO) {
                    SmartFinanApplication.database.spendDao().upsertSpend(newSpend)
                    spend.checked = true
                    SmartFinanApplication.database.spendDao().upsertSpend(spend)
                }
            }
        }
    }
}

