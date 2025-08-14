package com.safecom.taskmanagement.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safecom.taskmanagement.data.repository.MessageRepository
import com.safecom.taskmanagement.domain.model.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _conversationsState = MutableStateFlow<ConversationsState>(ConversationsState.Loading)
    val conversationsState: StateFlow<ConversationsState> = _conversationsState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private var allConversations = listOf<Conversation>()

    fun loadConversations() {
        viewModelScope.launch {
            try {
                _conversationsState.value = ConversationsState.Loading
                
                val conversations = messageRepository.getAllConversations()
                allConversations = conversations
                _conversationsState.value = ConversationsState.Success(conversations)
                
                // Update unread count
                val unreadCount = conversations.sumOf { it.unreadCount }
                _unreadCount.value = unreadCount
                
            } catch (e: Exception) {
                _conversationsState.value = ConversationsState.Error(
                    e.message ?: "Failed to load conversations"
                )
            }
        }
    }

    fun refreshConversations() {
        loadConversations()
    }

    fun searchConversations(query: String) {
        if (query.isEmpty()) {
            _conversationsState.value = ConversationsState.Success(allConversations)
            return
        }

        val filteredConversations = allConversations.filter { conversation ->
            conversation.participantName.contains(query, ignoreCase = true) ||
            conversation.lastMessage.contains(query, ignoreCase = true)
        }

        _conversationsState.value = ConversationsState.Success(filteredConversations)
    }

    fun markConversationAsRead(conversationId: String) {
        viewModelScope.launch {
            try {
                messageRepository.markConversationAsRead(conversationId)
                loadConversations() // Refresh to update unread counts
            } catch (e: Exception) {
                // Handle error silently for read status
            }
        }
    }
}

sealed class ConversationsState {
    object Loading : ConversationsState()
    data class Success(val conversations: List<Conversation>) : ConversationsState()
    data class Error(val message: String) : ConversationsState()
}
