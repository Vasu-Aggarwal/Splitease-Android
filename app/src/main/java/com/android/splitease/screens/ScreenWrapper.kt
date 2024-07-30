package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScreenWrapper(
    title: String,
    navigationIcon: (@Composable () -> Unit),
    actions: (@Composable RowScope.() -> Unit),
    content: @Composable (Modifier) -> Unit
) {
    Column {
        TopBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions
        )
        // Content area for the screen
        content(Modifier.weight(1f))
    }
}
