package com.example.myapplication.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AppNavigation(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val sessionState by sessionViewModel.sessionState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when(val state = sessionState) {
            is SessionState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SessionState.NotAuthenticated -> AuthNavigation()
            is SessionState.Authenticated -> MainNavigation(
                state.session,
                onLogout = sessionViewModel::logout
            )
        }
    }

}