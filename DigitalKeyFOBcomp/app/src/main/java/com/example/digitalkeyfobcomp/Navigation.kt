package com.example.digitalkeyfobcomp.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothUiState
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothViewModel
import com.example.digitalkeyfobcomp.ProfileEvent
import com.example.digitalkeyfobcomp.ProfileState
import com.example.digitalkeyfobcomp.ProfileViewModel
import com.example.digitalkeyfobcomp.R
import com.example.digitalkeyfobcomp.screens.ControlScreen
import com.example.digitalkeyfobcomp.screens.FaqScreen
import com.example.digitalkeyfobcomp.screens.ProfileScreen
import com.example.digitalkeyfobcomp.screens.StartScreen
import kotlinx.coroutines.flow.Flow

// Composable function for handling navigation between screens
@Composable
fun Navigation(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    profileNamesFlow: Flow<List<String>>,
    viewModel: ProfileViewModel,
    blueViewModel: BluetoothViewModel,
    bluetoothState: BluetoothUiState
) {
    // Create a NavHost for navigation
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Home") {
        // Screen for the Home destination
        composable("Home") {
            StartScreen(navController, state, onEvent, profileNamesFlow, viewModel, blueViewModel, bluetoothState)
        }
        // Screen for the Add destination
        composable("Add") {
            ProfileScreen(navController, state, onEvent, viewModel, blueViewModel, bluetoothState)
        }
        // Screen for the Controls destination
        composable("Controls") {
            ControlScreen(navController, blueViewModel, bluetoothState)
        }
        // Screen for the Faq destination
        composable("Faq") {
            FaqScreen(navController, blueViewModel, bluetoothState)
        }
    }
}

// Sealed class representing each item in the bottom navigation bar
sealed class BottomNavItem(
    var title: String,
    var icon: Int
) {
    // Home destination
    object Home : BottomNavItem("Home", R.drawable.homeicon)

    // Add destination
    object Add : BottomNavItem("Add", R.drawable.addicon)

    // Controls destination
    object Controls : BottomNavItem("Controls", R.drawable.caricon)

    // FAQ destination
    object FAQ : BottomNavItem("FAQ", R.drawable.faqicon)
}

// Composable function for creating the bottom navigation bar
@Composable
fun BottomNavigation(navController: NavController) {
    // List of navigation items
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Add,
        BottomNavItem.Controls,
        BottomNavItem.FAQ
    )

    // Navigation bar
    NavigationBar {
        items.forEach { item ->
            // Add each item to the navigation bar
            AddItem(screen = item, navController = navController)
        }
    }
}

// Composable function for creating a single item in the bottom navigation bar
@Composable
fun RowScope.AddItem(screen: BottomNavItem, navController: NavController) {
    // Navigation bar item
    NavigationBarItem(
        // Text that shows below the icon
        label = {
            Text(text = screen.title)
        },

        // The icon resource
        icon = {
            Icon(
                painterResource(id = screen.icon),
                contentDescription = screen.title
            )
        },

        // Display if the icon is selected or not
        selected = true,

        // Always show the label below the icon or not
        alwaysShowLabel = true,

        // Click listener for the icon
        onClick = { navController.navigate(route = screen.title) },

        // Control all the colors of the icon
        colors = NavigationBarItemDefaults.colors()
    )
}

//
//@Composable
//fun currentRoute(navController: NavHostController): String {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    return currentBackStackEntry.value?.destination?.route ?: ""
//}