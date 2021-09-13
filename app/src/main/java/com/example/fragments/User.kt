package com.example.fragments

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId")
    var userId: Int = 0,
    val name : String = "",
    val password : String = "",
    val date : Long = 0L,
    val size : String = "",
    val kilos : String = ""

) : Parcelable