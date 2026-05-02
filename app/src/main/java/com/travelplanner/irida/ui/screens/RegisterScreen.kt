package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.AuthViewModel

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf("") }

    val state by authViewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthViewModel.AuthUiState.Success) {
            authViewModel.resetState()
            onRegisterSuccess(email.trim())
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TurquoisePrimary,
        unfocusedBorderColor = GrayMid,
        focusedTextColor = White,
        unfocusedTextColor = White,
        cursorColor = TurquoisePrimary,
        focusedContainerColor = NavyLight,
        unfocusedContainerColor = NavyLight
    )
    val fieldShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = White,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Únete a Irida y empieza a planificar",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMid
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    localError = ""
                },
                label = { Text("Correo electrónico", color = GrayMid) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = fieldShape
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    localError = ""
                },
                label = { Text("Nombre de usuario", color = GrayMid) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = fieldShape
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    localError = ""
                },
                label = { Text("Contraseña", color = GrayMid) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = GrayMid
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = fieldShape
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    localError = ""
                },
                label = { Text("Confirmar contraseña", color = GrayMid) },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = GrayMid
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = fieldShape
            )

            Spacer(modifier = Modifier.height(8.dp))

            val errorMsg = localError.ifEmpty {
                (state as? AuthViewModel.AuthUiState.Error)?.message ?: ""
            }
            if (errorMsg.isNotEmpty()) {
                Text(
                    text = errorMsg,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    localError = ""
                    when {
                        email.isBlank() || username.isBlank() || password.isBlank() ->
                            localError = "Rellena todos los campos"
                        !emailRegex.matches(email.trim()) ->
                            localError = "El formato del correo no es válido"
                        password.length < 6 ->
                            localError = "La contraseña debe tener al menos 6 caracteres"
                        password != confirmPassword ->
                            localError = "Las contraseñas no coinciden"
                        else -> authViewModel.register(email.trim(), username.trim(), password)
                    }
                },
                enabled = state !is AuthViewModel.AuthUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state is AuthViewModel.AuthUiState.Loading) {
                    CircularProgressIndicator(color = NavyDeep, strokeWidth = 2.dp)
                } else {
                    Text("Registrarse", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = GrayMid)
            }
    }
}
