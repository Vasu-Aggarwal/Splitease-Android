package com.android.splitease.screens

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.CategoryViewModel

@Composable
fun CategoriesScreen(categoryViewModel: CategoryViewModel = hiltViewModel(), navController: NavController) {

    val categories: State<NetworkResult<List<GetCategoryResponse>>> = categoryViewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
    }
    Log.d("category", "CategoriesScreen: ${categories.value.data!!.get(0).category}")

//    LazyColumn {
//        items(categories.value.data!!){category ->
//            Log.d("category", "CategoriesScreen: ${category.category}")
//            CategoryItem(category)
//        }
//    }

    Text(text = "Categories")
}

@Composable
fun CategoryItem(category: GetCategoryResponse) {

}