package ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.projectmonth.shoppinglist.domain.models.LoginCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.models.RegisterCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.AuthInteractor
import ru.practicum.android.projectmonth.shoppinglist.data.network.dto.SecurityState
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.UiSecurityState
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.UiSecurityState.Default
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.UiSecurityState.Error
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.UiSecurityState.Loading
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.UiSecurityState.Success

open class AuthViewModel(
    val interactor : AuthInteractor
) : ViewModel() {

    private val _authState = MutableStateFlow<UiSecurityState>(Default)
    val authState: StateFlow<UiSecurityState> = _authState.asStateFlow()
    private val _registerState = MutableStateFlow<UiSecurityState>(Default)
    val registerState: StateFlow<UiSecurityState> = _registerState.asStateFlow()
    private val _recoveryPasswdState = MutableStateFlow<UiSecurityState>(Default)
    val recoveryPasswdState: StateFlow<UiSecurityState> = _recoveryPasswdState.asStateFlow()

    var currentUser: String = ""
        private set

    init {
        restoreSession()
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Loading
            interactor.register(RegisterCredentials(email, password))
                .collect {
                    resolveSecurityState(it, _registerState)
                }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = Loading
        viewModelScope.launch {
            interactor.authenticate(LoginCredentials(email, password))
                .collect {
                    resolveSecurityState(it, _authState)
                }
        }
    }

    fun recoveryPassword(email: String) {
        _recoveryPasswdState.value = Loading
        viewModelScope.launch {
            interactor.recoveryPassword(email)
                .collect {
                    resolveSecurityState(it, _recoveryPasswdState)
                }
        }
    }

    fun logout() {
        interactor.logout()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val result = interactor.restoreSession()

            if (result is SecurityState.SuccessAuth) {
                currentUser = interactor.currentUser()
            }

            resolveSecurityState(result, _authState)
        }
    }

    private fun resolveSecurityState(
        it: SecurityState,
        state: MutableStateFlow<UiSecurityState>
    ) {
        when(it) {
            SecurityState.Default -> {}
            SecurityState.Loading -> {
                state.value = Loading
            }

            is SecurityState.SuccessAuth,
            is SecurityState.SuccessRegister,
            is SecurityState.CheckAuthSuccess -> {
                state.value = Success()
            }
            is SecurityState.SuccessRecoveryPasswd -> {
                state.value = Success(it.message)
            }

            is SecurityState.ErrorState -> {
                state.value = Error("code=${it.errCode} ${it.message}")
            }
        }

    }

    fun refreshStates() {
        _authState.value = Default
        _registerState.value = Default
        _recoveryPasswdState.value = Default

    }
}
