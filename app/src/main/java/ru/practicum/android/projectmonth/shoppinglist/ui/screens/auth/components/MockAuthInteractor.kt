package ru.practicum.android.projectmonth.shoppinglist.ui.screens.auth.components

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.projectmonth.shoppinglist.domain.models.LoginCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.models.RegisterCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.AuthInteractor
import ru.practicum.android.projectmonth.shoppinglist.data.network.dto.SecurityState

class MockAuthInteractor : AuthInteractor {
    override fun authenticate(loginCredentials: LoginCredentials): Flow<SecurityState> = flow {
        emit(SecurityState.Default)
    }

    override fun register(registerCredentials: RegisterCredentials): Flow<SecurityState> = flow {
        emit(SecurityState.Default)
    }

    override fun recoveryPassword(email: String): Flow<SecurityState> = flow {
        emit(SecurityState.Default)
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun currentUser(): String {
        return "user@test.com"
    }

    override suspend fun restoreSession(): SecurityState {
        TODO("Not yet implemented")
    }

}
