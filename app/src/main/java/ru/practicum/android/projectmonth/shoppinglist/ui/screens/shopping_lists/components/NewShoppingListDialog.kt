package ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.ui.components.CustomTextInput

@Composable
fun NewShoppingListDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newShoppingListName by remember { mutableStateOf("") }

    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_new_shopping_list),
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(R.string.shopping_lists_new_title),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        text = {
            CustomTextInput(
                value = newShoppingListName,
                onValueChange = { newShoppingListName = it },
                labelResId = R.string.shopping_lists_new_textfield_label,
                placeholderResId = R.string.shopping_lists_new_textfield_placeholder
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = { onConfirm(newShoppingListName) }) {
                Text(
                    text = stringResource(R.string.shopping_lists_new_confirm),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.dismiss),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
fun NewShoppingListDialogPreview() {
    NewShoppingListDialog(
        onDismissRequest = { },
        onConfirm = { }
    )
}
