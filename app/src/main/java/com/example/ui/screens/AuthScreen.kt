package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.GlobalCatalog
import com.example.ui.components.countryFlag
import com.example.ui.viewmodel.AuthMode
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.widthIn(max = 560.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .clip(RoundedCornerShape(22.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_jodiva_hero_globe),
                        contentDescription = "JODIVA Global Community Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "JODIVA",
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "One World, One Community.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.mode == AuthMode.LOGIN,
                        onClick = { viewModel.switchMode(AuthMode.LOGIN) },
                        label = { Text("Sign In") },
                        modifier = Modifier.testTag("tab_login")
                    )
                    FilterChip(
                        selected = state.mode == AuthMode.REGISTER || state.mode == AuthMode.OTP_VERIFY,
                        onClick = { viewModel.switchMode(AuthMode.REGISTER) },
                        label = { Text("Create Account") },
                        modifier = Modifier.testTag("tab_register")
                    )
                    FilterChip(
                        selected = state.mode == AuthMode.FORGOT_PASSWORD,
                        onClick = { viewModel.switchMode(AuthMode.FORGOT_PASSWORD) },
                        label = { Text("Recover") },
                        modifier = Modifier.testTag("tab_recover")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                state.infoMessage?.let { info ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = info,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                state.errorMessage?.let { err ->
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (state.mode) {
                            AuthMode.LOGIN -> {
                                Text(
                                    text = "Sign In to JODIVA",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                OutlinedTextField(
                                    value = state.emailOrPhone,
                                    onValueChange = { v -> viewModel.updateField { it.copy(emailOrPhone = v) } },
                                    label = { Text("Email, Mobile, or Username") },
                                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_identifier_input")
                                )
                                OutlinedTextField(
                                    value = state.password,
                                    onValueChange = { v -> viewModel.updateField { it.copy(password = v) } },
                                    label = { Text("Password") },
                                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_input")
                                )
                                Button(
                                    onClick = { viewModel.submitLogin() },
                                    enabled = !state.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("login_submit_button")
                                ) {
                                    Text("Sign In")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.quickDemoLogin() },
                                    enabled = !state.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("demo_signin_button")
                                ) {
                                    Icon(Icons.Filled.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text("Quick Explore as Aarav (India)")
                                }
                                TextButton(
                                    onClick = { viewModel.switchMode(AuthMode.FORGOT_PASSWORD) },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Forgot Password / Recover Account?")
                                }
                            }

                            AuthMode.REGISTER -> {
                                Text(
                                    text = "Global Registration",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Every member gets a unique JODIVA ID, multilingual preferences, and full privacy control.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedTextField(
                                    value = state.displayName,
                                    onValueChange = { v -> viewModel.updateField { it.copy(displayName = v) } },
                                    label = { Text("Full Name") },
                                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("register_name_input")
                                )
                                OutlinedTextField(
                                    value = state.username,
                                    onValueChange = { v -> viewModel.updateField { it.copy(username = v) } },
                                    label = { Text("Username (handle)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("register_username_input")
                                )
                                OutlinedTextField(
                                    value = state.emailOrPhone,
                                    onValueChange = { v -> viewModel.updateField { it.copy(emailOrPhone = v) } },
                                    label = { Text("Email or Mobile Number") },
                                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("register_email_input")
                                )
                                OutlinedTextField(
                                    value = state.password,
                                    onValueChange = { v -> viewModel.updateField { it.copy(password = v) } },
                                    label = { Text("Create Secure Password (6+ chars)") },
                                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("register_password_input")
                                )

                                DropdownSelectorField(
                                    label = "Country",
                                    selected = "${countryFlag(state.country)} ${state.country}",
                                    options = GlobalCatalog.countries,
                                    displayTransform = { "${countryFlag(it)} $it" },
                                    onSelect = { c -> viewModel.updateField { it.copy(country = c) } }
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = state.stateProvince,
                                        onValueChange = { v -> viewModel.updateField { it.copy(stateProvince = v) } },
                                        label = { Text("State / Province") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = state.city,
                                        onValueChange = { v -> viewModel.updateField { it.copy(city = v) } },
                                        label = { Text("City / Village") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                DropdownSelectorField(
                                    label = "Preferred Language",
                                    selected = state.preferredLanguage,
                                    options = GlobalCatalog.languages,
                                    displayTransform = { it },
                                    onSelect = { l -> viewModel.updateField { it.copy(preferredLanguage = l) } }
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (state.isPrivateAccount) "Private Account" else "Public Account",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = if (state.isPrivateAccount) {
                                                "Only approved followers can view posts & stories."
                                            } else {
                                                "Anyone worldwide can follow & view public posts."
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = state.isPrivateAccount,
                                        onCheckedChange = { checked ->
                                            viewModel.updateField { it.copy(isPrivateAccount = checked) }
                                        }
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Optional Details (Skip Anytime)",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                            TextButton(
                                                onClick = {
                                                    viewModel.updateField { it.copy(gender = "", religion = "") }
                                                }
                                            ) {
                                                Text("Skip")
                                            }
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedTextField(
                                                value = state.gender,
                                                onValueChange = { v -> viewModel.updateField { it.copy(gender = v) } },
                                                label = { Text("Gender (Optional)") },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = state.religion,
                                                onValueChange = { v -> viewModel.updateField { it.copy(religion = v) } },
                                                label = { Text("Religion (Optional)") },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = { viewModel.beginRegistrationOtp() },
                                    enabled = !state.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("register_continue_otp_button")
                                ) {
                                    Text("Continue & Verify OTP")
                                }
                            }

                            AuthMode.OTP_VERIFY -> {
                                Text(
                                    text = "OTP Verification",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Enter the 6-digit verification code to activate your JODIVA ID.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedTextField(
                                    value = state.otpCode,
                                    onValueChange = { v -> viewModel.updateField { it.copy(otpCode = v) } },
                                    label = { Text("6-Digit OTP Code") },
                                    leadingIcon = { Icon(Icons.Filled.Security, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("otp_code_input")
                                )
                                Button(
                                    onClick = { viewModel.verifyOtpAndCreateAccount() },
                                    enabled = !state.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("verify_otp_button")
                                ) {
                                    Text("Verify & Create Account")
                                }
                                TextButton(onClick = { viewModel.switchMode(AuthMode.REGISTER) }) {
                                    Text("Back to Edit Registration")
                                }
                            }

                            AuthMode.FORGOT_PASSWORD -> {
                                Text(
                                    text = "Account Recovery",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                OutlinedTextField(
                                    value = state.emailOrPhone,
                                    onValueChange = { v -> viewModel.updateField { it.copy(emailOrPhone = v) } },
                                    label = { Text("Registered Email, Mobile, or Username") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = state.password,
                                    onValueChange = { v -> viewModel.updateField { it.copy(password = v) } },
                                    label = { Text("New Secure Password") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = { viewModel.submitAccountRecovery() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text("Reset Password")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectorField(
    label: String,
    selected: String,
    options: List<String>,
    displayTransform: (String) -> String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Filled.Language, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(displayTransform(item)) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
