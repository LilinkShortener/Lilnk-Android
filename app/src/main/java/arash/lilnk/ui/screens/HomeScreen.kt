package arash.lilnk.ui.screens

import LoadingDialog
import android.util.Log
import android.widget.Toast
import arash.lilnk.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import arash.lilnk.api.shortenLink
import arash.lilnk.ui.components.DashedLine
import arash.lilnk.ui.components.LabeledSwitch
import arash.lilnk.ui.dialogs.ResultBottomSheet
import arash.lilnk.ui.theme.LilnkTheme
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Statics.REQUEST_TAG
import arash.lilnk.utilities.isValidCustomUrl
import arash.lilnk.utilities.isValidUrl
import arash.lilnk.utilities.showSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {

    var longUrl by rememberSaveable { mutableStateOf("") }
    var customUrl by rememberSaveable { mutableStateOf("") }
    var showAds by remember { mutableStateOf(true) }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showResult by rememberSaveable { mutableStateOf(false) }
    var shortUrl by rememberSaveable { mutableStateOf("") }

    LoadingDialog(
        isLoading = isLoading,
        onDismiss = {
            Lilnk.instance?.cancelPendingRequest(REQUEST_TAG)
            isLoading = false
        }
    )

    ResultBottomSheet(
        shortenLink = shortUrl,
        hasAds = showAds,
        showResult = showResult,
        onDismiss = { showResult = false },
    )


    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "لینک طولانی خود را اینجا وارد کنید",
            textAlign = TextAlign.Right,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                onClick = { /*TODO*/ },
                modifier = Modifier.size(OutlinedTextFieldDefaults.MinHeight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_paste),
                        contentDescription = "Paste URL",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
//            FloatingActionButton(
//                onClick = { /*TODO*/ },
//                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_paste),
//                    contentDescription = "Paste URL",
//                    modifier = Modifier.size(22.dp)
//                )
//            }
            Spacer(modifier = Modifier.width(8.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                OutlinedTextField( /* original_link */
                    value = longUrl,
                    onValueChange = { longUrl = it },
                    placeholder = { Text("https://google.com/LongURL") },
                    textStyle = TextStyle(textDirection = TextDirection.Ltr),
                    singleLine = true,
                    shape = CardDefaults.shape,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "لینک خود را شخصی سازی کنید",
            textAlign = TextAlign.Right,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            OutlinedTextField( /* short_link */
                value = customUrl,
                onValueChange = { customUrl = it },
                placeholder = { Text("آدرس کوتاه دلخواه") },
                leadingIcon = {
                    Text(
                        text = "lilnk.ir/",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .alpha(.7f)
                    )
                },
                textStyle = TextStyle(textDirection = TextDirection.Ltr),
                singleLine = true,
                shape = CardDefaults.shape,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            text = "از اعداد و کاراکتر‌های انگلیسی استفاده کنید.",
            fontSize = 12.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.alpha(.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LabeledSwitch( /* with_ads */
            label = {
                Text(
                    text = "نمایش تبلیغ",
                    modifier = Modifier.weight(1f)
                )
            },
            state = showAds,
            modifier = Modifier.fillMaxWidth()
        ) {
            showAds = it
        }
        Text(
            text = "می‌توانید با نمایش تبلیغات روی لینک کوتاه شده کسب درآمد کنید.",
            fontSize = 12.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.alpha(.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExtendedFloatingActionButton(
            onClick = {
                if (!isValidUrl(longUrl)) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "یک لینک معتبر وارد کنید!"
                    )
                    return@ExtendedFloatingActionButton
                }
                if (!isValidCustomUrl(customUrl)) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "آدرس دلخواه نامعتبر است!"
                    )
                    return@ExtendedFloatingActionButton
                }

                isLoading = true
                shortenLink(
                    originalLink = longUrl,
                    customLink = customUrl,
                    withAds = showAds
                ) { success, result, code ->
                    isLoading = false
                    if (success) {
                        shortUrl = result
                        showResult = true
                    } else {
                        showSnackbar(
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            message = when (code) {
                                0 -> "خطا در برقراری ارتباط با سرور!"
                                4001 -> "لینک کوتاه شده تکراری است. آدرس دلخواه دیگری وارد کنید"
                                4002 -> "مشکلی در ایجاد لینک کوتاه شده وجود دارد."
                                else -> "خطای ناشناخته"
                            }
                        )
                    }
                }
            },
            icon = { Icon(Icons.Rounded.Check, "Extended floating action button.") },
            text = { Text(text = "کوتاه کردن لینک") },
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        )


    }
}