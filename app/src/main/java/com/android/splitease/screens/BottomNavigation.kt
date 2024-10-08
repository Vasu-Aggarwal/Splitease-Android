package com.android.splitease.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
fun BottomNavigationBar(navAppController: NavController) {

    val bottomItems = listOf(
        BottomNavigationItem(
            title = "Groups",
            selectedIcon = Icons.Filled.Home,
            unselectedItem = Icons.Outlined.Home,
            hasNews = false
        ),

        BottomNavigationItem(
            title = "Activities",
            selectedIcon = Icons.AutoMirrored.Filled.TrendingUp,
            unselectedItem = Icons.AutoMirrored.Outlined.TrendingUp,
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
        Screen.CategoryScreen.route,
        Screen.AddUsersToGroupScreen.route,
        Screen.RegisterNewUserScreen.route,
        Screen.SettleUpScreen.route,
        Screen.SettleUpPayerScreen.route,
        Screen.SettleUpReceiverScreen.route,
        Screen.UserDebtScreen.route,
        Screen.GroupSummaryScreen.route,
        Screen.SelectPayingUserScreen.route,
        Screen.SplitMethodScreen.route,
        Screen.DetailedTransactionScreen.route,
        Screen.GroupSettingScreen.route
    )

    Scaffold(bottomBar = {
        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentDestination = currentBackStackEntry?.destination?.route
        if (currentDestination !in bottomNavExcludedScreens) {
            Column(
                modifier = Modifier.padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
            ){
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    bottomItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(
                                    when (index) {
                                        0 -> Screen.GroupScreen.route
                                        1 -> Screen.ActivityScreen.route
                                        2 -> Screen.AccountScreen.route
                                        else -> Screen.GroupScreen.route
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
                    AccountScreen(navController = navAppController)
                }

                composable(route = Screen.ActivityScreen.route) {
                    ActivityScreen(navController = navController)
                }

                composable(route = Screen.NewGroupScreen.route,
                    arguments = listOf(
                        navArgument("mode") { type = NavType.StringType },
                        navArgument("groupId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val mode = backStackEntry.arguments?.getString("mode")
                    val groupId = backStackEntry.arguments?.getInt("groupId", 0)
                    mode?.let {
                        NewGroupScreen(navController = navController, mode = it, groupId = groupId)
                    }
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
                        UserDebtScreen(groupId = it, navController = navController)
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
                        GroupSummaryScreen(groupId = it, navController = navController)
                    }
                }

                composable(route = Screen.CategoryScreen.route) {
                    CategoriesScreen(navController = navController)
                }

                composable(route = Screen.RegisterNewUserScreen.route,
                    arguments = listOf(navArgument("name") { type = NavType.StringType })
                )
                { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name")
                    name?.let {
                        RegisterNewUserScreen(name = it, navController = navController)
                    }
                }

                composable(route = Screen.GroupSettingScreen.route,
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                )
                { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId")
                    groupId?.let {
                        GroupSettingScreen(groupId = it, navController = navController)
                    }
                }
            }
        }
    }
}