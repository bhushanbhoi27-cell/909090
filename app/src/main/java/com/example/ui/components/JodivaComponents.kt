package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ai.AiResult
import com.example.data.model.PostEntity
import com.example.ui.theme.BadgeBronze
import com.example.ui.theme.BadgeDiamond
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.BadgeLegend
import com.example.ui.theme.BadgeSilver
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBlue

fun countryFlag(country: String): String = when (country.lowercase()) {
    "india" -> "\uD83C\uDDEE\uD83C\uDDF3"
    "united states", "usa" -> "\uD83C\uDDFA\uD83C\uDDF8"
    "united kingdom", "uk" -> "\uD83C\uDDEC\uD83C\uDDE7"
    "canada" -> "\uD83C\uDDE8\uD83C\uDDE6"
    "japan" -> "\uD83C\uDDEF\uD83C\uDDF5"
    "germany" -> "\uD83C\uDDE9\uD83C\uDDEA"
    "france" -> "\uD83C\uDDEB\uD83C\uDDF7"
    "brazil" -> "\uD83C\uDDE7\uD83C\uDDF7"
    "australia" -> "\uD83C\uDDE6\uD83C\uDDFA"
    "united arab emirates" -> "\uD83C\uDDE6\uD83C\uDDEA"
    "singapore" -> "\uD83C\uDDF8\uD83C\uDDEC"
    "south korea" -> "\uD83C\uDDF0\uD83C\uDDF7"
    else -> "\uD83C\uDF0D"
}

fun badgeColorFor(tier: String): Color = when (tier) {
    "Legend" -> BadgeLegend
    "Diamond" -> BadgeDiamond
    "Gold" -> BadgeGold
    "Silver" -> BadgeSilver
    else -> BadgeBronze
}

fun drawableIdForName(name: String?): Int? = when (name) {
    "img_post_kyoto" -> R.drawable.img_post_kyoto
    "img_post_mumbai" -> R.drawable.img_post_mumbai
    "img_jodiva_hero_globe" -> R.drawable.img_jodiva_hero_globe
    "img_quiz_arena" -> R.drawable.img_quiz_arena
    else -> null
}

fun shareTextExternally(context: Context, title: String, content: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$content\n\n— Shared via JODIVA (One World, One Community)")
    }
    context.startActivity(Intent.createChooser(intent, "Share via JODIVA"))
}

@Composable
fun JodivaAvatar(
    name: String,
    avatarUri: String? = null,
    size: Dp = 44.dp,
    showOnlineDot: Boolean = false
) {
    Box(modifier = Modifier.size(size)) {
        if (!avatarUri.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(avatarUri),
                contentDescription = "$name profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape)
            )
        } else {
            val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "J"
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(TealPrimary, SaffronSecondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    style = if (size >= 56.dp) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (showOnlineDot) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(SuccessGreen)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

@Composable
fun BadgeAndTickRow(
    badgeTier: String,
    hasAchievementTick: Boolean,
    isIdentityVerified: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isIdentityVerified) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = "Identity verified",
                tint = VerifiedBlue,
                modifier = Modifier.size(15.dp)
            )
        }
        if (hasAchievementTick) {
            Icon(
                imageVector = Icons.Filled.Stars,
                contentDescription = "Quiz achievement tick",
                tint = badgeColorFor(badgeTier),
                modifier = Modifier.size(16.dp)
            )
        }
        Surface(
            color = badgeColorFor(badgeTier).copy(alpha = 0.16f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = badgeTier,
                style = MaterialTheme.typography.labelSmall,
                color = badgeColorFor(badgeTier),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun AiNoticeBanner(
    notice: String = AiResult.FIREBASE_REQUIRED_NOTICE,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "AI setup notice",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = notice,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun PostMediaVisual(
    post: PostEntity,
    modifier: Modifier = Modifier
) {
    val drawableId = drawableIdForName(post.drawableResName)
    when {
        !post.imageUri.isNullOrBlank() -> {
            AsyncImage(
                model = Uri.parse(post.imageUri),
                contentDescription = post.caption,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
        }
        drawableId != null -> {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = post.caption,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
        }
    }
}

@Composable
fun PostCard(
    post: PostEntity,
    isOwner: Boolean,
    preferredLanguage: String,
    translatedResult: AiResult?,
    onLike: () -> Unit,
    onCommentClick: () -> Unit,
    onSave: () -> Unit,
    onTranslate: () -> Unit,
    onClearTranslation: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
    onAuthorClick: () -> Unit
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAuthorClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    JodivaAvatar(name = post.authorName, size = 42.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (post.authorIsPrivate) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Private account",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            BadgeAndTickRow(
                                badgeTier = post.authorBadgeTier,
                                hasAchievementTick = post.authorHasAchievementTick
                            )
                        }
                        Text(
                            text = "@${post.authorHandle} • ${countryFlag(post.authorCountry)} ${post.authorCountry}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("post_menu_${post.id}")
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Post options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        if (isOwner) {
                            DropdownMenuItem(
                                text = { Text("Edit caption") },
                                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete post") },
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Report content") },
                            leadingIcon = { Icon(Icons.Filled.Flag, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onReportClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PostMediaVisual(post = post)

            if (!post.imageUri.isNullOrBlank() || drawableIdForName(post.drawableResName) != null) {
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (!post.musicTrack.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.MusicNote,
                            contentDescription = "Music track",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = post.musicTrack,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = post.caption,
                style = MaterialTheme.typography.bodyLarge
            )
            if (post.hashtags.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.hashtags,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (translatedResult != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Translated to $preferredLanguage",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = onClearTranslation) {
                                Text("Hide")
                            }
                        }
                        Text(
                            text = translatedResult.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        AiNoticeBanner(notice = translatedResult.notice)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLike,
                        modifier = Modifier.testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like post",
                            tint = if (post.isLikedByMe) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.likesCount.toString(),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier.testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Comments",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.commentsCount.toString(),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { shareTextExternally(context, "Post by @${post.authorHandle}", post.caption) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share post",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onTranslate,
                        modifier = Modifier.testTag("translate_post_${post.id}")
                    ) {
                        Icon(
                            Icons.Filled.Translate,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Translate")
                    }
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier.testTag("save_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isSavedByMe) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save post",
                            tint = if (post.isSavedByMe) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
