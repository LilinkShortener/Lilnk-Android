package arash.lilnk.ui.screens

import LoadingDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import arash.lilnk.api.getUserLinks
import arash.lilnk.model.Links
import arash.lilnk.ui.components.LinksItem
import arash.lilnk.ui.dialogs.ResultBottomSheet
import arash.lilnk.ui.dialogs.ResultType
import arash.lilnk.ui.theme.LilnkTheme
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics
import arash.lilnk.utilities.Statics.REQUEST_TAG
import arash.lilnk.utilities.showSnackbar
import kotlinx.coroutines.CoroutineScope

@Composable
fun StatsScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    // Define state variables
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var isEmpty by rememberSaveable { mutableStateOf(true) }
    var linkDataList by remember { mutableStateOf<List<Links>?>(null) }
    var earnings by rememberSaveable { mutableIntStateOf(0) }

    var hasAds by remember { mutableStateOf(true) }
    var showResult by rememberSaveable { mutableStateOf(false) }
    var shortUrl by rememberSaveable { mutableStateOf("") }

    // Launch the data fetching operation
    LaunchedEffect(Unit) {
        getUserLinks { success, errorCode, links, totalEarnings ->
            isLoading = false
            if (success && !links.isNullOrEmpty()) {
                linkDataList = links
                earnings = totalEarnings ?: 0
                isEmpty = false
            } else {
                isEmpty = true
                val message = when (errorCode) {
                    0 -> "خطا در برقراری ارتباط با سرور!"
                    3001 -> "کاربر پیدا نشد."
                    else -> "خطای ناشناخته"
                }
                showSnackbar(
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                    message = message
                )
            }
        }
    }

    // Display content based on state
    if (isLoading) {
        LoadingDialog(
            isLoading = true,
            onDismiss = {
                navController.popBackStack()
                Lilnk.instance?.cancelPendingRequest(REQUEST_TAG)
                isLoading = false
            }
        )
    } else if (isEmpty) {
        // Display empty state message
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "هیچ لینکی یافت نشد.")
        }
    } else {
        // Display list of links
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "تعداد لینک‌ها: ${linkDataList?.size ?: 0}",
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "مجموع درآمد: $earnings تومانءء")
                }
            }
            item {
                HorizontalDivider()
            }
            linkDataList?.also { list ->
                items(
                    count = list.size,
                ) { index ->
                    LinksItem(item = list[index]) {
                        shortUrl = list[index].shortUrl
                        hasAds = list[index].earnings > 0
                        showResult = true
                    }
                }
            } ?: run {
                isEmpty = true
            }
        }
    }
    ResultBottomSheet(
        resultType = ResultType.Link,
        shortenLink = shortUrl,
        hasAds = hasAds,
        showResult = showResult,
        onDismiss = { showResult = false },
    )
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun StartScreenPreview() {
//    LilnkTheme {
//        StatsScreen()
//    }
//}