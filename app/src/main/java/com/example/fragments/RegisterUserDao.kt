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

    @Query("SELECT * FROM RegisterUser ORDER BY registerUserId DESC limit 1")
    fun getAllRegisterUsers() : List<RegisterUser>

    @Query("SELECT * FROM RegisterUser ORDER BY registerUserId DESC")
    fun getAllRegister() : List<RegisterUser>

}