package ru.practicum.android.projectmonth.shoppinglist.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun TopBarButton(
    @DrawableRes iconResId: Int,
    onClick: () -> Unit = { },
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null
        )
    }
}
