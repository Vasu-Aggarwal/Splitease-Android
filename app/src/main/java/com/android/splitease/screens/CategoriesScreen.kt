package com.android.splitease.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.viewmodels.CategoryViewModel

@Composable
fun CategoriesScreen(categoryViewModel: CategoryViewModel = hiltViewModel(), navController: NavController) {
    Text(text = "Categories")
}