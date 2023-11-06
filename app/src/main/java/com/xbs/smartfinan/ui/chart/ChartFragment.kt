import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.xbs.smartfinan.databinding.FragmentChartBinding
import com.xbs.smartfinan.databinding.FragmentMonthIncomeBinding

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

        // Configurar los datos (números de ejemplo)
        val importantExpenses = listOf(100f, 200f, 150f, 300f, 250f, 200f, 180f, 250f, 300f, 350f, 200f, 150f)
        val nonImportantExpenses = listOf(50f, 75f, 80f, 90f, 100f, 60f, 70f, 50f, 80f, 100f, 60f, 75f)
        val savings = listOf(200f, 150f, 250f, 150f, 200f, 250f, 300f, 350f, 400f, 450f, 300f, 250f)

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

        val xAxis: XAxis = barChart.xAxis
        xAxis.valueFormatter = MonthAxisValueFormatter() // Puedes crear esta clase para formatear las etiquetas del eje X
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val yAxisLeft: YAxis = barChart.axisLeft
        yAxisLeft.axisMinimum = 0f

        val yAxisRight: YAxis = barChart.axisRight
        yAxisRight.axisMinimum = 0f

        val barDataSet = BarDataSet(entries, "Gastos y Ahorro")
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.groupBars(0f, 0.08f, 0.03f)

        return mBinding.root
    }

    inner class MonthAxisValueFormatter : ValueFormatter() {
        private val months = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return months[value.toInt()]
        }
    }
}
