package com.xbs.smartfinan.domain

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.databinding.ItemIncomeBinding
import com.xbs.smartfinan.databinding.ItemSpendBinding

class IncomeViewHolder(
    view: View,
    private val itemClickListener: (Income) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val binding = ItemIncomeBinding.bind(view)

    fun render(income: Income) {
        binding.tvIncomeAmount.text = income.amount.toString()
        binding.tvIncomeDescription.text = income.description
        binding.tvIncomeDate.text = income.dateAt
        itemView.setOnClickListener {
            itemClickListener(income)
        }
    }

}
