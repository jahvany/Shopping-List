package ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.IconCircleBg
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.RegularBrown
import kotlin.math.abs

@Composable
fun SwipeableShoppingListItem(
    item: ShoppingList,
    onItemClick: (ShoppingList) -> Unit,
    onDelete: (ShoppingList) -> Unit,
    onRename: (ShoppingList) -> Unit,
    onCopy: (ShoppingList) -> Unit,
    onIconLongClick: (ShoppingList) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val resources = LocalResources.current

    val iconsWidth = with(LocalDensity.current) { 200.dp.toPx() }
    val fullSwipeWidth = with(LocalDensity.current) { 300.dp.toPx() }
    val rightFullSwipeWidth = with(LocalDensity.current) { 300.dp.toPx() }
    val swipeThreshold = with(LocalDensity.current) { 50.dp.toPx() }

    val iconResId = remember(resources, item.iconRes) {
        resources.getIdentifier(
            item.iconRes,
            "drawable",
            context.packageName
        ).takeIf { it != 0 } ?: R.drawable.ic_shopping_list_default
    }

    val showSwipeIcons = offsetX < 0

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (showSwipeIcons) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            onRename(item)
                            offsetX = 0f
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(IconCircleBg, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = stringResource(R.string.edit_list),
                        tint = RegularBrown,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        scope.launch {
                            onCopy(item)
                            offsetX = 0f
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(IconCircleBg, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = stringResource(R.string.copy_list),
                        tint = RegularBrown,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        scope.launch {
                            onDelete(item)
                            offsetX = 0f
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(IconCircleBg, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.delete_list),
                        tint = RegularBrown,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.toInt(), 0) }
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFD0D0D0),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .clickable {
                    onItemClick(item)
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetX <= -fullSwipeWidth -> {
                                        onDelete(item)
                                        offsetX = 0f
                                    }
                                    offsetX >= rightFullSwipeWidth -> {
                                        onRename(item)
                                        offsetX = 0f
                                    }
                                    offsetX <= -iconsWidth -> {
                                        offsetX = -iconsWidth
                                    }
                                    offsetX < 0 && abs(offsetX) > swipeThreshold -> {
                                        offsetX = -iconsWidth
                                    }
                                    offsetX > 0 -> {
                                        offsetX = 0f
                                    }
                                    else -> {
                                        offsetX = 0f
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX = 0f
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = offsetX + dragAmount
                            offsetX = when {
                                newOffset > rightFullSwipeWidth -> rightFullSwipeWidth
                                newOffset < -fullSwipeWidth -> -fullSwipeWidth
                                else -> newOffset
                            }
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    )
                    .combinedClickable(
                        onClick = { },
                        onLongClick = { onIconLongClick(item) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
