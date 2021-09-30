package com.example.fragments

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RegisterUser(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "registerUserId")
    var registerUserId: Int = 0,
    val registerUsername : String = "",
    val registerPassword: String = "")
