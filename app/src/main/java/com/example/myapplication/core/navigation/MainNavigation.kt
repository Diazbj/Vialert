package com.example.myapplication.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.domain.model.UserSession
import com.example.myapplication.features.explore.ExploreScreen
import com.example.myapplication.features.homeuser.HomeUserScreen
import com.example.myapplication.features.notifications.NotificationsScreen
import com.example.myapplication.features.profile.ProfileScreen
import com.example.myapplication.features.newreport.NewReportScreen
import com.example.myapplication.features.myreports.MyReportsScreen
import com.example.myapplication.features.resetPassword.ResetPassword

@Composable
fun MainNavigation(
    session: UserSession,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeUser
    ) {
        composable<ResetPassword> {
            ResetPassword(navController = navController)
        }
        composable<HomeUser> {
            HomeUserScreen(navController = navController)
        }
        composable<Reports> {
            MyReportsScreen(navController = navController)
        }
        composable<ReportDetail> {
            NewReportScreen(navController = navController)
        }
        composable<Explore> {
            ExploreScreen(navController = navController)
        }
        composable<Profile> {
            ProfileScreen(navController = navController)
        }
        composable<Notifications> {
            NotificationsScreen(navController = navController)
        }
    }
}