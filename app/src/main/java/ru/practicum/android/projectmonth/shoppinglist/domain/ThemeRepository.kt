package ru.practicum.android.projectmonth.shoppinglist.domain

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val darkModeEnabled: Flow<Boolean>
    suspend fun switchTheme()
}