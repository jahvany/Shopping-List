package ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.DarkText
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.LightBrownElements
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.RegularBrown

@Composable
fun DeleteConfirmationDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_attention),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = stringResource(R.string.attention_icon)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier
                    .width(104.dp)
                    .background(
                        color = RegularBrown,
                        shape = RoundedCornerShape(46.dp)
                    )
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .width(104.dp)
                    .background(
                        color = LightBrownElements,
                        shape = RoundedCornerShape(46.dp)
                    )
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkText
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun DeleteAllListsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    DeleteConfirmationDialog(
        title = stringResource(R.string.delete_all_lists_title),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

@Composable
fun DeleteListDialog(
    listName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    DeleteConfirmationDialog(
        title = stringResource(R.string.delete_list_title, listName),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

@Preview(showBackground = true)
@Composable
fun DeleteConfirmationDialogPreview() {
    DeleteConfirmationDialog(
        title = stringResource(R.string.delete_all_lists_title),
        onDismiss = {},
        onConfirm = {}
    )
}
