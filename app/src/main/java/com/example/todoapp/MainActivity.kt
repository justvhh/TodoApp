package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://todoapp-52d1f-default-rtdb.firebaseio.com/")
    private val databaseReference = firebaseDatabase.getReference("Tasks")
    private val tasksList = mutableListOf<Task>()
    private var completedTasks= mutableListOf<Task>()
    private var incompleteTasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupPaging()
        setupClickListeners()
        setupSearchBox()
        setupTouchListener()

    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        adapter = TaskAdapter(tasksList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        val textViews = listOf(binding.textView1, binding.textView2, binding.textView3)

        var lastSelectedView: TextView? = null

        val clickListener = View.OnClickListener { view ->
            lastSelectedView?.setBackgroundResource(R.drawable.border_button)
            when (view.id) {
                binding.textView1.id-> adapter.updateList(tasksList)
                binding.textView3.id -> adapter.updateList(completedTasks)
                binding.textView2.id -> adapter.updateList(incompleteTasks)
            }
            view.setBackgroundResource(R.drawable.border_selected)
            lastSelectedView = view as TextView
        }
        textViews.forEach { it.setOnClickListener(clickListener) }

        binding.addButton.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            startActivity(intent)
        }
    }

    private fun setupSearchBox() {
        val handler = Handler(Looper.getMainLooper())

        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    adapter.filter.filter(charSequence)
                }, 500)
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setupTouchListener() {
        binding.main.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                binding.searchBox.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                view.performClick()
            }
            false
        }
    }

    private fun setupPaging() {
        databaseProcess(object : tasksListUpdateCallBack {
            override fun updateList(newList: List<Task>) {
                completedTasks = newList.filter { it.isCompleted }.toMutableList()
                incompleteTasks = newList.filter { !it.isCompleted }.toMutableList()
            }
        })
    }

    private fun databaseProcess(callBack: tasksListUpdateCallBack) {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val task: Task = snapshot.getValue(Task::class.java)!!
                tasksList.add(task).apply { tasksList.sortWith(compareByDescending<Task> { !it.isCompleted }.thenByDescending { it.isUrgent }) }
                callBack.updateList(tasksList)
                adapter.updateList(tasksList)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val task: Task = snapshot.getValue(Task::class.java)!!
                val position = tasksList.indexOfFirst { it.id == task.id }
                tasksList[position] = task
                adapter.notifyItemChanged(position)
                callBack.updateList(tasksList)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val task: Task = snapshot.getValue(Task::class.java)!!
                val position = tasksList.indexOfFirst { it.id == task.id }
                tasksList.removeAt(position)
                adapter.notifyItemRemoved(position)
                callBack.updateList(tasksList)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Not implemented
            }

            override fun onCancelled(error: DatabaseError) {
                // Not implemented
            }
        })
    }
    interface tasksListUpdateCallBack {
        fun updateList(newList: List<Task>)
    }
}
