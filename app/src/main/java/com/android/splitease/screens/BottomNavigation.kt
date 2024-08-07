package com.android.splitease.screens

import android.os.Build
import android.util.LayoutDirection
import android.util.Log
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.splitease.navigation.BottomNavigationItem
import com.android.splitease.navigation.Screen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(){

    val bottomItems = listOf(
        BottomNavigationItem(
            title = "Groups",
            selectedIcon = Icons.Filled.Home,
            unselectedItem = Icons.Outlined.Home,
            hasNews = false
        ),

        BottomNavigationItem(
            title = "Friends",
            selectedIcon = Icons.Filled.Menu,
            unselectedItem = Icons.Outlined.Menu,
            hasNews = false
        ),

        BottomNavigationItem(
            title = "Account",
            selectedIcon = Icons.Filled.Person,
            unselectedItem = Icons.Outlined.Person,
            hasNews = false
        )
    )

    val navController = rememberNavController()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val bottomNavExcludedScreens = listOf(
        Screen.NewGroupScreen.route,
        Screen.AddExpenseScreen.route,
        Screen.CategoryScreen.route
    )

    Scaffold(bottomBar = {
        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentDestination = currentBackStackEntry?.destination?.route
        if (currentDestination !in bottomNavExcludedScreens) {
            NavigationBar {
                bottomItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(
                                when (index) {
                                    0 -> "groups"
                                    1 -> "friends"
                                    2 -> "account"
                                    else -> "home"
                                }
                            )
                        },
                        label = {
                            Text(text = item.title)
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.badgeCount != null) {
                                        Badge {
                                            Text(text = item.badgeCount.toString())
                                        }
                                    } else if (item.hasNews) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (index == selectedItemIndex)
                                        item.selectedIcon
                                    else
                                        item.unselectedItem, contentDescription = item.title
                                )
                            }
                        }
                    )
                }
            }
        }
    }){ innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.GroupScreen.route,
                modifier = Modifier.padding(innerPadding.calculateStartPadding(LocalLayoutDirection.current), 0.dp, innerPadding.calculateEndPadding(LocalLayoutDirection.current), innerPadding.calculateBottomPadding())
            ) {

                composable(route = Screen.GroupScreen.route) {
                    GroupScreen(navController = navController)
                }

                composable(route = Screen.FriendsScreen.route) {
                    FriendsScreen()
                }

                composable(route = Screen.AccountScreen.route) {
                    AccountScreen()
                }

                composable(route = Screen.NewGroupScreen.route) {
                    NewGroupScreen(navController = navController)
                }


                composable(route = Screen.DetailedGroupScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        DetailedGroupScreen(groupId = it, navController = navController)
                    }
                }

                composable(route = Screen.AddExpenseScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        AddExpenseScreen(groupId = it, navController = navController)
                    }
                }

                composable(route = Screen.DetailedTransactionScreen.route,
                    arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val transactionId = backStackEntry.arguments?.getInt("transactionId")
                    transactionId?.let {
                        DetailedTransactionScreen(transactionId = it, navController = navController)
                    }
                }

                composable(route = Screen.AddUsersToGroupScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        AddUsersToGroupScreen(groupId = it, navController = navController)
                    }
                }

                composable(route = Screen.UserDebtScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        UserDebtScreen(groupId = it)
                    }
                }

                composable(route = Screen.SelectPayingUserScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        SelectPayingUserScreen(groupId = it, navController = navController)
                    }
                }

                composable(route = Screen.SplitMethodScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType },
                        navArgument("amount") { type = NavType.StringType }
                    )) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    val amountString = backStackEntry.arguments?.getString("amount")
                    val amount = amountString?.toDoubleOrNull() // Convert String back to Double
                    groupId?.let {
                        SplitMethodsScreen(
                            groupId = it,
                            navController = navController,
                            amount = amount!!
                        )
                    }
                }

                composable(route = Screen.SettleUpPayerScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        SettleUpPayerScreen(groupId = it, navController = navController)
                    }
                }

                composable(
                    route = Screen.SettleUpReceiverScreen.route,
                    arguments = listOf(
                        navArgument("groupId") { type = NavType.IntType },
                        navArgument("payerUuid") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    val payerUuid = backStackEntry.arguments?.getString("payerUuid")

                    if (groupId != null && payerUuid != null) {
                        SettleUpReceiverScreen(
                            groupId = groupId,
                            navController = navController,
                            payerUuid = payerUuid
                        )
                    }
                }

                composable(
                    route = Screen.SettleUpScreen.route,
                    arguments = listOf(
                        navArgument("groupId") { type = NavType.IntType },
                        navArgument("payerUuid") { type = NavType.StringType },
                        navArgument("receiverUuid") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    val payerUuid = backStackEntry.arguments?.getString("payerUuid")
                    val receiverUuid = backStackEntry.arguments?.getString("receiverUuid")

                    if (groupId != null && payerUuid != null && receiverUuid != null) {
                        SettleUpScreen(
                            groupId = groupId,
                            navController = navController,
                            payerUuid = payerUuid,
                            receiverUuid = receiverUuid
                        )
                    }
                }

                composable(route = Screen.GroupSummaryScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        GroupSummaryScreen(groupId = it)
                    }
                }

                composable(route = Screen.CategoryScreen.route) {
                    CategoriesScreen(navController = navController)
                }
            }
        }
    }
}