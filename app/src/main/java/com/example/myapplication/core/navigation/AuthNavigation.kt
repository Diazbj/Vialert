package com.example.myapplication.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.features.home.HomeScreen
import com.example.myapplication.features.login.LoginScreen
import com.example.myapplication.features.signup.RegistrationSuccessScreen
import com.example.myapplication.features.signup.SignUpScreen
import com.example.myapplication.features.forgetPassword.ForgetPasswordScreen

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {

        composable<Home> {
            HomeScreen(
                navController = navController,
                onNavigateToRegister = {
                    navController.navigate(SignUp)
                }
            )
        }

        composable<Login> {
            LoginScreen(
                navController = navController,
                onNavigateToRegister = {
                    navController.navigate(SignUp)
                }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                navController = navController,
                onNavigateToBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<RegistrationSuccess> {
            RegistrationSuccessScreen(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Home) { inclusive = false }
                    }
                }
            )
        }

        composable<ForgetPassword> {
            ForgetPasswordScreen(
                navController = navController,
                onNavigateToRegister = {
                    navController.navigate(ForgetPassword)
                }
            )
        }
        
        composable<HomeUser> {
            // Pantalla principal una vez logueado
        }

    }
}
