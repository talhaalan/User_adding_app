package com.example.fragments

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class,RegisterUser::class],version = 10, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao() : UserDao
    abstract fun registerUserDao() : RegisterUserDao

    companion object {
        private var instance: UserDatabase? = null

        fun getUserDatabase(context: Context) : UserDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context,UserDatabase::class.java,"user.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance
        }
    }



}