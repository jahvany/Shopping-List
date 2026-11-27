package ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList

@Composable
fun RenameShoppingListDialog(
    shoppingList: ShoppingList,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(shoppingList.name) }

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.rename_list_title),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = {
                    Text(stringResource(R.string.list_name_label))
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.list_name_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newName) },
                enabled = newName.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.rename),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
fun RenameShoppingListDialogPreview() {
    RenameShoppingListDialog(
        shoppingList = ShoppingList(1, "Продукты", "ic_default", emptyList(), 0),
        onDismiss = {},
        onConfirm = {}
    )
}
