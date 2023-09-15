package com.xbs.smartfinan.view

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.xbs.smartfinan.databinding.ItemSpendBinding

class SpendViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemSpendBinding.bind(view)

    fun render(spend: Spend){
        binding.tvSpendAmount.text = spend.amount.toString()
        binding.tvSpendDescription.text = spend.description
        binding.tvSpendDate.text = spend.dateAt.toString()
        binding.tvSpendCategory.text = spend.category.toString()
        itemView.setOnClickListener{
            Toast.makeText(itemView.context, "Click en ${spend.spend_id}", Toast.LENGTH_SHORT).show()
            //MainActivity.renderFragment(SpendDetailFragment(spend))
        }
    }

}
