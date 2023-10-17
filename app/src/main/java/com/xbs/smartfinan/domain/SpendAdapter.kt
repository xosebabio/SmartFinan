package com.xbs.smartfinan.domain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xbs.smartfinan.R
import com.xbs.smartfinan.data.entity.Spend

class SpendAdapter(
    private var spendList: MutableList<Spend>
) : RecyclerView.Adapter<SpendViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpendViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SpendViewHolder(layoutInflater.inflate(R.layout.item_spend, parent, false))
    }

    override fun getItemCount(): Int = spendList.size

    override fun onBindViewHolder(holder: SpendViewHolder, position: Int) {
        val item = spendList[position]
        holder.render(item)
    }
}
