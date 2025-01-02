package com.example.todoapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.MainActivity
import com.example.todoapp.databinding.ActivityEditTaskBinding
import com.example.todoapp.entity.Task
import com.google.firebase.database.FirebaseDatabase

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://todoapp-52d1f-default-rtdb.firebaseio.com/")
    private val databaseReference = firebaseDatabase.getReference("Tasks")
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
    }

    private fun initialize() {
        task = if (intent.hasExtra("task")) intent.getParcelableExtra("task") else null
        task?.let {
            binding.taskTitle.setText(it.title)
            binding.taskDescription.setText(it.description)
            binding.isUrgent.isChecked = it.isUrgent
        }
        binding.saveButton.setOnClickListener { updateTask() }
        binding.cancelButton.setOnClickListener { finish() }
    }

    private fun updateTask() {
        databaseReference.child(task?.id.toString()).setValue(
            Task(
                binding.taskTitle.text.toString(),
                binding.taskDescription.text.toString(),
                binding.isUrgent.isChecked,
                task?.isCompleted ?: false,
                task?.id ?: ""
            )
        )
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }
}