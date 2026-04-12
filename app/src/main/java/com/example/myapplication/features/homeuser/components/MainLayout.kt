package com.example.myapplication.features.homeuser.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.R
import com.example.myapplication.core.navigation.HomeUser
import com.example.myapplication.core.navigation.MainRoutes
import com.example.myapplication.core.navigation.Notifications

@Composable
fun MainLayout(
    navController: NavController?,
    topBarTitleRes: Int = R.string.home_user_topbar_title,
    showNotificationsAction: Boolean = true,
    showSupportFab: Boolean = false,
    onSupportClick: (() -> Unit)? = null,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val currentBackStackEntry = navController?.currentBackStackEntryAsState()?.value
    val currentDestination = currentBackStackEntry?.destination

    val onNavigate: (MainRoutes) -> Unit = { route ->
        navController?.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    Scaffold(
        topBar = {
            HomeUserTopBar(
                title = stringResource(id = topBarTitleRes),
                showNotificationsAction = showNotificationsAction,
                onTitleClick = { onNavigate(HomeUser) },
                onNotificationsClick = { navController?.navigate(Notifications) }
            )
        },
        bottomBar = {
            HomeBottomNavBar(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            if (showSupportFab && onSupportClick != null) {
                FloatingActionButton(
                    onClick = onSupportClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = stringResource(id = R.string.home_user_support_content_description)
                    )
                }
            }
        },
        content = content
    )
}
