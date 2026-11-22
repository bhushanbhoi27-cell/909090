package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiResult
import com.example.data.local.UserPreferencesState
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatThreadEntity
import com.example.data.model.CommentEntity
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
import com.example.data.repository.JodivaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HelpMessage(
    val isUser: Boolean,
    val text: String,
    val notice: String? = null
)

data class TranslationPreview(
    val originalId: String,
    val result: AiResult
)

@OptIn(ExperimentalCoroutinesApi::class)
class JodivaViewModel(private val repository: JodivaRepository) : ViewModel() {

    val preferences: StateFlow<UserPreferencesState> = repository.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesState())

    val currentUser: StateFlow<UserProfileEntity?> = preferences
        .flatMapLatest { prefs ->
            val uid = prefs.currentUserId
            if (uid.isNullOrBlank()) flowOf(null) else repository.observeUser(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserProfileEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPosts: StateFlow<List<PostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reels: StateFlow<List<PostEntity>> = repository.reels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPosts: StateFlow<List<PostEntity>> = repository.savedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<StoryEntity>> = repository.observeStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myHighlights: StateFlow<List<StoryEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeHighlights(user.userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followRelations: StateFlow<List<FollowRelationEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeFollowRelations(user.userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomingFollowRequests: StateFlow<List<FollowRelationEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeIncomingRequests(user.userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatThreads: StateFlow<List<ChatThreadEntity>> = repository.chatThreads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyMissions: StateFlow<List<DailyMissionEntity>> = repository.dailyMissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTickets: StateFlow<List<SupportTicketEntity>> = repository.supportTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizHistory: StateFlow<List<QuizScoreEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeQuizHistory(user.userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loginActivity: StateFlow<List<LoginActivityEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeLoginActivity(user.userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _translatedItems = MutableStateFlow<Map<String, AiResult>>(emptyMap())
    val translatedItems: StateFlow<Map<String, AiResult>> = _translatedItems.asStateFlow()

    private val _helpMessages = MutableStateFlow(
        listOf(
            HelpMessage(
                isUser = false,
                text = "Welcome to JODIVA Help Center! Ask about account recovery, privacy controls, quiz rankings, or subscriptions.",
                notice = AiResult.FIREBASE_REQUIRED_NOTICE
            )
        )
    )
    val helpMessages: StateFlow<List<HelpMessage>> = _helpMessages.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun observeComments(postId: Long): Flow<List<CommentEntity>> = repository.observeComments(postId)
    fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>> = repository.observeMessages(threadId)

    fun toggleLikePost(post: PostEntity) {
        viewModelScope.launch { repository.toggleLikePost(post) }
    }

    fun toggleSavePost(post: PostEntity) {
        viewModelScope.launch { repository.toggleSavePost(post) }
    }

    fun addComment(postId: Long, text: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch { repository.addComment(postId, me, text) }
    }

    fun createPost(
        postType: String,
        caption: String,
        hashtags: String,
        musicTrack: String?,
        imageUri: String?
    ) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.createPost(me, postType, caption, hashtags, musicTrack, imageUri)
            _statusBanner.value = "${postType.lowercase().replaceFirstChar { it.uppercase() }} published to JODIVA!"
        }
    }

    fun editPost(postId: Long, caption: String, hashtags: String) {
        viewModelScope.launch {
            repository.editPost(postId, caption, hashtags)
            _statusBanner.value = "Post updated."
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
            _statusBanner.value = "Post deleted."
        }
    }

    fun createStory(
        storyType: String,
        textContent: String,
        musicTitle: String?,
        backgroundColorHex: Long,
        imageUri: String?
    ) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.createStory(me, storyType, textContent, musicTitle, backgroundColorHex, imageUri)
            _statusBanner.value = "Story shared (24-hour expiry)."
        }
    }

    fun reactToStory(storyId: Long) {
        val me = currentUser.value ?: return
        viewModelScope.launch { repository.reactToStory(storyId, me.userId) }
    }

    fun toggleHighlight(story: StoryEntity) {
        viewModelScope.launch {
            repository.setStoryHighlight(story.id, !story.isHighlight)
            _statusBanner.value = if (!story.isHighlight) "Saved to Profile Highlights." else "Removed from Highlights."
        }
    }

    fun toggleFollow(target: UserProfileEntity) {
        val me = currentUser.value ?: return
        val relation = followRelations.value.firstOrNull { it.targetUserId == target.userId }
        viewModelScope.launch {
            repository.toggleFollow(me, target, relation?.status)
        }
    }

    fun approveFollowRequest(requesterId: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.approveFollowRequest(requesterId, me)
            _statusBanner.value = "Follow request approved."
        }
    }

    fun blockUser(targetUserId: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.blockUser(me.userId, targetUserId)
            _statusBanner.value = "User blocked."
        }
    }

    fun openDirectMessage(target: UserProfileEntity, onReady: (String) -> Unit) {
        viewModelScope.launch {
            val threadId = repository.startOrOpenDirectThread(target)
            onReady(threadId)
        }
    }

    fun sendChatMessage(
        threadId: String,
        text: String,
        messageType: String = "TEXT",
        voiceDurationSec: Int = 0
    ) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.sendMessage(threadId, me, text, messageType, voiceDurationSec)
        }
    }

    fun reactToMessage(messageId: Long, emoji: String?) {
        viewModelScope.launch { repository.reactToMessage(messageId, emoji) }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch { repository.deleteMessage(messageId) }
    }

    fun markThreadRead(threadId: String) {
        viewModelScope.launch { repository.markThreadRead(threadId) }
    }

    fun translateContent(key: String, text: String) {
        val targetLang = preferences.value.preferredLanguage
        viewModelScope.launch {
            val result = repository.translateWithAi(text, targetLang)
            _translatedItems.update { it + (key to result) }
        }
    }

    fun clearTranslation(key: String) {
        _translatedItems.update { it - key }
    }

    fun completeQuiz(category: String, correct: Int, total: Int, onPointsAwarded: (Int) -> Unit) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            val earned = repository.recordQuizCompletion(me.userId, category, correct, total)
            onPointsAwarded(earned)
        }
    }

    fun updateProfile(updated: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(updated)
            _statusBanner.value = "Profile saved."
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setPreferredLanguage(language: String) {
        viewModelScope.launch {
            repository.setPreferredLanguage(language, currentUser.value?.userId)
        }
    }

    fun setAutoTranslate(enabled: Boolean) {
        viewModelScope.launch { repository.setAutoTranslate(enabled) }
    }

    fun updateNotificationPrefs(social: Boolean, quiz: Boolean, security: Boolean) {
        viewModelScope.launch { repository.updateNotificationSettings(social, quiz, security) }
    }

    fun markNotificationsRead() {
        viewModelScope.launch { repository.markNotificationsRead() }
    }

    fun submitReport(targetType: String, targetId: String, reason: String) {
        viewModelScope.launch {
            repository.submitReport(targetType, targetId, reason)
            _statusBanner.value = "Report received for moderation review."
        }
    }

    fun askHelpAssistant(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val handle = currentUser.value?.username ?: "member"
        _helpMessages.update { it + HelpMessage(isUser = true, text = trimmed) }
        viewModelScope.launch {
            val result = repository.askAiAssistant(trimmed, handle)
            _helpMessages.update {
                it + HelpMessage(isUser = false, text = result.text, notice = result.notice)
            }
        }
    }

    fun submitSupportTicket(subject: String, category: String, description: String) {
        if (subject.isBlank() || description.isBlank()) return
        viewModelScope.launch {
            val ticketNum = repository.submitSupportTicket(subject, category, description)
            _statusBanner.value = "Support ticket $ticketNum submitted and queued."
        }
    }

    fun logoutOtherDevices() {
        val uid = currentUser.value?.userId ?: return
        viewModelScope.launch {
            repository.logoutOtherDevices(uid)
            _statusBanner.value = "Logged out from all other devices."
        }
    }

    fun logoutCurrentSession() {
        viewModelScope.launch { repository.logoutCurrentSession() }
    }

    fun logoutAllDevices() {
        val uid = currentUser.value?.userId ?: return
        viewModelScope.launch { repository.logoutAllDevices(uid) }
    }

    companion object {
        fun factory(repository: JodivaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return JodivaViewModel(repository) as T
                }
            }
    }
}
