package ru.practicum.android.projectmonth.shoppinglist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RegularBrown,
    onPrimary = LightBackground,
    background = LightBackground,
    surface = LightBackground,
    onSurface = DarkText,
    onBackground = LightOnBackGround,
    primaryContainer = LightBrownElements
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBrown,
    onPrimary = NightBackground,
    background = NightBackground,
    surface = NightBackground,
    onSurface = LightText,
    onBackground = DarkOnBackGround,
    primaryContainer = NightBrownElements
)

@Composable
fun ShoppingListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
