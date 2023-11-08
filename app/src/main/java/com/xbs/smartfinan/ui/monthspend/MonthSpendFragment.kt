package com.xbs.smartfinan.ui.monthspend

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
import com.xbs.smartfinan.databinding.FragmentMonthSpendBinding
import com.xbs.smartfinan.domain.OnClickListener
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.domain.SpendAdapter
import com.xbs.smartfinan.ui.spend.SpendActivity
import java.text.SimpleDateFormat
import java.util.*

class MonthSpendFragment : Fragment(), OnClickListener {

    private var _binding: FragmentMonthSpendBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mContext: Context
    private lateinit var spends: MutableList<Spend>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private var currentMonth: Int = 0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthSpendBinding.inflate(inflater, container, false)
        spends = mutableListOf()
        mContext = requireContext()

        mBinding.fabAddSpend.setOnClickListener {
            navigatetoNewSpend()
        }

        currentMonth=getActualMonth()

        mBinding.btnBackMonth.setOnClickListener {
            showMonthSpends(getPreviousMonth(currentMonth))
            currentMonth--
        }

        mBinding.btnNextMonth.setOnClickListener {
            showMonthSpends(getNextMonth(currentMonth))
            currentMonth++
        }

        Thread {
            spends = SmartFinanApplication.database.spendDao().getSpendsBetweenDates(firstDayOfMonth(currentMonth), lastDayOfMonth(currentMonth))
        }.start()
        initRecyclerView()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        println("On resume")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapter = SpendAdapter(spends) { item ->
            val intent = Intent(mContext, EditDeleteSpendActivity::class.java)
            intent.putExtra("spend_id", item.spend_id)
            startActivity(intent)
        }
        mLayoutManager = LinearLayoutManager(mContext)
        mBinding.rvMonthSpend.layoutManager = mLayoutManager
        mBinding.rvMonthSpend.adapter = adapter

        mBinding.tvMonth.text = getActualMonthName(currentMonth)
        updateTotalSpends(spends)
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

    override fun onClick(spend: Spend, position: Int) {
        TODO("Not yet implemented")
    }

    private fun updateTotalSpends(spends: MutableList<Spend>) {
        if (spends.isEmpty()) {
            mBinding.tvMonthSpend.text = "No hay gastos"
        } else {
            mBinding.tvMonthSpend.text = "Gastos del mes: ${spends.sumOf { it.amount }}"
        }
    }

    private fun navigatetoNewSpend() {
        val intent = Intent(mContext, SpendActivity::class.java)
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
    // Función para cargar los gastos de un mes específico
    private fun showMonthSpends(month: Int) {
        val firstDay = firstDayOfMonth(month)
        val lastDay = lastDayOfMonth(month)

        Thread {
            spends = SmartFinanApplication.database.spendDao().getSpendsBetweenDates(firstDay, lastDay)
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
