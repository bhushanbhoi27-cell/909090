package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.repository.JodivaSeedCatalog
import com.example.ui.components.BadgeAndTickRow
import com.example.ui.components.JodivaAvatar
import com.example.ui.components.countryFlag
import com.example.ui.viewmodel.JodivaViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizLeaderboardScreen(
    viewModel: JodivaViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val missions by viewModel.dailyMissions.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf("Arena") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var leaderboardScope by remember { mutableStateOf("World") }

    // Active Timed Quiz State
    var quizActive by remember { mutableStateOf(false) }
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var secondsLeft by remember { mutableIntStateOf(20) }
    var lastEarnedPoints by remember { mutableStateOf<Int?>(null) }

    val questions = remember(selectedCategory) {
        val bank = JodivaSeedCatalog.quizBank
        if (selectedCategory == "All Categories") bank else bank.filter { it.category == selectedCategory }.ifEmpty { bank }
    }

    LaunchedEffect(quizActive, currentQuestionIdx, selectedOption) {
        if (quizActive && selectedOption == -1) {
            secondsLeft = 20
            while (secondsLeft > 0 && quizActive && selectedOption == -1) {
                delay(1000L)
                secondsLeft--
            }
            if (secondsLeft == 0 && selectedOption == -1 && quizActive) {
                selectedOption = -2 // Timed out
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Quiz Arena & Rankings") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (quizActive) {
            val q = questions[currentQuestionIdx]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Question ${currentQuestionIdx + 1} / ${questions.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${secondsLeft}s", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { (currentQuestionIdx + 1).toFloat() / questions.size.toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(q.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(q.question, style = MaterialTheme.typography.titleLarge)

                        q.options.forEachIndexed { idx, opt ->
                            val isChosen = selectedOption == idx
                            val isCorrect = idx == q.correctIndex
                            val buttonText = when {
                                selectedOption == -1 -> opt
                                isCorrect -> "$opt  ✓"
                                isChosen -> "$opt  ✕"
                                else -> opt
                            }
                            OutlinedButton(
                                onClick = {
                                    if (selectedOption == -1) {
                                        selectedOption = idx
                                        if (idx == q.correctIndex) correctCount++
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quiz_option_$idx")
                            ) {
                                Text(buttonText, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        if (selectedOption != -1) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    q.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    if (currentQuestionIdx + 1 < questions.size) {
                                        currentQuestionIdx++
                                        selectedOption = -1
                                    } else {
                                        quizActive = false
                                        viewModel.completeQuiz(
                                            category = selectedCategory,
                                            correct = correctCount,
                                            total = questions.size
                                        ) { earned ->
                                            lastEarnedPoints = earned
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (currentQuestionIdx + 1 < questions.size) "Next Question" else "Finish & Claim Points")
                            }
                        }
                    }
                }
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_quiz_arena),
                        contentDescription = "Quiz Arena Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            lastEarnedPoints?.let { pts ->
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                "Quiz Complete! Score: $correctCount/${questions.size} • +$pts verified points added!",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Arena", "Missions", "Leaderboard").forEach { tab ->
                        FilterChip(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            label = { Text(tab) }
                        )
                    }
                }
            }

            when (selectedTab) {
                "Arena" -> {
                    item {
                        Text("Select Quiz Category", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("All Categories", "General Knowledge", "Science", "Mathematics", "History", "Geography", "Sports", "Current Affairs").forEach { cat ->
                                item {
                                    FilterChip(
                                        selected = selectedCategory == cat,
                                        onClick = { selectedCategory = cat },
                                        label = { Text(cat) }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("10-Question Timed Global Challenge", style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "Earn +5 verified points per correct answer (+15 bonus for 70%+ accuracy). Unlock Bronze, Silver, Gold, Diamond & Legend badges.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(
                                    onClick = {
                                        currentQuestionIdx = 0
                                        correctCount = 0
                                        selectedOption = -1
                                        lastEarnedPoints = null
                                        quizActive = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("start_quiz_button")
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Timed Challenge")
                                }
                            }
                        }
                    }
                }

                "Missions" -> {
                    items(missions, key = { it.id }) { mission ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(mission.title, style = MaterialTheme.typography.titleSmall)
                                    Text(mission.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    LinearProgressIndicator(
                                        progress = { mission.progress.toFloat() / mission.target.toFloat() },
                                        modifier = Modifier.fillMaxWidth(0.85f)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    if (mission.isCompleted) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = "Completed", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Text("+${mission.rewardPoints} pts", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
                                }
                            }
                        }
                    }
                }

                "Leaderboard" -> {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("World", currentUser?.country ?: "India").forEach { scope ->
                                FilterChip(
                                    selected = leaderboardScope == scope,
                                    onClick = { leaderboardScope = scope },
                                    label = { Text(if (scope == "World") "World Ranking" else "${countryFlag(scope)} $scope") }
                                )
                            }
                        }
                    }

                    val rankedUsers = allUsers
                        .filter { leaderboardScope == "World" || it.country == leaderboardScope }
                        .sortedByDescending { it.points }

                    itemsIndexed(rankedUsers, key = { _, u -> u.userId }) { index, user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (user.userId == currentUser?.userId)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(34.dp)
                                )
                                JodivaAvatar(name = user.displayName, size = 40.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(user.displayName, style = MaterialTheme.typography.titleSmall)
                                        BadgeAndTickRow(user.badgeTier, user.hasAchievementTick, user.isIdentityVerified)
                                    }
                                    Text("${countryFlag(user.country)} ${user.city}, ${user.country}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("${user.points} pts", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
