package com.xbs.smartfinan.data.database

import android.app.Application
import androidx.room.Room

class SmartFinanApplication : Application() {
    companion object{
        lateinit var database: SmartFinanDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, SmartFinanDatabase::class.java, "SpendDatabase").build()
    }
}