package com.example.myapplication.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.domain.model.NotificacionUsuario
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.features.homeuser.components.MainLayout
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = viewModel(),
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
                    Text(text = stringResource(id = R.string.notifications_mark_all))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (uiState.contentState) {
                NotificationsUiContentState.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
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
                                onClick = { viewModel.markAsRead(item.notificacion.id) }
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

    TabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selectedFilter },
        modifier = modifier
    ) {
        tabs.forEach { (filter, label) ->
            Tab(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                text = { Text(text = label) }
            )
        }
    }
}

@Composable
private fun NotificationItem(
    item: NotificacionUsuario,
    onClick: () -> Unit
) {
    val icon = iconForType(item.notificacion.tipo)
    val relativeTime = relativeTimeLabel(item.notificacion.creadoEn)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.leido) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.notificacion.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.notificacion.mensaje,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = relativeTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (!item.leido) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = Color(0xFF6A1B9A),
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
        TipoNotificacion.REPORTE_CREADO -> Icons.Default.Campaign
        TipoNotificacion.REPORTE_ACTUALIZADO -> Icons.Default.Update
        TipoNotificacion.REPORTE_COMENTARIO -> Icons.Default.ChatBubble
        TipoNotificacion.REPORTE_REACCION -> Icons.Default.Favorite
        TipoNotificacion.REPORTE_CERRADO -> Icons.Default.CheckCircle
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
