package com.xbs.smartfinan.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xbs.smartfinan.databinding.ItemSpendBinding

class SpendViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemSpendBinding.bind(view)

    fun render(spend: Spend){

        binding.tvSpendAmount.text = spend.amount.toString()
        binding.tvSpendDescription.text = spend.description
        binding.tvSpendDate.text = spend.dateAt.toString()
        binding.tvSpendCategory.text = spend.category.toString()
    }

}
