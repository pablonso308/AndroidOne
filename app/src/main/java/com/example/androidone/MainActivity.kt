package com.example.androidone

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.androidone.DataBase.AppDatabase
import com.example.androidone.DataBase.Task
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "task-database"
        ).build()

        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {

            val description = etDescription.text.toString()
            val task = Task(description = description, isCompleted = false)

            thread {
                db.taskDao().insertTask(task)
                finish()
            }
        }
    }
}