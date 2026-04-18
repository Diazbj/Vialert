package com.example.myapplication.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.theme.VialertPurple
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.features.homeuser.components.MainLayout
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    MainLayout(
        navController = navController,
        topBarTitleRes = R.string.notifications_topbar_title,
        showNotificationsAction = false,
        showSupportFab = true,
        onSupportClick = {}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NotificationsTabs(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = viewModel::filterNotifications,
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = viewModel::markAllAsRead) {
                    Text(
                        text = stringResource(id = R.string.notifications_mark_all),
                        color = VialertPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (uiState.contentState) {
                NotificationsUiContentState.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VialertPurple)
                    }
                }

                NotificationsUiContentState.EMPTY -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.notifications_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                NotificationsUiContentState.SUCCESS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = uiState.visibleNotifications, key = { it.id }) { item ->
                            NotificationItem(
                                item = item,
                                onClick = { viewModel.markAsRead(item.notification.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsTabs(
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        NotificationFilter.TODAS to stringResource(id = R.string.notifications_filter_all),
        NotificationFilter.NO_LEIDAS to stringResource(id = R.string.notifications_filter_unread)
    )
    val selectedIndex = tabs.indexOfFirst { it.first == selectedFilter }

    TabRow(
        selectedTabIndex = selectedIndex.coerceAtLeast(0),
        modifier = modifier,
        containerColor = Color.White,
        contentColor = VialertPurple,
        divider = {},
        indicator = { tabPositions ->
            if (selectedIndex != -1) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = VialertPurple
                )
            }
        }
    ) {
        tabs.forEach { (filter, label) ->
            Tab(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                text = { 
                    Text(
                        text = label,
                        color = if (selectedFilter == filter) VialertPurple else Color.Gray
                    ) 
                }
            )
        }
    }
}

@Composable
private fun NotificationItem(
    item: NotificationDisplayItem,
    onClick: () -> Unit
) {
    val icon = iconForType(item.notification.tipo)
    val relativeTime = relativeTimeLabel(item.notification.creadoEn)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.leido) {
                Color.White
            } else {
                Color(0xFF6A1B9A).copy(alpha = 0.05f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.leido) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = VialertPurple.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = VialertPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.notification.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.notification.mensaje,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = relativeTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
                if (!item.leido) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = VialertPurple,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

private fun iconForType(tipo: TipoNotificacion): ImageVector {
    return when (tipo) {
        TipoNotificacion.REPORTE_CREADO -> Icons.Outlined.Campaign
        TipoNotificacion.REPORTE_ACTUALIZADO -> Icons.Outlined.Update
        TipoNotificacion.REPORTE_COMENTARIO -> Icons.Outlined.ChatBubble
        TipoNotificacion.REPORTE_REACCION -> Icons.Outlined.Favorite
        TipoNotificacion.REPORTE_CERRADO -> Icons.Outlined.CheckCircle
    }
}

@Composable
private fun relativeTimeLabel(createdAt: LocalDateTime): String {
    val now = LocalDateTime.now()
    val minutes = Duration.between(createdAt, now).toMinutes()
    return when {
        minutes < 1 -> stringResource(id = R.string.notifications_now)
        minutes < 60 -> stringResource(id = R.string.notifications_minutes_ago, minutes.toInt())
        minutes < 1440 -> stringResource(id = R.string.notifications_hours_ago, (minutes / 60).toInt())
        else -> stringResource(id = R.string.notifications_yesterday)
    }
}
