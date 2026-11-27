package ru.practicum.android.projectmonth.shoppinglist.ui.screens.auth.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun WarnTextField(
    notificationText: String
) {
    Text(
        text = notificationText,
        fontSize = 12.sp,
        color = Color.Red
    )

}

@Preview
@Composable
fun WarnTextFieldPreview(){
    WarnTextField(
        notificationText = "Тестовое предупреждение"
    )
}
