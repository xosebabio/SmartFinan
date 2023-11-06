package com.xbs.smartfinan.ui.spenddetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.databinding.FragmentSpendDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SpendDetailFragment(private val spend: Spend) : Fragment() {

    private var _binding: FragmentSpendDetailBinding? = null
    private val mBinding get() = _binding!!
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpendDetailBinding.inflate(inflater, container, false)
        mBinding.tvDescription.text = spend.description
        mBinding.tvAmount.text = spend.amount.toString()
        mBinding.tvDate.text = dateFormat.format(spend.dateAt)
        mBinding.tvCategory.text = spend.category.toString()
        //mBinding.btnDelete.setOnClickListener { db.deleteSpend(spend)}
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
