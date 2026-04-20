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
import com.example.myapplication.features.profile.EditProfileScreen
import com.example.myapplication.features.statistics.StatisticsScreen
import com.example.myapplication.features.admin.AdminControlPanelScreen
import com.example.myapplication.features.admin.AdminDataAnalysisScreen
import com.example.myapplication.features.admin.AdminReportModerationScreen
import com.example.myapplication.features.resetPassword.ResetPassword
import com.example.myapplication.features.detailreport.DetailReportScreen

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
        composable<NewReport> {
            NewReportScreen(navController = navController)
        }
        composable<ReportDetail> {
            DetailReportScreen(navController = navController)
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
        composable<EditProfile> {
            EditProfileScreen(navController = navController)
        }
        composable<Statistics> {
            StatisticsScreen(navController = navController)
        }
        composable<AdminControlPanel> {
            AdminControlPanelScreen(navController = navController)
        }
        composable<AdminDataAnalysis> {
            AdminDataAnalysisScreen(navController = navController)
        }
        composable<AdminReportModeration> {
            AdminReportModerationScreen(navController = navController)
        }
    }
}
