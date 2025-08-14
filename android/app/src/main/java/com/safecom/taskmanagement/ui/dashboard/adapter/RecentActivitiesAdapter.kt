package com.safecom.taskmanagement.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.databinding.ItemRecentActivityBinding
import com.safecom.taskmanagement.domain.model.ActivityType
import com.safecom.taskmanagement.domain.model.RecentActivity
import java.text.SimpleDateFormat
import java.util.*

class RecentActivitiesAdapter(
    private val onActivityClick: (RecentActivity) -> Unit
) : ListAdapter<RecentActivity, RecentActivitiesAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemRecentActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ActivityViewHolder(
        private val binding: ItemRecentActivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: RecentActivity) {
            binding.apply {
                tvActivityTitle.text = activity.title
                tvActivityDescription.text = activity.description
                tvUserName.text = activity.userName
                
                // Set timestamp
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                val now = Calendar.getInstance()
                val activityTime = Calendar.getInstance().apply {
                    time = activity.timestamp
                }
                
                tvTimestamp.text = if (now.get(Calendar.DAY_OF_YEAR) == activityTime.get(Calendar.DAY_OF_YEAR) &&
                    now.get(Calendar.YEAR) == activityTime.get(Calendar.YEAR)) {
                    timeFormat.format(activity.timestamp)
                } else {
                    dateFormat.format(activity.timestamp)
                }
                
                // Set activity icon and color based on type
                val (iconRes, colorRes) = when (activity.type) {
                    ActivityType.TASK_CREATED -> Pair(R.drawable.ic_add_task, R.color.activity_task_created)
                    ActivityType.TASK_UPDATED -> Pair(R.drawable.ic_edit, R.color.activity_task_updated)
                    ActivityType.TASK_COMPLETED -> Pair(R.drawable.ic_check_circle, R.color.activity_task_completed)
                    ActivityType.TASK_ASSIGNED -> Pair(R.drawable.ic_person_add, R.color.activity_task_assigned)
                    ActivityType.MESSAGE_SENT -> Pair(R.drawable.ic_message, R.color.activity_message_sent)
                    ActivityType.USER_JOINED -> Pair(R.drawable.ic_person_add, R.color.activity_user_joined)
                    ActivityType.COMMENT_ADDED -> Pair(R.drawable.ic_comment, R.color.activity_comment_added)
                    ActivityType.FILE_UPLOADED -> Pair(R.drawable.ic_attachment, R.color.activity_file_uploaded)
                    ActivityType.DEADLINE_APPROACHING -> Pair(R.drawable.ic_schedule, R.color.activity_deadline_approaching)
                }
                
                ivActivityIcon.setImageResource(iconRes)
                ivActivityIcon.setColorFilter(ContextCompat.getColor(root.context, colorRes))
                
                // Set background color for icon
                val backgroundColor = when (activity.type) {
                    ActivityType.TASK_CREATED -> R.color.activity_task_created_background
                    ActivityType.TASK_UPDATED -> R.color.activity_task_updated_background
                    ActivityType.TASK_COMPLETED -> R.color.activity_task_completed_background
                    ActivityType.TASK_ASSIGNED -> R.color.activity_task_assigned_background
                    ActivityType.MESSAGE_SENT -> R.color.activity_message_sent_background
                    ActivityType.USER_JOINED -> R.color.activity_user_joined_background
                    ActivityType.COMMENT_ADDED -> R.color.activity_comment_added_background
                    ActivityType.FILE_UPLOADED -> R.color.activity_file_uploaded_background
                    ActivityType.DEADLINE_APPROACHING -> R.color.activity_deadline_approaching_background
                }
                
                cardActivityIcon.setCardBackgroundColor(
                    ContextCompat.getColor(root.context, backgroundColor)
                )
                
                // Load user avatar
                activity.userAvatar?.let { avatarUrl ->
                    com.bumptech.glide.Glide.with(root.context)
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle)
                        .circleCrop()
                        .into(ivUserAvatar)
                } ?: run {
                    ivUserAvatar.setImageResource(R.drawable.ic_account_circle)
                }
                
                // Set click listener
                root.setOnClickListener {
                    onActivityClick(activity)
                }
                
                // Special handling for task-related activities
                if (activity.taskId != null && activity.taskTitle != null) {
                    tvActivityDescription.text = "${activity.description} - ${activity.taskTitle}"
                }
                
                // Add time-based styling
                val timeDiff = System.currentTimeMillis() - activity.timestamp.time
                val hoursAgo = timeDiff / (1000 * 60 * 60)
                
                if (hoursAgo < 1) {
                    // Recent activity - make it more prominent
                    tvActivityTitle.setTextColor(ContextCompat.getColor(root.context, R.color.text_primary))
                    root.alpha = 1.0f
                } else if (hoursAgo < 24) {
                    // Today's activity
                    tvActivityTitle.setTextColor(ContextCompat.getColor(root.context, R.color.text_primary))
                    root.alpha = 0.9f
                } else {
                    // Older activity
                    tvActivityTitle.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                    root.alpha = 0.7f
                }
            }
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<RecentActivity>() {
        override fun areItemsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
            return oldItem == newItem
        }
    }
}
