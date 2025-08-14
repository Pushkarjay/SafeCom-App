package com.safecom.taskmanagement.ui.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.databinding.ItemTaskBinding
import com.safecom.taskmanagement.domain.model.Task
import com.safecom.taskmanagement.domain.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskStatusChanged: (Task, TaskStatus) -> Unit
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description
                
                // Set priority indicator
                val priorityColor = when (task.priority) {
                    "High" -> ContextCompat.getColor(root.context, R.color.priority_high)
                    "Medium" -> ContextCompat.getColor(root.context, R.color.priority_medium)
                    "Low" -> ContextCompat.getColor(root.context, R.color.priority_low)
                    else -> ContextCompat.getColor(root.context, R.color.priority_default)
                }
                viewPriorityIndicator.setBackgroundColor(priorityColor)
                tvPriority.text = task.priority
                tvPriority.setTextColor(priorityColor)
                
                // Set status
                tvStatus.text = when (task.status) {
                    TaskStatus.PENDING -> "Pending"
                    TaskStatus.IN_PROGRESS -> "In Progress"
                    TaskStatus.COMPLETED -> "Completed"
                    TaskStatus.OVERDUE -> "Overdue"
                    TaskStatus.CANCELLED -> "Cancelled"
                }
                
                val statusColor = when (task.status) {
                    TaskStatus.PENDING -> ContextCompat.getColor(root.context, R.color.status_pending)
                    TaskStatus.IN_PROGRESS -> ContextCompat.getColor(root.context, R.color.status_in_progress)
                    TaskStatus.COMPLETED -> ContextCompat.getColor(root.context, R.color.status_completed)
                    TaskStatus.OVERDUE -> ContextCompat.getColor(root.context, R.color.status_overdue)
                    TaskStatus.CANCELLED -> ContextCompat.getColor(root.context, R.color.status_cancelled)
                }
                chipStatus.setChipBackgroundColorResource(
                    when (task.status) {
                        TaskStatus.PENDING -> R.color.status_pending_background
                        TaskStatus.IN_PROGRESS -> R.color.status_in_progress_background
                        TaskStatus.COMPLETED -> R.color.status_completed_background
                        TaskStatus.OVERDUE -> R.color.status_overdue_background
                        TaskStatus.CANCELLED -> R.color.status_cancelled_background
                    }
                )
                chipStatus.setTextColor(statusColor)
                
                // Set due date
                task.dueDate?.let { dueDate ->
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    tvDueDate.text = "Due: ${dateFormat.format(dueDate)}"
                    
                    // Check if overdue
                    val now = Date()
                    if (dueDate.before(now) && task.status != TaskStatus.COMPLETED) {
                        tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.status_overdue))
                        ivDueDateIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.status_overdue))
                    } else {
                        tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                        ivDueDateIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.text_secondary))
                    }
                } ?: run {
                    tvDueDate.text = "No due date"
                    tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                }
                
                // Set assigned user
                task.assignedTo?.let { assignedTo ->
                    tvAssignedTo.text = "Assigned to: $assignedTo"
                } ?: run {
                    tvAssignedTo.text = "Unassigned"
                }
                
                // Set category/tags
                if (task.tags.isNotEmpty()) {
                    tvTags.text = task.tags.joinToString(", ") { "#$it" }
                } else {
                    tvTags.text = task.category ?: ""
                }
                
                // Set progress indicator for in-progress tasks
                if (task.status == TaskStatus.IN_PROGRESS) {
                    progressBar.visibility = android.view.View.VISIBLE
                    // Set progress based on task completion or time elapsed
                    progressBar.progress = 50 // Default progress
                } else {
                    progressBar.visibility = android.view.View.GONE
                }
                
                // Set click listeners
                root.setOnClickListener {
                    onTaskClick(task)
                }
                
                // Status quick actions
                btnMarkCompleted.setOnClickListener {
                    onTaskStatusChanged(task, TaskStatus.COMPLETED)
                }
                
                btnMarkInProgress.setOnClickListener {
                    onTaskStatusChanged(task, TaskStatus.IN_PROGRESS)
                }
                
                // Show/hide action buttons based on current status
                when (task.status) {
                    TaskStatus.PENDING -> {
                        btnMarkInProgress.visibility = android.view.View.VISIBLE
                        btnMarkCompleted.visibility = android.view.View.VISIBLE
                    }
                    TaskStatus.IN_PROGRESS -> {
                        btnMarkInProgress.visibility = android.view.View.GONE
                        btnMarkCompleted.visibility = android.view.View.VISIBLE
                    }
                    TaskStatus.COMPLETED -> {
                        btnMarkInProgress.visibility = android.view.View.GONE
                        btnMarkCompleted.visibility = android.view.View.GONE
                    }
                    else -> {
                        btnMarkInProgress.visibility = android.view.View.GONE
                        btnMarkCompleted.visibility = android.view.View.GONE
                    }
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
