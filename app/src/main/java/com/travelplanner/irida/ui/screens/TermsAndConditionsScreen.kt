package com.travelplanner.irida.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.R
import com.travelplanner.irida.ui.theme.*

// Modificado para guardar el ID de los strings en lugar de texto plano
data class TermsSection(
    val emoji: String,
    @param: StringRes val titleRes: Int,
    @param: StringRes val contentRes: Int
)

val termsSections = listOf(
    TermsSection(
        emoji = "📋",
        titleRes = R.string.terms_title_1,
        contentRes = R.string.terms_content_1
    ),
    TermsSection(
        emoji = "🔒",
        titleRes = R.string.terms_title_2,
        contentRes = R.string.terms_content_2
    ),
    TermsSection(
        emoji = "✈️",
        titleRes = R.string.terms_title_3,
        contentRes = R.string.terms_content_3
    ),
    TermsSection(
        emoji = "🌐",
        titleRes = R.string.terms_title_4,
        contentRes = R.string.terms_content_4
    ),
    TermsSection(
        emoji = "📝",
        titleRes = R.string.terms_title_5,
        contentRes = R.string.terms_content_5
    ),
    TermsSection(
        emoji = "⚠️",
        titleRes = R.string.terms_title_6,
        contentRes = R.string.terms_content_6
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
                Text(stringResource(R.string.dialog_title_rechazar), color = White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = stringResource(R.string.dialog_desc_rechazar),
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
                    Text(stringResource(R.string.btn_si_rechazar), color = White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRejectDialog = false },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark)
                ) {
                    Text(stringResource(R.string.btn_cancelar), color = GrayMid)
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_volver),
                tint = White
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_terminos),
                style = MaterialTheme.typography.titleLarge,
                color = White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.subtitle_actualizacion_terminos),
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
                        // Aquí llamamos a stringResource usando la variable entera que guardamos
                        text = stringResource(id = section.titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    // Y aquí igual para el contenido
                    text = stringResource(id = section.contentRes),
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
                Text(stringResource(R.string.btn_rechazar))
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
                Text(stringResource(R.string.btn_aceptar_continuar), fontWeight = FontWeight.Bold)
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