package com.xbs.smartfinan.ui.monthincome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.databinding.FragmentMonthIncomeBinding
import com.xbs.smartfinan.domain.IncomeAdapter
import com.xbs.smartfinan.ui.income.IncomeActivity
import java.text.SimpleDateFormat
import java.util.*

class MonthIncomeFragment : Fragment() {

    private var _binding: FragmentMonthIncomeBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mContext: Context
    private lateinit var incomes: MutableList<Income>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private var currentMonth: Int = 0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthIncomeBinding.inflate(inflater, container, false)
        incomes = mutableListOf()
        mContext = requireContext()

        mBinding.fabAddIncome.setOnClickListener {
            navigateToNewIncome()
        }

        currentMonth=getActualMonth()

        mBinding.btnBackMonth.setOnClickListener {
            showMonthIncome(getPreviousMonth(currentMonth))
            currentMonth--
        }

        mBinding.btnNextMonth.setOnClickListener {
            showMonthIncome(getNextMonth(currentMonth))
            currentMonth++
        }

        Thread {
            incomes = SmartFinanApplication.database.incomeDao().getIncomesByDate(firstDayOfMonth(currentMonth), lastDayOfMonth(currentMonth))
            activity?.runOnUiThread {
                initRecyclerView()
            }
        }.start()

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        println("On resume")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapter = IncomeAdapter(incomes) { item ->
            val intent = Intent(mContext, EditDeleteIncomeActivity::class.java)
            intent.putExtra("income_id", item.income_id)
            startActivity(intent)
        }
        mLayoutManager = LinearLayoutManager(mContext)
        mBinding.rvMonthIncome.layoutManager = mLayoutManager
        mBinding.rvMonthIncome.adapter = adapter

        mBinding.tvMonth.text = getActualMonthName(currentMonth)
        updateTotalIncomes(incomes)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getActualMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH)
    }

    private fun getActualMonthName(monthInt: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, monthInt)
        val dateFormat = SimpleDateFormat("MMMM", Locale("es", "ES"))
        var month: String = dateFormat.format(calendar.time)
        month = month.substring(0, 1).uppercase() + month.substring(1)
        return month
    }


    private fun updateTotalIncomes(incomes: MutableList<Income>) {
        if (incomes.isEmpty()) {
            mBinding.tvMonthIncome.text = "No hay ingresos"
        } else {
            mBinding.tvMonthIncome.text = "Ingresos del mes: ${incomes.sumOf { it.amount }}"
        }
    }

    private fun navigateToNewIncome(){
        val intent = Intent(mContext, IncomeActivity::class.java)
        startActivity(intent)
    }

    private fun firstDayOfMonth(mes: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, mes)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        return dateFormat.format(calendar.time)
    }

    private fun lastDayOfMonth(mes: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, mes)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return dateFormat.format(calendar.time)
    }

    private fun showMonthIncome(month: Int) {
        val firstDay = firstDayOfMonth(month)
        val lastDay = lastDayOfMonth(month)

        Thread {
            incomes = SmartFinanApplication.database.incomeDao().getIncomesByDate(firstDay, lastDay)
            activity?.runOnUiThread {
                initRecyclerView()
            }
        }.start()

    }

    // Obtener el mes anterior
    private fun getPreviousMonth(currentMonth: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.add(Calendar.MONTH, -1)
        return calendar.get(Calendar.MONTH)
    }

    // Obtener el mes siguiente
    private fun getNextMonth(currentMonth: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.add(Calendar.MONTH, 1)
        return calendar.get(Calendar.MONTH)
    }
}
