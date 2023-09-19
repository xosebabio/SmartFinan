package com.xbs.smartfinan.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Spend::class), version = 1)
abstract class SpendDatabase : RoomDatabase() {
    abstract fun spendDao(): SpendDao
}