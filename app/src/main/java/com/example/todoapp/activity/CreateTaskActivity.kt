package com.example.todoapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.databinding.ActivityCreateTaskBinding
import com.example.todoapp.entity.Task
import com.google.firebase.database.FirebaseDatabase

private val firebaseDatabase = FirebaseDatabase.getInstance("https://todoapp-52d1f-default-rtdb.firebaseio.com/")
private val databaseReference = firebaseDatabase.getReference("Tasks")

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var viewModel: TaskViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        initialize()
    }

    private fun initialize() {

        binding.saveButton.setOnClickListener { saveTask() }
        binding.cancelButton.setOnClickListener { finish() }

    }

    private fun saveTask() {
        val title = binding.taskTitle.text.toString()
        val description = binding.taskDescription.text.toString()
        val isUrgent = binding.isUrgent.isChecked
        if (title.isNotBlank()) {
            val task = Task(title, description, isUrgent)
            viewModel.addTask(task)
            finish()
        } else {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
class TaskViewModel : ViewModel() {

    fun addTask(task: Task) {
        val key = databaseReference.push().key
        key?.let {
            task.id = it
            databaseReference.child(it).setValue(task)
        }
    }
    fun updateTask(task: Task) {
        databaseReference.child(task.id).setValue(task)
    }
}