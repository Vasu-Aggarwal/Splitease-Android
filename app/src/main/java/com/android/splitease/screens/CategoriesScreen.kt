package com.android.splitease.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.models.responses.SubCategoryResponse
import com.android.splitease.ui.theme.Black
import com.android.splitease.ui.theme.White
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.CategoryViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(categoryViewModel: CategoryViewModel = hiltViewModel(), navController: NavController) {

    val categories: State<NetworkResult<List<GetCategoryResponse>>> = categoryViewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
    }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search or select a category", fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp)),
                        singleLine = true,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.background, focusedContainerColor = MaterialTheme.colorScheme.background)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        when(categories.value){
            is NetworkResult.Error -> {}
            is NetworkResult.Idle -> {

            }
            is NetworkResult.Loading -> {
                Text(text = "loading")
            }
            is NetworkResult.Success -> {
                Box(modifier = Modifier.padding(paddingValues)){
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
            .padding(5.dp)
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