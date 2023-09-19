package com.xbs.smartfinan.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.xbs.smartfinan.R
import com.xbs.smartfinan.data.SpendApplication
import com.xbs.smartfinan.databinding.FragmentMonthSpendBinding
import com.xbs.smartfinan.databinding.ItemSpendBinding
import java.text.SimpleDateFormat
import java.util.*

class MonthSpendFragment : Fragment(), OnClickListener {

    private var _binding: FragmentMonthSpendBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var editTextDate: TextInputEditText
    private var dialog: Dialog? = null
    private lateinit var mContext: Context
    private lateinit var spends: MutableList<Spend>
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: SpendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthSpendBinding.inflate(inflater, container, false)
        spends = mutableListOf()

        Thread {
            spends = SpendApplication.database.spendDao().getAllSpends()
        }.start()

        mContext = requireContext()

        mBinding.fabAddSpend.setOnClickListener {
            showAddSpendDialog(spends)
        }

        mBinding.tvMonth.text = getActualMonthName()
        if (spends.isEmpty()) {
            mBinding.tvMonthSpend.text = "No hay gastos"
        } else {
            mBinding.tvMonthSpend.text = "Gastos del mes: ${spends.sumOf { it.amount }}"
        }
        initRecyclerView()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        Thread {
            spends = SpendApplication.database.spendDao().getAllSpends()
        }.start()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = SpendAdapter(
            spends
        )
        mLayoutManager = LinearLayoutManager(mContext)
        mBinding.rvMonthSpend.layoutManager = mLayoutManager
        mBinding.rvMonthSpend.adapter = adapter
    }

    private fun OnItemSelected(spend: Any) {

    }

    private fun showAddSpendDialog(spends: MutableList<Spend>) {
        dialog = Dialog(requireContext())
        dialog!!.setContentView(R.layout.add_spend_dialog)
        val fabCloseDialog: FloatingActionButton = dialog!!.findViewById(R.id.fabClose)
        val btnSaveSpend: Button = dialog!!.findViewById(R.id.btnSaveSpend)
        val btnPickDate: Button = dialog!!.findViewById(R.id.btnDatePicker)
        val spinnerRegularity = dialog!!.findViewById<Spinner>(R.id.spnRegularity)
        val spinnerCategory = dialog!!.findViewById<Spinner>(R.id.spnCategory)

        val regularity = Regularity.values().map { it.name }
        val category = Category.values().map { it.name }
        setSpinner(spinnerRegularity, regularity)
        setSpinner(spinnerCategory, category)

        btnSaveSpend.setOnClickListener {
            saveSpend(dialog!!, spends)
            dialog!!.dismiss()
        }

        fabCloseDialog.setOnClickListener {
            dialog!!.dismiss()
        }

        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        dialog!!.show()
    }

    private fun setSpinner(spinner: Spinner, list: List<String>) {
        val adapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(CalendarConstraints.Builder().build())
            .setSelection(calendar.timeInMillis)

        editTextDate = dialog?.findViewById(R.id.etDate) ?: TextInputEditText(requireContext())

        val datePicker = datePickerBuilder.build()

        datePicker.addOnPositiveButtonClickListener {
            val selectedDateInMillis = it
            val selectedDate = Date(selectedDateInMillis)
            val formattedDate = dateFormat.format(selectedDate)
            editTextDate.setText(formattedDate)
        }

        datePicker.show(parentFragmentManager, "DatePicker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getActualMonthName(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM", Locale("es", "ES"))
        var month: String = dateFormat.format(calendar.time)
        month = month.substring(0, 1).uppercase() + month.substring(1)
        return month
    }

    private fun saveSpend(dialog: Dialog, spends: MutableList<Spend>) {
        val spend: Spend = Spend(
            0,
            dialog.findViewById<EditText>(R.id.edtAmount).text.toString().toDouble(),
            dialog.findViewById<EditText>(R.id.edtDescription).text.toString(),
            Regularity.valueOf(dialog.findViewById<Spinner>(R.id.spnRegularity).selectedItem.toString()),
            Category.valueOf(dialog.findViewById<Spinner>(R.id.spnCategory).selectedItem.toString()),
            dialog.findViewById<TextInputEditText>(R.id.etDate).text.toString(),
            dialog.findViewById<EditText>(R.id.edtSubcategory).text.toString(),
            0
        )
        Thread {
            SpendApplication.database.spendDao().addSpend(spend)
        }.start()
        spends.add(spend)
        adapter.notifyDataSetChanged()
    }

    override fun onClick(spend: Spend, position: Int) {
        TODO("Not yet implemented")
    }

    inner class SpendHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSpendBinding.bind(view)

        fun setListener(spend: Spend) {
            // Implementar la lógica para manejar clics en elementos del RecyclerView si es necesario
        }
    }
}
