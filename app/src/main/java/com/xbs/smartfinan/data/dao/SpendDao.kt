package com.xbs.smartfinan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.xbs.smartfinan.data.entity.ChartInfo
import com.xbs.smartfinan.data.entity.Spend

@Dao
interface SpendDao {


    @Query("SELECT * FROM Spend")
    fun getAllSpends(): MutableList<Spend>

    @Upsert
    fun upsertSpend(spend: Spend)

    @Delete
    fun deleteSpend(spend: Spend)

    @Query("SELECT * FROM Spend WHERE spend_id = :id")
    fun getSpendById(id: Int): Spend

    @Query("SELECT * FROM Spend WHERE category = :category AND dateAt BETWEEN :start AND :end ORDER BY dateAt DESC")
    fun getSpendsBetweenDatesAndCategory(start: String, end: String, category: String): MutableList<Spend>
    @Query("SELECT * FROM Spend WHERE dateAt BETWEEN :start AND :end ORDER BY dateAt DESC")
    fun getSpendsBetweenDates(start: String, end: String): MutableList<Spend>

    @Query("SELECT strftime('%Y-%m', dateAt) AS month, SUM(amount) AS amount FROM Spend WHERE category = :category GROUP BY month ORDER BY month")
    fun getSpendsByMonth(category: String): MutableList<ChartInfo>

}