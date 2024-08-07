package com.android.splitease.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.models.responses.SubCategoryResponse
import com.android.splitease.ui.theme.Black
import com.android.splitease.ui.theme.Green400
import com.android.splitease.ui.theme.White
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.CategoryViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen(categoryViewModel: CategoryViewModel = hiltViewModel(), navController: NavController) {

    val categories: State<NetworkResult<List<GetCategoryResponse>>> = categoryViewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
    }
    when(categories.value){
        is NetworkResult.Error -> {}
        is NetworkResult.Idle -> {

        }
        is NetworkResult.Loading -> {
            Text(text = "loading")
        }
        is NetworkResult.Success -> {
            LazyColumn {
                categories.value.data?.forEach { category ->
                    stickyHeader {
                        CategoryItem(category)
                    }
                    items(category.subcategories) { subcategory ->
                        SubCategoryItem(subcategory)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: GetCategoryResponse) {
    Text(
        text = category.category,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .padding(16.dp)
    )
}

@Composable
fun SubCategoryItem(subcategory: SubCategoryResponse) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { }
    ) {
        Image(
            painter = rememberImagePainter(subcategory.imageUrl),
            contentDescription = subcategory.name,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp)
                .background(color = White)
        )
        Text(
            text = subcategory.name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}