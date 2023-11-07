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

//
@Composable
fun Navigation(state: ProfileState,
               onEvent:(ProfileEvent) -> Unit,
               profileNamesFlow: Flow<List<String>>,
               viewModel: ProfileViewModel,
               blueViewModel: BluetoothViewModel,
               bluetoothState: BluetoothUiState

) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Home") {
        composable("Home") { StartScreen(navController,
            state, onEvent, profileNamesFlow, viewModel, blueViewModel, bluetoothState ) }
        composable("Add") { ProfileScreen(navController,
           state, onEvent ) }
        composable("Controls") { ControlScreen(navController) }
        composable("Faq") { FaqScreen(navController) }
    }
}

sealed class BottomNavItem(
    var title: String,
    var icon: Int
) {
    object Home : BottomNavItem(
        "Home",
        R.drawable.homeicon
    )

    object Add : BottomNavItem(
        "Add",
        R.drawable.addicon
    )

    object Controls : BottomNavItem(
        "Controls",
        R.drawable.caricon
    )

    object FAQ : BottomNavItem(
        "FAQ",
        R.drawable.faqicon
    )
}


@Composable
fun BottomNavigation(navController: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Add,
        BottomNavItem.Controls,
        BottomNavItem.FAQ
    )

    NavigationBar {
        items.forEach { item ->
            AddItem(
                screen = item,
                navController = navController
            )
        }
    }
}


@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    navController: NavController
) {

    NavigationBarItem(
        // Text that shows bellow the icon
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

        // Display if the icon it is select or not
        selected = true,

        // Always show the label bellow the icon or not
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