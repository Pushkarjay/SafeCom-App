package com.safecom.taskmanagement.data.remote.api

import com.safecom.taskmanagement.data.remote.dto.*
import retrofit2.http.*

interface TaskApiService {

    @GET("tasks")
    suspend fun getAllTasks(): List<TaskDto>

    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") taskId: String): TaskDto

    @POST("tasks")
    suspend fun createTask(@Body task: CreateTaskDto): TaskDto

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") taskId: String, @Body task: UpdateTaskDto): TaskDto

    @PATCH("tasks/{id}/status")
    suspend fun updateTaskStatus(@Path("id") taskId: String, @Body status: UpdateTaskStatusDto): TaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: String)

    @GET("tasks/status/{status}")
    suspend fun getTasksByStatus(@Path("status") status: String): List<TaskDto>

    @GET("tasks/assignee/{userId}")
    suspend fun getTasksByAssignee(@Path("userId") userId: String): List<TaskDto>

    @GET("tasks/search")
    suspend fun searchTasks(@Query("q") query: String): List<TaskDto>

    @GET("tasks/count")
    suspend fun getTasksCount(): Int

    @GET("tasks/count/status/{status}")
    suspend fun getTasksCountByStatus(@Path("status") status: String): Int

    @GET("activities/recent")
    suspend fun getRecentActivities(@Query("limit") limit: Int): List<RecentActivityDto>

    @POST("tasks/{id}/comments")
    suspend fun addTaskComment(@Path("id") taskId: String, @Body comment: CreateCommentDto): TaskCommentDto

    @GET("tasks/{id}/comments")
    suspend fun getTaskComments(@Path("id") taskId: String): List<TaskCommentDto>

    @POST("tasks/{id}/attachments")
    suspend fun uploadTaskAttachment(@Path("id") taskId: String, @Body attachment: UploadAttachmentDto): AttachmentDto
}

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto

    @POST("auth/logout")
    suspend fun logout()

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): TokenResponseDto

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto)

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto)

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto)
}

interface UserApiService {

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: String): UserDto

    @PUT("users/profile")
    suspend fun updateProfile(@Body profile: UpdateProfileDto): UserDto

    @POST("users/upload-avatar")
    suspend fun uploadProfileImage(@Body image: UploadImageDto): String

    @DELETE("users/avatar")
    suspend fun removeProfileImage()

    @GET("users/statistics")
    suspend fun getUserTaskStatistics(): UserTaskStatisticsDto

    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): List<UserDto>
}

interface MessageApiService {

    @GET("conversations")
    suspend fun getAllConversations(): List<ConversationDto>

    @GET("conversations/{id}")
    suspend fun getConversationById(@Path("id") conversationId: String): ConversationDto

    @GET("conversations/{id}/messages")
    suspend fun getMessagesByConversation(@Path("id") conversationId: String): List<MessageDto>

    @POST("messages")
    suspend fun sendMessage(@Body message: SendMessageDto): MessageDto

    @PATCH("messages/{id}/read")
    suspend fun markMessageAsRead(@Path("id") messageId: String)

    @PATCH("conversations/{id}/read")
    suspend fun markConversationAsRead(@Path("id") conversationId: String)

    @DELETE("messages/{id}")
    suspend fun deleteMessage(@Path("id") messageId: String)

    @GET("messages/search")
    suspend fun searchMessages(@Query("q") query: String): List<MessageDto>

    @GET("messages/unread-count")
    suspend fun getUnreadMessagesCount(): Int

    @POST("conversations")
    suspend fun createConversation(@Body conversation: CreateConversationDto): ConversationDto
}
