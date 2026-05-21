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
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.theme.VialertPurple
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.features.homeuser.components.MainLayout

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val visible by viewModel.visibleNotifications.collectAsState()
    val filter by viewModel.filter.collectAsState()

    MainLayout(
        navController = navController,
        topBarTitleRes = R.string.notifications_topbar_title,
        showNotificationsAction = false,
        showSupportFab = true,
        onSupportClick = {},
        onNavigateBack = { navController?.popBackStack() }
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
                    selectedFilter = filter,
                    onFilterSelected = viewModel::setFilter,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = viewModel::markAllAsRead) {
                    Text(stringResource(R.string.notifications_mark_all), color = VialertPurple)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (visible.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.notifications_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = visible, key = { it.id }) { notif ->
                        NotificationItem(
                            notif = notif,
                            onClick = { viewModel.markAsRead(notif.id) }
                        )
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
        NotificationFilter.TODAS to stringResource(R.string.notifications_filter_all),
        NotificationFilter.NO_LEIDAS to stringResource(R.string.notifications_filter_unread)
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
        tabs.forEach { (f, label) ->
            Tab(
                selected = selectedFilter == f,
                onClick = { onFilterSelected(f) },
                text = {
                    Text(label, color = if (selectedFilter == f) VialertPurple else Color.Gray)
                }
            )
        }
    }
}

@Composable
private fun NotificationItem(notif: Notification, onClick: () -> Unit) {
    val icon = iconForType(notif.tipo)
    val timeLabel = relativeTimeLabel(notif.creadoEn)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.leido) Color.White else VialertPurple.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notif.leido) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = VialertPurple.copy(alpha = 0.10f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = VialertPurple, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notif.titulo, style = MaterialTheme.typography.titleSmall, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(notif.mensaje, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(timeLabel, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                if (!notif.leido) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.size(8.dp).background(VialertPurple, CircleShape))
                }
            }
        }
    }
}

private fun iconForType(tipo: TipoNotificacion): ImageVector = when (tipo) {
    TipoNotificacion.REPORTE_CREADO -> Icons.Outlined.Campaign
    TipoNotificacion.REPORTE_ACTUALIZADO -> Icons.Outlined.Update
    TipoNotificacion.REPORTE_COMENTARIO -> Icons.Outlined.ChatBubble
    TipoNotificacion.REPORTE_REACCION -> Icons.Outlined.Favorite
    TipoNotificacion.REPORTE_CERRADO -> Icons.Outlined.CheckCircle
}

@Composable
private fun relativeTimeLabel(epochMillis: Long): String {
    val diff = System.currentTimeMillis() - epochMillis
    val minutes = diff / 60_000
    return when {
        minutes < 1 -> stringResource(R.string.notifications_now)
        minutes < 60 -> stringResource(R.string.notifications_minutes_ago, minutes.toInt())
        minutes < 1440 -> stringResource(R.string.notifications_hours_ago, (minutes / 60).toInt())
        else -> stringResource(R.string.notifications_yesterday)
    }
}
