package com.xbs.smartfinan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.xbs.smartfinan.data.entity.Income

@Dao
interface IncomeDao {

    @Upsert
    fun upsertIncome(income: Income)

    @Delete
    fun deleteIncome(income: Income)

    @Query("SELECT * FROM Income")
    fun getAllIncomes(): MutableList<Income>

    @Query("SELECT * FROM Income where dateAt BETWEEN :from AND :at")
    fun getIncomesByDate(from: String, at: String): MutableList<Income>

     @Query("SELECT * FROM Income where income_id = :id")
    fun getIncomeById(id: Int): Income

}