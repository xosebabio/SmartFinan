package com.xbs.smartfinan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xbs.smartfinan.domain.Category
import com.xbs.smartfinan.domain.Regularity
import java.util.Date

@Entity(tableName = "Spend")
data class Spend(
    @PrimaryKey(autoGenerate = true)
    var spend_id: Int,
    val amount: Double,
    val description: String,
    val regularity: Regularity,
    val category: Category,
    val dateAt: String,
    var checked: Boolean = false
) {
}
