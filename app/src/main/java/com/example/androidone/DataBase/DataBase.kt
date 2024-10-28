package com.example.androidone.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 2) // Increment the version number
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}