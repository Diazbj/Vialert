package com.example.myapplication.features.homeuser.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.myapplication.R
import com.example.myapplication.core.navigation.Explore
import com.example.myapplication.core.navigation.HomeUser
import com.example.myapplication.core.navigation.MainRoutes
import com.example.myapplication.core.navigation.Profile
import com.example.myapplication.core.navigation.Reports
import com.example.myapplication.core.navigation.NewReport

private data class BottomNavItem(
    val route: MainRoutes,
    val icon: ImageVector,
    val labelRes: Int,
    val showCenterBadge: Boolean = false
)

private val bottomNavItems = listOf(
    BottomNavItem(route = HomeUser, icon = Icons.Outlined.Home, labelRes = R.string.home_user_nav_home),
    BottomNavItem(route = Reports, icon = Icons.Outlined.Report, labelRes = R.string.home_user_nav_reports),
    BottomNavItem(
        route = NewReport(), // Cambiado de NewReport a NewReport()
        icon = Icons.Outlined.Add,
        labelRes = R.string.home_user_create_report,
        showCenterBadge = true
    ),
    BottomNavItem(route = Explore, icon = Icons.Outlined.Explore, labelRes = R.string.home_user_nav_explore),
    BottomNavItem(route = Profile, icon = Icons.Outlined.Person, labelRes = R.string.home_user_nav_profile)
)

@Composable
fun HomeBottomNavBar(
    currentDestination: NavDestination?,
    onNavigate: (MainRoutes) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            bottomNavItems.forEach { item ->
                val selected = when (item.route) {
                    HomeUser -> currentDestination?.hierarchy?.any { it.hasRoute<HomeUser>() } == true
                    Reports -> currentDestination?.hierarchy?.any { it.hasRoute<Reports>() } == true
                    is NewReport -> currentDestination?.hierarchy?.any { it.hasRoute<NewReport>() } == true
                    Explore -> currentDestination?.hierarchy?.any { it.hasRoute<Explore>() } == true
                    Profile -> currentDestination?.hierarchy?.any { it.hasRoute<Profile>() } == true
                    else -> false
                }

                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        if (item.showCenterBadge) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = Color(0xFF6A1B9A).copy(alpha = 0.1f),
                                contentColor = Color(0xFF6A1B9A)
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = stringResource(id = R.string.home_user_create_report),
                                    tint = Color(0xFF6A1B9A)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = stringResource(id = item.labelRes),
                                tint = if (selected) Color(0xFF6A1B9A) else Color.Gray
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.labelRes),
                            color = if (selected) Color(0xFF6A1B9A) else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
