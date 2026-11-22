package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LiveStreamRoom
import com.example.data.repository.JodivaSeedCatalog
import com.example.ui.components.AiNoticeBanner
import com.example.ui.components.countryFlag
import com.example.ui.components.shareTextExternally
import com.example.ui.theme.LiveRed
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.JodivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreHubScreen(
    viewModel: JodivaViewModel,
    initialSection: String = "News",
    onBack: () -> Unit
) {
    var activeSection by remember(initialSection) { mutableStateOf(initialSection) }
    var selectedNewsCategory by remember { mutableStateOf("All") }
    var activeLiveRoom by remember { mutableStateOf<LiveStreamRoom?>(null) }
    var isBroadcasting by remember { mutableStateOf(false) }

    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val translatedMap by viewModel.translatedItems.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Global Hub") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("News", "Live", "Education", "Careers").forEach { sec ->
                item {
                    FilterChip(
                        selected = activeSection == sec,
                        onClick = { activeSection = sec },
                        label = { Text(sec) },
                        leadingIcon = {
                            Icon(
                                imageVector = when (sec) {
                                    "Live" -> Icons.Filled.LiveTv
                                    "Education" -> Icons.Filled.School
                                    "Careers" -> Icons.Filled.Work
                                    else -> Icons.Filled.Newspaper
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }

        when (activeSection) {
            "News" -> {
                val categories = listOf("All", "India News", "Maharashtra News", "World News", "Technology News", "Education News", "Sports News")
                val filteredNews = JodivaSeedCatalog.newsArticles.filter {
                    selectedNewsCategory == "All" || it.category == selectedNewsCategory
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = selectedNewsCategory == cat,
                                    onClick = { selectedNewsCategory = cat },
                                    label = { Text(cat.removeSuffix(" News")) }
                                )
                            }
                        }
                    }

                    items(filteredNews, key = { it.id }) { article ->
                        val translated = translatedMap["news_${article.id}"]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "${countryFlag(article.country)} ${article.category} • ${article.source} • ${article.publishedAgo}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(article.headline, style = MaterialTheme.typography.titleMedium)
                                Text(article.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                if (translated != null) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                "${prefs.preferredLanguage}: ${translated.text}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            AiNoticeBanner(notice = translated.notice)
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = {
                                        viewModel.translateContent("news_${article.id}", "${article.headline}. ${article.summary}")
                                    }) {
                                        Icon(Icons.Filled.Translate, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Translate to ${prefs.preferredLanguage}")
                                    }
                                    TextButton(onClick = {
                                        shareTextExternally(context, article.headline, article.summary)
                                    }) {
                                        Text("Share")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Live" -> {
                if (activeLiveRoom != null) {
                    val room = activeLiveRoom!!
                    val liveComments = remember(room.id) {
                        mutableStateListOf<Pair<String, String>>().apply { addAll(room.initialComments) }
                    }
                    var likeCount by remember(room.id) { mutableIntStateOf(142) }
                    var commentInput by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .background(
                                    Brush.linearGradient(listOf(TealPrimary, SaffronSecondary)),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(color = LiveRed, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        "LIVE • ${room.viewerCount} watching",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                TextButton(onClick = { activeLiveRoom = null }) {
                                    Text(if (isBroadcasting) "End Live" else "Leave", color = Color.White)
                                }
                            }

                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(room.title, style = MaterialTheme.typography.titleLarge, color = Color.White)
                                Text(
                                    "Host: ${room.hostName} (${countryFlag(room.hostCountry)} ${room.hostCountry})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { likeCount++ }) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Like ($likeCount)")
                            }
                            OutlinedButton(onClick = {
                                shareTextExternally(context, "Watch Live on JODIVA", "${room.hostName} is live: ${room.title}")
                            }) {
                                Text("Share Stream")
                            }
                            OutlinedButton(onClick = {
                                viewModel.submitReport("LIVE", room.id, "Reported live stream")
                            }) {
                                Icon(Icons.Filled.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Report")
                            }
                        }

                        Text("Live Chat & Moderation", style = MaterialTheme.typography.titleSmall)
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(liveComments) { (sender, msg) ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("$sender: $msg", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = commentInput,
                                onValueChange = { commentInput = it },
                                placeholder = { Text("Send a live comment...") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                if (commentInput.isNotBlank()) {
                                    liveComments.add((currentUser?.displayName ?: "You") to commentInput.trim())
                                    commentInput = ""
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("One-to-Many Global Broadcast", style = MaterialTheme.typography.titleMedium)
                                        Text("Start your own moderated live room with viewer controls.", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Button(
                                        onClick = {
                                            isBroadcasting = true
                                            activeLiveRoom = LiveStreamRoom(
                                                id = "live_me",
                                                hostName = currentUser?.displayName ?: "You",
                                                hostHandle = currentUser?.username ?: "creator",
                                                hostCountry = currentUser?.country ?: "India",
                                                title = "Live Community Hangout & Q&A",
                                                category = "Community",
                                                viewerCount = 1,
                                                language = prefs.preferredLanguage,
                                                initialComments = listOf("JODIVA Safety Bot" to "Broadcast moderation controls active.")
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                                    ) {
                                        Icon(Icons.Filled.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Go Live")
                                    }
                                }
                            }
                        }

                        items(JodivaSeedCatalog.liveStreams, key = { it.id }) { room ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Surface(color = LiveRed, shape = RoundedCornerShape(6.dp)) {
                                            Text(
                                                "LIVE • ${room.viewerCount}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text("${countryFlag(room.hostCountry)} ${room.language}", style = MaterialTheme.typography.labelMedium)
                                    }
                                    Text(room.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Hosted by ${room.hostName} (@${room.hostHandle})", style = MaterialTheme.typography.bodySmall)
                                    Button(onClick = {
                                        isBroadcasting = false
                                        activeLiveRoom = room
                                    }) {
                                        Text("Watch Stream")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Education" -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(JodivaSeedCatalog.learningCourses, key = { it.id }) { course ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("${course.category} • ${course.level}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(course.title, style = MaterialTheme.typography.titleMedium)
                                Text(course.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${course.lessonsCount} Lessons • Languages: ${course.language}", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            "Careers" -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(JodivaSeedCatalog.careerOpportunities, key = { it.id }) { job ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("${job.sector} • ${job.location}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                Text(job.title, style = MaterialTheme.typography.titleMedium)
                                Text(job.organization, style = MaterialTheme.typography.labelLarge)
                                Text(job.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Deadline: ${job.deadline}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
