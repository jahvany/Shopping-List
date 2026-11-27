package ru.practicum.android.projectmonth.shoppinglist.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.projectmonth.shoppinglist.domain.ThemeRepository

class ThemeRepositoryImpl(private val dataStore: DataStore<Preferences>) : ThemeRepository {
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    override val darkModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    override suspend fun switchTheme(darkModeEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = darkModeEnabled
        }
    }
}