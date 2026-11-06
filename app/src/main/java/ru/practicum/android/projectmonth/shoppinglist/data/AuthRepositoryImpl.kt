package ru.practicum.android.projectmonth.shoppinglist.data

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.projectmonth.shoppinglist.data.converter.network.AuthConverter
import ru.practicum.android.projectmonth.shoppinglist.data.network.AuthApiService
import ru.practicum.android.projectmonth.shoppinglist.domain.AuthRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.models.LoginCredentials
import ru.practicum.android.projectmonth.shoppinglist.domain.models.RegisterCredentials
import ru.practicum.android.projectmonth.shoppinglist.data.network.dto.SecurityState

class AuthRepositoryImpl(
    val sharedPreferences: SharedPreferences,
    val authApiService: AuthApiService,
    val authConverter: AuthConverter
): AuthRepository {

    override suspend fun registerUser(registerCredentials: RegisterCredentials): SecurityState {
        val response = authApiService.registerUser(authConverter.map(registerCredentials))
        val responseCode = response.code()

        if(responseCode in 200..299) {
            val respBody = response.body()
            return SecurityState.SuccessRegister("success register. User ID = ${respBody?.userid}")
        } else {
            return SecurityState.ErrorRegister(message = response.errorBody()?.string(), errCode = response.code())
        }

    }

    override suspend fun authenticate(loginCredentials: LoginCredentials): SecurityState {
        val response = authApiService.loginUser(authConverter.map(loginCredentials))
        val responseCode = response.code()

        if(responseCode in 200..299) {
            val respBody = response.body()
            sharedPreferences.edit {
                putString(ACCESS_TOKEN, respBody?.accessToken)
                putString(REFRESH_TOKEN, respBody?.refreshToken)
                putString(LOGIN, loginCredentials.email)
            }
            return SecurityState.SuccessAuth(sharedPreferences.getString(ACCESS_TOKEN, null))
        } else {
            return SecurityState.ErrorAuth(message = response.errorBody()?.string(), errCode = response.code())
        }

    }

    override suspend fun recoveryPassword(email: String): SecurityState {
        val response = authApiService.recoveryPassword(email)
        val responseCode = response.code()

        if(responseCode in 200..299) {
            val respBody = response.body()?.string()
            return SecurityState.SuccessRecoveryPasswd(respBody?: RECOVERY_REQUEST_SENT)
        } else {
            return SecurityState.ErrorRecoveryPasswd(
                message = response.errorBody()?.string(),
                errCode = response.code()
            )
        }
    }

    override fun currentUser(): String {
        return sharedPreferences.getString(LOGIN, "") ?: ""
    }

    override fun refreshAccessToken(): Flow<SecurityState> = flow {
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, null)
        if (refreshToken == null) {
            emit(SecurityState.ErrorAuth(NO_REFRESH_TOKEN, INTERNAL_ERROR_CODE))
            return@flow
        }

        val response = authApiService.refreshAccessToken(refreshToken)
        val responseCode = response.code()

        if(responseCode in 200..299) {
            val respBody = response.body()
            sharedPreferences.edit {
                putString(ACCESS_TOKEN, respBody?.accessToken)
                putString(REFRESH_TOKEN, respBody?.refreshToken)
            }
            emit(SecurityState.SuccessAuth(sharedPreferences.getString(ACCESS_TOKEN, null)))
        } else {
            emit(SecurityState.ErrorAuth(message = response.errorBody()?.string(), errCode = response.code()))
        }
    }
        .flowOn(Dispatchers.IO)


    override fun checkAuthorization(): Flow<SecurityState> = flow {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        if (accessToken == null) {
            emit(SecurityState.CheckAuthError(NO_ACCESS_TOKEN, INTERNAL_ERROR_CODE))
            return@flow
        }
        val response = authApiService.checkAuthorization(accessToken)
        val responseCode = response.code()

        if(responseCode in 200..299) {
            emit(SecurityState.CheckAuthSuccess())
        } else {
            emit(SecurityState.CheckAuthError(message = response.errorBody()?.string(), errCode = response.code()))
        }

    }
        .flowOn(Dispatchers.IO)

    override fun logout() {
        sharedPreferences.edit {
            remove(ACCESS_TOKEN)
            remove(REFRESH_TOKEN)
            remove(LOGIN)
        }
    }

    override suspend fun restoreSession(): SecurityState {
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, null)

        return if (refreshToken == null) {
            SecurityState.Default
        } else {
            SecurityState.SuccessAuth(null)
        }
    }

    companion object {
        const val INTERNAL_ERROR_CODE = -1
        const val NO_ACCESS_TOKEN = "В приложении не был сохранен access_token"
        const val NO_REFRESH_TOKEN = "В приложении не был сохранен refresh_token"
        const val RECOVERY_REQUEST_SENT = "Запрос на сброс пароля отправлен"
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val LOGIN = "login"
    }
}
