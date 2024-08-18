package arash.lilnk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import arash.lilnk.ui.screens.navigation.Screens
import arash.lilnk.ui.theme.LilnkTheme
import kotlinx.coroutines.CoroutineScope
import arash.lilnk.ui.screens.navigation.SetupNavGraph
import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics
import arash.lilnk.viewmodel.ViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LilnkTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope: CoroutineScope = rememberCoroutineScope()
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

                navController = rememberNavController()

                val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute =
                    currentNavBackStackEntry?.destination?.route ?: Screens.Home.route
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                ModalNavigationDrawer(drawerContent = {
                    AppDrawer(
                        route = currentRoute,
                        navigateToHome = { navController.navigate(Screens.Home.route) },
                        navigateToStats = { navController.navigate(Screens.Stats.route) },
                        closeDrawer = {
                            coroutineScope.launch { drawerState.close() }
                        },
                        navigateToWithdraw = { navController.navigate(Screens.Withdrawals.route) },
                        logout = {
                            Preferences[Statics.USER_ID] = 0
                            Preferences[Statics.USER_EMAIL] = null
                            navController.navigate(Screens.Start.route)
                        },
                        modifier = Modifier
                    )
                }, drawerState = drawerState) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = "لیلینک",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                navigationIcon = {
                                    if (currentRoute != Screens.Start.route)
                                        IconButton(onClick = {
                                            coroutineScope.launch { drawerState.open() }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Menu,
                                                contentDescription = "Menu"
                                            )
                                        }
                                },
//                            actions = {
//                                IconButton(onClick = { /* do something */ }) {
//                                    Icon(
//                                        imageVector = Icons.Filled.Menu,
//                                        contentDescription = "Localized description"
//                                    )
//                                }
//                            },
                                scrollBehavior = scrollBehavior,
                            )
                        },
                    ) { contentPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                        ) {
                            SetupNavGraph(
                                navHostController = navController,
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun AppDrawer(
        route: String,
        modifier: Modifier = Modifier,
        navigateToHome: () -> Unit,
        navigateToStats: () -> Unit,
        navigateToWithdraw: () -> Unit,
        logout: () -> Unit,
        closeDrawer: () -> Unit,
    ) {
        ModalDrawerSheet(modifier = Modifier) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.drawer_banner),
                        contentDescription = null,
                        modifier = Modifier.clip(CardDefaults.shape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "خانه") },
                        selected = route == Screens.Home.route,
                        onClick = {
                            if (route != Screens.Home.route) navigateToHome()
                            closeDrawer()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Home,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(bottom = 4.dp),
                    )

                    NavigationDrawerItem(
                        label = { Text(text = "لینک‌ها") },
                        selected = route == Screens.Stats.route,
                        onClick = {
                            if (route != Screens.Stats.route) navigateToStats()
                            closeDrawer()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.List,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(bottom = 4.dp),
                    )

                    NavigationDrawerItem(
                        label = { Text(text = "اطلاعات مالی") },
                        selected = route == Screens.Withdrawals.route,
                        onClick = {
                            navigateToWithdraw()
                            closeDrawer()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingCart,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "درباره ما") },
                        selected = false,
                        onClick = {
                            closeDrawer()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 4.dp),
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "خروج از حساب کاربری",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        selected = false,
                        onClick = {
                            closeDrawer()
                            logout()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                    )
                }
                Text(
                    text = "آرش عزیزی و دلارام میرانی",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(.7f)
                        .fillMaxWidth()
                )
            }
        }
    }
}
