package com.safecom.taskmanagement.data.repository

import com.safecom.taskmanagement.data.local.dao.MessageDao
import com.safecom.taskmanagement.data.mappers.*
import com.safecom.taskmanagement.data.remote.api.MessageApiService
import com.safecom.taskmanagement.domain.model.Conversation
import com.safecom.taskmanagement.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messageApiService: MessageApiService,
    private val messageDao: MessageDao
) {

    suspend fun getAllConversations(): List<Conversation> {
        return try {
            val apiConversations = messageApiService.getAllConversations()
            messageDao.insertConversations(apiConversations.map { it.toEntity() })
            apiConversations.map { it.toDomainModel() }
        } catch (e: Exception) {
            messageDao.getAllConversations().map { it.toDomainModel() }
        }
    }

    suspend fun getConversationById(conversationId: String): Conversation? {
        return try {
            val apiConversation = messageApiService.getConversationById(conversationId)
            messageDao.insertConversation(apiConversation.toEntity())
            apiConversation.toDomainModel()
        } catch (e: Exception) {
            messageDao.getConversationById(conversationId)?.toDomainModel()
        }
    }

    suspend fun getMessagesByConversation(conversationId: String): List<Message> {
        return try {
            val apiMessages = messageApiService.getMessagesByConversation(conversationId)
            messageDao.insertMessages(apiMessages.map { it.toEntity() })
            apiMessages.map { it.toDomainModel() }
        } catch (e: Exception) {
            messageDao.getMessagesByConversation(conversationId).map { it.toDomainModel() }
        }
    }

    suspend fun sendMessage(message: Message): Result<Message> {
        return try {
            val sentMessage = messageApiService.sendMessage(message.toDto())
            messageDao.insertMessage(sentMessage.toEntity())
            Result.success(sentMessage.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markMessageAsRead(messageId: String): Result<Unit> {
        return try {
            messageApiService.markMessageAsRead(messageId)
            messageDao.markMessageAsRead(messageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markConversationAsRead(conversationId: String): Result<Unit> {
        return try {
            messageApiService.markConversationAsRead(conversationId)
            messageDao.markConversationAsRead(conversationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            messageApiService.deleteMessage(messageId)
            messageDao.deleteMessage(messageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMessages(query: String): List<Message> {
        return try {
            val apiMessages = messageApiService.searchMessages(query)
            apiMessages.map { it.toDomainModel() }
        } catch (e: Exception) {
            messageDao.searchMessages("%$query%").map { it.toDomainModel() }
        }
    }

    suspend fun getUnreadMessagesCount(): Int {
        return try {
            messageApiService.getUnreadMessagesCount()
        } catch (e: Exception) {
            messageDao.getUnreadMessagesCount()
        }
    }

    fun getConversationsFlow(): Flow<List<Conversation>> = flow {
        try {
            val conversations = getAllConversations()
            emit(conversations)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getMessagesFlow(conversationId: String): Flow<List<Message>> = flow {
        try {
            val messages = getMessagesByConversation(conversationId)
            emit(messages)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
