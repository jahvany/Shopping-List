package ru.practicum.android.projectmonth.shoppinglist.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.projectmonth.shoppinglist.domain.models.LoginCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.models.RegisterCredentials
import ru.practicum.android.projectmonth.shoppinglist.data.network.dto.SecurityState

interface AuthRepository {

    suspend fun registerUser(registerCredentials: RegisterCredentials): SecurityState
    suspend fun authenticate(loginCredentials: LoginCredentials): SecurityState
    suspend fun recoveryPassword(email: String): SecurityState
    fun currentUser(): String
    fun refreshAccessToken(): Flow<SecurityState>
    fun checkAuthorization(): Flow<SecurityState>
    fun logout()

    suspend fun restoreSession(): SecurityState
}
