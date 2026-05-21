package com.example.myapplication.features.homeuser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    onNavigateBack: (() -> Unit)? = null,
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
        containerColor = Color.White, // Fondo homogéneo para toda la app
        topBar = {
            HomeUserTopBar(
                title = stringResource(id = topBarTitleRes),
                showNotificationsAction = showNotificationsAction,
                onTitleClick = { onNavigate(HomeUser) },
                onNotificationsClick = { navController?.navigate(Notifications) },
                onNavigateBack = onNavigateBack
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
                    containerColor = Color.Transparent, // Sin fondo contrastante
                    elevation = FloatingActionButtonDefaults.elevation(0.dp) // Sin sombra pesada
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6A1B9A).copy(alpha = 0.1f)), // Estilo circular suave
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SupportAgent, // Icono Outline
                            contentDescription = stringResource(id = R.string.home_user_support_content_description),
                            tint = Color(0xFF6A1B9A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        content = content
    )
}
