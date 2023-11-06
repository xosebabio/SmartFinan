package com.xbs.smartfinan.ui.spend

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.databinding.ActivitySpendBinding
import com.xbs.smartfinan.domain.Category
import com.xbs.smartfinan.domain.Regularity
import com.xbs.smartfinan.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SpendActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySpendBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySpendBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val spinnerRegularity = mBinding.spnRegularity
        val spinnerCategory = mBinding.spnCategory

        val regularity = Regularity.values().map { it.name }
        val category = Category.values().map { it.name }
        setSpinner(spinnerRegularity, regularity)
        setSpinner(spinnerCategory, category)
        mBinding.btnDatePicker.setOnClickListener{
            showDatePicker()
        }
        mBinding.btnSaveSpend.setOnClickListener{
            saveSpend()
        }
    }


    private fun saveSpend() {
        val spend: Spend = Spend(
            0,
            mBinding.edtAmount.text.toString().toDouble(),
            mBinding.edtDescription.text.toString(),
            Regularity.valueOf(mBinding.spnRegularity.selectedItem.toString()),
            Category.valueOf(mBinding.spnCategory.selectedItem.toString()),
            dateFormat.format(calendar.time),
            false
        )
        Thread {
            SmartFinanApplication.database.spendDao().addSpend(spend)
        }.start()
        navigateToMonthlySpends()
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

    private fun setSpinner(spinner: Spinner, list: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun navigateToMonthlySpends() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun updateDateText() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        mBinding.etDate.setText(formattedDate)
    }


}