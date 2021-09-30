package com.example.fragments

import androidx.room.*

@Dao
interface RegisterUserDao {

    @Insert
    fun addRegisterUser(registerUser: RegisterUser)

    @Delete
    fun deleteUser(registerUser: RegisterUser)

    @Update
    fun updateUser(registerUser: RegisterUser)

    @Query("SELECT * FROM RegisterUser ORDER BY registerUserId DESC")
    fun getAllRegisterUsers() : List<RegisterUser>

}