package com.xbs.smartfinan.data

import android.app.Application
import androidx.room.Room

class SpendApplication : Application() {
    companion object{
        lateinit var database: SpendDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, SpendDatabase::class.java, "SpendDatabase").build()
    }
}