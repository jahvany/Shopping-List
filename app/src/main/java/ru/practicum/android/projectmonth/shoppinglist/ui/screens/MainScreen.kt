package ru.practicum.android.projectmonth.shoppinglist.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.core.navigation.Destination
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.UiSecurityState
import ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel.AuthViewModel
import ru.practicum.android.projectmonth.shoppinglist.ui.components.IllustratedMessage
import ru.practicum.android.projectmonth.shoppinglist.ui.components.MainScreenTitle

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val restoredSession by viewModel.authState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate(Destination.Auth.route)
            }
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MainScreenTitle()

        if (restoredSession is UiSecurityState.Success) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.main_screen_user, viewModel.currentUser))

                TextButton(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Destination.Auth.route) {
                            popUpTo(Destination.Main.route) {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.main_screen_logout))
                }
            }
        }

        IllustratedMessage(
            imageResId = R.drawable.img_main_screen,
            headerResId = R.string.main_screen_header,
            messageResId = R.string.main_screen_message
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}
