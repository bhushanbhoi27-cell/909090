package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.PostEntity
import com.example.data.model.StoryEntity
import com.example.ui.components.JodivaAvatar
import com.example.ui.components.PostCard
import com.example.ui.components.countryFlag
import com.example.ui.components.drawableIdForName
import com.example.ui.theme.LiveRed
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.JodivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: JodivaViewModel,
    onOpenMessages: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenExploreHub: (String) -> Unit,
    onOpenHelp: () -> Unit,
    onOpenCreateStory: () -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val followRelations by viewModel.followRelations.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val threads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val translatedMap by viewModel.translatedItems.collectAsStateWithLifecycle()

    var selectedFeedFilter by remember { mutableStateOf("For You") }
    var activeStory by remember { mutableStateOf<StoryEntity?>(null) }
    var commentsPostId by remember { mutableStateOf<Long?>(null) }
    var editPostTarget by remember { mutableStateOf<PostEntity?>(null) }
    var reportPostTarget by remember { mutableStateOf<PostEntity?>(null) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    val unreadNotifications = notifications.count { !it.isRead }
    val unreadMessages = threads.sumOf { it.unreadCount }

    val blockedIds = remember(followRelations) {
        followRelations.filter { it.status == "BLOCKED" }.map { it.targetUserId }.toSet()
    }
    val followingIds = remember(followRelations) {
        followRelations.filter { it.status == "FOLLOWING" }.map { it.targetUserId }.toSet()
    }

    val visiblePosts = remember(posts, currentUser, blockedIds, followingIds, selectedFeedFilter) {
        val me = currentUser?.userId
        posts.filter { post ->
            if (post.authorId in blockedIds) return@filter false
            val canView = !post.authorIsPrivate || post.authorId == me || post.authorId in followingIds
            if (!canView) return@filter false
            when (selectedFeedFilter) {
                "Following" -> post.authorId == me || post.authorId in followingIds
                "Trending" -> post.likesCount >= 300
                "Local" -> post.authorCountry == (currentUser?.country ?: "India")
                else -> true
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "JODIVA",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${countryFlag(currentUser?.country ?: "India")} ${prefs.preferredLanguage}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        showNotificationsSheet = true
                        viewModel.markNotificationsRead()
                    },
                    modifier = Modifier.testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) {
                                Badge { Text(unreadNotifications.toString()) }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                }
                IconButton(
                    onClick = onOpenMessages,
                    modifier = Modifier.testTag("messages_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadMessages > 0) {
                                Badge { Text(unreadMessages.toString()) }
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages")
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stories Tray
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onOpenCreateStory() }
                                .testTag("add_story_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add Story",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Your Story", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    items(stories, key = { it.id }) { story ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(68.dp)
                                .clickable { activeStory = story }
                                .testTag("story_item_${story.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .border(
                                        width = 2.5.dp,
                                        brush = Brush.linearGradient(listOf(TealPrimary, SaffronSecondary)),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                            ) {
                                JodivaAvatar(
                                    name = story.authorName,
                                    avatarUri = story.imageUri,
                                    size = 56.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${countryFlag(story.authorCountry)} ${story.authorName}",
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Global Hub Quick Launch Strip
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        HubShortcutPill(
                            icon = Icons.Filled.EmojiEvents,
                            label = "Quiz & Ranks",
                            accent = MaterialTheme.colorScheme.tertiary,
                            onClick = onOpenQuiz,
                            tag = "hub_quiz"
                        )
                    }
                    item {
                        HubShortcutPill(
                            icon = Icons.Filled.LiveTv,
                            label = "Live Now",
                            accent = LiveRed,
                            onClick = { onOpenExploreHub("Live") },
                            tag = "hub_live"
                        )
                    }
                    item {
                        HubShortcutPill(
                            icon = Icons.Filled.Newspaper,
                            label = "World News",
                            accent = MaterialTheme.colorScheme.primary,
                            onClick = { onOpenExploreHub("News") },
                            tag = "hub_news"
                        )
                    }
                    item {
                        HubShortcutPill(
                            icon = Icons.Filled.School,
                            label = "Learning",
                            accent = MaterialTheme.colorScheme.secondary,
                            onClick = { onOpenExploreHub("Education") },
                            tag = "hub_learn"
                        )
                    }
                    item {
                        HubShortcutPill(
                            icon = Icons.Filled.Work,
                            label = "Careers",
                            accent = MaterialTheme.colorScheme.primary,
                            onClick = { onOpenExploreHub("Careers") },
                            tag = "hub_careers"
                        )
                    }
                    item {
                        HubShortcutPill(
                            icon = Icons.Filled.SupportAgent,
                            label = "AI Help",
                            accent = MaterialTheme.colorScheme.tertiary,
                            onClick = onOpenHelp,
                            tag = "hub_help"
                        )
                    }
                }
            }

            // Feed Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("For You", "Trending", "Local", "Following").forEach { filter ->
                        item {
                            FilterChip(
                                selected = selectedFeedFilter == filter,
                                onClick = { selectedFeedFilter = filter },
                                label = { Text(filter) }
                            )
                        }
                    }
                }
            }

            if (visiblePosts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No posts in this filter yet", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Follow creators worldwide or switch to 'For You' to explore global posts.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(visiblePosts, key = { it.id }) { post ->
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
                            onReportClick = { reportPostTarget = post },
                            onAuthorClick = { onOpenUserProfile(post.authorId) }
                        )
                    }
                }
            }
        }
    }

    // Story Viewer Dialog
    activeStory?.let { story ->
        StoryViewerDialog(
            story = story,
            isOwner = story.authorId == currentUser?.userId,
            onDismiss = { activeStory = null },
            onReact = {
                viewModel.reactToStory(story.id)
                activeStory = story.copy(reactionCount = story.reactionCount + 1)
            },
            onToggleHighlight = {
                viewModel.toggleHighlight(story)
                activeStory = story.copy(isHighlight = !story.isHighlight)
            }
        )
    }

    // Comments Bottom Sheet
    commentsPostId?.let { postId ->
        CommentsBottomSheet(
            postId = postId,
            viewModel = viewModel,
            onDismiss = { commentsPostId = null }
        )
    }

    // Notifications Bottom Sheet
    if (showNotificationsSheet) {
        ModalBottomSheet(onDismissRequest = { showNotificationsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Notifications", style = MaterialTheme.typography.titleLarge)
                if (notifications.isEmpty()) {
                    Text("You are all caught up!", style = MaterialTheme.typography.bodyMedium)
                } else {
                    notifications.take(8).forEach { notif ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(notif.title, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    notif.body,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Edit Caption Dialog
    editPostTarget?.let { post ->
        var caption by remember(post.id) { mutableStateOf(post.caption) }
        var tags by remember(post.id) { mutableStateOf(post.hashtags) }
        AlertDialog(
            onDismissRequest = { editPostTarget = null },
            title = { Text("Edit Post") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        label = { Text("Caption") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Hashtags") },
                        modifier = Modifier.fillMaxWidth()
                    )
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

    // Report Post Dialog
    reportPostTarget?.let { post ->
        ReportDialog(
            targetLabel = "post by @${post.authorHandle}",
            onDismiss = { reportPostTarget = null },
            onSubmit = { reason ->
                viewModel.submitReport("POST", post.id.toString(), reason)
                reportPostTarget = null
            }
        )
    }
}

@Composable
private fun HubShortcutPill(
    icon: ImageVector,
    label: String,
    accent: Color,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun StoryViewerDialog(
    story: StoryEntity,
    isOwner: Boolean,
    onDismiss: () -> Unit,
    onReact: () -> Unit,
    onToggleHighlight: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(story.backgroundColorHex),
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val resId = drawableIdForName(story.drawableResName)
                if (!story.imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = Uri.parse(story.imageUri),
                        contentDescription = story.textContent,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (resId != null) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = story.textContent,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${story.authorName} • ${countryFlag(story.authorCountry)} ${story.authorCountry}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "${story.viewersCount} viewers • Disappears in 24h",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close story", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    story.musicTitle?.let {
                        Text(
                            text = "\uD83C\uDFB5 $it",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Text(
                        text = story.textContent,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = onReact) {
                            Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("React (${story.reactionCount})")
                        }
                        if (isOwner || true) {
                            TextButton(onClick = onToggleHighlight) {
                                Icon(
                                    Icons.Filled.BookmarkAdd,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (story.isHighlight) "Highlighted" else "Save Highlight",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    postId: Long,
    viewModel: JodivaViewModel,
    onDismiss: () -> Unit
) {
    val comments by viewModel.observeComments(postId).collectAsStateWithLifecycle(initialValue = emptyList())
    var commentDraft by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Comments", style = MaterialTheme.typography.titleLarge)
            if (comments.isEmpty()) {
                Text(
                    "Be the first to share a thoughtful reply!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                comments.forEach { c ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "${c.authorName} (@${c.authorHandle})",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(text = c.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = commentDraft,
                    onValueChange = { commentDraft = it },
                    placeholder = { Text("Add a comment...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        viewModel.addComment(postId, commentDraft)
                        commentDraft = ""
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send comment")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReportDialog(
    targetLabel: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    val reasons = listOf(
        "Spam or misleading information",
        "Harassment or abusive behavior",
        "Copyright or licensing violation",
        "Inappropriate or unsafe content"
    )
    var selected by remember { mutableStateOf(reasons.first()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report $targetLabel") },
        text = {
            Column {
                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = reason }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = selected == reason, onClick = { selected = reason })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(selected) }) { Text("Submit Report") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
