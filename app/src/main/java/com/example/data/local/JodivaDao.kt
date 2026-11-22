package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface JodivaDao {

    // Users
    @Query("SELECT * FROM users ORDER BY points DESC")
    fun observeAllUsers(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun observeUserById(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserProfileEntity?

    @Query("SELECT * FROM users WHERE LOWER(emailOrPhone) = LOWER(:credential) OR LOWER(username) = LOWER(:credential) LIMIT 1")
    suspend fun findUserByCredential(credential: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsersIfAbsent(users: List<UserProfileEntity>)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    // Posts & Reels
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun observeAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE postType = 'REEL' ORDER BY createdAt DESC")
    fun observeReels(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun observePostsByAuthor(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSavedByMe = 1 ORDER BY createdAt DESC")
    fun observeSavedPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: Long)

    @Query("UPDATE posts SET caption = :newCaption, hashtags = :newHashtags WHERE id = :postId")
    suspend fun updatePostCaption(postId: Long, newCaption: String, newHashtags: String)

    // Comments
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun observeCommentsForPost(postId: Long): Flow<List<CommentEntity>>

    @Insert
    suspend fun insertComment(comment: CommentEntity)

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementPostCommentCount(postId: Long)

    // Stories & Highlights
    @Query("SELECT * FROM stories WHERE expiresAt > :now OR isHighlight = 1 ORDER BY createdAt DESC")
    fun observeActiveStoriesAndHighlights(now: Long): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE authorId = :userId AND isHighlight = 1 ORDER BY createdAt DESC")
    fun observeHighlightsForUser(userId: String): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET reactionCount = reactionCount + 1 WHERE id = :storyId")
    suspend fun reactToStory(storyId: Long)

    @Query("UPDATE stories SET isHighlight = :isHighlight WHERE id = :storyId")
    suspend fun setStoryHighlight(storyId: Long, isHighlight: Boolean)

    // Follow Relations
    @Query("SELECT * FROM follow_relations WHERE followerId = :myUserId")
    fun observeMyFollowRelations(myUserId: String): Flow<List<FollowRelationEntity>>

    @Query("SELECT * FROM follow_relations WHERE targetUserId = :myUserId AND status = 'PENDING'")
    fun observeIncomingFollowRequests(myUserId: String): Flow<List<FollowRelationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFollowRelation(relation: FollowRelationEntity)

    @Query("DELETE FROM follow_relations WHERE followerId = :followerId AND targetUserId = :targetUserId")
    suspend fun deleteFollowRelation(followerId: String, targetUserId: String)

    // Chat Threads & Messages
    @Query("SELECT * FROM chat_threads ORDER BY updatedAt DESC")
    fun observeChatThreads(): Flow<List<ChatThreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChatThread(thread: ChatThreadEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatThreads(threads: List<ChatThreadEntity>)

    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY createdAt ASC")
    fun observeMessagesForThread(threadId: String): Flow<List<ChatMessageEntity>>

    @Insert
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_messages SET reactionEmoji = :emoji WHERE id = :messageId")
    suspend fun setMessageReaction(messageId: Long, emoji: String?)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteChatMessage(messageId: Long)

    @Query("UPDATE chat_threads SET unreadCount = 0 WHERE threadId = :threadId")
    suspend fun markThreadRead(threadId: String)

    // Quiz Scores & Daily Missions
    @Query("SELECT * FROM quiz_scores WHERE userId = :userId ORDER BY playedAt DESC")
    fun observeQuizHistory(userId: String): Flow<List<QuizScoreEntity>>

    @Insert
    suspend fun insertQuizScore(score: QuizScoreEntity)

    @Query("SELECT * FROM daily_missions ORDER BY isCompleted ASC, rewardPoints DESC")
    fun observeDailyMissions(): Flow<List<DailyMissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyMissions(missions: List<DailyMissionEntity>)

    @Query("SELECT * FROM daily_missions WHERE id = :missionId LIMIT 1")
    suspend fun getMissionById(missionId: String): DailyMissionEntity?

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Insert
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // Login Activity & Devices
    @Query("SELECT * FROM login_activity WHERE userId = :userId ORDER BY timestamp DESC")
    fun observeLoginActivity(userId: String): Flow<List<LoginActivityEntity>>

    @Insert
    suspend fun insertLoginActivity(activity: LoginActivityEntity)

    @Query("DELETE FROM login_activity WHERE userId = :userId AND isCurrentSession = 0")
    suspend fun logoutOtherDevices(userId: String)

    @Query("DELETE FROM login_activity WHERE userId = :userId")
    suspend fun clearAllSessions(userId: String)

    // Support Tickets & Reports
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun observeSupportTickets(): Flow<List<SupportTicketEntity>>

    @Insert
    suspend fun insertSupportTicket(ticket: SupportTicketEntity)

    @Insert
    suspend fun insertContentReport(report: ContentReportEntity)
}
