package com.example.todoapp

data class Task(val title: String = "", val description: String = "", var isUrgent: Boolean = false, var isCompleted: Boolean = false, var id: String = "")