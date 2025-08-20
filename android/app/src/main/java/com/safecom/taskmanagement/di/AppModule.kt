package com.safecom.taskmanagement.di

import android.content.Context
import androidx.room.Room
import com.safecom.taskmanagement.BuildConfig
import com.safecom.taskmanagement.data.local.database.SafeComDatabase
import com.safecom.taskmanagement.data.local.dao.MessageDao
import com.safecom.taskmanagement.data.local.dao.TaskDao
import com.safecom.taskmanagement.data.local.dao.UserDao
import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import com.safecom.taskmanagement.data.remote.api.AuthApiService
import com.safecom.taskmanagement.data.remote.api.MessageApiService
import com.safecom.taskmanagement.data.remote.api.TaskApiService
import com.safecom.taskmanagement.data.remote.api.CompatTaskApiService
import com.safecom.taskmanagement.data.remote.api.UserApiService
import com.safecom.taskmanagement.data.remote.interceptor.AuthInterceptor
import com.safecom.taskmanagement.data.repository.AuthRepository
import com.safecom.taskmanagement.data.repository.MessageRepository
import com.safecom.taskmanagement.data.repository.TaskRepository
import com.safecom.taskmanagement.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SafeComDatabase {
        return Room.databaseBuilder(
            context,
            SafeComDatabase::class.java,
            "safecom_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideTaskDao(database: SafeComDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideUserDao(database: SafeComDatabase): UserDao = database.userDao()

    @Provides
    fun provideMessageDao(database: SafeComDatabase): MessageDao = database.messageDao()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferences: UserPreferences): AuthInterceptor {
        return AuthInterceptor(userPreferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL) // Now uses centralized configuration
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskApiService(retrofit: Retrofit): TaskApiService {
        return retrofit.create(TaskApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCompatTaskApiService(retrofit: Retrofit): CompatTaskApiService {
        return retrofit.create(CompatTaskApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMessageApiService(retrofit: Retrofit): MessageApiService {
        return retrofit.create(MessageApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskApiService: TaskApiService,
        compatTaskApiService: CompatTaskApiService,
        taskDao: TaskDao,
        userPreferences: UserPreferences
    ): TaskRepository {
        return TaskRepository(taskApiService, compatTaskApiService, taskDao, userPreferences)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userApiService: UserApiService,
        userDao: UserDao,
        userPreferences: UserPreferences
    ): UserRepository {
        return UserRepository(userApiService, userDao, userPreferences)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        messageApiService: MessageApiService,
        messageDao: MessageDao
    ): MessageRepository {
        return MessageRepository(messageApiService, messageDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApiService: AuthApiService,
        userPreferences: UserPreferences,
        userRepository: UserRepository
    ): AuthRepository {
        return AuthRepository(authApiService, userPreferences, userRepository)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetTasksUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetTasksUseCase(taskRepository)

    @Provides
    fun provideGetTaskByIdUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetTaskByIdUseCase(taskRepository)

    @Provides
    fun provideCreateTaskUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.CreateTaskUseCase(taskRepository)

    @Provides
    fun provideUpdateTaskUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.UpdateTaskUseCase(taskRepository)

    @Provides
    fun provideUpdateTaskStatusUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.UpdateTaskStatusUseCase(taskRepository)

    @Provides
    fun provideDeleteTaskUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.DeleteTaskUseCase(taskRepository)

    @Provides
    fun provideSearchTasksUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.SearchTasksUseCase(taskRepository)

    @Provides
    fun provideGetTasksByStatusUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetTasksByStatusUseCase(taskRepository)

    @Provides
    fun provideGetUserTasksUseCase(taskRepository: TaskRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetUserTasksUseCase(taskRepository)

    @Provides
    fun provideGetConversationsUseCase(messageRepository: MessageRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetConversationsUseCase(messageRepository)

    @Provides
    fun provideGetMessagesUseCase(messageRepository: MessageRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetMessagesUseCase(messageRepository)

    @Provides
    fun provideSendMessageUseCase(messageRepository: MessageRepository) = 
        com.safecom.taskmanagement.domain.usecase.SendMessageUseCase(messageRepository)

    @Provides
    fun provideMarkMessageAsReadUseCase(messageRepository: MessageRepository) = 
        com.safecom.taskmanagement.domain.usecase.MarkMessageAsReadUseCase(messageRepository)

    @Provides
    fun provideSearchMessagesUseCase(messageRepository: MessageRepository) = 
        com.safecom.taskmanagement.domain.usecase.SearchMessagesUseCase(messageRepository)

    @Provides
    fun provideGetUnreadMessageCountUseCase(messageRepository: MessageRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetUnreadMessageCountUseCase(messageRepository)

    @Provides
    fun provideGetUserProfileUseCase(userRepository: UserRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetUserProfileUseCase(userRepository)

    @Provides
    fun provideUpdateUserProfileUseCase(userRepository: UserRepository) = 
        com.safecom.taskmanagement.domain.usecase.UpdateUserProfileUseCase(userRepository)

    @Provides
    fun provideUpdateUserSettingsUseCase(userRepository: UserRepository) = 
        com.safecom.taskmanagement.domain.usecase.UpdateUserSettingsUseCase(userRepository)

    @Provides
    fun provideGetUserTaskStatisticsUseCase(userRepository: UserRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetUserTaskStatisticsUseCase(userRepository)

    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.LoginUseCase(authRepository)

    @Provides
    fun provideLoginWithBiometricUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.LoginWithBiometricUseCase(authRepository)

    @Provides
    fun provideRegisterUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.RegisterUseCase(authRepository)

    @Provides
    fun provideLogoutUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.LogoutUseCase(authRepository)

    @Provides
    fun provideForgotPasswordUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.ForgotPasswordUseCase(authRepository)

    @Provides
    fun provideResetPasswordUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.ResetPasswordUseCase(authRepository)

    @Provides
    fun provideChangePasswordUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.ChangePasswordUseCase(authRepository)

    @Provides
    fun provideRefreshTokenUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.RefreshTokenUseCase(authRepository)

    @Provides
    fun provideIsLoggedInUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.IsLoggedInUseCase(authRepository)

    @Provides
    fun provideGetCurrentAuthTokenUseCase(authRepository: AuthRepository) = 
        com.safecom.taskmanagement.domain.usecase.GetCurrentAuthTokenUseCase(authRepository)
}
