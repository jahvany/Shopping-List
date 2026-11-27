package ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.domain.models.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsMenu(
    onDismissRequest: () -> Unit,
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    onDeleteAllClick: () -> Unit,
    onClearPurchasedClick: () -> Unit
) {
    // Состояние видимости всплывающего меню сортировки
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.padding(bottom = 16.dp)) {

            // Пункт "Сортировка" (Контейнер для DropdownMenu)
            Box {
                val sortLabel = when (currentSortType) {
                    SortType.ALPHABETICAL -> stringResource(R.string.products_menu_sort_alphabetical)
                    SortType.CUSTOM -> stringResource(R.string.products_menu_sort_custom)
                    SortType.NONE -> stringResource(R.string.products_menu_sort_none)
                }

                ProductsMenuItem(
                    textResId = R.string.products_menu_sort,
                    iconResId = R.drawable.ic_sort,
                    onClick = {
                        isSortMenuExpanded = true
                    },
                    supportingContent = { Text(sortLabel) }
                )

                // Всплывающее меню вариантов сортировки
                DropdownMenu(
                    expanded = isSortMenuExpanded,
                    onDismissRequest = { isSortMenuExpanded = false },
                ) {
                    // Вариант: По алфавиту
                    SortTypeMenuItem(
                        textResId = R.string.products_menu_sort_alphabetical,
                        iconResId = R.drawable.ic_sort_alpha,
                        isSelected = (currentSortType == SortType.ALPHABETICAL),
                        onClick = {
                            onSortTypeSelected(SortType.ALPHABETICAL)
                            isSortMenuExpanded = false
                        }
                    )

                    // Вариант: Пользовательская
                    SortTypeMenuItem(
                        textResId = R.string.products_menu_sort_custom,
                        iconResId = R.drawable.ic_sort_custom,
                        isSelected = (currentSortType == SortType.CUSTOM),
                        onClick = {
                            onSortTypeSelected(SortType.CUSTOM)
                            isSortMenuExpanded = false
                        }
                    )
                }
            }

            // Пункт "Удалить все"
            ProductsMenuItem(
                textResId = R.string.products_menu_delete_all,
                iconResId = R.drawable.ic_delete,
                onClick = {
                    onDeleteAllClick()
                    onDismissRequest()
                }
            )

            // Пункт "Очистить купленные"
            ProductsMenuItem(
                textResId = R.string.products_menu_clear_purchased,
                iconResId = R.drawable.ic_clear_products,
                onClick = {
                    onClearPurchasedClick()
                    onDismissRequest()
                }
            )
        }
    }
}

@Composable
fun ProductsMenuItem(
    @StringRes textResId: Int,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(textResId),
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        supportingContent = supportingContent,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            headlineColor = MaterialTheme.colorScheme.onSurface,
            supportingColor = Color(0xFF4A4459),
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

@Composable
fun SortTypeMenuItem(
    @StringRes textResId: Int,
    @DrawableRes iconResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(textResId),
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null
            )
        },
        trailingIcon = {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        },
        onClick = onClick,
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
