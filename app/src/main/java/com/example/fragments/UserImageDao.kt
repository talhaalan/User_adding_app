package com.example.fragments

import androidx.room.*

@Dao
interface UserImageDao {
    @Insert
    fun addImage(userImage: UserImage)

    @Delete
    fun deleteImage(userImage: UserImage)

    @Update
    fun updateImage(userImage: UserImage)

    @Query("SELECT * FROM userimage ORDER BY userImageId DESC LIMIT 1")
    fun getUserImage() : List<UserImage>
}