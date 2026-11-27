package ru.practicum.android.projectmonth.shoppinglist.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.koinInject
import ru.practicum.android.projectmonth.shoppinglist.domain.ThemeRepository

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
    themeRepository: ThemeRepository = koinInject(),
    content: @Composable () -> Unit
) {

    val darkModeEnabled by themeRepository.darkModeEnabled.collectAsState(
        initial = isSystemInDarkTheme()
    )

    val colorScheme = when {
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            if (darkModeEnabled) {
//                dynamicDarkColorScheme(LocalContext.current)
//            } else {
//                dynamicLightColorScheme(LocalContext.current)
//            }
//        }
        darkModeEnabled -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
