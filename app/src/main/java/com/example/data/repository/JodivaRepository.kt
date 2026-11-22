package com.example.data.repository

import com.example.data.ai.AiRepository
import com.example.data.ai.AiResult
import com.example.data.local.JodivaDao
import com.example.data.local.SessionPreferences
import com.example.data.local.UserPreferencesState
import com.example.data.model.BadgeTier
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
import com.example.data.model.ThemeMode
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import kotlin.random.Random

class JodivaRepository(
    private val dao: JodivaDao,
    private val prefs: SessionPreferences,
    private val aiRepository: AiRepository
) {
    val preferencesFlow: Flow<UserPreferencesState> = prefs.preferencesFlow
    val allUsers: Flow<List<UserProfileEntity>> = dao.observeAllUsers()
    val allPosts: Flow<List<PostEntity>> = dao.observeAllPosts()
    val reels: Flow<List<PostEntity>> = dao.observeReels()
    val savedPosts: Flow<List<PostEntity>> = dao.observeSavedPosts()
    val chatThreads: Flow<List<ChatThreadEntity>> = dao.observeChatThreads()
    val dailyMissions: Flow<List<DailyMissionEntity>> = dao.observeDailyMissions()
    val notifications: Flow<List<NotificationEntity>> = dao.observeNotifications()
    val supportTickets: Flow<List<SupportTicketEntity>> = dao.observeSupportTickets()

    fun observeUser(userId: String): Flow<UserProfileEntity?> = dao.observeUserById(userId)
    fun observeStories(): Flow<List<StoryEntity>> =
        dao.observeActiveStoriesAndHighlights(System.currentTimeMillis())

    fun observeHighlights(userId: String): Flow<List<StoryEntity>> =
        dao.observeHighlightsForUser(userId)

    fun observeComments(postId: Long): Flow<List<CommentEntity>> =
        dao.observeCommentsForPost(postId)

    fun observeFollowRelations(myUserId: String): Flow<List<FollowRelationEntity>> =
        dao.observeMyFollowRelations(myUserId)

    fun observeIncomingRequests(myUserId: String): Flow<List<FollowRelationEntity>> =
        dao.observeIncomingFollowRequests(myUserId)

    fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>> =
        dao.observeMessagesForThread(threadId)

    fun observeQuizHistory(userId: String): Flow<List<QuizScoreEntity>> =
        dao.observeQuizHistory(userId)

    fun observeLoginActivity(userId: String): Flow<List<LoginActivityEntity>> =
        dao.observeLoginActivity(userId)

    suspend fun ensureSeeded() {
        if (dao.getUserCount() > 0) return
        val now = System.currentTimeMillis()
        dao.insertUsersIfAbsent(JodivaSeedCatalog.starterUsers(now))
        dao.insertPosts(JodivaSeedCatalog.starterPosts(now))
        dao.insertStories(JodivaSeedCatalog.starterStories(now))
        dao.insertChatThreads(JodivaSeedCatalog.starterChatThreads(now))
        dao.insertChatMessages(JodivaSeedCatalog.starterChatMessages(now))
        dao.upsertDailyMissions(JodivaSeedCatalog.starterMissions())
        dao.insertNotifications(JodivaSeedCatalog.starterNotifications(now))
    }

    suspend fun registerUser(
        displayName: String,
        username: String,
        emailOrPhone: String,
        password: String,
        country: String,
        stateProvince: String,
        city: String,
        preferredLanguage: String,
        gender: String?,
        religion: String?,
        isPrivateAccount: Boolean
    ): Result<UserProfileEntity> {
        ensureSeeded()
        val cleanHandle = username.trim().lowercase().replace(" ", "_").removePrefix("@")
        if (cleanHandle.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters."))
        }
        val existing = dao.findUserByCredential(emailOrPhone.trim())
            ?: dao.findUserByCredential(cleanHandle)
        if (existing != null) {
            return Result.failure(IllegalStateException("An account with this email/phone or username already exists."))
        }

        val countryCode = country.take(2).uppercase()
        val jodivaId = "JDV-$countryCode-${Random.nextInt(100000, 999999)}"
        val userId = "usr_${System.currentTimeMillis()}_${Random.nextInt(100, 999)}"
        val user = UserProfileEntity(
            userId = userId,
            jodivaId = jodivaId,
            username = cleanHandle,
            displayName = displayName.trim().ifEmpty { cleanHandle },
            emailOrPhone = emailOrPhone.trim(),
            passwordHash = hashPassword(password),
            bio = "New on JODIVA from $city, $country • Connecting worldwide!",
            country = country,
            stateProvince = stateProvince.ifBlank { country },
            city = city.ifBlank { country },
            preferredLanguage = preferredLanguage,
            gender = gender?.takeIf { it.isNotBlank() },
            religion = religion?.takeIf { it.isNotBlank() },
            isPrivateAccount = isPrivateAccount,
            points = 50,
            streakDays = 1,
            badgeTier = "Bronze"
        )
        dao.upsertUser(user)
        dao.insertLoginActivity(
            LoginActivityEntity(
                userId = userId,
                deviceName = "Android Device (This Device)",
                location = "$city, $country",
                isCurrentSession = true
            )
        )
        dao.insertLoginActivity(
            LoginActivityEntity(
                userId = userId,
                deviceName = "JODIVA Web Browser Session",
                location = "$city, $country",
                timestamp = System.currentTimeMillis() - 86_400_000L,
                isCurrentSession = false
            )
        )
        dao.insertNotification(
            NotificationEntity(
                title = "Welcome to JODIVA, ${user.displayName}!",
                body = "Your unique ID is ${user.jodivaId}. Complete today's mission to earn +50 points.",
                category = "SOCIAL"
            )
        )
        prefs.setCurrentUserId(userId)
        prefs.setPreferredLanguage(preferredLanguage)
        return Result.success(user)
    }

    suspend fun loginUser(credential: String, password: String): Result<UserProfileEntity> {
        ensureSeeded()
        val found = dao.findUserByCredential(credential.trim().removePrefix("@"))
            ?: return Result.failure(IllegalArgumentException("No JODIVA account found for \"$credential\"."))
        if (found.passwordHash != hashPassword(password) && !found.passwordHash.startsWith("seed_hash_")) {
            return Result.failure(IllegalArgumentException("Incorrect password. Use Forgot Password to recover."))
        }
        dao.insertLoginActivity(
            LoginActivityEntity(
                userId = found.userId,
                deviceName = "Android Device (This Device)",
                location = "${found.city}, ${found.country}",
                isCurrentSession = true
            )
        )
        prefs.setCurrentUserId(found.userId)
        prefs.setPreferredLanguage(found.preferredLanguage)
        return Result.success(found)
    }

    suspend fun quickDemoSignIn(): UserProfileEntity {
        ensureSeeded()
        val user = dao.getUserById("usr_aarav_in") ?: dao.observeAllUsers().first().first()
        dao.insertLoginActivity(
            LoginActivityEntity(
                userId = user.userId,
                deviceName = "Android Device (This Device)",
                location = "${user.city}, ${user.country}",
                isCurrentSession = true
            )
        )
        prefs.setCurrentUserId(user.userId)
        prefs.setPreferredLanguage(user.preferredLanguage)
        return user
    }

    suspend fun recoverAccount(credential: String, newPassword: String): Result<String> {
        ensureSeeded()
        val found = dao.findUserByCredential(credential.trim().removePrefix("@"))
            ?: return Result.failure(IllegalArgumentException("No account matches \"$credential\"."))
        dao.upsertUser(found.copy(passwordHash = hashPassword(newPassword)))
        return Result.success("Password updated for @${found.username} (${found.jodivaId}).")
    }

    suspend fun logoutCurrentSession() {
        prefs.setCurrentUserId(null)
    }

    suspend fun logoutAllDevices(userId: String) {
        dao.clearAllSessions(userId)
        prefs.setCurrentUserId(null)
    }

    suspend fun logoutOtherDevices(userId: String) {
        dao.logoutOtherDevices(userId)
    }

    suspend fun updateProfile(updated: UserProfileEntity) {
        dao.upsertUser(updated)
        prefs.setPreferredLanguage(updated.preferredLanguage)
    }

    suspend fun setThemeMode(mode: ThemeMode) = prefs.setThemeMode(mode)
    suspend fun setPreferredLanguage(language: String, userId: String?) {
        prefs.setPreferredLanguage(language)
        if (userId != null) {
            dao.getUserById(userId)?.let { dao.upsertUser(it.copy(preferredLanguage = language)) }
        }
    }

    suspend fun setAutoTranslate(enabled: Boolean) = prefs.setAutoTranslate(enabled)
    suspend fun updateNotificationSettings(social: Boolean, quiz: Boolean, security: Boolean) =
        prefs.updateNotificationPrefs(social, quiz, security)

    suspend fun createPost(
        author: UserProfileEntity,
        postType: String,
        caption: String,
        hashtags: String,
        musicTrack: String?,
        imageUri: String?
    ) {
        val post = PostEntity(
            authorId = author.userId,
            authorName = author.displayName,
            authorHandle = author.username,
            authorCountry = author.country,
            authorBadgeTier = author.badgeTier,
            authorHasAchievementTick = author.hasAchievementTick,
            authorIsPrivate = author.isPrivateAccount,
            postType = postType,
            caption = caption.trim(),
            hashtags = hashtags.trim().ifEmpty { "#JODIVA #GlobalCommunity" },
            musicTrack = musicTrack?.takeIf { it.isNotBlank() },
            imageUri = imageUri,
            language = author.preferredLanguage
        )
        dao.insertPost(post)
        advanceMission(author.userId, "msn_share_post")
    }

    suspend fun toggleLikePost(post: PostEntity) {
        val nextLiked = !post.isLikedByMe
        val nextLikes = (post.likesCount + if (nextLiked) 1 else -1).coerceAtLeast(0)
        dao.updatePost(post.copy(isLikedByMe = nextLiked, likesCount = nextLikes))
    }

    suspend fun toggleSavePost(post: PostEntity) {
        dao.updatePost(post.copy(isSavedByMe = !post.isSavedByMe))
    }

    suspend fun editPost(postId: Long, newCaption: String, newHashtags: String) {
        dao.updatePostCaption(postId, newCaption.trim(), newHashtags.trim())
    }

    suspend fun deletePost(postId: Long) {
        dao.deletePostById(postId)
    }

    suspend fun addComment(postId: Long, author: UserProfileEntity, text: String) {
        if (text.isBlank()) return
        dao.insertComment(
            CommentEntity(
                postId = postId,
                authorName = author.displayName,
                authorHandle = author.username,
                text = text.trim()
            )
        )
        dao.incrementPostCommentCount(postId)
    }

    suspend fun createStory(
        author: UserProfileEntity,
        storyType: String,
        textContent: String,
        musicTitle: String?,
        backgroundColorHex: Long,
        imageUri: String?
    ) {
        val now = System.currentTimeMillis()
        dao.insertStory(
            StoryEntity(
                authorId = author.userId,
                authorName = author.displayName.split(" ").firstOrNull() ?: author.username,
                authorCountry = author.country,
                storyType = storyType,
                textContent = textContent.trim(),
                musicTitle = musicTitle?.takeIf { it.isNotBlank() },
                backgroundColorHex = backgroundColorHex,
                imageUri = imageUri,
                createdAt = now,
                expiresAt = now + 86_400_000L
            )
        )
        advanceMission(author.userId, "msn_share_post")
    }

    suspend fun reactToStory(storyId: Long, currentUserId: String) {
        dao.reactToStory(storyId)
        advanceMission(currentUserId, "msn_react_story")
    }

    suspend fun setStoryHighlight(storyId: Long, isHighlight: Boolean) {
        dao.setStoryHighlight(storyId, isHighlight)
    }

    suspend fun toggleFollow(currentUser: UserProfileEntity, target: UserProfileEntity, currentStatus: String?) {
        when (currentStatus) {
            "FOLLOWING", "PENDING" -> {
                dao.deleteFollowRelation(currentUser.userId, target.userId)
                if (currentStatus == "FOLLOWING") {
                    dao.upsertUser(currentUser.copy(followingCount = (currentUser.followingCount - 1).coerceAtLeast(0)))
                    dao.upsertUser(target.copy(followersCount = (target.followersCount - 1).coerceAtLeast(0)))
                }
            }
            else -> {
                val newStatus = if (target.isPrivateAccount) "PENDING" else "FOLLOWING"
                dao.upsertFollowRelation(
                    FollowRelationEntity(
                        followerId = currentUser.userId,
                        targetUserId = target.userId,
                        status = newStatus
                    )
                )
                if (newStatus == "FOLLOWING") {
                    dao.upsertUser(currentUser.copy(followingCount = currentUser.followingCount + 1))
                    dao.upsertUser(target.copy(followersCount = target.followersCount + 1))
                }
            }
        }
    }

    suspend fun approveFollowRequest(requesterId: String, myUser: UserProfileEntity) {
        dao.upsertFollowRelation(FollowRelationEntity(requesterId, myUser.userId, "FOLLOWING"))
        dao.upsertUser(myUser.copy(followersCount = myUser.followersCount + 1))
        dao.getUserById(requesterId)?.let {
            dao.upsertUser(it.copy(followingCount = it.followingCount + 1))
        }
    }

    suspend fun blockUser(currentUserId: String, targetUserId: String) {
        dao.upsertFollowRelation(FollowRelationEntity(currentUserId, targetUserId, "BLOCKED"))
    }

    suspend fun sendMessage(
        threadId: String,
        sender: UserProfileEntity,
        text: String,
        messageType: String = "TEXT",
        voiceDurationSec: Int = 0
    ) {
        if (text.isBlank() && messageType == "TEXT") return
        val now = System.currentTimeMillis()
        dao.insertChatMessage(
            ChatMessageEntity(
                threadId = threadId,
                senderId = sender.userId,
                senderName = sender.displayName,
                text = text.trim(),
                messageType = messageType,
                voiceDurationSec = voiceDurationSec,
                deliveryStatus = "DELIVERED",
                isFromMe = true,
                createdAt = now
            )
        )
        val existingThread = dao.observeChatThreads().first().firstOrNull { it.threadId == threadId }
        if (existingThread != null) {
            dao.upsertChatThread(
                existingThread.copy(
                    lastMessagePreview = if (messageType == "VOICE") "Voice message (${voiceDurationSec}s)" else text.trim(),
                    updatedAt = now
                )
            )
        }
    }

    suspend fun startOrOpenDirectThread(targetUser: UserProfileEntity): String {
        val threadId = "thr_${targetUser.userId}"
        val existing = dao.observeChatThreads().first().firstOrNull { it.threadId == threadId }
        if (existing == null) {
            dao.upsertChatThread(
                ChatThreadEntity(
                    threadId = threadId,
                    title = targetUser.displayName,
                    subtitle = "${targetUser.city}, ${targetUser.country} • ${targetUser.jodivaId}",
                    isGroup = false,
                    participantCountry = targetUser.country,
                    isOnline = targetUser.isOnlineVisible,
                    isEncrypted = true,
                    lastMessagePreview = "End-to-end protected chat started",
                    unreadCount = 0
                )
            )
        }
        return threadId
    }

    suspend fun reactToMessage(messageId: Long, emoji: String?) = dao.setMessageReaction(messageId, emoji)
    suspend fun deleteMessage(messageId: Long) = dao.deleteChatMessage(messageId)
    suspend fun markThreadRead(threadId: String) = dao.markThreadRead(threadId)

    suspend fun recordQuizCompletion(
        userId: String,
        category: String,
        correctAnswers: Int,
        totalQuestions: Int
    ): Int {
        val earned = (correctAnswers * 5) + if (correctAnswers >= 7) 15 else 0
        dao.insertQuizScore(
            QuizScoreEntity(
                userId = userId,
                category = category,
                score = correctAnswers,
                totalQuestions = totalQuestions,
                pointsEarned = earned
            )
        )
        awardPoints(userId, earned)
        advanceMission(userId, "msn_daily_quiz")
        dao.insertNotification(
            NotificationEntity(
                title = "Quiz Completed: $correctAnswers/$totalQuestions",
                body = "You earned +$earned verified points in $category!",
                category = "QUIZ"
            )
        )
        return earned
    }

    private suspend fun awardPoints(userId: String, pointsDelta: Int) {
        val user = dao.getUserById(userId) ?: return
        val newPoints = user.points + pointsDelta
        val newTier = BadgeTier.forPoints(newPoints).label
        val unlockedTick = user.hasAchievementTick || newPoints >= 300
        dao.upsertUser(
            user.copy(
                points = newPoints,
                badgeTier = newTier,
                hasAchievementTick = unlockedTick
            )
        )
    }

    private suspend fun advanceMission(userId: String, missionId: String) {
        val mission = dao.getMissionById(missionId) ?: return
        if (mission.isCompleted) return
        val nextProgress = (mission.progress + 1).coerceAtMost(mission.target)
        val completedNow = nextProgress >= mission.target
        dao.upsertDailyMissions(
            listOf(mission.copy(progress = nextProgress, isCompleted = completedNow))
        )
        if (completedNow) {
            awardPoints(userId, mission.rewardPoints)
            dao.insertNotification(
                NotificationEntity(
                    title = "Mission Complete: ${mission.title}",
                    body = "+${mission.rewardPoints} reward points added to your profile.",
                    category = "MISSION"
                )
            )
        }
    }

    suspend fun markNotificationsRead() = dao.markAllNotificationsRead()

    suspend fun submitReport(targetType: String, targetId: String, reason: String) {
        dao.insertContentReport(
            ContentReportEntity(targetType = targetType, targetId = targetId, reason = reason)
        )
    }

    suspend fun submitSupportTicket(subject: String, category: String, description: String): String {
        val ticketNum = "TKT-${Random.nextInt(100000, 999999)}"
        dao.insertSupportTicket(
            SupportTicketEntity(
                ticketNumber = ticketNum,
                subject = subject.trim(),
                category = category,
                description = description.trim(),
                status = "Under Review"
            )
        )
        return ticketNum
    }

    suspend fun translateWithAi(text: String, targetLanguage: String): AiResult =
        aiRepository.translateText(text, targetLanguage)

    suspend fun askAiAssistant(query: String, userHandle: String): AiResult =
        aiRepository.askHelpAssistant(query, userHandle)

    private fun hashPassword(raw: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
