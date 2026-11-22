package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class BadgeTier(val label: String, val minPoints: Int) {
    BRONZE("Bronze", 0),
    SILVER("Silver", 100),
    GOLD("Gold", 300),
    DIAMOND("Diamond", 700),
    LEGEND("Legend", 1500);

    companion object {
        fun forPoints(points: Int): BadgeTier {
            return entries.lastOrNull { points >= it.minPoints } ?: BRONZE
        }
    }
}

@Entity(tableName = "users")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val jodivaId: String,
    val username: String,
    val displayName: String,
    val emailOrPhone: String,
    val passwordHash: String,
    val bio: String = "",
    val country: String = "India",
    val stateProvince: String = "Maharashtra",
    val city: String = "Mumbai",
    val preferredLanguage: String = "English",
    val gender: String? = null,
    val religion: String? = null,
    val isPrivateAccount: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val points: Int = 50,
    val streakDays: Int = 1,
    val badgeTier: String = "Bronze",
    val isIdentityVerified: Boolean = false,
    val hasAchievementTick: Boolean = false,
    val subscriptionTier: String = "Free",
    val avatarUri: String? = null,
    val twoFactorEnabled: Boolean = false,
    val isOnlineVisible: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorCountry: String,
    val authorBadgeTier: String,
    val authorHasAchievementTick: Boolean,
    val authorIsPrivate: Boolean,
    val postType: String, // PHOTO, TEXT, MUSIC, REEL
    val caption: String,
    val hashtags: String,
    val musicTrack: String? = null,
    val imageUri: String? = null,
    val drawableResName: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false,
    val language: String = "English",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorName: String,
    val authorHandle: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: String,
    val authorName: String,
    val authorCountry: String,
    val storyType: String, // PHOTO, TEXT, MUSIC
    val textContent: String,
    val musicTitle: String? = null,
    val backgroundColorHex: Long = 0xFF0A5C68,
    val imageUri: String? = null,
    val drawableResName: String? = null,
    val viewersCount: Int = 1,
    val reactionCount: Int = 0,
    val isHighlight: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86_400_000L
)

@Entity(tableName = "follow_relations", primaryKeys = ["followerId", "targetUserId"])
data class FollowRelationEntity(
    val followerId: String,
    val targetUserId: String,
    val status: String // FOLLOWING, PENDING, BLOCKED
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val threadId: String,
    val title: String,
    val subtitle: String,
    val isGroup: Boolean,
    val participantCountry: String,
    val isOnline: Boolean,
    val isEncrypted: Boolean = true,
    val lastMessagePreview: String,
    val unreadCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val messageType: String = "TEXT", // TEXT, VOICE, IMAGE
    val voiceDurationSec: Int = 0,
    val reactionEmoji: String? = null,
    val deliveryStatus: String = "READ", // SENT, DELIVERED, READ
    val isFromMe: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val category: String,
    val score: Int,
    val totalQuestions: Int,
    val pointsEarned: Int,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_missions")
data class DailyMissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val progress: Int,
    val target: Int,
    val isCompleted: Boolean
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val category: String, // SOCIAL, QUIZ, MISSION, SECURITY, LIVE
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "login_activity")
data class LoginActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val deviceName: String,
    val location: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isCurrentSession: Boolean = true
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketNumber: String,
    val subject: String,
    val category: String,
    val description: String,
    val status: String = "Open",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ContentReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // POST, REEL, USER, LIVE
    val targetId: String,
    val reason: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class NewsArticle(
    val id: String,
    val category: String,
    val country: String,
    val headline: String,
    val summary: String,
    val source: String,
    val publishedAgo: String
)

data class QuizQuestion(
    val id: String,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class LiveStreamRoom(
    val id: String,
    val hostName: String,
    val hostHandle: String,
    val hostCountry: String,
    val title: String,
    val category: String,
    val viewerCount: Int,
    val language: String,
    val initialComments: List<Pair<String, String>>
)

data class LearningCourse(
    val id: String,
    val title: String,
    val category: String,
    val language: String,
    val lessonsCount: Int,
    val level: String,
    val summary: String
)

data class CareerOpportunity(
    val id: String,
    val title: String,
    val organization: String,
    val sector: String, // Government, Private, International, Internship
    val location: String,
    val deadline: String,
    val summary: String
)

object GlobalCatalog {
    val countries = listOf(
        "India", "United States", "United Kingdom", "Canada",
        "Japan", "Germany", "France", "Brazil", "Australia",
        "United Arab Emirates", "Singapore", "South Korea"
    )

    val languages = listOf(
        "English", "Hindi", "Marathi", "Spanish", "Portuguese",
        "French", "German", "Japanese", "Arabic", "Chinese"
    )

    val communities = listOf(
        "Global Travelers & Culture",
        "STEM & Competitive Exams",
        "Indie Music & Creators",
        "World Photography Guild",
        "Tech & Career Builders",
        "Maharashtra & India Heritage"
    )
}
