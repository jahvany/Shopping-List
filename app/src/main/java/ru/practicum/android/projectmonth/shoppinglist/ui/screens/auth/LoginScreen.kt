package ru.practicum.android.projectmonth.shoppinglist.ui.screens.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import ru.practicum.android.projectmonth.shoppinglist.ui.components.CustomTextInput
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.auth.components.MockAuthInteractor
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.auth.components.WarnTextField

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {

    var email by rememberSaveable  { mutableStateOf("") }
    var password by rememberSaveable  { mutableStateOf("") }
    var activePwdAndButtonElements by rememberSaveable  { mutableStateOf(false) }

    var errorMsg by rememberSaveable { mutableStateOf("") }
    val state by viewModel.authState.collectAsStateWithLifecycle()

    val onLogin: () -> Unit = {
        if (activePwdAndButtonElements) {
            viewModel.login(email, password)
        }
    }

    when(state) {
        is UiSecurityState.Default -> { errorMsg = "" }
        is UiSecurityState.Loading -> { errorMsg = "" }
        is UiSecurityState.Error -> { errorMsg = (state as UiSecurityState.Error).errMsg}
        is UiSecurityState.Success -> {
            navController.navigate(Destination.ShoppingLists.route)
            viewModel.refreshStates()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextInput(
                value = email,
                onValueChange = { email = it },
                labelResId = R.string.email,
                placeholderResId = R.string.enter_email,
                modifier = Modifier.fillMaxWidth(),
                includeClearIcon = true
            )

            CustomTextInput(
                value = password,
                onValueChange = {
                    if (activePwdAndButtonElements) {
                        password = it
                    }
                    if (it.trim() == "") {
                        password = it
                    }
                },
                labelResId = R.string.password,
                placeholderResId = R.string.enter_password,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onLogin() }),
                includeClearIcon = true
            )
            if (errorMsg.isNotBlank()) {
                WarnTextField(
                    notificationText = errorMsg
                )
            }
            if (!viewModel.isValidEmail(email)) {
                WarnTextField(
                    notificationText = if (email.isBlank()) "" else stringResource(R.string.email_wrong_format)
                )
                activePwdAndButtonElements = false
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                activePwdAndButtonElements = true
            }
        }

        Image(
            painter = painterResource(R.drawable.img_main_screen),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 44.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
        TextButton(
            enabled = activePwdAndButtonElements,
            onClick = onLogin
        ) {

            Text(
                text = stringResource(R.string.enter_in_shoppinglist),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        TextButton(onClick = {
            navController.navigate(Destination.RecoveryPassword.route)
        }) {

            Text(
                text = stringResource(R.string.password_recovery),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        navController = rememberNavController(),
        viewModel = AuthViewModel(MockAuthInteractor())
    )
}
