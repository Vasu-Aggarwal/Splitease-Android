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
import com.android.splitease.screens.NoNetworkScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(isInitialized: Boolean, isNetworkConnected: Boolean) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination =
    if(isInitialized && isNetworkConnected) {
        Screen.BottomNavigationBar.route
    } else if (!isNetworkConnected) {
        Screen.NoNetworkScreen.route
    } else {
        Screen.LoginScreen.route
    }) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.NoNetworkScreen.route) {
            NoNetworkScreen()
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
    data object NoNetworkScreen: Screen("noNetwork")
    data object GroupScreen: Screen("groups")
    data object FriendsScreen: Screen("friends")
    data object AccountScreen: Screen("account")
    data object DetailedGroupScreen: Screen("detailedGroup/{groupId}"){
        fun createRoute(groupId: Int) = "detailedGroup/$groupId"
    }
    data object AddExpenseScreen: Screen("addExpense/{groupId}"){
        fun createRoute(groupId: Int) = "addExpense/$groupId"
    }
    data object DetailedTransactionScreen: Screen("detailedTransaction/{transactionId}"){
        fun createRoute(transactionId: Int) = "detailedTransaction/$transactionId"
    }

    data object NewGroupScreen: Screen("newGroup")

    data object AddUsersToGroupScreen: Screen("addUsersToGroup/{groupId}"){
        fun createRoute(groupId: Int) = "addUsersToGroup/$groupId"
    }
    data object UserDebtScreen: Screen("userDebt/{groupId}"){
        fun createRoute(groupId: Int) = "userDebt/$groupId"
    }

    data object SelectPayingUserScreen: Screen("selectPayingUserScreen/{groupId}"){
        fun createRoute(groupId: Int) = "selectPayingUserScreen/$groupId"
    }

    data object SplitMethodScreen: Screen("splitMethodScreen/{groupId}/{amount}"){
        fun createRoute(groupId: Int, amount: Double) = "splitMethodScreen/$groupId/$amount"
    }

    data object SettleUpPayerScreen: Screen("settleUpPayerScreen/{groupId}"){
        fun createRoute(groupId: Int) = "settleUpPayerScreen/$groupId"
    }

    data object SettleUpReceiverScreen: Screen("settleUpReceiverScreen/{groupId}/{payerUuid}"){
        fun createRoute(groupId: Int, payerUuid: String) = "settleUpReceiverScreen/$groupId/$payerUuid"
    }

    data object SettleUpScreen: Screen("settleUpScreen/{groupId}/{payerUuid}/{receiverUuid}"){
        fun createRoute(groupId: Int, payerUuid: String, receiverUuid: String) = "settleUpScreen/$groupId/$payerUuid/$receiverUuid"
    }

    data object GroupSummaryScreen: Screen("groupSummary/{groupId}"){
        fun createRoute(groupId: Int) = "groupSummary/$groupId"
    }

}