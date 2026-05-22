package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GoldAccent
import com.travelplanner.irida.ui.theme.GrayDark
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.NavyMid
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.ReservationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(
    onBack: () -> Unit,
    viewModel: ReservationsViewModel = hiltViewModel()
) {
    val uiState     by viewModel.uiState.collectAsState()
    val cancelState by viewModel.cancelState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var tripToCancel by remember { mutableStateOf<Trip?>(null) }

    LaunchedEffect(cancelState) {
        when (cancelState) {
            is ReservationsViewModel.CancelState.Success -> {
                snackbarHostState.showSnackbar("Reserva cancelada correctamente")
                viewModel.resetCancelState()
            }
            is ReservationsViewModel.CancelState.Error -> {
                snackbarHostState.showSnackbar(
                    (cancelState as ReservationsViewModel.CancelState.Error).message
                )
                viewModel.resetCancelState()
            }
            else -> Unit
        }
    }

    tripToCancel?.let { trip ->
        AlertDialog(
            onDismissRequest = { tripToCancel = null },
            containerColor = NavyLight,
            title = {
                Text("Cancelar reserva", color = White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "¿Cancelar la reserva de \"${trip.title}\"? Esta acción no se puede deshacer.",
                    color = GrayMid,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.cancel(trip); tripToCancel = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Cancelar reserva", color = White) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { tripToCancel = null },
                    border = BorderStroke(1.dp, GrayDark)
                ) { Text("Volver", color = GrayMid) }
            }
        )
    }

    Scaffold(
        containerColor = NavyDeep,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        val isCancelling = cancelState is ReservationsViewModel.CancelState.Loading

        when (val state = uiState) {
            is ReservationsViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = TurquoisePrimary) }
            }

            is ReservationsViewModel.UiState.Success -> {
                if (state.reservations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏨", fontSize = 56.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "No tienes reservas activas",
                                color = White,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Busca hoteles para hacer tu primera reserva",
                                color = GrayMid,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NavyDeep)
                            .padding(padding),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.reservations, key = { it.id }) { trip ->
                            ReservationCard(
                                trip = trip,
                                isCancelling = isCancelling,
                                onCancelClick = { tripToCancel = trip }
                            )
                        }
                    }
                }
            }

            is ReservationsViewModel.UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = ErrorRed, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(
    trip: Trip,
    isCancelling: Boolean,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = trip.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = trip.title,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = GrayDark.copy(alpha = 0.5f))
            Spacer(Modifier.height(12.dp))

            if (trip.reservationStart != null && trip.reservationEnd != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = TurquoisePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${trip.reservationStart} → ${trip.reservationEnd}",
                        color = GrayMid,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(6.dp))
            }

            trip.reservationPrice?.let { price ->
                Text(
                    "%.2f € total".format(price),
                    color = GoldAccent,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(6.dp))
            }

            trip.reservationId?.let { id ->
                Text(
                    "ID: …${id.takeLast(8)}",
                    color = GrayDark,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCancelClick,
                enabled = !isCancelling,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, if (isCancelling) GrayDark else ErrorRed),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRed,
                    disabledContentColor = GrayDark,
                    containerColor = Color.Transparent
                )
            ) {
                if (isCancelling) {
                    CircularProgressIndicator(
                        color = GrayDark,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Cancelar reserva", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
