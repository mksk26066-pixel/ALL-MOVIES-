package com.example.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.auth.AuthManager
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Library : BottomNavItem("library", Icons.Default.VideoLibrary, "Library")
    object Admin : BottomNavItem("admin", Icons.Default.AdminPanelSettings, "Admin")
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager.getInstance(context) }
    val currentUser by authManager.currentUser.collectAsStateWithLifecycle()

    if (currentUser == null) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(onNavigateToRegister = {
                    navController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }
            composable("register") {
                RegisterScreen(onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                })
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                if (currentRoute != "player/{videoId}") {
                    NavigationBar(
                        containerColor = SurfaceDark,
                        contentColor = PrimaryIndigo
                    ) {
                        val items = mutableListOf(
                            BottomNavItem.Home,
                            BottomNavItem.Library
                        )
                        if (currentUser?.role == "ADMIN") {
                            items.add(BottomNavItem.Admin)
                        }
                        
                        items.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TextPrimary,
                                    selectedTextColor = TextPrimary,
                                    indicatorColor = PrimaryIndigo,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(onVideoClick = { videoId ->
                        navController.navigate("player/$videoId")
                    })
                }
                composable(BottomNavItem.Library.route) {
                    LibraryScreen()
                }
                composable(BottomNavItem.Admin.route) {
                    AdminDashboardScreen()
                }
                composable("player/{videoId}") { backStackEntry ->
                    val videoId = backStackEntry.arguments?.getString("videoId") ?: "1"
                    VideoPlayerScreen(videoId = videoId, onNavigateBack = {
                        navController.popBackStack()
                    })
                }
            }
        }
    }
}
