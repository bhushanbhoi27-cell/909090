package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.data.model.ThemeMode
import com.example.ui.components.AiNoticeBanner
import com.example.ui.viewmodel.JodivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndSettingsScreen(
    viewModel: JodivaViewModel,
    initialTab: String = "Settings",
    onBack: () -> Unit
) {
    var activeTab by remember(initialTab) { mutableStateOf(initialTab) }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val helpMessages by viewModel.helpMessages.collectAsStateWithLifecycle()
    val tickets by viewModel.supportTickets.collectAsStateWithLifecycle()
    val loginActivity by viewModel.loginActivity.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings, Security & Help") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Settings", "AI Help", "Plans & Legal").forEach { tab ->
                FilterChip(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    label = { Text(tab) }
                )
            }
        }

        when (activeTab) {
            "Settings" -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Appearance & Language", style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ThemeMode.entries.forEach { mode ->
                                        FilterChip(
                                            selected = prefs.themeMode == mode,
                                            onClick = { viewModel.setThemeMode(mode) },
                                            label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                                        )
                                    }
                                }
                                DropdownSelectorField(
                                    label = "App & Translation Language",
                                    selected = prefs.preferredLanguage,
                                    options = GlobalCatalog.languages,
                                    displayTransform = { it },
                                    onSelect = { viewModel.setPreferredLanguage(it) }
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Notifications", style = MaterialTheme.typography.titleMedium)
                                SettingToggleRow("Followers, Likes & Comments", prefs.notifySocial) {
                                    viewModel.updateNotificationPrefs(it, prefs.notifyQuiz, prefs.notifySecurity)
                                }
                                SettingToggleRow("Quiz Challenges & Missions", prefs.notifyQuiz) {
                                    viewModel.updateNotificationPrefs(prefs.notifySocial, it, prefs.notifySecurity)
                                }
                                SettingToggleRow("Login & Security Alerts", prefs.notifySecurity) {
                                    viewModel.updateNotificationPrefs(prefs.notifySocial, prefs.notifyQuiz, it)
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
                                Text("Security & Device Management", style = MaterialTheme.typography.titleMedium)
                                SettingToggleRow(
                                    "Two-Factor Authentication (2FA)",
                                    currentUser?.twoFactorEnabled ?: false
                                ) { enabled ->
                                    currentUser?.let { viewModel.updateProfile(it.copy(twoFactorEnabled = enabled)) }
                                }
                                Text("Recent Login Activity", style = MaterialTheme.typography.labelLarge)
                                loginActivity.take(4).forEach { act ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(act.deviceName, style = MaterialTheme.typography.titleSmall)
                                            Text(
                                                "${act.location} • ${if (act.isCurrentSession) "Current Session" else "Active Session"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                OutlinedButton(
                                    onClick = { viewModel.logoutOtherDevices() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Log Out Other Devices")
                                }
                                Button(
                                    onClick = { viewModel.logoutAllDevices() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Logout From All Devices")
                                }
                            }
                        }
                    }
                }
            }

            "AI Help" -> {
                var queryDraft by remember { mutableStateOf("") }
                var ticketSubject by remember { mutableStateOf("") }
                var ticketCategory by remember { mutableStateOf("Account & Login") }
                var ticketDesc by remember { mutableStateOf("") }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AiNoticeBanner()
                    }
                    item {
                        Text("AI Help Assistant", style = MaterialTheme.typography.titleMedium)
                    }
                    items(helpMessages) { msg ->
                        Surface(
                            color = if (msg.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(msg.text, style = MaterialTheme.typography.bodyMedium)
                                msg.notice?.let { AiNoticeBanner(notice = it) }
                            }
                        }
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = queryDraft,
                                onValueChange = { queryDraft = it },
                                placeholder = { Text("Ask about login, privacy, badges, billing...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_help_input"),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    viewModel.askHelpAssistant(queryDraft)
                                    queryDraft = ""
                                },
                                modifier = Modifier.testTag("ai_help_send")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send help question")
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Complaint & Support Ticket System", style = MaterialTheme.typography.titleMedium)
                                DropdownSelectorField(
                                    label = "Issue Category",
                                    selected = ticketCategory,
                                    options = listOf("Account & Login", "Privacy & Abuse Report", "Payment & Subscription", "Technical Bug"),
                                    displayTransform = { it },
                                    onSelect = { ticketCategory = it }
                                )
                                OutlinedTextField(
                                    value = ticketSubject,
                                    onValueChange = { ticketSubject = it },
                                    label = { Text("Subject") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = ticketDesc,
                                    onValueChange = { ticketDesc = it },
                                    label = { Text("Describe the issue") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = {
                                        viewModel.submitSupportTicket(ticketSubject, ticketCategory, ticketDesc)
                                        ticketSubject = ""
                                        ticketDesc = ""
                                    },
                                    enabled = ticketSubject.isNotBlank() && ticketDesc.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Generate Support Ticket")
                                }

                                if (tickets.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Your Tracked Tickets", style = MaterialTheme.typography.titleSmall)
                                    tickets.forEach { t ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("${t.ticketNumber} • ${t.status}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                                Text(t.subject, style = MaterialTheme.typography.titleSmall)
                                                Text(t.category, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Plans & Legal" -> {
                val plans = listOf(
                    "Free" to "Core social feed, stories, messaging, quizzes, and community access.",
                    "Silver" to "Custom profile themes, extra story highlight slots, and Silver supporter frame.",
                    "Gold" to "Advanced creator analytics, Gold profile frame, and priority support.",
                    "Premium" to "Ad-free global browsing, exclusive customization, and expanded media storage."
                )
                val policies = listOf(
                    "Privacy Policy & Data Protection",
                    "Terms & Conditions",
                    "Community Guidelines & Safety",
                    "Copyright & Licensed Audio Policy",
                    "Age & Child Safety Policy",
                    "Subscription & Refund Policy"
                )

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("JODIVA Membership Plans", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Fair Play Guarantee: Quiz rankings and achievement badges depend strictly on verified performance and can never be purchased.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(plans) { (name, desc) ->
                        val isCurrent = currentUser?.subscriptionTier == name
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, style = MaterialTheme.typography.titleMedium)
                                    Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                if (isCurrent) {
                                    OutlinedButton(onClick = {}) { Text("Active") }
                                } else {
                                    Button(onClick = {
                                        currentUser?.let { viewModel.updateProfile(it.copy(subscriptionTier = name)) }
                                    }) { Text("Select") }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Global Legal & Safety Policies", style = MaterialTheme.typography.titleMedium)
                    }
                    items(policies) { policy ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(policy, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
