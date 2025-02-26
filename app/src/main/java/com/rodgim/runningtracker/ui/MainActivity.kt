package com.rodgim.runningtracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.ui.models.BottomNavItem
import com.rodgim.runningtracker.ui.theme.RunningTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RunningTrackerTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            items = listOf(
                                BottomNavItem(
                                    name = "Your Runs",
                                    route = "runs",
                                    icon = ImageVector.vectorResource(R.drawable.ic_run)
                                ),
                                BottomNavItem(
                                    name = "Statistics",
                                    route = "statistics",
                                    icon = ImageVector.vectorResource(R.drawable.ic_graph),
                                    badgeCount = 23
                                ),
                                BottomNavItem(
                                    name = "Settings",
                                    route = "settings",
                                    icon = ImageVector.vectorResource(R.drawable.ic_settings)
                                )
                            ),
                            navController = navController,
                            modifier = Modifier,
                            onItemClick = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                ) { padding ->
                    Navigation(navHostController = navController)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier,
        containerColor = Color.DarkGray,
        tonalElevation = 2.dp,
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.Green,
                    selectedTextColor = Color.Green,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedTextColor = Color.Gray,
                    unselectedIconColor = Color.Gray,
                    disabledIconColor = Color.LightGray,
                    disabledTextColor = Color.LightGray
                ),
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name
                        )
                    }
                },
                label = {
                    Text(
                        text = item.name,
                        textAlign = TextAlign.Center,
                        color = if (selected) Color.Green else Color.Gray,
                        fontSize = 10.sp
                    )
                }
            )
        }
    }
}

@Composable
fun Navigation(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "runs") {
        composable("runs") {
            RunsScreen()
        }
        composable("statistics") {
            StatisticsScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}

@Composable
fun RunsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Your Runs Screen")
    }
}

@Composable
fun StatisticsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Statistics Screen")
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Settings Screen")
    }
}
