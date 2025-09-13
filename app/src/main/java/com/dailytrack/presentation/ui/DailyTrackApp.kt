package com.dailytrack.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dailytrack.R
import com.dailytrack.presentation.ui.screens.AnalyticsScreen
import com.dailytrack.presentation.ui.screens.AttendanceScreen
import com.dailytrack.presentation.ui.screens.StudentsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTrackApp() {
    val navController = rememberNavController()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Today, 
                            contentDescription = stringResource(R.string.nav_attendance)
                        ) 
                    },
                    label = { Text(stringResource(R.string.nav_attendance)) },
                    selected = currentDestination?.hierarchy?.any { it.route == "attendance" } == true,
                    onClick = {
                        navController.navigate("attendance") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.People, 
                            contentDescription = stringResource(R.string.nav_students)
                        ) 
                    },
                    label = { Text(stringResource(R.string.nav_students)) },
                    selected = currentDestination?.hierarchy?.any { it.route == "students" } == true,
                    onClick = {
                        navController.navigate("students") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Analytics, 
                            contentDescription = stringResource(R.string.nav_analytics)
                        ) 
                    },
                    label = { Text(stringResource(R.string.nav_analytics)) },
                    selected = currentDestination?.hierarchy?.any { it.route == "analytics" } == true,
                    onClick = {
                        navController.navigate("analytics") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "attendance",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("attendance") { 
                AttendanceScreen()
            }
            composable("students") { 
                StudentsScreen()
            }
            composable("analytics") { 
                AnalyticsScreen()
            }
        }
    }
}