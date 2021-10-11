package com.example.fragments

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserImage(
    @PrimaryKey(autoGenerate = false)
@ColumnInfo(name = "userImageId")
    var userImageId: Int = 0,
    var profilePhoto: Bitmap?
)