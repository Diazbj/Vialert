package com.example.myapplication.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.features.explore.ExploreScreen
import com.example.myapplication.features.forgetPassword.ForgetPassword
import com.example.myapplication.features.home.HomeScreen
import com.example.myapplication.features.homeuser.HomeUserScreen
import com.example.myapplication.features.login.LoginScreen
import com.example.myapplication.features.notifications.NotificationsScreen
import com.example.myapplication.features.profile.ProfileScreen
import com.example.myapplication.features.reportdetail.ReportDetailScreen
import com.example.myapplication.features.reports.ReportsScreen
import com.example.myapplication.features.resetPassword.ResetPassword
import com.example.myapplication.features.signup.SignUpScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(navController = navController)
        }
        composable<Login> {
            LoginScreen(navController = navController)
        }
        composable<SignUp> {
            SignUpScreen(navController = navController)
        }
        composable<ForgetPassword> {
            ForgetPassword(navController = navController)
        }
        composable<ResetPassword> {
            ResetPassword(navController = navController)
        }
        composable<HomeUser> {
            HomeUserScreen(navController = navController)
        }
        composable<Reports> {
            ReportsScreen(navController = navController)
        }
        composable<ReportDetail> {
            ReportDetailScreen(navController = navController)
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