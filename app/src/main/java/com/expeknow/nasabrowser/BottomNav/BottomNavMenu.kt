package com.expeknow.nasabrowser.BottomNav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavMenu (
    val route : String,
    val icon :ImageVector,
    val title: String
        ) {

    object Home: BottomNavMenu("home", Icons.Default.Home, "Home")
    object Search: BottomNavMenu("search", Icons.Default.Search, "Search")
    object Favorite: BottomNavMenu("favorite", Icons.Default.Favorite, "Saved")
}
