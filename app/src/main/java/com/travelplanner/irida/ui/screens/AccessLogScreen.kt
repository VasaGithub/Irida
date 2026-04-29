package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.travelplanner.irida.data.local.entity.AccessLogEntity
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.NavyMid
import com.travelplanner.irida.ui.theme.SuccessGreen
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.AccessLogViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessLogScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccessLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial de accesos",
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyMid)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is AccessLogViewModel.LogUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavyDeep),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = TurquoisePrimary) }
            }

            is AccessLogViewModel.LogUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavyDeep)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        state.message,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is AccessLogViewModel.LogUiState.Success -> {
                if (state.logs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NavyDeep),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔒", fontSize = 48.sp)
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                "Sin registros de acceso",
                                color = White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                "Aquí aparecerán tus inicios y cierres de sesión",
                                color = GrayMid,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NavyDeep)
                            .padding(padding)
                    ) {
                        // Cabecera con resumen
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total de registros",
                                color = GrayMid,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "${state.logs.size}",
                                color = TurquoisePrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = 20.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(state.logs) { _, log ->
                                AccessLogItem(log = log)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessLogItem(log: AccessLogEntity) {
    val isLogin = log.action.uppercase() == "LOGIN"

    val actionColor = if (isLogin) SuccessGreen else ErrorRed
    val actionLabel = if (isLogin) "Inicio de sesión" else "Cierre de sesión"
    val actionIcon = if (isLogin) Icons.AutoMirrored.Filled.Login else Icons.AutoMirrored.Filled.Logout

    val formattedDate = runCatching {
        LocalDateTime.parse(log.datetime).format(logFormatter)
    }.getOrDefault(log.datetime)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo circular
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(actionColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionLabel,
                    tint = actionColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actionLabel,
                    color = White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formattedDate,
                    color = GrayMid,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Badge de acción
            Box(
                modifier = Modifier
                    .background(
                        color = actionColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = log.action,
                    color = actionColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
