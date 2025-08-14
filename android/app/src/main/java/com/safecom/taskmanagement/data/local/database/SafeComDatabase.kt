package com.safecom.taskmanagement.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.safecom.taskmanagement.data.local.dao.MessageDao
import com.safecom.taskmanagement.data.local.dao.TaskDao
import com.safecom.taskmanagement.data.local.dao.UserDao
import com.safecom.taskmanagement.data.local.entity.ConversationEntity
import com.safecom.taskmanagement.data.local.entity.MessageEntity
import com.safecom.taskmanagement.data.local.entity.TaskEntity
import com.safecom.taskmanagement.data.local.entity.UserEntity
import com.safecom.taskmanagement.data.local.converter.Converters

@Database(
    entities = [
        TaskEntity::class,
        UserEntity::class,
        MessageEntity::class,
        ConversationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SafeComDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    
    companion object {
        @Volatile
        private var INSTANCE: SafeComDatabase? = null
        
        fun getDatabase(context: Context): SafeComDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafeComDatabase::class.java,
                    "safecom_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
