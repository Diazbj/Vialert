package com.example.myapplication.features.homeuser

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.features.homeuser.components.MainLayout
import com.example.myapplication.features.homeuser.components.ReportList

@Composable
fun HomeUserScreen(
    viewModel: HomeUserViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    MainLayout(
        navController = navController,
        showSupportFab = true,
        onSupportClick = viewModel::onSupportAction
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportList(
                reports = uiState.reports,
                reportTimes = uiState.reportTimes,
                onImportantClick = { reportId ->
                    viewModel.onImportantClick(reportId)
                },
                onShareClick = {}
            )
        }
    }

}
