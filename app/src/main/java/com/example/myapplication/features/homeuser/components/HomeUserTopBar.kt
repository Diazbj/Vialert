package com.example.myapplication.features.homeuser.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(id = R.string.home_user_notifications_content_description)
                    )
                }
            }
        }
    )
}
