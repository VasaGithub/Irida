package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by authViewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthViewModel.AuthUiState.Success) {
            authViewModel.resetState()
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Irida",
            style = MaterialTheme.typography.displayMedium,
            color = TurquoisePrimary,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Tu planificador de viajes",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMid
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico", color = GrayMid) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TurquoisePrimary,
                unfocusedBorderColor = GrayMid,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = TurquoisePrimary,
                focusedContainerColor = NavyLight,
                unfocusedContainerColor = NavyLight
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = GrayMid) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TurquoisePrimary,
                unfocusedBorderColor = GrayMid,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = TurquoisePrimary,
                focusedContainerColor = NavyLight,
                unfocusedContainerColor = NavyLight
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state is AuthViewModel.AuthUiState.Error) {
            Text(
                text = (state as AuthViewModel.AuthUiState.Error).message,
                color = androidx.compose.ui.graphics.Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { authViewModel.login(email.trim(), password) },
            enabled = state !is AuthViewModel.AuthUiState.Loading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state is AuthViewModel.AuthUiState.Loading) {
                CircularProgressIndicator(color = NavyDeep, strokeWidth = 2.dp)
            } else {
                Text("Iniciar sesión", color = NavyDeep, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onNavigateToForgotPassword) {
            Text("¿Olvidaste tu contraseña?", color = TurquoisePrimary)
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate", color = GrayMid)
        }
    }
}
