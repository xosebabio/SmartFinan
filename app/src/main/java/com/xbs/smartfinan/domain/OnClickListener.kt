package com.xbs.smartfinan.domain

import com.xbs.smartfinan.data.Spend

interface OnClickListener {
    fun onClick(spend: Spend, position: Int)
}