package com.xbs.smartfinan.ui.monthincome

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.databinding.ActivityEditDeleteIncomeBinding
import com.xbs.smartfinan.domain.Regularity
import com.xbs.smartfinan.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class EditDeleteIncomeActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = MainScope().coroutineContext

    private lateinit var mBinding: ActivityEditDeleteIncomeBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEditDeleteIncomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val spinnerRegularity = mBinding.spnRegularity
        val regularity = Regularity.values().map { it.name }
        setSpinner(spinnerRegularity, regularity)

        mBinding.btnDatePicker.setOnClickListener{
            showDatePicker()
        }


        val incomeId = intent.getIntExtra("income_id", -1)
        launch(Dispatchers.IO) {
            val income: Income? = SmartFinanApplication.database.incomeDao().getIncomeById(incomeId)

            // Actualiza la interfaz de usuario en el contexto principal
            launch(Dispatchers.Main) {
                if (income != null) {
                    mBinding.edtAmount.setText(income.amount.toString())
                    mBinding.edtDescription.setText(income.description)
                    mBinding.spnRegularity.setSelection(income.regularity.ordinal)
                    mBinding.etDate.setText(income.dateAt)
                    mBinding.btnDeleteIncome.setOnClickListener{
                        deleteIncome(income)
                    }
                    mBinding.btnCancelIncome.setOnClickListener{
                        navigateToMonthlySpends()
                    }

                    mBinding.btnUpdateIncome.setOnClickListener{
                        updateIncome(incomeId)
                    }
                } else {
                    // Manejar el caso en el que no se encontró un Spend con el ID dado
                }
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
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

    private fun updateIncome(incomeId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que deseas actualizar este ingreso?")
        // Agrega un botón "Sí" para confirmar la eliminación
        builder.setPositiveButton("Sí") { dialog, which ->
            try {
                val income: Income = Income(
                    incomeId,
                    mBinding.edtAmount.text.toString().toDouble(),
                    mBinding.edtDescription.text.toString(),
                    Regularity.valueOf(mBinding.spnRegularity.selectedItem.toString()),
                    dateFormat.format(calendar.time)
                )
                launch(Dispatchers.IO) {
                    SmartFinanApplication.database.incomeDao().upsertIncome(income)
                    withContext(Dispatchers.Main) {
                        navigateToMonthlySpends()
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateIncome", "Error: ${e.message}", e)
            }
        }
        // Agrega un botón "No" para cancelar la eliminación
        builder.setNegativeButton("No") { dialog, which ->
            // Cierre el diálogo y no realice la eliminación
            dialog.dismiss()
        }
        // Muestra el diálogo de confirmación
        builder.show()
    }


    private fun deleteIncome(income: Income) {
        // Construye un diálogo de confirmación
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que deseas eliminar este ingreso?")
        // Agrega un botón "Sí" para confirmar la eliminación
        builder.setPositiveButton("Sí") { dialog, which ->
            // Elimina el gasto
            launch(Dispatchers.IO) {
                SmartFinanApplication.database.incomeDao().deleteIncome(income)
                // Vuelve al hilo principal (Main) si es necesario
                withContext(Dispatchers.Main) {
                    navigateToMonthlySpends()
                }
            }
        }
        // Agrega un botón "No" para cancelar la eliminación
        builder.setNegativeButton("No") { dialog, which ->
            // Cierre el diálogo y no realice la eliminación
            dialog.dismiss()
        }
        // Muestra el diálogo de confirmación
        builder.show()
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