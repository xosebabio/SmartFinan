package com.xbs.smartfinan.domain

import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.databinding.ItemSpendBinding

class SpendViewHolder(
    view: View,
    private val itemClickListener: (Spend) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSpendBinding.bind(view)

    fun render(spend: Spend) {
        binding.tvSpendAmount.text = spend.amount.toString()
        binding.tvSpendDescription.text = spend.description
        binding.tvSpendDate.text = spend.dateAt
        binding.tvSpendCategory.text = spend.category.toString()
        itemView.setOnClickListener {
            itemClickListener(spend)
        }
    }

}
