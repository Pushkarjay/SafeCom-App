package com.safecom.taskmanagement.ui.messages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.databinding.ItemConversationBinding
import com.safecom.taskmanagement.domain.model.Conversation
import java.text.SimpleDateFormat
import java.util.*

class ConversationsAdapter(
    private val onConversationClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationsAdapter.ConversationViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConversationViewHolder(
        private val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.apply {
                // Set participant name (group name for groups)
                tvParticipantName.text = if (conversation.isGroup) {
                    conversation.groupName ?: "Group Chat"
                } else {
                    conversation.participantName
                }
                
                // Set last message
                tvLastMessage.text = conversation.lastMessage
                
                // Set timestamp
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val now = Calendar.getInstance()
                val messageTime = Calendar.getInstance().apply {
                    time = conversation.lastMessageTime
                }
                
                tvTimestamp.text = if (now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) &&
                    now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)) {
                    timeFormat.format(conversation.lastMessageTime)
                } else {
                    dateFormat.format(conversation.lastMessageTime)
                }
                
                // Set profile image
                val avatarUrl = if (conversation.isGroup) {
                    conversation.groupAvatar
                } else {
                    conversation.participantAvatar
                }
                
                Glide.with(root.context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_account_circle)
                    .error(R.drawable.ic_account_circle)
                    .circleCrop()
                    .into(ivParticipantAvatar)
                
                // Set unread count
                if (conversation.unreadCount > 0) {
                    tvUnreadCount.visibility = android.view.View.VISIBLE
                    tvUnreadCount.text = if (conversation.unreadCount > 99) {
                        "99+"
                    } else {
                        conversation.unreadCount.toString()
                    }
                    
                    // Make text bold for unread conversations
                    tvParticipantName.setTypeface(null, android.graphics.Typeface.BOLD)
                    tvLastMessage.setTypeface(null, android.graphics.Typeface.BOLD)
                    tvLastMessage.setTextColor(ContextCompat.getColor(root.context, R.color.text_primary))
                } else {
                    tvUnreadCount.visibility = android.view.View.GONE
                    tvParticipantName.setTypeface(null, android.graphics.Typeface.NORMAL)
                    tvLastMessage.setTypeface(null, android.graphics.Typeface.NORMAL)
                    tvLastMessage.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                }
                
                // Set online status indicator
                ivOnlineStatus.visibility = if (conversation.isGroup) {
                    android.view.View.GONE
                } else {
                    // TODO: Implement online status logic
                    android.view.View.VISIBLE
                }
                
                // Set muted indicator
                ivMutedIndicator.visibility = if (conversation.isMuted) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
                
                // Set archived indicator
                if (conversation.isArchived) {
                    root.alpha = 0.6f
                } else {
                    root.alpha = 1.0f
                }
                
                // Set click listener
                root.setOnClickListener {
                    onConversationClick(conversation)
                }
                
                // Long click for additional actions
                root.setOnLongClickListener {
                    showConversationActions(conversation)
                    true
                }
            }
        }
        
        private fun showConversationActions(conversation: Conversation) {
            // TODO: Implement context menu for archive, mute, delete actions
            val context = binding.root.context
            val options = mutableListOf<String>()
            
            if (conversation.isMuted) {
                options.add("Unmute")
            } else {
                options.add("Mute")
            }
            
            if (conversation.isArchived) {
                options.add("Unarchive")
            } else {
                options.add("Archive")
            }
            
            options.add("Delete")
            
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setItems(options.toTypedArray()) { _, which ->
                    when (options[which]) {
                        "Mute" -> {
                            // TODO: Implement mute conversation
                        }
                        "Unmute" -> {
                            // TODO: Implement unmute conversation
                        }
                        "Archive" -> {
                            // TODO: Implement archive conversation
                        }
                        "Unarchive" -> {
                            // TODO: Implement unarchive conversation
                        }
                        "Delete" -> {
                            // TODO: Implement delete conversation with confirmation
                        }
                    }
                }
                .show()
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}
