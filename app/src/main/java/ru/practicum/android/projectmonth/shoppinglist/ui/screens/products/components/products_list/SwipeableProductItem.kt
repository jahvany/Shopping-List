package ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.products_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.domain.models.Product
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.RoundIconButton
import kotlin.math.abs

@Composable
fun SwipeableProductItem(
    item: Product,
    onCheckedChange: (Boolean) -> Unit,
    onProductChange: (Product) -> Unit,
    onProductDelete: (Product) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    // Ширина для 2 иконок в кружочках (40 x 2 + 4 + 16 x 2) - размеры и отступы
    val iconsWidth = with(LocalDensity.current) { 116.dp.toPx() }
    val swipeThreshold = with(LocalDensity.current) { 50.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .matchParentSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка редактирования
            RoundIconButton(
                iconResId = R.drawable.ic_edit,
                onClick = {
                    onProductChange(item)
                    offsetX = 0f
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Кнопка удаления
            RoundIconButton(
                iconResId = R.drawable.ic_delete,
                onClick = {
                    onProductDelete(item)
                    offsetX = 0f
                }
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        // Основной контент (перемещаемый)
        ProductItem(
            item = item,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (abs(offsetX) > swipeThreshold) {
                                -iconsWidth
                            } else {
                                0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = offsetX + dragAmount
                            offsetX = when {
                                newOffset > 0 -> 0f
                                newOffset < -iconsWidth -> -iconsWidth
                                else -> newOffset
                            }
                        }
                    )
                }
                .clickable {
                    if (offsetX != 0f) {
                        offsetX = 0f
                    } else {
                        onCheckedChange(!item.checked)
                    }
                }
                .background(color = MaterialTheme.colorScheme.surface)
        )
    }
}
