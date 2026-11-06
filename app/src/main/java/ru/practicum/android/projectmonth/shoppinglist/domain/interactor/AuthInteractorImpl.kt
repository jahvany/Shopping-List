package ru.practicum.android.projectmonth.shoppinglist.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.projectmonth.shoppinglist.domain.AuthRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.models.LoginCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.models.RegisterCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.AuthInteractor
import ru.practicum.android.projectmonth.shoppinglist.data.network.dto.SecurityState

class AuthInteractorImpl(
    val repository: AuthRepository
) : AuthInteractor{

    override fun authenticate(loginCredentials: LoginCredentials): Flow<SecurityState> = flow {
        val respState = repository.authenticate(loginCredentials)
        emit(respState)
    }
        .flowOn(Dispatchers.IO)

    override fun register(registerCredentials: RegisterCredentials): Flow<SecurityState> = flow {
        val respState = repository.registerUser(registerCredentials)
        emit(respState)
    }
        .flowOn(Dispatchers.IO)

    override fun recoveryPassword(email: String): Flow<SecurityState> = flow {
        val respState = repository.recoveryPassword(email)
        emit(respState)
    }
        .flowOn(Dispatchers.IO)

    override fun logout() {
        repository.logout()
    }

    override suspend fun currentUser(): String {
        return repository.currentUser()
    }

    override suspend fun restoreSession(): SecurityState = repository.restoreSession()

}
