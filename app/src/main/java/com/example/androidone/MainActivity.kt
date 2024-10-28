// app/src/main/java/com/example/androidone/MainActivity.kt
package com.example.androidone

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.androidone.DataBase.AppDatabase
import com.example.androidone.DataBase.Task
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "task-database"
        ).fallbackToDestructiveMigration() // Add this line
         .build()

        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadTasks()

        btnSave.setOnClickListener {
            val description = etDescription.text.toString()
            val task = Task(description = description, isCompleted = false)

            thread {
                db.taskDao().insertTask(task)
                runOnUiThread {
                    loadTasks()
                }
            }
        }
    }

    private fun loadTasks() {
        thread {
            val tasks = db.taskDao().getAllTasks()
            runOnUiThread {
                taskAdapter = TaskAdapter(tasks)
                recyclerView.adapter = taskAdapter
            }
        }
    }
}