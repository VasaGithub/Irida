package com.travelplanner.irida.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.SuccessGreen
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private const val RESEND_COOLDOWN_SECONDS = 60

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    var sentToEmail by remember { mutableStateOf("") }
    var cooldown by remember { mutableIntStateOf(0) }

    val state by authViewModel.state.collectAsState()

    // Cuenta regresiva de reenvío
    LaunchedEffect(cooldown) {
        if (cooldown > 0) {
            delay(1000L)
            cooldown--
        }
    }

    LaunchedEffect(state) {
        if (state is AuthViewModel.AuthUiState.Success) {
            authViewModel.resetState()
            sentToEmail = email.trim()
            emailSent = true
            cooldown = RESEND_COOLDOWN_SECONDS
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (emailSent) {
            // ── Paso 2: Email enviado ──────────────────────────────────────
            Icon(
                imageVector = Icons.Filled.MarkEmailRead,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Email enviado",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Hemos enviado un enlace para restablecer tu contraseña a:",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sentToEmail,
                style = MaterialTheme.typography.bodyLarge,
                color = TurquoisePrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Revisa también la carpeta de spam si no lo encuentras.",
                style = MaterialTheme.typography.bodySmall,
                color = GrayMid,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón abrir app de correo
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    runCatching { context.startActivity(intent) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = NavyDeep,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Abrir app de correo", color = NavyDeep, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reenviar con cooldown
            OutlinedButton(
                onClick = {
                    email = sentToEmail
                    authViewModel.sendPasswordReset(sentToEmail)
                },
                enabled = cooldown == 0 && state !is AuthViewModel.AuthUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (cooldown == 0) TurquoisePrimary else NavyLight
                )
            ) {
                val label = if (cooldown > 0) "Reenviar en ${cooldown}s" else "Reenviar enlace"
                Text(label, color = if (cooldown == 0) TurquoisePrimary else GrayMid, fontSize = 14.sp)
            }

        } else {
            // ── Paso 1: Formulario ────────────────────────────────────────
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = TurquoisePrimary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recuperar contraseña",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Introduce tu correo y te enviaremos un enlace para restablecer tu contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid,
                textAlign = TextAlign.Center
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
                shape = RoundedCornerShape(12.dp)
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
                        email.isBlank() ->
                            localError = "Introduce tu correo electrónico"
                        !emailRegex.matches(email.trim()) ->
                            localError = "El formato del correo no es válido"
                        else -> authViewModel.sendPasswordReset(email.trim())
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
                    Text("Enviar enlace", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Volver al inicio de sesión", color = GrayMid)
        }
    }
}
