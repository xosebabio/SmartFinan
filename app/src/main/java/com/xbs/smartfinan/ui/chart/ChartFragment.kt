import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.xbs.smartfinan.R
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.ChartInfo
import com.xbs.smartfinan.databinding.FragmentChartBinding
import com.xbs.smartfinan.domain.Category
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        barChart = mBinding.barChart

        val currentYear = getActualYear()

        mBinding.tvYear.text = currentYear

        val importantExpenses = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val nonImportantExpenses = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val savings = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        Thread {
            val imSpends = SmartFinanApplication.database.spendDao()
                .getSpendsByMonth(Category.NECESSARY.value.uppercase())
            val nonImSpends = SmartFinanApplication.database.spendDao()
                .getSpendsByMonth(Category.UNNECESSARY.value.uppercase())
            val incomes = SmartFinanApplication.database.incomeDao().getIncomesByMonth()

            activity?.runOnUiThread {
                setData(imSpends, nonImSpends, incomes, currentYear, importantExpenses, nonImportantExpenses, savings)
            }

        }.start()

        return mBinding.root
    }

    inner class MonthAxisValueFormatter : ValueFormatter() {
        private val months = arrayOf(
            "Ene",
            "Feb",
            "Mar",
            "Abr",
            "May",
            "Jun",
            "Jul",
            "Ago",
            "Sep",
            "Oct",
            "Nov",
            "Dic"
        )

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return months[value.toInt()]
        }
    }

    private fun initChart(
        importantExpenses: List<Float>,
        nonImportantExpenses: List<Float>,
        savings: List<Float>
    ) {
        val entries: MutableList<BarEntry> = mutableListOf()
        for (i in importantExpenses.indices) {
            entries.add(
                BarEntry(
                    i.toFloat(),
                    floatArrayOf(
                        importantExpenses[i],
                        nonImportantExpenses[i],
                        savings[i]
                    )
                )
            )
        }

        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.importantExpenseColor),
            ContextCompat.getColor(requireContext(), R.color.nonImportantExpenseColor),
            ContextCompat.getColor(requireContext(), R.color.savingsColor)
        )

        val xAxis: XAxis = barChart.xAxis
        xAxis.valueFormatter =
            MonthAxisValueFormatter() // Puedes crear esta clase para formatear las etiquetas del eje X
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val yAxisLeft: YAxis = barChart.axisLeft
        yAxisLeft.axisMinimum = 0f

        val yAxisRight: YAxis = barChart.axisRight
        yAxisRight.axisMinimum = 0f


        val barDataSet = BarDataSet(entries, "Gastos y Ahorro")
        barDataSet.colors = colors.toList()

        barDataSet.stackLabels = arrayOf(
            "Gastos Importantes",
            "Gastos No Importantes",
            "Ahorro"
        ) // Asigna un nombre a cada conjunto de datos de gastos y ahorro

        val barData = BarData(barDataSet)
        barChart.data = barData

        // Configura la leyenda
        val legend = barChart.legend
        legend.isWordWrapEnabled =
            true // Para que los nombres de la leyenda se envuelvan en múltiples líneas si es necesario


        barChart.setFitBars(true)
        barChart.description.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setData(
        imSpends: MutableList<ChartInfo>,
        nonImSpends: MutableList<ChartInfo>,
        incomes: MutableList<ChartInfo>,
        year: String,
        importantExpenses: MutableList<Float>,
        nonImportantExpenses: MutableList<Float>,
        savings: MutableList<Float>
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
            savings[month] = incomes[i].amount.toFloat()-importantExpenses[month]-nonImportantExpenses[month]
        }

        initChart(importantExpenses, nonImportantExpenses, savings)
    }

    private fun getActualYear(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy", Locale("es", "ES"))
        return dateFormat.format(calendar.time)
    }

//    private fun prevYear(currentYear: String){
//        currentYear = (currentYear.toInt()-1).toString()
//    }
//
//    private fun nextYear(currentYear: String){
//        currentYear = (currentYear.toInt()+1).toString()
//    }

}
