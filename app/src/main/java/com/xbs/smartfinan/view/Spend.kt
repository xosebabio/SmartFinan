package com.xbs.smartfinan.view

import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import com.google.firebase.Timestamp


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

data class Spend(
    var spend_id: String,
    val amount: Double,
    val description: String,
    val regularity: Regularity,
    val category: Category,
    val dateAt: Date,
    val subcategory: String,
    val userId: Long
) {
    companion object {
        fun fromFirestore(documentSnapshot: DocumentSnapshot): Spend {
            val data = documentSnapshot.data
            val spend_id = data?.get("spend_id") as String
            val amount = data?.get("amount") as Double
            val description = data?.get("description") as String
            val regularity = Regularity.valueOf(data?.get("regularity").toString())
            val category = Category.valueOf(data?.get("category").toString())
            val dateAt = formatDate(data?.get("dateAt") as Timestamp)
            val subcategory = data?.get("subcategory") as String
            val userId = data?.get("userId") as Long
            return Spend(spend_id, amount, description, regularity, category, dateAt, subcategory, userId)
        }

        fun formatDate(dateAt: Timestamp): Date{
            val dateString = dateFormat.format(dateAt.toDate())
            return dateFormat.parse(dateString)
        }
    }
}
