package ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.ui.components.TopBarButton
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.DarkText
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsTopBar(
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    darkModeEnabled: Boolean = false,
    onSearchQueryChange: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSearchClose: () -> Unit = {},
    onDeleteAllClick: () -> Unit = {},
    onSwitchThemeClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            delay(100.milliseconds)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_hint),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_clear),
                                    contentDescription = stringResource(R.string.clear_text),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(0.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            } else {
                Text(
                    text = stringResource(R.string.shopping_lists_screen_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        navigationIcon = {
            if (isSearchActive) {
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onSearchClose()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.close_search),
                        tint = DarkText
                    )
                }
            }
        },
        actions = {
            if (!isSearchActive) {
                TopBarButton(
                    iconResId = R.drawable.ic_search,
                    onClick = onSearchClick
                )
                TopBarButton(
                    iconResId = R.drawable.ic_delete,
                    onClick = onDeleteAllClick
                )
                TopBarButton(
                    iconResId = if (darkModeEnabled) {
                        R.drawable.ic_light_theme
                    } else {
                        R.drawable.ic_night_theme
                    },
                    onClick = onSwitchThemeClick
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isSearchActive) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}

@Preview(showBackground = true)
@Composable
fun ShoppingListsTopBarPreview() {
    ShoppingListsTopBar()
}
