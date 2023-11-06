package com.xbs.smartfinan.domain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xbs.smartfinan.R
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.data.entity.Spend

class IncomeAdapter(
    private var incomeList: MutableList<Income>,
    private val itemClickListener: (Income) -> Unit
) : RecyclerView.Adapter<IncomeViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncomeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_income, parent, false)
        return IncomeViewHolder(view, itemClickListener)
    }

    override fun getItemCount(): Int = incomeList.size

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val item = incomeList[position]
        holder.render(item)
    }
}
