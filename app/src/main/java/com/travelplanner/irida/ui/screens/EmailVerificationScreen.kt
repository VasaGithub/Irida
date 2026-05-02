package com.travelplanner.irida.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
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

private const val RESEND_COOLDOWN_SECONDS = 60

@Composable
fun EmailVerificationScreen(
    email: String,
    authViewModel: AuthViewModel,
    onVerifiedSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val verifyState by authViewModel.verifyState.collectAsState()

    var cooldown by remember { mutableIntStateOf(0) }
    var resendFeedback by remember { mutableStateOf("") }

    // Cuenta regresiva de reenvío
    LaunchedEffect(cooldown) {
        if (cooldown > 0) {
            delay(1000L)
            cooldown--
        }
    }

    // Reaccionar al estado de verificación
    LaunchedEffect(verifyState) {
        when (verifyState) {
            is AuthViewModel.VerifyUiState.Verified -> {
                authViewModel.resetVerifyState()
                onVerifiedSuccess()
            }
            is AuthViewModel.VerifyUiState.ResendSuccess -> {
                resendFeedback = "Email reenviado correctamente. Revisa tu bandeja."
                cooldown = RESEND_COOLDOWN_SECONDS
                authViewModel.resetVerifyState()
            }
            else -> Unit
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
        // Icono
        Icon(
            imageVector = Icons.Filled.Email,
            contentDescription = null,
            tint = TurquoisePrimary,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Verifica tu correo",
            style = MaterialTheme.typography.headlineMedium,
            color = White,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hemos enviado un enlace de verificación a:",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMid,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = TurquoisePrimary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Abre el email y pulsa el enlace antes de continuar.",
            style = MaterialTheme.typography.bodySmall,
            color = GrayMid,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mensaje de feedback de reenvío
        AnimatedVisibility(
            visible = resendFeedback.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.MarkEmailRead,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = resendFeedback,
                    color = SuccessGreen,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Error de verificación
        if (verifyState is AuthViewModel.VerifyUiState.Error) {
            Text(
                text = (verifyState as AuthViewModel.VerifyUiState.Error).message,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón principal: Ya he verificado mi email
        Button(
            onClick = { authViewModel.checkEmailVerified() },
            enabled = verifyState !is AuthViewModel.VerifyUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (verifyState is AuthViewModel.VerifyUiState.Loading) {
                CircularProgressIndicator(color = NavyDeep, strokeWidth = 2.dp)
            } else {
                Text(
                    "Ya he verificado mi email",
                    color = NavyDeep,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón secundario: Reenviar email (con cooldown)
        OutlinedButton(
            onClick = {
                resendFeedback = ""
                authViewModel.resendVerificationEmail()
            },
            enabled = cooldown == 0 && verifyState !is AuthViewModel.VerifyUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (cooldown == 0) TurquoisePrimary else NavyLight
            )
        ) {
            val label = if (cooldown > 0) "Reenviar en ${cooldown}s" else "Reenviar email"
            Text(label, color = if (cooldown == 0) TurquoisePrimary else GrayMid, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            authViewModel.resetVerifyState()
            onNavigateToLogin()
        }) {
            Text("Volver al inicio de sesión", color = GrayMid)
        }
    }
}
