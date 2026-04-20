package com.example.myapplication.features.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.components.ResultDialog
import com.example.myapplication.core.components.VialertPasswordField
import com.example.myapplication.core.components.VialertTextField
import com.example.myapplication.core.navigation.ForgetPassword
import com.example.myapplication.core.navigation.HomeUser
import com.example.myapplication.core.utils.RequestResult

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController? = null,
    onNavigateToRegister: () -> Unit
) {
    val loginResult by viewModel.loginResult.collectAsState()

    ResultDialog(
        result = loginResult,
        onDismiss = {
            if (loginResult is RequestResult.Success) {
                viewModel.clearResult()
                navController?.navigate(HomeUser)
            } else {
                viewModel.clearResult()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF6A1B9A),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(15.dp))

        VialertTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.onChange(it) },
            label = stringResource(R.string.login_label_email),
            placeholder = stringResource(R.string.login_placeholder_email),
            isError = viewModel.email.error != null,
            supportingText = viewModel.email.error
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = stringResource(R.string.login_forgot_password),
            color = Color(0xFF6A1B9A),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    navController?.navigate(ForgetPassword)
                }
        )

        VialertPasswordField(
            value = viewModel.password.value,
            onValueChange = { viewModel.onPasswordChanged(it) },
            isError = viewModel.password.error != null,
            supportingText = viewModel.password.error
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.loginFunction()
            },
            enabled = loginResult !is RequestResult.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A1B9A),
                contentColor = Color.White
            )
        ) {
            if (loginResult is RequestResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = stringResource(R.string.login_btn_login))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController?.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6A1B9A))
        ) {
            Text(text = stringResource(R.string.login_btn_back))
        }

        Spacer(modifier = Modifier.height(15.dp))
        
        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.login_no_account), color = Color(0xFF6A1B9A))
        }
    }
}
