package com.xbs.smartfinan.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xbs.smartfinan.view.Spend

@Database(entities = arrayOf(Spend::class), version = 1)
abstract class SpendDatabase : RoomDatabase() {
    abstract fun spendDao(): SpendDao
}