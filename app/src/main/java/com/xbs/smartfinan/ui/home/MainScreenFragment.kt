package com.xbs.smartfinan.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.databinding.FragmentMainScreenBinding
import com.xbs.smartfinan.domain.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainScreenFragment : Fragment() {
    private var totalImportantSpends: Double = 0.0
    private var totalNotImportantSpends: Double = 0.0
    private var totalSpends: Double = 0.0


    private var totalMonthImportantSpends: Double = 0.0
    private var totalMonthNotImportantSpends: Double = 0.0
    private var totalMonthSpends: Double = 0.0

    private var totalIncomes: Double = 0.0
    private var totalMonthIncomes: Double = 0.0

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()


    private val spendDao = SmartFinanApplication.database.spendDao()
    private val incomeDao = SmartFinanApplication.database.incomeDao()

    private lateinit var _binding: FragmentMainScreenBinding
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        val date = calendar.time
        MainScope().launch(Dispatchers.Main) {
            calculateAmounts(date)
        }

        mBinding.tvMonth.text = "Este mes " + getActualMonthName() + " has gastado:"
        mBinding.tvYear.text = "Este año " + getActualYear() + " has gastado:"
        mBinding.tvRecomendation.text = calc()
        return mBinding.root
    }

    private fun getActualYear(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy", Locale("es", "ES"))
        return dateFormat.format(calendar.time)
    }

    private suspend fun calculateAmounts(date: Date = Date()) = runBlocking {
        val importantSpendJob = GlobalScope.async(Dispatchers.IO) {
            getSpendSumAmount(
                spendDao.getSpendsBetweenDatesAndCategory(
                    getStartYear(date),
                    getEndYear(date),
                    Category.NECESSARY.value.uppercase()
                )
            )
        }

        val notImportantSpendJob = GlobalScope.async(Dispatchers.IO) {
            getSpendSumAmount(
                spendDao.getSpendsBetweenDatesAndCategory(
                    getStartYear(date),
                    getEndYear(date),
                    Category.UNNECESSARY.value.uppercase()
                )
            )
        }

        val incomesJob = GlobalScope.async(Dispatchers.IO) {
            getIncomeSumAmount(incomeDao.getIncomesByDate(getStartYear(date), getEndYear(date)))
        }

        val monthImportantSpendJob = GlobalScope.async(Dispatchers.IO) {
            getSpendSumAmount(
                spendDao.getSpendsBetweenDatesAndCategory(
                    getStartMonth(date),
                    getEndMonth(date),
                    Category.NECESSARY.value.uppercase()
                )
            )
        }

        val monthNotImportantSpendJob = GlobalScope.async(Dispatchers.IO) {
            getSpendSumAmount(
                spendDao.getSpendsBetweenDatesAndCategory(
                    getStartMonth(date),
                    getEndMonth(date),
                    Category.UNNECESSARY.value.uppercase()
                )
            )
        }

        val monthIncomesJob = GlobalScope.async(Dispatchers.IO) {
            getIncomeSumAmount(incomeDao.getIncomesByDate(getStartMonth(date), getEndMonth(date)))
        }

        val results = awaitAll(
            importantSpendJob, notImportantSpendJob, incomesJob,
            monthImportantSpendJob, monthNotImportantSpendJob, monthIncomesJob
        )

        totalImportantSpends = results[0]
        totalNotImportantSpends = results[1]
        totalIncomes = results[2]
        totalMonthImportantSpends = results[3]
        totalMonthNotImportantSpends = results[4]
        totalMonthIncomes = results[5]

        totalSpends = totalImportantSpends + totalNotImportantSpends
        totalMonthSpends = totalMonthImportantSpends + totalMonthNotImportantSpends

        mBinding.tvMonthSpend.text = totalMonthSpends.toString() + "€"
        mBinding.tvYearSpend.text = totalSpends.toString() + "€"
    }

    private fun calc(): String {
        val stringBuilder = StringBuilder()

        if (totalNotImportantSpends > totalIncomes * 0.3) {
            stringBuilder.append("Tienes demasiados gastos innecesarios. Este año te pasas en ${totalNotImportantSpends - (totalIncomes * 0.3)}€. ")
        }
        if (totalImportantSpends > totalIncomes * 0.5) {
            stringBuilder.append("Tu nivel de vida es demasiado alto. Superas ${totalImportantSpends - (totalIncomes * 0.5)}€ en el estándar de ahorro recomendado durante este año. ")
        }
        if (totalMonthNotImportantSpends > totalMonthIncomes * 0.3) {
            stringBuilder.append("Este mes has superado en ${totalMonthNotImportantSpends - (totalMonthIncomes * 0.3)}€ los gastos innecesarios recomendados. ")
        }
        if (totalMonthImportantSpends > totalMonthIncomes * 0.5) {
            stringBuilder.append("Tus gastos importantes este mes superan en ${totalMonthImportantSpends - (totalMonthIncomes * 0.5)}€ a los recomendados, intenta reducirlos. ")
        }

        val totalMonthSpends = totalMonthImportantSpends + totalMonthNotImportantSpends
        val monthSaving = totalMonthIncomes - totalMonthSpends

        if (totalMonthIncomes * 0.2 > monthSaving && monthSaving > 0) {
            stringBuilder.append("Este mes has ahorrado ${monthSaving}€, esto supone ${totalMonthIncomes * 0.2 - monthSaving}€ menos de lo recomendado. ")
        }
        if (monthSaving < 0) {
            stringBuilder.append("Este mes has gastado ${abs(monthSaving)}€ más de lo que has ingresado. ")
        }

        val advise = stringBuilder.toString()

        if (advise.isEmpty()) {
            return "No tenemos ninguna recomendación para ti."
        }

        return advise
    }


    private fun getStartMonth(date: Date): String {
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return dateFormat.format(calendar.time)
    }

    private fun getEndMonth(date: Date): String {
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return dateFormat.format(calendar.time)
    }

    private fun getStartYear(date: Date): String {
        calendar.time = date
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        return dateFormat.format(calendar.time)
    }

    private fun getEndYear(date: Date): String {
        calendar.time = date
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
        return dateFormat.format(calendar.time)
    }

    private fun getSpendSumAmount(spends: List<Spend>): Double {
        var total = 0.0
        for (spend in spends) {
            total += spend.amount
        }
        return total
    }

    private fun getIncomeSumAmount(incomes: List<Income>): Double {
        var total = 0.0
        for (income in incomes) {
            total += income.amount
        }
        return total
    }


    private fun getActualMonthName(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM", Locale("es", "ES"))
        var month: String = dateFormat.format(calendar.time)
        month = month.substring(0, 1).uppercase() + month.substring(1)
        return month
    }
}