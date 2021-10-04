package com.example.fragments

import androidx.room.*

@Dao
interface UserProfileDao {
    @Insert
    fun addUserDetails(userProfile: UserProfile)

    @Delete
    fun delete(userProfile: UserProfile)

    @Update
    fun update(userProfile: UserProfile)

    @Query("SELECT * FROM userprofile ORDER BY userDetailId DESC LIMIT 1")
    fun getAllUserProfile() : List<UserProfile>

}