package com.xbs.smartfinan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xbs.smartfinan.data.entity.Spend

@Dao
interface SpendDao {

    @Insert
    fun addSpend(spend: Spend)


    @Query("SELECT * FROM Spend WHERE category = :category AND dateAt BETWEEN :start AND :end")
    fun getSpendsBetweenDatesAndCategory(start: String, end: String, category: String): MutableList<Spend>
    @Query("SELECT * FROM Spend WHERE dateAt BETWEEN :start AND :end")
    fun getSpendsBetweenDates(start: String, end: String): MutableList<Spend>


}