package com.example.todoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.entity.Task

class TaskAdapter(private var tasksList: List<Task>, private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(), Filterable {

    private var filteredTaskList: List<Task> = tasksList.toMutableList()

    class TaskViewHolder(itemView: View, onItemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.task_name)
        val taskColor: LinearLayout = itemView.findViewById(R.id.task_color)
        var status: TextView = itemView.findViewById(R.id.task_status)
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return TaskViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredTaskList[position]
        holder.title.text = task.title
        holder.taskColor.setBackgroundColor(getTaskColor(task.isUrgent, holder.itemView.context))
        holder.status.text = "Status: " + if (task.isCompleted) "Completed" else "Incomplete"
    }
    private fun getTaskColor(isUrgent: Boolean, context: Context): Int {
        val colorRes = if (isUrgent) R.color.red else R.color.green
        return ContextCompat.getColor(context, colorRes)
    }


    override fun getItemCount(): Int {
        return filteredTaskList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    tasksList
                } else {
                    tasksList.filter {
                        it.title.contains(constraint, ignoreCase = true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredTaskList = results?.values as List<Task>
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newList: List<Task>) {
        tasksList = newList
        filteredTaskList = newList
        notifyDataSetChanged()
    }

}