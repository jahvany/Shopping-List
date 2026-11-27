package ru.practicum.android.projectmonth.shoppinglist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject
import ru.practicum.android.projectmonth.shoppinglist.domain.ThemeRepository

private val LightColorScheme = lightColorScheme(
    primary = RegularBrown,
    onPrimary = LightBackground,
    background = LightBackground,
    onBackground = DarkOnBackground,
    surface = LightBackground,
    surfaceVariant = LightBrownSurface,
    surfaceContainer = LightDropdownColor,
    surfaceContainerLow = BottomSheetPeach,
    onSurface = DarkText,
    onSurfaceVariant = MediumDarkText,
    primaryContainer = LightBrownElements,
    secondaryContainer = LightBrownElementsSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBrown,
    onPrimary = NightBackground,
    background = NightBackground,
    onBackground = LightOnBackground,
    surface = NightBackground,
    surfaceVariant = NightBrownSurface,
    surfaceContainer = NightDropdownColor,
    surfaceContainerLow = DarkText,
    onSurface = LightText,
    onSurfaceVariant = MediumLightText,
    primaryContainer = NightBrownElements,
    secondaryContainer = NightBrownElementsSecondary
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
//        Динамические цвета (на всякий случай)
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
