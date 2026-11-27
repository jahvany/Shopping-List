package ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.domain.models.Product
import ru.practicum.android.projectmonth.shoppinglist.ui.components.CustomTextInput
import kotlin.String

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProductBottomSheet(
    onNameChange: (String) -> Unit,
    onNumberChange: (Float) -> Unit,
    onUnitChange: (String) -> Unit,
    productToChange: Product? = null,
    suggestionsList: List<String> = emptyList()
) {
    val measureUnits = stringArrayResource(R.array.measure_units)

    var productName by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("") }

    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isSuggestionsDropdownExpanded by remember { mutableStateOf(false) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    val currentNumber = number.toFloatOrNull() ?: 0f
    val minusButtonEnabled = currentNumber >= 1

    // Фильтрация подсказок на основе ввода
    val filteredSuggestions = remember(productName, suggestionsList) {
        if (productName.isBlank()) {
            emptyList()
        } else {
            suggestionsList
                .filter { it.contains(productName, ignoreCase = true) }
                .take(5)
        }
    }

    // Управление видимостью выпадающего меню подсказок
    LaunchedEffect(filteredSuggestions, isTextFieldFocused, productName) {
        isSuggestionsDropdownExpanded = isTextFieldFocused &&
                filteredSuggestions.isNotEmpty() &&
                productName.isNotBlank() &&
                // Для случаев, когда единственная подсказка идентична введенному названию
                !(filteredSuggestions.size == 1 && filteredSuggestions.first() == productName)
    }

    // Заполнение полей из изменяемого продукта
    LaunchedEffect(productToChange) {
        productName = productToChange?.name ?: ""
        number = productToChange?.let { trimInteger(it.number) } ?: ""
        selectedUnit = productToChange?.measureUnit ?: ""

        onNameChange(productName)
        onNumberChange(productToChange?.number ?: 0f)
        onUnitChange(selectedUnit)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Поле ввода названия товара
            CustomTextInput(
                value = productName,
                onValueChange = {
                    productName = it
                    onNameChange(productName)
                },
                labelResId = R.string.products_new_textfield_label,
                placeholderResId = R.string.products_new_textfield_placeholder,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusEvent { focusState ->
                        isTextFieldFocused = focusState.isFocused
                    }
            )

            // Всплывающая подсказка названия товара
            if (isSuggestionsDropdownExpanded) {
                SuggestionPopup(
                    suggestions = filteredSuggestions,
                    onClick = { suggestion ->
                        productName = suggestion
                        onNameChange(suggestion)
                        isSuggestionsDropdownExpanded = false
                        isTextFieldFocused = false
                    },
                    onDismissRequest = {
                        isSuggestionsDropdownExpanded = false
                    }
                )
            }

            // Нижний ряд элементов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Поле ввода количества
                CustomTextInput(
                    value = number,
                    onValueChange = {
                        number = it

                        onNumberChange(number.toFloatOrNull() ?: 0f)
                    },
                    labelResId = R.string.products_new_textfield_number,
                    placeholderResId = R.string.products_new_textfield_number,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                )

                // Выпадающий список единиц измерения
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.measure_placeholder),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        measureUnits.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    isDropdownExpanded = false

                                    onUnitChange(unit)
                                }
                            )
                        }
                    }
                }

                // Кнопка минус
                RoundIconButton(
                    onClick = {
                        val newNumber = currentNumber - 1

                        number = trimInteger(newNumber)

                        onNumberChange(newNumber)
                    },
                    iconResId = R.drawable.ic_remove,
                    enabled = minusButtonEnabled,
                    modifier = Modifier.offset(y = (-8).dp)
                )

                // Кнопка плюс
                RoundIconButton(
                    onClick = {
                        val newNumber = currentNumber + 1

                        number = trimInteger(newNumber)

                        onNumberChange(newNumber)
                    },
                    iconResId = R.drawable.ic_add,
                    modifier = Modifier.offset(y = (-8).dp)
                )
            }
        }
    }
}

@Composable
fun SuggestionPopup(
    suggestions: List<String>,
    onClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Popup(
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onDismissRequest,
        popupPositionProvider = TopSuggestionPositionProvider()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            shape = RoundedCornerShape(4.dp),
            shadowElevation = 4.dp
        ) {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(items = suggestions) { suggestion ->
                    TextButton(
                        onClick = {
                            onClick(suggestion)
                        }
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NewProductBottomSheetPreview() {
    NewProductBottomSheet(
        onNameChange = { },
        onNumberChange = { },
        onUnitChange = { }
    )
}

// Обрезать .0 для целого количества товаров
fun trimInteger(digit: Float): String {
    return if (digit % 1.0 == 0.0) digit.toInt().toString() else digit.toString()
}

// Провайдер координат для размещения окна над текстовым полем
class TopSuggestionPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // x остается по левому краю текстового поля
        // y поднимаем вверх на высоту самого popup (popupContentSize.height)
        return IntOffset(
            x = anchorBounds.left,
            y = anchorBounds.top - popupContentSize.height
        )
    }
}
