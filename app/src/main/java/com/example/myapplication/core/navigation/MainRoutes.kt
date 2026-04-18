package com.example.myapplication.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home : MainRoutes()

@Serializable
data object Login : MainRoutes()

@Serializable
data object SignUp : MainRoutes()

@Serializable
data object ForgetPassword : MainRoutes()

@Serializable
data object ResetPassword : MainRoutes()

@Serializable
data object HomeUser : MainRoutes()

@Serializable
data object Reports : MainRoutes()

@Serializable
data class NewReport(val reportId: String? = null) : MainRoutes()

@Serializable
data class ReportDetail(val reportId: String) : MainRoutes()

@Serializable
data object Explore : MainRoutes()

@Serializable
data object Profile : MainRoutes()

@Serializable
data object Notifications : MainRoutes()

sealed class MainRoutes
