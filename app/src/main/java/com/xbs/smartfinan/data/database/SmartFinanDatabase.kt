package com.xbs.smartfinan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xbs.smartfinan.data.converter.Converters
import com.xbs.smartfinan.data.dao.IncomeDao
import com.xbs.smartfinan.data.entity.Income
import com.xbs.smartfinan.data.entity.Spend
import com.xbs.smartfinan.data.dao.SpendDao

@Database(entities = arrayOf(Spend::class, Income::class), version = 1)
@TypeConverters(Converters::class)
abstract class SmartFinanDatabase : RoomDatabase() {
    abstract fun spendDao(): SpendDao
    abstract fun incomeDao(): IncomeDao
}