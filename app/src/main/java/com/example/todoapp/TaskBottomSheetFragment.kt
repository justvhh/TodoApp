package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.activity.EditTaskActivity
import com.example.todoapp.activity.TaskViewModel
import com.example.todoapp.databinding.FragmentTaskBottomSheetBinding
import com.example.todoapp.entity.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.FirebaseDatabase

class TaskBottomSheetFragment(private val task: Task) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTaskBottomSheetBinding
    private lateinit var viewModel: TaskViewModel
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://todoapp-52d1f-default-rtdb.firebaseio.com/")
    private val databaseReference = firebaseDatabase.getReference("Tasks")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBottomSheetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        initialize()
        return binding.root
    }

    private fun initialize() {
        binding.taskTitle.text = task.title
        binding.taskCompletedCheckbox.isChecked = task.isCompleted

        binding.taskCompletedCheckbox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            viewModel.updateTask(task)
        }
        binding.editButton.setOnClickListener { editTask() }
        binding.deleteButton.setOnClickListener { deleteTask() }
    }

    private fun deleteTask() {
        databaseReference.child(task.id).removeValue()
    }

    private fun editTask() {
        val intent = Intent(requireContext(), EditTaskActivity::class.java)
        intent.putExtra("task", task)
        startActivity(intent)
    }
}