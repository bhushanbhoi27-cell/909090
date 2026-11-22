package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.JodivaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN, REGISTER, OTP_VERIFY, FORGOT_PASSWORD
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val displayName: String = "",
    val username: String = "",
    val emailOrPhone: String = "",
    val password: String = "",
    val otpCode: String = "",
    val expectedOtpCode: String = "482910",
    val country: String = "India",
    val stateProvince: String = "Maharashtra",
    val city: String = "Mumbai",
    val preferredLanguage: String = "English",
    val gender: String = "",
    val religion: String = "",
    val isPrivateAccount: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(private val repository: JodivaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun switchMode(mode: AuthMode) {
        _uiState.update { it.copy(mode = mode, errorMessage = null, infoMessage = null) }
    }

    fun updateField(transform: (AuthUiState) -> AuthUiState) {
        _uiState.update { transform(it).copy(errorMessage = null) }
    }

    fun submitLogin() {
        val state = _uiState.value
        if (state.emailOrPhone.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your email/mobile/username and password.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.loginUser(state.emailOrPhone, state.password)
            result.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun beginRegistrationOtp() {
        val state = _uiState.value
        if (state.displayName.isBlank() || state.username.isBlank() || state.emailOrPhone.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in your name, username, and email or phone.") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters for security.") }
            return
        }
        val generatedOtp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                mode = AuthMode.OTP_VERIFY,
                expectedOtpCode = generatedOtp,
                otpCode = "",
                infoMessage = "Verification OTP dispatched to ${state.emailOrPhone}. Demo code: $generatedOtp",
                errorMessage = null
            )
        }
    }

    fun verifyOtpAndCreateAccount() {
        val state = _uiState.value
        if (state.otpCode.trim() != state.expectedOtpCode) {
            _uiState.update { it.copy(errorMessage = "Invalid OTP code. Enter ${state.expectedOtpCode} to verify.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.registerUser(
                displayName = state.displayName,
                username = state.username,
                emailOrPhone = state.emailOrPhone,
                password = state.password,
                country = state.country,
                stateProvince = state.stateProvince,
                city = state.city,
                preferredLanguage = state.preferredLanguage,
                gender = state.gender,
                religion = state.religion,
                isPrivateAccount = state.isPrivateAccount
            )
            result.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun submitAccountRecovery() {
        val state = _uiState.value
        if (state.emailOrPhone.isBlank() || state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Enter your email/username and a new 6+ character password.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.recoverAccount(state.emailOrPhone, state.password)
            result.onSuccess { msg ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mode = AuthMode.LOGIN,
                        infoMessage = msg,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    fun quickDemoLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.quickDemoSignIn()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    companion object {
        fun factory(repository: JodivaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(repository) as T
                }
            }
    }
}
