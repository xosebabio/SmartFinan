package com.xbs.smartfinan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xbs.smartfinan.domain.Regularity
import java.util.Date

@Entity(tableName = "Income")
data class Income(
    @PrimaryKey(autoGenerate = true)
    var income_id: Int,
    val amount: Double,
    val description: String,
    val regularity: Regularity,
    val dateAt: String,
    var checked: Boolean = false
)
