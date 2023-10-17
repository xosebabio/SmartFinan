package com.xbs.smartfinan.ui.income

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.databinding.ActivityIncomeBinding
import com.xbs.smartfinan.domain.Regularity
import java.text.SimpleDateFormat
import java.util.Locale

class IncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomeBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val incomeDao = SmartFinanApplication.database.incomeDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val regularity = Regularity.values().map { it.name }
        setSpinner(binding.spnRegularity, regularity)
        binding.btnCancelIncome.setOnClickListener { finish() }
        binding.btnSaveIncome.setOnClickListener { newIncome() }
    }

    private fun newIncome(){
        var income = Income(
            0,
            binding.edtAmount.text.toString().toDouble(),
            binding.edtDescription.text.toString(),
            Regularity.valueOf(binding.spnRegularity.selectedItem.toString()),
            dateFormat.format(binding.etDate.text.toString())
        )

        incomeDao.insertIncome(income)
    }

    private fun setSpinner(spinner: Spinner, list: List<String>) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}