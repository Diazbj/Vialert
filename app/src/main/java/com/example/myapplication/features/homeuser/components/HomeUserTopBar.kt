package com.example.myapplication.features.homeuser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUserTopBar(
    title: String,
    showNotificationsAction: Boolean,
    onTitleClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White // Fondo homogéneo (blanco)
        ),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 28.sp,
                color = Color(0xFF6A1B9A),
                modifier = Modifier.clickable(onClick = onTitleClick)
            )
        },
        actions = {
            if (showNotificationsAction) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6A1B9A).copy(alpha = 0.1f)), // Círculo de fondo suave
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications, // Icono Outline
                            contentDescription = stringResource(id = R.string.home_user_notifications_content_description),
                            tint = Color(0xFF6A1B9A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    )
}
