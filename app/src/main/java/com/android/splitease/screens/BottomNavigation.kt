package com.android.splitease.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

    Scaffold(bottomBar = {
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
    }){
            innerPadding ->
        NavHost(navController = navController, startDestination = Screen.GroupScreen.route, modifier = Modifier.padding(innerPadding)){

            composable(route = Screen.GroupScreen.route){
                GroupScreen(navController = navController)
            }

            composable(route = Screen.FriendsScreen.route){
                FriendsScreen()
            }

            composable(route = Screen.AccountScreen.route){
                AccountScreen()
            }

            composable(route = Screen.NewGroupScreen.route){
                NewGroupScreen()
            }

            composable(route = Screen.DetailedGroupScreen.route,
                arguments = listOf(navArgument("groupId"){type = NavType.IntType}))
            { backStackEntry ->
                val groupId = backStackEntry.arguments?.getInt("groupId")
                groupId?.let {
                    DetailedGroupScreen(groupId = it, navController = navController)
                }
            }

            composable(route = Screen.AddExpenseScreen.route,
                arguments = listOf(navArgument("groupId"){type = NavType.IntType}))
            { backStackEntry ->
                val groupId = backStackEntry.arguments?.getInt("groupId")
                groupId?.let {
                    AddExpenseScreen(groupId = it, navController = navController)
                }
            }

            composable(route = Screen.DetailedTransactionScreen.route,
                arguments = listOf(navArgument("transactionId"){type = NavType.IntType}))
            { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getInt("transactionId")
                transactionId?.let {
                    DetailedTransactionScreen(transactionId = it, navController = navController)
                }
            }
        }
    }
}