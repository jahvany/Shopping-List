package ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun RoundIconButton(
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(40.dp)
    ) {
        Icon(
            painterResource(iconResId),
            contentDescription = null
        )
    }
}
