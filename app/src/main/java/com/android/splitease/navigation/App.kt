package com.android.splitease.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.splitease.screens.BottomNavigationBar
import com.android.splitease.screens.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.BottomNavigationBar.route) {
            BottomNavigationBar()
        }
    }
}
data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedItem: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

sealed class Screen(val route: String){
    data object LoginScreen: Screen("login")
    data object BottomNavigationBar: Screen("bottomBar")
    data object GroupScreen: Screen("groups")
    data object FriendsScreen: Screen("friends")
    data object AccountScreen: Screen("account")
    data object DetailedGroupScreen: Screen("detailedGroup/{groupId}"){
        fun createRoute(groupId: Int) = "detailedGroup/$groupId"
    }
    data object AddExpenseScreen: Screen("addExpense/{groupId}"){
        fun createRoute(groupId: Int) = "addExpense/$groupId"
    }


}