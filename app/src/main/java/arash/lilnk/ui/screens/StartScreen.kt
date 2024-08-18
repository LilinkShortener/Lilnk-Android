package arash.lilnk.ui.screens

import LoadingDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import arash.lilnk.api.isValidEmail
import arash.lilnk.api.isValidPassword
import arash.lilnk.api.loginUser
import arash.lilnk.api.registerUser
import arash.lilnk.ui.screens.navigation.Screens
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics
import arash.lilnk.utilities.Statics.REQUEST_TAG
import arash.lilnk.utilities.showSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val whyLilnk = listOf(
    "همیشه رایگان",
    "سرعت بالا",
    "آپتایم مناسب",
    "امکان کسب درآمد",
    "لینک‌های دلخواه و دائمی"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StartScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    val scrollState = rememberScrollState()
    var email by rememberSaveable { mutableStateOf("arash33a@gmail.com") }
    var password by rememberSaveable { mutableStateOf("arash33a") }
    var isLoading by rememberSaveable { mutableStateOf(false) }


    LoadingDialog(
        isLoading = isLoading,
        onDismiss = {
            Lilnk.instance?.cancelPendingRequest(REQUEST_TAG)
            isLoading = false
        }
    )

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state = scrollState)

    ) {
        Text(
            text = "به لیلینک خوش اومدین. لطفا اگر اکانت دارید وارد شوید و اگر کاربر جدید هستید ثبتنام کنید :)",
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("sample@mail.com") },
                textStyle = TextStyle(textDirection = TextDirection.Ltr),
                singleLine = true,
                shape = CardDefaults.shape,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                textStyle = TextStyle(textDirection = TextDirection.Ltr),
                singleLine = true,
                shape = CardDefaults.shape,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (!isValidEmail(email)) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "ایمیل خود را وارد کنید!"
                    )
                    return@TextButton
                }

                if (password.isEmpty()) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "رمز عبور خود را وارد کنید!"
                    )
                    return@TextButton
                }
                isLoading = true
                loginUser(email, password) { success, result ->
                    isLoading = false
                    if (success) {
                        Preferences[Statics.USER_ID] = result
                        Preferences[Statics.USER_EMAIL] = email
                        navController.navigate(Screens.Home.route)
                    } else {
                        showSnackbar(
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            message = when (result) {
                                0 -> "خطا در برقراری ارتباط با سرور!"
                                2001 -> "ایمیل یا رمز عبور اشتباه است."
                                else -> "خطای ناشناخته"
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ورود")
        }

        Button(
            onClick = {
                if (!isValidEmail(email)) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "یک ایمیل معتبر وارد کنید!"
                    )
                    return@Button
                }
                if (!isValidPassword(password)) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "رمز عبور باید حداقل ۸ کاراکتر داشته باشد و شامل حروف بزرگ، کوچک و عدد باشد!"
                    )
                    return@Button
                }
                isLoading = true
                registerUser(email, password) { success, result ->
                    isLoading = false
                    if (success) {
                        Preferences[Statics.USER_ID] = result
                        Preferences[Statics.USER_EMAIL] = email
                        navController.navigate(Screens.Home.route)
                    } else {
                        showSnackbar(
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            message = when (result) {
                                0 -> "خطا در برقراری ارتباط با سرور!"
                                1001 -> "کاربر با این ایمیل قبلا ثبت‌نام کرده است."
                                1002 -> "مشکلی در ثبت‌نام کاربر رخ داده است."
                                else -> "خطای ناشناخته"
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ثبت‌نام")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "چرا لیلینک؟",
            textAlign = TextAlign.Right,
            modifier = Modifier
                .fillMaxWidth()
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start
            ),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            whyLilnk.forEach { future ->
                AssistChip(
                    label = {
                        Text(text = future)
                    },
                    onClick = {},
                )
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun StartScreenPreview() {
//    LilnkTheme {
//        StartScreen()
//    }
//}