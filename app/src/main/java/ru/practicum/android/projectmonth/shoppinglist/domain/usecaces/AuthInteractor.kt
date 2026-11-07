package ru.practicum.android.projectmonth.shoppinglist.domain.usecaces

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.projectmonth.shoppinglist.domain.models.LoginCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.models.RegisterCredentials
import ru.practicum.android.projectmonth.shoppinglist.data.network.dto.SecurityState

interface AuthInteractor {
    fun authenticate(loginCredentials: LoginCredentials) : Flow<SecurityState>
    fun register(registerCredentials: RegisterCredentials) : Flow<SecurityState>
    fun recoveryPassword(email: String) : Flow<SecurityState>
    fun logout()
    suspend fun currentUser(): String
    suspend fun restoreSession(): SecurityState
}
