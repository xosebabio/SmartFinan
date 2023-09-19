package com.xbs.smartfinan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SpendDao {

    @Insert
    fun addSpend(spend: Spend)

    @Query("SELECT * FROM Spend")
    fun getAllSpends(): MutableList<Spend>

}