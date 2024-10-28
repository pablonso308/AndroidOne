package com.example.androidone.DataBase
import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Insert
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}