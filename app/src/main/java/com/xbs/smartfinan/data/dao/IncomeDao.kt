package com.xbs.smartfinan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xbs.smartfinan.data.entity.Income

@Dao
interface IncomeDao {

    @Insert
    fun insertIncome(income: Income)

    @Query("SELECT * FROM Income")
    fun getAllIncomes(): MutableList<Income>

    @Query("SELECT * FROM Income where dateAt BETWEEN :from AND :at")
    fun getIncomesByDate(from: String, at: String): MutableList<Income>

    /* @Query("SELECT * FROM Income WHERE dateAt BETWEEN :start AND :end")
     fun getIncomesBetweenDates(start: String, end: String): MutableList<Income>*/
}