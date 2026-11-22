package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BadgeTier
import com.example.data.model.GlobalCatalog
import com.example.data.model.PostEntity
import com.example.data.model.StoryEntity
import com.example.data.model.UserProfileEntity
import com.example.ui.components.BadgeAndTickRow
import com.example.ui.components.JodivaAvatar
import com.example.ui.components.PostCard
import com.example.ui.components.badgeColorFor
import com.example.ui.components.countryFlag
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.JodivaViewModel

@Composable
fun ProfileScreen(
    viewModel: JodivaViewModel,
    viewedUserId: String? = null,
    onBack: (() -> Unit)? = null,
    onOpenSettings: () -> Unit,
    onOpenThread: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allPosts by viewModel.allPosts.collectAsStateWithLifecycle()
    val savedPosts by viewModel.savedPosts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val followRelations by viewModel.followRelations.collectAsStateWithLifecycle()
    val incomingRequests by viewModel.incomingFollowRequests.collectAsStateWithLifecycle()
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val translatedMap by viewModel.translatedItems.collectAsStateWithLifecycle()

    val profileUser: UserProfileEntity = remember(viewedUserId, currentUser, allUsers) {
        if (viewedUserId.isNullOrBlank() || viewedUserId == currentUser?.userId) {
            currentUser ?: allUsers.first()
        } else {
            allUsers.firstOrNull { it.userId == viewedUserId } ?: currentUser ?: allUsers.first()
        }
    }

    val isOwnProfile = profileUser.userId == currentUser?.userId
    val relationStatus = followRelations.firstOrNull { it.targetUserId == profileUser.userId }?.status
    val canViewPrivateContent = isOwnProfile || !profileUser.isPrivateAccount || relationStatus == "FOLLOWING"

    val userPosts = remember(allPosts, profileUser.userId) {
        allPosts.filter { it.authorId == profileUser.userId }
    }
    val userHighlights = remember(stories, profileUser.userId) {
        stories.filter { it.authorId == profileUser.userId && it.isHighlight }
    }

    val globalRank = remember(allUsers, profileUser.userId) {
        val idx = allUsers.sortedByDescending { it.points }.indexOfFirst { it.userId == profileUser.userId }
        if (idx >= 0) idx + 1 else 1
    }
    val countryRank = remember(allUsers, profileUser.userId, profileUser.country) {
        val idx = allUsers
            .filter { it.country == profileUser.country }
            .sortedByDescending { it.points }
            .indexOfFirst { it.userId == profileUser.userId }
        if (idx >= 0) idx + 1 else 1
    }

    var selectedTab by remember { mutableStateOf("Posts") }
    var showEditDialog by remember { mutableStateOf(false) }
    var activeHighlight by remember { mutableStateOf<StoryEntity?>(null) }
    var commentsPostId by remember { mutableStateOf<Long?>(null) }
    var editPostTarget by remember { mutableStateOf<PostEntity?>(null) }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && isOwnProfile) {
            viewModel.updateProfile(profileUser.copy(avatarUri = uri.toString()))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cover + Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                        .background(Brush.horizontalGradient(listOf(TealPrimary, SaffronSecondary)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (isOwnProfile) {
                            IconButton(
                                onClick = onOpenSettings,
                                modifier = Modifier.testTag("profile_settings_button")
                            ) {
                                Icon(Icons.Filled.Settings, contentDescription = "Settings & Security", tint = Color.White)
                            }
                        } else {
                            IconButton(onClick = { viewModel.blockUser(profileUser.userId) }) {
                                Icon(Icons.Filled.Block, contentDescription = "Block user", tint = Color.White)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier.clickable(enabled = isOwnProfile) {
                                avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        ) {
                            JodivaAvatar(
                                name = profileUser.displayName,
                                avatarUri = profileUser.avatarUri,
                                size = 76.dp,
                                showOnlineDot = profileUser.isOnlineVisible
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (isOwnProfile) {
                                OutlinedButton(
                                    onClick = { showEditDialog = true },
                                    modifier = Modifier.testTag("edit_profile_button")
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit Profile")
                                }
                            } else {
                                OutlinedButton(onClick = {
                                    viewModel.openDirectMessage(profileUser) { threadId -> onOpenThread(threadId) }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Message")
                                }
                                Button(onClick = { viewModel.toggleFollow(profileUser) }) {
                                    Text(
                                        when (relationStatus) {
                                            "FOLLOWING" -> "Following"
                                            "PENDING" -> "Requested"
                                            else -> if (profileUser.isPrivateAccount) "Request" else "Follow"
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(profileUser.displayName, style = MaterialTheme.typography.headlineSmall)
                        BadgeAndTickRow(
                            badgeTier = profileUser.badgeTier,
                            hasAchievementTick = profileUser.hasAchievementTick,
                            isIdentityVerified = profileUser.isIdentityVerified
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "@${profileUser.username} • ${profileUser.jodivaId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (profileUser.isPrivateAccount) Icons.Filled.Lock else Icons.Filled.Public,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (profileUser.isPrivateAccount) "Private" else "Public",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(profileUser.bio, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${countryFlag(profileUser.country)} ${profileUser.city}, ${profileUser.stateProvince}, ${profileUser.country} • Language: ${profileUser.preferredLanguage}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Grid
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStat("Followers", profileUser.followersCount.toString())
                            ProfileStat("Following", profileUser.followingCount.toString())
                            ProfileStat("Points", profileUser.points.toString())
                            ProfileStat("World", "#$globalRank")
                            ProfileStat(profileUser.country.take(3).uppercase(), "#$countryRank")
                        }
                    }
                }
            }
        }

        // Incoming Follow Requests (only for Private Account owner)
        if (isOwnProfile && incomingRequests.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Pending Follow Requests (${incomingRequests.size})", style = MaterialTheme.typography.titleSmall)
                        incomingRequests.forEach { req ->
                            val requester = allUsers.firstOrNull { it.userId == req.followerId }
                            if (requester != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${requester.displayName} (@${requester.username})")
                                    Button(onClick = { viewModel.approveFollowRequest(requester.userId) }) {
                                        Text("Approve")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!canViewPrivateContent) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(36.dp))
                        Text("This Account Is Private", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Follow requests require approval from @${profileUser.username} before posts, stories, and highlights become visible.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            // Permanent Highlights
            if (userHighlights.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("Permanent Highlights", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(userHighlights, key = { "hl_${it.id}" }) { hl ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { activeHighlight = hl }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color(hl.backgroundColorHex)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        hl.musicTitle ?: "Highlight",
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = if (isOwnProfile) listOf("Posts", "Saved", "Achievements") else listOf("Posts", "Achievements")
                    tabs.forEach { tab ->
                        FilterChip(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            label = { Text(tab) }
                        )
                    }
                }
            }

            when (selectedTab) {
                "Posts" -> {
                    if (userPosts.isEmpty()) {
                        item {
                            Text(
                                "No posts published yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        items(userPosts, key = { "prof_post_${it.id}" }) { post ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                PostCard(
                                    post = post,
                                    isOwner = isOwnProfile,
                                    preferredLanguage = prefs.preferredLanguage,
                                    translatedResult = translatedMap["post_${post.id}"],
                                    onLike = { viewModel.toggleLikePost(post) },
                                    onCommentClick = { commentsPostId = post.id },
                                    onSave = { viewModel.toggleSavePost(post) },
                                    onTranslate = { viewModel.translateContent("post_${post.id}", post.caption) },
                                    onClearTranslation = { viewModel.clearTranslation("post_${post.id}") },
                                    onEditClick = { editPostTarget = post },
                                    onDeleteClick = { viewModel.deletePost(post.id) },
                                    onReportClick = { viewModel.submitReport("POST", post.id.toString(), "Reported from profile") },
                                    onAuthorClick = {}
                                )
                            }
                        }
                    }
                }
                "Saved" -> {
                    items(savedPosts, key = { "saved_post_${it.id}" }) { post ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            PostCard(
                                post = post,
                                isOwner = post.authorId == currentUser?.userId,
                                preferredLanguage = prefs.preferredLanguage,
                                translatedResult = translatedMap["post_${post.id}"],
                                onLike = { viewModel.toggleLikePost(post) },
                                onCommentClick = { commentsPostId = post.id },
                                onSave = { viewModel.toggleSavePost(post) },
                                onTranslate = { viewModel.translateContent("post_${post.id}", post.caption) },
                                onClearTranslation = { viewModel.clearTranslation("post_${post.id}") },
                                onEditClick = { editPostTarget = post },
                                onDeleteClick = { viewModel.deletePost(post.id) },
                                onReportClick = { viewModel.submitReport("POST", post.id.toString(), "Reported") },
                                onAuthorClick = {}
                            )
                        }
                    }
                }
                "Achievements" -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Performance Badge Progression", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Achievement badges & ticks are earned strictly through verified quizzes and daily missions—never purchased.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                BadgeTier.entries.forEach { tier ->
                                    val unlocked = profileUser.points >= tier.minPoints
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${tier.label} Badge (${tier.minPoints}+ pts)",
                                            color = if (unlocked) badgeColorFor(tier.label) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(if (unlocked) "Unlocked" else "Locked", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                                if (isOwnProfile && quizHistory.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Recent Quiz Attempts", style = MaterialTheme.typography.titleSmall)
                                    quizHistory.take(4).forEach { q ->
                                        Text(
                                            "• ${q.category}: ${q.score}/${q.totalQuestions} (+${q.pointsEarned} pts)",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && isOwnProfile) {
        EditProfileDialog(
            user = profileUser,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.updateProfile(updated)
                showEditDialog = false
            }
        )
    }

    activeHighlight?.let { story ->
        StoryViewerDialog(
            story = story,
            isOwner = isOwnProfile,
            onDismiss = { activeHighlight = null },
            onReact = { viewModel.reactToStory(story.id) },
            onToggleHighlight = {
                viewModel.toggleHighlight(story)
                activeHighlight = null
            }
        )
    }

    commentsPostId?.let { postId ->
        CommentsBottomSheet(postId = postId, viewModel = viewModel, onDismiss = { commentsPostId = null })
    }

    editPostTarget?.let { post ->
        var caption by remember(post.id) { mutableStateOf(post.caption) }
        var tags by remember(post.id) { mutableStateOf(post.hashtags) }
        AlertDialog(
            onDismissRequest = { editPostTarget = null },
            title = { Text("Edit Post") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Caption") })
                    OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Hashtags") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.editPost(post.id, caption, tags)
                    editPostTarget = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editPostTarget = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EditProfileDialog(
    user: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (UserProfileEntity) -> Unit
) {
    var displayName by remember { mutableStateOf(user.displayName) }
    var bio by remember { mutableStateOf(user.bio) }
    var country by remember { mutableStateOf(user.country) }
    var city by remember { mutableStateOf(user.city) }
    var language by remember { mutableStateOf(user.preferredLanguage) }
    var isPrivate by remember { mutableStateOf(user.isPrivateAccount) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile & Privacy") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = { Text("Display Name") }, singleLine = true)
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, maxLines = 3)
                DropdownSelectorField(
                    label = "Country",
                    selected = country,
                    options = GlobalCatalog.countries,
                    displayTransform = { "${countryFlag(it)} $it" },
                    onSelect = { country = it }
                )
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, singleLine = true)
                DropdownSelectorField(
                    label = "Preferred Language",
                    selected = language,
                    options = GlobalCatalog.languages,
                    displayTransform = { it },
                    onSelect = { language = it }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isPrivate) "Private Account" else "Public Account", style = MaterialTheme.typography.titleSmall)
                    Switch(checked = isPrivate, onCheckedChange = { isPrivate = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    user.copy(
                        displayName = displayName.trim().ifEmpty { user.displayName },
                        bio = bio.trim(),
                        country = country,
                        city = city.trim().ifEmpty { user.city },
                        preferredLanguage = language,
                        isPrivateAccount = isPrivate
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
