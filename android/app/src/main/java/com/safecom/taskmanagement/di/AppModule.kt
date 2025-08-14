package com.safecom.taskmanagement.di

import android.content.Context
import androidx.room.Room
import com.safecom.taskmanagement.data.local.database.SafeComDatabase
import com.safecom.taskmanagement.data.local.dao.MessageDao
import com.safecom.taskmanagement.data.local.dao.TaskDao
import com.safecom.taskmanagement.data.local.dao.UserDao
import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import com.safecom.taskmanagement.data.remote.api.AuthApiService
import com.safecom.taskmanagement.data.remote.api.MessageApiService
import com.safecom.taskmanagement.data.remote.api.TaskApiService
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
            .baseUrl("https://api.safecom.com/v1/") // Replace with actual API base URL
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
        taskDao: TaskDao
    ): TaskRepository {
        return TaskRepository(taskApiService, taskDao)
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
