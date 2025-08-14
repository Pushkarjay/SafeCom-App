package com.safecom.taskmanagement.data.local.dao

import androidx.room.*
import com.safecom.taskmanagement.data.local.entity.ConversationEntity
import com.safecom.taskmanagement.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    
    // Message operations
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesByConversation(conversationId: String): List<MessageEntity>
    
    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?
    
    @Query("SELECT * FROM messages WHERE content LIKE :query ORDER BY timestamp DESC")
    suspend fun searchMessages(query: String): List<MessageEntity>
    
    @Query("SELECT COUNT(*) FROM messages WHERE isRead = 0 AND receiverId = :userId")
    suspend fun getUnreadMessagesCount(userId: String = ""): Int
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesFlow(conversationId: String): Flow<List<MessageEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
    
    @Update
    suspend fun updateMessage(message: MessageEntity)
    
    @Query("UPDATE messages SET isRead = 1, readAt = :readAt WHERE id = :messageId")
    suspend fun markMessageAsRead(messageId: String, readAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE messages SET isRead = 1, readAt = :readAt WHERE conversationId = :conversationId AND isRead = 0")
    suspend fun markConversationAsRead(conversationId: String, readAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteMessage(message: MessageEntity)
    
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
    
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)
    
    // Conversation operations
    @Query("SELECT * FROM conversations ORDER BY lastMessageTime DESC")
    suspend fun getAllConversations(): List<ConversationEntity>
    
    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationEntity?
    
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY lastMessageTime DESC")
    suspend fun getActiveConversations(): List<ConversationEntity>
    
    @Query("SELECT * FROM conversations WHERE isArchived = 1 ORDER BY lastMessageTime DESC")
    suspend fun getArchivedConversations(): List<ConversationEntity>
    
    @Query("SELECT * FROM conversations ORDER BY lastMessageTime DESC")
    fun getConversationsFlow(): Flow<List<ConversationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)
    
    @Update
    suspend fun updateConversation(conversation: ConversationEntity)
    
    @Query("UPDATE conversations SET lastMessage = :lastMessage, lastMessageTime = :lastMessageTime, unreadCount = :unreadCount WHERE id = :conversationId")
    suspend fun updateConversationLastMessage(
        conversationId: String,
        lastMessage: String,
        lastMessageTime: Long,
        unreadCount: Int
    )
    
    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :conversationId")
    suspend fun markConversationAsRead(conversationId: String)
    
    @Query("UPDATE conversations SET isArchived = :isArchived WHERE id = :conversationId")
    suspend fun updateConversationArchiveStatus(conversationId: String, isArchived: Boolean)
    
    @Query("UPDATE conversations SET isMuted = :isMuted WHERE id = :conversationId")
    suspend fun updateConversationMuteStatus(conversationId: String, isMuted: Boolean)
    
    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)
    
    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)
    
    @Query("DELETE FROM conversations")
    suspend fun deleteAllConversations()
    
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}
