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
        if (spend.category.value==Category.NECESSARY.value) {
            binding.tvSpendCategory.text = "Importante"
            binding.tvSpendCategory.setTextColor(itemView.resources.getColor(android.R.color.holo_green_dark))
        }else {
            binding.tvSpendCategory.text = "Innecesario"
            binding.tvSpendCategory.setTextColor(itemView.resources.getColor(android.R.color.holo_red_dark))
        }
        itemView.setOnClickListener {
            itemClickListener(spend)
        }
    }

}
