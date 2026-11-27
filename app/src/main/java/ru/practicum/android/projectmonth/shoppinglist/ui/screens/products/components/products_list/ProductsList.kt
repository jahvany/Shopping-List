package ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.products_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.domain.models.Product
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.productListDividerColor
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ProductsList(
    products: List<Product>,
    onCheckedChange: (Product, Boolean) -> Unit,
    onProductChange: (Product) -> Unit,
    onProductDelete: (Product) -> Unit,
    isReorderable: Boolean,
    onReorder: (Int, Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorder(from.index, to.index)
    }

    LazyColumn(
        state = lazyListState
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->

            if (isReorderable) {
                ReorderableItem(
                    state = reorderableLazyColumnState,
                    key = product.id
                ) {
                    Box {
                        SwipeableProductItem(
                            item = product,
                            onCheckedChange = { isChecked ->
                                onCheckedChange(product, isChecked)
                            },
                            onProductChange = {
                                onProductChange(product)
                            },
                            onProductDelete = {
                                onProductDelete(product)
                            }
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_drag_handle),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .draggableHandle(),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                }
            } else {
                SwipeableProductItem(
                    item = product,
                    onCheckedChange = { isChecked ->
                        onCheckedChange(product, isChecked)
                    },
                    onProductChange = {
                        onProductChange(product)
                    },
                    onProductDelete = {
                        onProductDelete(product)
                    }
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = productListDividerColor
            )
        }
    }
}
