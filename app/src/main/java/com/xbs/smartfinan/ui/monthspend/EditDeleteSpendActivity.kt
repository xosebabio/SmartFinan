package com.xbs.smartfinan.ui.monthspend

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.xbs.smartfinan.R
import com.xbs.smartfinan.data.database.SmartFinanApplication
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.databinding.ActivityEditDeleteSpendBinding
import com.xbs.smartfinan.domain.Category
import com.xbs.smartfinan.domain.Regularity
import com.xbs.smartfinan.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class EditDeleteSpendActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = MainScope().coroutineContext

    private lateinit var mBinding: ActivityEditDeleteSpendBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEditDeleteSpendBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val spinnerRegularity = mBinding.spnRegularity
        val spinnerCategory = mBinding.spnCategory
        val regularity = Regularity.values().map { it.name }
        val category = Category.values().map { it.name }
        setSpinner(spinnerRegularity, regularity)
        setSpinner(spinnerCategory, category)

        mBinding.btnDatePicker.setOnClickListener {
            showDatePicker()
        }


        val spendId = intent.getIntExtra("spend_id", -1)
        launch(Dispatchers.IO) {
            val spend: Spend? = SmartFinanApplication.database.spendDao().getSpendById(spendId)

            // Actualiza la interfaz de usuario en el contexto principal
            launch(Dispatchers.Main) {
                if (spend != null) {
                    mBinding.edtAmount.setText(spend.amount.toString())
                    mBinding.edtDescription.setText(spend.description)
                    mBinding.spnRegularity.setSelection(spend.regularity.ordinal)
                    mBinding.spnCategory.setSelection(spend.category.ordinal)
                    mBinding.etDate.setText(spend.dateAt)
                    mBinding.btnDeleteSpend.setOnClickListener {
                        deleteSpend(spend)
                    }
                    mBinding.btnCancelSpend.setOnClickListener {
                        navigateToMonthlySpends()
                    }

                    mBinding.btnUpdateSpend.setOnClickListener {
                        updateSpend(spendId)
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

    private fun updateSpend(spend_id: Int) {
        // Construye un diálogo de confirmación
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que deseas actualizar este gasto?")
        // Agrega un botón "Sí" para confirmar la eliminación
        builder.setPositiveButton("Sí") { dialog, which ->
            try {
                val spend: Spend = Spend(
                    spend_id,
                    mBinding.edtAmount.text.toString().toDouble(),
                    mBinding.edtDescription.text.toString(),
                    Regularity.valueOf(mBinding.spnRegularity.selectedItem.toString()),
                    Category.valueOf(mBinding.spnCategory.selectedItem.toString()),
                    dateFormat.format(calendar.time),
                    false
                )
                Log.d("UpdateSpend", "Updating spend: $spend")
                launch(Dispatchers.IO) {
                    SmartFinanApplication.database.spendDao().addSpend(spend)
                    Log.d("UpdateSpend", "Spend updated")
                    withContext(Dispatchers.Main) {
                        navigateToMonthlySpends()
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateSpend", "Error: ${e.message}", e)
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

    private fun deleteSpend(spend: Spend) {
        // Construye un diálogo de confirmación
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que deseas eliminar este gasto?")
        // Agrega un botón "Sí" para confirmar la eliminación
        builder.setPositiveButton("Sí") { dialog, which ->
            // Elimina el gasto
            launch(Dispatchers.IO) {
                SmartFinanApplication.database.spendDao().deleteSpend(spend)
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