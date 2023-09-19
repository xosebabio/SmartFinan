package com.xbs.smartfinan.view

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date



val dateFormat = SimpleDateFormat("yyyy-MM-dd")
enum class Regularity(val value: String) {
    ONCE("once"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly")
}
enum class Category(val value: String) {
    NECESSARY("necessary"),
    UNNECESSARY("unnecessary"),
}

@Entity(tableName = "Spend")
data class Spend(
    @PrimaryKey(autoGenerate = true)
    var spend_id: Int,
    val amount: Double,
    val description: String,
    val regularity: Regularity,
    val category: Category,
    val dateAt: String,
    val subcategory: String,
    val userId: Long
) {
}
