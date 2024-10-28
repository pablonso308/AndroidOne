// app/src/main/java/com/example/androidone/MainActivity.kt
package com.example.androidone

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
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
    private lateinit var completedTaskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var completedRecyclerView: RecyclerView
    private var selectedTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "task-database"
        ).fallbackToDestructiveMigration()
         .build()

        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)
        recyclerView = findViewById(R.id.recyclerView)
        completedRecyclerView = findViewById(R.id.completedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        completedRecyclerView.layoutManager = LinearLayoutManager(this)

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

        registerForContextMenu(recyclerView)
    }

    private fun loadTasks() {
        thread {
            val tasks = db.taskDao().getAllTasks()
            val completedTasks = db.taskDao().getCompletedTasks()
            runOnUiThread {
                taskAdapter = TaskAdapter(tasks.filter { !it.isCompleted }) { task ->
                    selectedTask = task
                    openContextMenu(recyclerView)
                }
                completedTaskAdapter = TaskAdapter(completedTasks) { task ->
                    selectedTask = task
                    openContextMenu(completedRecyclerView)
                }
                recyclerView.adapter = taskAdapter
                completedRecyclerView.adapter = completedTaskAdapter
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                selectedTask?.let { task ->
                    thread {
                        db.taskDao().deleteTask(task)
                        runOnUiThread {
                            loadTasks()
                        }
                    }
                }
                true
            }
            R.id.mark_complete -> {
                selectedTask?.let { task ->
                    task.isCompleted = true
                    thread {
                        db.taskDao().updateTask(task)
                        runOnUiThread {
                            loadTasks()
                        }
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}