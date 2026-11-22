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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatThreadEntity
import com.example.ui.components.AiNoticeBanner
import com.example.ui.components.JodivaAvatar
import com.example.ui.components.countryFlag
import com.example.ui.viewmodel.JodivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: JodivaViewModel,
    initialThreadId: String? = null,
    onBack: () -> Unit
) {
    val threads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    var activeThreadId by remember(initialThreadId) { mutableStateOf(initialThreadId) }
    val activeThread = remember(threads, activeThreadId) {
        threads.firstOrNull { it.threadId == activeThreadId }
    }

    if (activeThread != null) {
        ChatConversationView(
            thread = activeThread,
            preferredLanguage = prefs.preferredLanguage,
            autoTranslate = prefs.autoTranslate,
            viewModel = viewModel,
            onBack = { activeThreadId = null }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Messages & Groups") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text("Online", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = currentUser?.isOnlineVisible ?: true,
                        onCheckedChange = { enabled ->
                            currentUser?.let { viewModel.updateProfile(it.copy(isOnlineVisible = enabled)) }
                        }
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Text(
                                "Private chats use end-to-end security & instant ${prefs.preferredLanguage} translation.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            items(threads, key = { it.threadId }) { thread ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.markThreadRead(thread.threadId)
                            activeThreadId = thread.threadId
                        }
                        .testTag("chat_thread_${thread.threadId}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JodivaAvatar(name = thread.title, size = 48.dp, showOnlineDot = thread.isOnline)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(thread.title, style = MaterialTheme.typography.titleSmall)
                                Text(countryFlag(thread.participantCountry), style = MaterialTheme.typography.labelMedium)
                            }
                            Text(
                                thread.lastMessagePreview,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        if (thread.unreadCount > 0) {
                            Badge { Text(thread.unreadCount.toString()) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatConversationView(
    thread: ChatThreadEntity,
    preferredLanguage: String,
    autoTranslate: Boolean,
    viewModel: JodivaViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.observeMessages(thread.threadId).collectAsStateWithLifecycle(initialValue = emptyList())
    val translatedMap by viewModel.translatedItems.collectAsStateWithLifecycle()
    var messageDraft by remember { mutableStateOf("") }

    LaunchedEffect(thread.threadId) {
        viewModel.markThreadRead(thread.threadId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(thread.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${if (thread.isEncrypted) "\uD83D\uDD12 Encrypted • " else ""}${thread.subtitle}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to chats")
                }
            },
            actions = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                    Icon(Icons.Filled.Translate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = autoTranslate,
                        onCheckedChange = { viewModel.setAutoTranslate(it) }
                    )
                }
            }
        )

        if (autoTranslate) {
            AiNoticeBanner(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageBubble(
                    message = msg,
                    preferredLanguage = preferredLanguage,
                    translatedText = translatedMap["msg_${msg.id}"]?.text,
                    onTranslate = { viewModel.translateContent("msg_${msg.id}", msg.text) },
                    onReact = { emoji -> viewModel.reactToMessage(msg.id, emoji) },
                    onDelete = { viewModel.deleteMessage(msg.id) }
                )
            }
        }

        Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {
                    viewModel.sendChatMessage(
                        threadId = thread.threadId,
                        text = "Voice note recorded",
                        messageType = "VOICE",
                        voiceDurationSec = 12
                    )
                }) {
                    Icon(Icons.Filled.Mic, contentDescription = "Send voice message")
                }
                OutlinedTextField(
                    value = messageDraft,
                    onValueChange = { messageDraft = it },
                    placeholder = { Text("Message in any language...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        viewModel.sendChatMessage(thread.threadId, messageDraft)
                        messageDraft = ""
                    },
                    modifier = Modifier.testTag("send_chat_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send message")
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessageEntity,
    preferredLanguage: String,
    translatedText: String?,
    onTranslate: () -> Unit,
    onReact: (String) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (message.isFromMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!message.isFromMe) {
                    Text(message.senderName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(2.dp))
                }
                if (message.messageType == "VOICE") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Voice message (${message.voiceDurationSec}s)", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Text(message.text, style = MaterialTheme.typography.bodyMedium)
                }

                if (translatedText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$preferredLanguage: $translatedText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("\u2764\uFE0F", "\uD83D\uDC4D", "\uD83D\uDD25").forEach { emoji ->
                            Text(
                                text = emoji,
                                modifier = Modifier
                                    .clickable { onReact(emoji) }
                                    .padding(2.dp)
                            )
                        }
                        message.reactionEmoji?.let {
                            Text(it, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onTranslate) {
                            Text("Translate", style = MaterialTheme.typography.labelSmall)
                        }
                        if (message.isFromMe) {
                            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete message", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
