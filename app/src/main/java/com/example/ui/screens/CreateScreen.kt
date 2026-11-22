package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.JodivaViewModel

@Composable
fun CreateScreen(
    viewModel: JodivaViewModel,
    initialMode: String = "Post",
    onPublished: () -> Unit
) {
    var mode by remember(initialMode) { mutableStateOf(initialMode) }
    var caption by remember { mutableStateOf("") }
    var hashtags by remember { mutableStateOf("#JODIVA #OneWorld") }
    var musicTrack by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var storyBgHex by remember { mutableLongStateOf(0xFF0A5C68L) }

    // Zero-permission Android Photo Picker (Google Play compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    val storyPalette = listOf(0xFF0A5C68L, 0xFFD95D39L, 0xFFC88A14L, 0xFF3F3D56L)
    val licensedSounds = listOf(
        "None",
        "World Horizon Acoustic (Licensed)",
        "Kyoto Morning Chimes (Licensed)",
        "Mumbai Monsoon Lo-Fi (Licensed)",
        "Rio Carnival Pulse (Licensed)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Creator Studio", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Share photos, stories (24h), music notes, or short reels with the global JODIVA community.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Post", "Story", "Reel").forEach { tab ->
                FilterChip(
                    selected = mode == tab,
                    onClick = { mode = tab },
                    label = { Text(tab) },
                    modifier = Modifier.testTag("create_mode_$tab")
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_media_button")
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedImageUri == null) "Choose Photo from Gallery" else "Change Selected Photo")
                }

                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected media preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                }

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = {
                        Text(
                            when (mode) {
                                "Story" -> "Story text or message (disappears in 24h)"
                                "Reel" -> "Reel description"
                                else -> "Write a post caption..."
                            }
                        )
                    },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_caption_input")
                )

                if (mode != "Story") {
                    OutlinedTextField(
                        value = hashtags,
                        onValueChange = { hashtags = it },
                        label = { Text("Hashtags") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Story Background Color", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        storyPalette.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(
                                        width = if (storyBgHex == hex) 3.dp else 1.dp,
                                        color = if (storyBgHex == hex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { storyBgHex = hex }
                            )
                        }
                    }
                }

                Text("Licensed Music & Sound Library", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    licensedSounds.take(3).forEach { track ->
                        FilterChip(
                            selected = (track == "None" && musicTrack.isEmpty()) || musicTrack == track,
                            onClick = { musicTrack = if (track == "None") "" else track },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (track != "None") {
                                        Icon(Icons.Filled.MusicNote, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                    }
                                    Text(track.substringBefore(" ("))
                                }
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (caption.isBlank() && selectedImageUri == null) return@Button
                        val finalCaption = caption.ifBlank { "Shared a moment on JODIVA!" }
                        when (mode) {
                            "Story" -> viewModel.createStory(
                                storyType = if (selectedImageUri != null) "PHOTO" else if (musicTrack.isNotEmpty()) "MUSIC" else "TEXT",
                                textContent = finalCaption,
                                musicTitle = musicTrack.ifBlank { null },
                                backgroundColorHex = storyBgHex,
                                imageUri = selectedImageUri?.toString()
                            )
                            "Reel" -> viewModel.createPost(
                                postType = "REEL",
                                caption = finalCaption,
                                hashtags = hashtags,
                                musicTrack = musicTrack.ifBlank { "JODIVA Original Sound" },
                                imageUri = selectedImageUri?.toString()
                            )
                            else -> viewModel.createPost(
                                postType = if (selectedImageUri != null) "PHOTO" else if (musicTrack.isNotEmpty()) "MUSIC" else "TEXT",
                                caption = finalCaption,
                                hashtags = hashtags,
                                musicTrack = musicTrack.ifBlank { null },
                                imageUri = selectedImageUri?.toString()
                            )
                        }
                        caption = ""
                        selectedImageUri = null
                        onPublished()
                    },
                    enabled = caption.isNotBlank() || selectedImageUri != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_content_button")
                ) {
                    Icon(Icons.Filled.Publish, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish $mode")
                }
            }
        }
    }
}
