package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.PostEntity
import com.example.ui.components.AiNoticeBanner
import com.example.ui.components.BadgeAndTickRow
import com.example.ui.components.JodivaAvatar
import com.example.ui.components.countryFlag
import com.example.ui.components.drawableIdForName
import com.example.ui.components.shareTextExternally
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.JodivaViewModel

@Composable
fun ReelsScreen(
    viewModel: JodivaViewModel,
    onCreateReel: () -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val reels by viewModel.reels.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val followRelations by viewModel.followRelations.collectAsStateWithLifecycle()
    val translatedMap by viewModel.translatedItems.collectAsStateWithLifecycle()
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var commentTargetPostId by remember { mutableStateOf<Long?>(null) }
    var reportTarget by remember { mutableStateOf<PostEntity?>(null) }

    if (reels.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Text("No Reels Yet", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upload your first short video reel to entertain the global community.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onCreateReel) { Text("Create Short Reel") }
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { reels.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_vertical_pager")
    ) { pageIndex ->
        val reel = reels[pageIndex]
        val creator = allUsers.firstOrNull { it.userId == reel.authorId }
        val isFollowing = followRelations.any { it.targetUserId == reel.authorId && it.status == "FOLLOWING" }
        val translated = translatedMap["reel_${reel.id}"]

        Box(modifier = Modifier.fillMaxSize()) {
            val resId = drawableIdForName(reel.drawableResName)
            when {
                !reel.imageUri.isNullOrBlank() -> AsyncImage(
                    model = Uri.parse(reel.imageUri),
                    contentDescription = reel.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                resId != null -> Image(
                    painter = painterResource(id = resId),
                    contentDescription = reel.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                else -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(TealPrimary, SaffronSecondary)))
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.82f)
                            )
                        )
                    )
            )

            // Top badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("JODIVA Reels • ${pageIndex + 1}/${reels.size}", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    }
                }
                Button(
                    onClick = onCreateReel,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("+ Upload Reel")
                }
            }

            // Right Action Rail
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { viewModel.toggleLikePost(reel) }) {
                        Icon(
                            imageVector = if (reel.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like reel",
                            tint = if (reel.isLikedByMe) Color(0xFFFF4D6D) else Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(reel.likesCount.toString(), color = Color.White, style = MaterialTheme.typography.labelMedium)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { commentTargetPostId = reel.id }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Comments",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(reel.commentsCount.toString(), color = Color.White, style = MaterialTheme.typography.labelMedium)
                }

                IconButton(onClick = { shareTextExternally(context, "JODIVA Reel by @${reel.authorHandle}", reel.caption) }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share reel", tint = Color.White, modifier = Modifier.size(28.dp))
                }

                IconButton(onClick = { viewModel.translateContent("reel_${reel.id}", reel.caption) }) {
                    Icon(Icons.Filled.Translate, contentDescription = "Translate caption", tint = Color.White, modifier = Modifier.size(26.dp))
                }

                IconButton(onClick = { reportTarget = reel }) {
                    Icon(Icons.Filled.Flag, contentDescription = "Report reel", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(24.dp))
                }
            }

            // Bottom Creator Info & Caption
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 78.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    JodivaAvatar(name = reel.authorName, size = 40.dp)
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "@${reel.authorHandle}",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                            BadgeAndTickRow(reel.authorBadgeTier, reel.authorHasAchievementTick)
                        }
                        Text(
                            text = "${countryFlag(reel.authorCountry)} ${reel.authorCountry}",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if (creator != null) {
                        Surface(
                            onClick = { viewModel.toggleFollow(creator) },
                            color = if (isFollowing) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isFollowing) "Following" else "Follow",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Text(
                    text = reel.caption,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = reel.hashtags,
                    color = Color(0xFF7BE8F0),
                    style = MaterialTheme.typography.labelMedium
                )

                if (translated != null) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "${prefs.preferredLanguage}: ${translated.text}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AiNoticeBanner(notice = translated.notice)
                        }
                    }
                }

                reel.musicTrack?.let { track ->
                    Surface(
                        color = Color.White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text(track, color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }

    commentTargetPostId?.let { id ->
        CommentsBottomSheet(postId = id, viewModel = viewModel, onDismiss = { commentTargetPostId = null })
    }

    reportTarget?.let { reel ->
        ReportDialog(
            targetLabel = "reel by @${reel.authorHandle}",
            onDismiss = { reportTarget = null },
            onSubmit = { reason ->
                viewModel.submitReport("REEL", reel.id.toString(), reason)
                reportTarget = null
            }
        )
    }
}
