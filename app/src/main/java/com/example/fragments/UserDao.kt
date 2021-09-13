package com.example.fragments

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun addUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM user ORDER BY userId DESC")
    fun getAllUsers() : List<User>


}