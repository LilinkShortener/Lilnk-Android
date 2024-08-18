package arash.lilnk.ui.screens


import LoadingDialog
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import arash.lilnk.api.getWithdrawalsStats
import arash.lilnk.api.processWithdrawal
import arash.lilnk.model.Withdrawal
import arash.lilnk.model.WithdrawalsStats
import arash.lilnk.ui.components.WithdrawalItem
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Statics.REQUEST_TAG
import arash.lilnk.utilities.shareText
import arash.lilnk.utilities.showSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WithdrawalsStatsScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    val context = LocalContext.current

    // Define state variables
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var withdrawalsStats by remember { mutableStateOf<WithdrawalsStats?>(null) }

    val pagerState = rememberPagerState(pageCount = { WithdrawalsScreenTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    // Launch the data fetching operation
    LaunchedEffect(Unit) {
        getWithdrawalsStats { success, errorMessage, stats ->
            isLoading = false
            if (success && stats != null) {
                withdrawalsStats = stats
            } else {
                val message = errorMessage ?: "خطای ناشناخته"
                showSnackbar(
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                    message = message
                )
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            WithdrawalsScreenTabs.entries.forEachIndexed { index, currentTab ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.outline,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentTab.ordinal)
                        }
                    },
                    text = { Text(text = currentTab.text) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTabIndex.value == index)
                                currentTab.selectedIcon else currentTab.unselectedIcon,
                            contentDescription = "Tab Icon"
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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
                } else {
                    Column {
                        withdrawalsStats?.let { stats ->
                            if (selectedTabIndex.value == 0) {
                                UserInfo(
                                    context = context,
                                    wStats = stats,
                                    cashoutButton = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    })
                            } else {
                                CashoutAndHistory(
                                    wStats = stats,
                                    onWithdrawClick = { iban, name, surname ->
                                        if (!iban.isDigitsOnly() || iban.length < 24) {
                                            showSnackbar(
                                                coroutineScope = coroutineScope,
                                                snackbarHostState = snackbarHostState,
                                                message = "شماره شبا وارد شده را بررسی کنید"
                                            )
                                            return@CashoutAndHistory
                                        }
                                        if (name.isEmpty() || surname.isEmpty()) {
                                            showSnackbar(
                                                coroutineScope = coroutineScope,
                                                snackbarHostState = snackbarHostState,
                                                message = "نام و نام خانوادگی خود را درست وارد کنید"
                                            )
                                            return@CashoutAndHistory
                                        }

                                        isLoading = true
                                        processWithdrawal(
                                            iban = "IR$iban",
                                            name = name,
                                            surname = surname
                                        ) { success, result, code ->
                                            isLoading = false
                                            if (success) {
                                                showSnackbar(
                                                    coroutineScope = coroutineScope,
                                                    snackbarHostState = snackbarHostState,
                                                    message = "درخواست برداشت شما با موفقیت ثبت شده و به آن رسیدگی خواهد شد."
                                                )
                                            } else {
                                                showSnackbar(
                                                    coroutineScope = coroutineScope,
                                                    snackbarHostState = snackbarHostState,
                                                    message = when (code) {
                                                        0->"اتصال به سرور امکان پذیر نبود."
                                                        6001 -> "موجودی کاربر کافی نیست."
                                                        6004 -> "مشکلی در پردازش درخواست واریز وجود دارد."
                                                        6005 -> "کاربر پیدا نشد."
                                                        else -> "خطای ناشناخته"
                                                    }
                                                )
                                            }
                                        }

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfo(
    context: Context,
    wStats: WithdrawalsStats,
    cashoutButton: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // کارت اطلاعات کاربر
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "شناسه کاربری: ${wStats.userId}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = wStats.email, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "زمان ثبت‌نام: ${wStats.registrationTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // کارت اطلاعات لینک‌ها
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "لینک‌ها و کلیک‌ها", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "کل لینک‌ها: ${wStats.totalLinks}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "کل کلیک‌ها: ${wStats.totalClicks}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "لینک‌های با تبلیغ: ${wStats.adsLinks}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "لینک‌های بدون تبلیغ: ${wStats.noAdsLinks}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // کارت اطلاعات مالی
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "اطلاعات مالی", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "موجودی فعلی: ${wStats.currentBalance} تومان",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "از زمان شروع همکاریمون ${wStats.adsEarnings} درآمد کسب کردید! ",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(onClick = {
                shareText(
                    context, """
                من از اپلیکیشن لیلینک استفاده می‌کنم و تا حالا ${wStats.totalLinks} لینک رو کوتاه کردم و ${wStats.totalClicks} کلیک گرفتم! حتی تونستم ${wStats.adsEarnings} تومان از تبلیغات درآمد کسب کنم. شما هم امتحان کنید و با کوتاه کردن لینک‌ها درآمد کسب کنید! لینک‌های کوتاه و کسب درآمد با لیلینک 👇
                https://lilink.app
            """
                )
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "اشتراک گذاری")
            }
            Button(onClick = { cashoutButton() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "برداشت درآمد")
            }
        }

    }
}

@Composable
fun CashoutAndHistory(
    wStats: WithdrawalsStats,
    onWithdrawClick: (iban: String, name: String, surname: String) -> Unit
) {
    var iban by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
        Text(
            text = "ایجاد درخواست برداشت",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = wStats.currentBalance.toString(),
            onValueChange = { },
            textStyle = TextStyle(textDirection = TextDirection.Ltr),
            readOnly = true,
            singleLine = true,
            leadingIcon = {
                Text(
                    text = "موجودی قابل برداشت:",
                    modifier = Modifier.padding(start = 16.dp)
                )
            },
            trailingIcon = {
                Text(
                    text = "تومانءء",
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            },
            shape = CardDefaults.shape,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = CardDefaults.shape
                )
                .focusable(false)
                .clickable(false) {}
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = iban,
            onValueChange = { if (it.length <= 24) iban = it },
            placeholder = { Text("120560082981099999933001") },
            label = { Text(text = "شماره شبا") },
            textStyle = TextStyle(textDirection = TextDirection.Ltr),
            trailingIcon = {
                Text(
                    text = "IR",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .alpha(.5f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            supportingText = { Text("شماره شبا ۲۴ رقمی حساب بانکی متعلق به خود") },
            shape = CardDefaults.shape,
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("آرش") },
                label = { Text(text = "نام") },
                textStyle = TextStyle(textDirection = TextDirection.Rtl),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                shape = CardDefaults.shape,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                placeholder = { Text("عزیزی") },
                label = { Text(text = "نام خانوادگی") },
                textStyle = TextStyle(textDirection = TextDirection.Rtl),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                shape = CardDefaults.shape,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ExtendedFloatingActionButton(
            onClick = { onWithdrawClick(iban, name, surname) },
            icon = { Icon(imageVector = Icons.Rounded.Check, contentDescription = name) },
            text = { Text(text = "ثبت درخواست برداشت") },
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "سوابق برداشت",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clip(shape = CardDefaults.shape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            if (wStats.withdrawals.isEmpty()) Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(.5f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DateRange,
                    contentDescription = "",
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "درخواست برداشتی وجود ندارد\nاولین درخواست خود را ثبت کنید",
                    textAlign = TextAlign.Center
                )
            }
            else LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                wStats.withdrawals.let { list ->
                    items(count = list.size) { index ->
                        WithdrawalItem(item = list[index])
                        if (index != list.size - 1) HorizontalDivider()
                    }
                }
            }
        }
    }
}

enum class WithdrawalsScreenTabs(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val text: String
) {
    UserDetail(
        unselectedIcon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.AccountCircle,
        text = "اطلاعات کاربری"
    ),
    WithdrawalHistory(
        unselectedIcon = Icons.Outlined.DateRange,
        selectedIcon = Icons.Filled.DateRange,
        text = "درخواست برداشت و سوابق"
    ),
}