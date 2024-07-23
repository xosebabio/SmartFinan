package com.xbs.smartfinan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.ChartInfo
import com.xbs.smartfinan.databinding.FragmentMainScreenBinding
import com.xbs.smartfinan.domain.Category
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    private val currentYear = getActualYear()

    private val importantExpenses = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val nonImportantExpenses = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val savings = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val incomesList = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)



    private val spendDao = SmartFinanApplication.database.spendDao()
    private val incomeDao = SmartFinanApplication.database.incomeDao()

    private lateinit var _binding: FragmentMainScreenBinding
    private val mBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)

        Thread {
            val imSpends = SmartFinanApplication.database.spendDao()
                .getSpendsByMonth(Category.NECESSARY.value.uppercase())
            val nonImSpends = SmartFinanApplication.database.spendDao()
                .getSpendsByMonth(Category.UNNECESSARY.value.uppercase())
            val incomes = SmartFinanApplication.database.incomeDao().getIncomesByMonth()

            activity?.runOnUiThread {
                calculate(imSpends, nonImSpends, incomes, currentYear, importantExpenses, nonImportantExpenses, savings, incomesList)
            }

        }.start()

        //mBinding.tvMonth.text = "Este mes " + getActualMonthName() + " has gastado:"
        mBinding.tvMonth.text = "Este mes Diciembre has gastado:"
        mBinding.tvYear.text = "Este año " + getActualYear() + " has gastado:"

        return mBinding.root
    }

    private fun calculate(
        imSpends: MutableList<ChartInfo>,
        nonImSpends: MutableList<ChartInfo>,
        incomes: MutableList<ChartInfo>,
        year: String,
        importantExpenses: MutableList<Float>,
        nonImportantExpenses: MutableList<Float>,
        savings: MutableList<Float>,
        incomesList: MutableList<Float>
    ) {

        for (i in imSpends.indices) {
            val month = imSpends[i].month.substring(5).toInt() - 1
            if (imSpends[i].month.substring(0, 4) != year) {
                continue
            }
            importantExpenses[month] = imSpends[i].amount.toFloat()
        }

        for (i in nonImSpends.indices) {
            val month = nonImSpends[i].month.substring(5).toInt() - 1
            if (nonImSpends[i].month.substring(0, 4) != year) {
                continue
            }
            nonImportantExpenses[month] = nonImSpends[i].amount.toFloat()
        }

        for (i in incomes.indices) {
            val month = incomes[i].month.substring(5).toInt() - 1
            if (incomes[i].month.substring(0, 4) != year) {
                continue
            }
            savings[month] =
                incomes[i].amount.toFloat() - importantExpenses[month] - nonImportantExpenses[month]
            incomesList[month] = incomes[i].amount.toFloat()
        }

        val monthNum = 11

        val monthSpend = importantExpenses[monthNum]+nonImportantExpenses[monthNum]
        val totalSpend = importantExpenses.sum()+nonImportantExpenses.sum()

        mBinding.tvMonthSpend.text = ("%.2f".format(monthSpend)).toString() + "€"
        mBinding.tvYearSpend.text = ("%.2f".format(totalSpend)).toString() + "€"

        mBinding.tvRecomendation.text = calc(importantExpenses, nonImportantExpenses, savings, incomesList, monthNum)
    }

    private fun getActualYear(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy", Locale("es", "ES"))
        return dateFormat.format(calendar.time)
    }

    private fun calc(
        importantExpenses: List<Float>,
        nonImportantExpenses: List<Float>,
        savings: List<Float>,
        incomesList: List<Float>,
        monthNum: Int
    ): String {
        val stringBuilder = StringBuilder()

        val importantSpendsPercentage = (importantExpenses.sum() / incomesList.sum()) * 100
        val notImportantSpendsPercentage = (nonImportantExpenses.sum() / incomesList.sum()) * 100
        val monthImportantSpendsPercentage = (importantExpenses[monthNum] / incomesList[monthNum]) * 100
        val monthNotImportantSpendsPercentage =
            (nonImportantExpenses[monthNum] / incomesList[monthNum]) * 100
        val monthSavingPercentage =
            (savings[monthNum] / incomesList[monthNum]) * 100

        if (notImportantSpendsPercentage > 30.0) {
            stringBuilder.append("Tienes demasiados gastos innecesarios. Este año te pasas en ${"%.2f".format(nonImportantExpenses.sum() - (incomesList.sum() * 0.3))}€. ")
        }
        if (importantSpendsPercentage > 50.0) {
            stringBuilder.append("Tu nivel de vida es demasiado alto. Superas ${"%.2f".format(importantExpenses.sum() - (incomesList.sum() * 0.5))}€ en los gastos importantes recomendados durante este año. ")
        }
        if (monthNotImportantSpendsPercentage > 30.0) {
            stringBuilder.append("Este mes has superado en ${"%.2f".format(nonImportantExpenses[monthNum] - (incomesList[monthNum] * 0.3))}€ los gastos innecesarios recomendados. ")
        }
        if (monthImportantSpendsPercentage > 50.0) {
            stringBuilder.append("Tus gastos importantes este mes superan en ${"%.2f".format(importantExpenses[monthNum] - (incomesList[monthNum] * 0.5))}€ a los recomendados, intenta reducirlos. ")
        }

        if (monthSavingPercentage < 20.0 && monthSavingPercentage > 0) {
            stringBuilder.append("Este mes has ahorrado ${monthSavingPercentage}% de tus ingresos, lo cual es menos del 20% recomendado. Intenta aumentar tus ahorros. ")
        }

        stringBuilder.append("Este año has ahorrado ${"%.2f".format(savings.sum())}€")

        val advise = stringBuilder.toString()

        if (advise.isEmpty()) {
            return "No tenemos ninguna recomendación para ti."
        }

        return advise
    }
}