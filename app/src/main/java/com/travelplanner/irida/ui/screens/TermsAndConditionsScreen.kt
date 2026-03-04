package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.ui.theme.*

data class TermsSection(
    val emoji: String,
    val title: String,
    val content: String
)

val termsSections = listOf(
    TermsSection(
        emoji = "📋",
        title = "Aceptación de los términos",
        content = "Al utilizar Irida Travel Planner aceptas estos términos y condiciones. Si no estás de acuerdo con alguna de estas condiciones, no debes usar la aplicación."
    ),
    TermsSection(
        emoji = "🔒",
        title = "Privacidad y datos",
        content = "Irida recopila únicamente los datos necesarios para el funcionamiento de la app: nombre de usuario, destinos guardados e itinerarios. No compartimos tus datos con terceros sin tu consentimiento explícito."
    ),
    TermsSection(
        emoji = "✈️",
        title = "Uso de la aplicación",
        content = "Irida es una herramienta de planificación personal. La información mostrada es orientativa. Verifica siempre los datos con los proveedores oficiales antes de realizar reservas."
    ),
    TermsSection(
        emoji = "🌐",
        title = "Servicios de terceros",
        content = "La aplicación puede integrar servicios externos como Google Maps o APIs de vuelos. El uso de estos servicios está sujeto a sus propias políticas de privacidad y términos de uso."
    ),
    TermsSection(
        emoji = "📝",
        title = "Modificaciones",
        content = "Nos reservamos el derecho de modificar estos términos en cualquier momento. Los cambios entrarán en vigor al publicar la versión actualizada en la aplicación."
    ),
    TermsSection(
        emoji = "⚠️",
        title = "Limitación de responsabilidad",
        content = "El equipo de desarrollo no se hace responsable de errores en la información de vuelos, hoteles o actividades mostrada en la app. Siempre confirma con los proveedores oficiales."
    )
)

@Composable
fun TermsAndConditionsScreen(
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    onBack: () -> Unit = {},
    showButtons: Boolean = true
) {
    var showRejectDialog by remember { mutableStateOf(false) }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            containerColor = NavyLight,
            title = {
                Text("¿Rechazar términos?", color = White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Si rechazas los términos y condiciones no podrás usar la aplicación.",
                    color = GrayMid,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRejectDialog = false
                        onReject()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Sí, rechazar", color = White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRejectDialog = false },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark)
                ) {
                    Text("Cancelar", color = GrayMid)
                }
            }
        )
    }

    Scaffold(
        containerColor = NavyDeep,
        bottomBar = {
            if (showButtons) {
                TermsBottomBar(
                    onAccept = onAccept,
                    onReject = { showRejectDialog = true }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header
            item {
                TermsHeader(onBack = onBack)
            }

            // Sections
            termsSections.forEachIndexed { index, section ->
                item {
                    TermsSectionCard(
                        number = index + 1,
                        section = section
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun TermsHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyMid)
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = White
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Términos y Condiciones",
                style = MaterialTheme.typography.titleLarge,
                color = White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Última actualización: 1 de marzo de 2025",
                style = MaterialTheme.typography.labelSmall,
                color = GrayMid
            )
        }
    }
}

@Composable
fun TermsSectionCard(number: Int, section: TermsSection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Number badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(TurquoisePrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelSmall,
                color = TurquoisePrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = section.emoji, fontSize = 18.sp)
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = section.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
            }
        }
    }
}

@Composable
fun TermsBottomBar(onAccept: () -> Unit, onReject: () -> Unit) {
    Surface(
        color = NavyMid,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReject,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GrayMid)
            ) {
                Text("Rechazar")
            }

            Button(
                onClick = onAccept,
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TurquoisePrimary,
                    contentColor = NavyDeep
                )
            ) {
                Text("✓ Aceptar y continuar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TermsAndConditionsScreenPreview() {
    IridaTheme {
        TermsAndConditionsScreen()
    }
}