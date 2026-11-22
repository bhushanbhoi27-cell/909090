package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatThreadEntity
import com.example.data.model.CommentEntity
import com.example.data.model.ContentReportEntity
import com.example.data.model.DailyMissionEntity
import com.example.data.model.FollowRelationEntity
import com.example.data.model.LoginActivityEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PostEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.StoryEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        FollowRelationEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class,
        QuizScoreEntity::class,
        DailyMissionEntity::class,
        NotificationEntity::class,
        LoginActivityEntity::class,
        SupportTicketEntity::class,
        ContentReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JodivaDatabase : RoomDatabase() {
    abstract fun jodivaDao(): JodivaDao

    companion object {
        @Volatile
        private var INSTANCE: JodivaDatabase? = null

        fun getInstance(context: Context): JodivaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JodivaDatabase::class.java,
                    "jodiva_local_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
