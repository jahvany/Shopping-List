package ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.products_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.practicum.android.projectmonth.shoppinglist.domain.models.Product
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.trimInteger

@Composable
fun ProductItem(
    item: Product,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column {
            Text(
                text = item.name,
                style = if (item.checked) {
                    MaterialTheme.typography.labelLarge.copy(
                        textDecoration = TextDecoration.LineThrough
                    )
                } else {
                    MaterialTheme.typography.labelLarge
                }
            )
            Text(
                text = "${trimInteger(item.number)} ${item.measureUnit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
