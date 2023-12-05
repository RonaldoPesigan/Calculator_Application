package com.example.applicationcal

import NumberGeneratorScreen
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.applicationcal.ui.theme.ApplicationCalTheme


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationCalTheme {
                val navController = rememberNavController()
                val bottomNavigationItems = listOf(
                    "Calculator" to Icons.Default.Calculate,
                    "Number Generator" to Icons.Default.Pin
                )

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val navigateTo: (String) -> Unit = {
                    navController.navigate(it)
                }

                var selectedItem by remember { mutableStateOf(0) }

                Scaffold(
                    bottomBar = {
                        BottomAppBar() {
                            NavigationBar {
                                bottomNavigationItems.forEachIndexed { index, (title, icon) ->
                                    val isSelected = currentRoute == "screen$index"
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            if (!isSelected) {
                                                selectedItem = index
                                                navigateTo("screen$index")
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) icon else icon,
                                                contentDescription = title
                                            )
                                        },
                                        label = { Text(title) }
                                    )
                                }
                            }
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = "screen0") {
                        composable("screen0") {
                            CalculatorScreen()
                        }
                        composable("screen1") {
                            NumberGeneratorScreen()
                        }
                    }
                }
            }
        }
    }
}
