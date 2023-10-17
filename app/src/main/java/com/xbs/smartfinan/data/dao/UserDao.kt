package com.xbs.smartfinan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.xbs.smartfinan.data.entity.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)
}