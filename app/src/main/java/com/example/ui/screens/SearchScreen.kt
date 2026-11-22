package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GlobalCatalog
import com.example.ui.components.BadgeAndTickRow
import com.example.ui.components.JodivaAvatar
import com.example.ui.components.countryFlag
import com.example.ui.viewmodel.JodivaViewModel

@Composable
fun SearchScreen(
    viewModel: JodivaViewModel,
    onOpenThread: (String) -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allPosts by viewModel.allPosts.collectAsStateWithLifecycle()
    val relations by viewModel.followRelations.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var selectedScope by remember { mutableStateOf("All") }

    val relationByTarget = remember(relations) { relations.associateBy { it.targetUserId } }
    val q = query.trim().lowercase()

    val matchingUsers = remember(allUsers, currentUser, q, relationByTarget) {
        allUsers.filter { u ->
            u.userId != currentUser?.userId &&
                relationByTarget[u.userId]?.status != "BLOCKED" &&
                (q.isEmpty() ||
                    u.displayName.lowercase().contains(q) ||
                    u.username.lowercase().contains(q) ||
                    u.jodivaId.lowercase().contains(q) ||
                    u.country.lowercase().contains(q) ||
                    u.city.lowercase().contains(q))
        }
    }

    val matchingPosts = remember(allPosts, q, relationByTarget, currentUser) {
        allPosts.filter { post ->
            val canView = !post.authorIsPrivate ||
                post.authorId == currentUser?.userId ||
                relationByTarget[post.authorId]?.status == "FOLLOWING"
            canView && (q.isEmpty() ||
                post.caption.lowercase().contains(q) ||
                post.hashtags.lowercase().contains(q) ||
                post.authorCountry.lowercase().contains(q))
        }
    }

    val matchingCommunities = remember(q) {
        GlobalCatalog.communities.filter { q.isEmpty() || it.lowercase().contains(q) }
    }

    val trendingTags = listOf("#OneWorld", "#QuizArena", "#Kyoto", "#Mumbai", "#STEM", "#Samba")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Smart Global Search", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search people, @username, JODIVA ID, country, #tag...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("smart_search_input")
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "People", "Posts", "Communities").forEach { scope ->
                    item {
                        FilterChip(
                            selected = selectedScope == scope,
                            onClick = { selectedScope = scope },
                            label = { Text(scope) }
                        )
                    }
                }
            }
        }

        item {
            Text("Trending Hashtags", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(trendingTags) { tag ->
                    Surface(
                        onClick = { query = tag.removePrefix("#") },
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Tag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(tag.removePrefix("#"), style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        if (selectedScope == "All" || selectedScope == "People") {
            item {
                Text("People & Creators", style = MaterialTheme.typography.titleMedium)
            }
            items(matchingUsers, key = { it.userId }) { user ->
                val status = relationByTarget[user.userId]?.status
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenUserProfile(user.userId) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JodivaAvatar(name = user.displayName, avatarUri = user.avatarUri, size = 46.dp, showOnlineDot = user.isOnlineVisible)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(user.displayName, style = MaterialTheme.typography.titleSmall)
                                if (user.isPrivateAccount) {
                                    Icon(Icons.Filled.Lock, contentDescription = "Private", modifier = Modifier.size(14.dp))
                                }
                                BadgeAndTickRow(user.badgeTier, user.hasAchievementTick, user.isIdentityVerified)
                            }
                            Text(
                                "@${user.username} • ${user.jodivaId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${countryFlag(user.country)} ${user.city}, ${user.country} • ${user.points} pts",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = {
                            viewModel.openDirectMessage(user) { threadId -> onOpenThread(threadId) }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Message ${user.displayName}")
                        }
                        when (status) {
                            "FOLLOWING" -> OutlinedButton(onClick = { viewModel.toggleFollow(user) }) {
                                Text("Following")
                            }
                            "PENDING" -> OutlinedButton(onClick = { viewModel.toggleFollow(user) }) {
                                Text("Requested")
                            }
                            else -> Button(onClick = { viewModel.toggleFollow(user) }) {
                                Text(if (user.isPrivateAccount) "Request" else "Follow")
                            }
                        }
                    }
                }
            }
        }

        if (selectedScope == "All" || selectedScope == "Communities") {
            item {
                Text("Global Communities", style = MaterialTheme.typography.titleMedium)
            }
            items(matchingCommunities) { community ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(community, style = MaterialTheme.typography.titleSmall)
                            Text("Multilingual community • Open worldwide", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        if (selectedScope == "All" || selectedScope == "Posts") {
            item {
                Text("Matching Posts & Topics", style = MaterialTheme.typography.titleMedium)
            }
            items(matchingPosts.take(6), key = { "search_post_${it.id}" }) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "${post.authorName} (@${post.authorHandle}) • ${countryFlag(post.authorCountry)} ${post.authorCountry}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(post.caption, style = MaterialTheme.typography.bodyMedium)
                        Text(post.hashtags, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
