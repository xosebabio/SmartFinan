package com.xbs.smartfinan.ui.income

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.databinding.ActivityIncomeBinding
import com.xbs.smartfinan.domain.Regularity
import com.xbs.smartfinan.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncomeActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()

    private lateinit var binding: ActivityIncomeBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val incomeDao = SmartFinanApplication.database.incomeDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val regularity = Regularity.values().map { it.name }
        setSpinner(binding.spnRegularity, regularity)
        binding.btnDatePicker.setOnClickListener { showDatePicker() }
        binding.btnCancelIncome.setOnClickListener { finish() }
        binding.btnSaveIncome.setOnClickListener { saveIncome() }
    }

    private fun saveIncome() {
        val income: Income = Income(
            0,
            binding.edtAmount.text.toString().toDouble(),
            binding.edtDescription.text.toString(),
            Regularity.valueOf(binding.spnRegularity.selectedItem.toString()),
            dateFormat.format(calendar.time)
        )
        Thread {
            incomeDao.upsertIncome(income)
        }.start()
        navigateToMonthlySpends()
    }

    private fun setSpinner(spinner: Spinner, list: List<String>) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun navigateToMonthlySpends() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    private fun updateDateText() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        binding.etDate.setText(formattedDate)
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }



}