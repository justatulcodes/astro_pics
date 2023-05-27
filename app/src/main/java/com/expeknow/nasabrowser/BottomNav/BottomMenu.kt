package com.expeknow.nasabrowser.BottomNav

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun BottomMenu(navController: NavController) {
    val menuItem = listOf(
        BottomNavMenu.Home,
        BottomNavMenu.Search,
        BottomNavMenu.Favorite
    )

    BottomNavigation(
        backgroundColor = Color.Black,
        elevation = 8.dp
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        var currentRoute = navBackStackEntry?.destination?.route
        if(currentRoute == null){
            currentRoute = BottomNavMenu.Home.route
        }

        menuItem.forEach {
            BottomNavigationItem(
                label = { Text(text = it.title)},
                alwaysShowLabel = true,
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Gray,
                selected = currentRoute == it.route,
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.title
                    )
                },
                onClick = {
                    navController.navigate(it.route){
                        navController.graph.startDestinationRoute?.let {
                            route ->
                                popUpTo(route){
                                    saveState = true
                                }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    BottomMenu(navController = rememberNavController())
}