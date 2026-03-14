package com.travelplanner.irida.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelplanner.irida.R
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.ui.theme.*
import com.travelplanner.irida.ui.viewmodels.TripListUiState
import com.travelplanner.irida.ui.viewmodels.TripListViewModel
import java.time.format.DateTimeFormatter

private const val HOME_TAG = "HomeScreen"
private val cardDateFormatter = DateTimeFormatter.ofPattern("dd MMM")

@Composable
fun HomeScreen(
    onTripClick: (Trip) -> Unit = {},
    onAddTripClick: () -> Unit = {},
    onEditTripClick: (Trip) -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: TripListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    // Diálogo de confirmación de borrado
    tripToDelete?.let { trip ->
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            containerColor = NavyLight,
            title = { Text(stringResource(R.string.title_eliminar_viaje), color = White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = stringResource(R.string.msg_confirmar_eliminar_viaje, trip.title),
                    color = GrayMid,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.i(HOME_TAG, "deleteTrip confirmado para id=${trip.id}")
                        viewModel.deleteTrip(trip.id)
                        tripToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text(stringResource(R.string.btn_eliminar), color = White) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { tripToDelete = null },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark)
                ) { Text(stringResource(R.string.btn_cancelar), color = GrayMid) }
            }
        )
    }

    Scaffold(
        containerColor = NavyDeep,
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        0 -> onNavigate("home")
                        1 -> onNavigate("activities")
                        2 -> onNavigate("gallery")
                        3 -> onNavigate("settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { HomeHeader() }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.title_proximos_viajes),
                        style = MaterialTheme.typography.titleLarge,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    val count = (uiState as? TripListUiState.Success)?.trips?.size ?: 0
                    Text(
                        text = stringResource(R.string.label_cantidad_viajes, count),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TurquoisePrimary
                    )
                }
            }

            when (val state = uiState) {
                is TripListUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = TurquoisePrimary) }
                    }
                }
                is TripListUiState.Success -> {
                    if (state.trips.isEmpty()) {
                        item { TripsEmptyState() }
                    } else {
                        items(state.trips, key = { it.id }) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = {
                                    Log.d(HOME_TAG, "Trip seleccionado: ${trip.id}")
                                    onTripClick(trip)
                                },
                                onEditClick = { onEditTripClick(trip) },
                                onDeleteClick = { tripToDelete = trip }
                            )
                        }
                    }
                }
                is TripListUiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { Text(state.message, color = ErrorRed) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Log.d(HOME_TAG, "Añadir viaje pulsado")
                            onAddTripClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TurquoisePrimary),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, TurquoisePrimary.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.btn_add_nuevo_viaje))
                    }
                }
            }
        }
    }
}

// ── Componentes ────────────────────────────────────────────────────────────

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(colors = listOf(NavyMid, NavyDeep)))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        // He dejado 'Iker' como parámetro por si en el futuro tenéis perfiles de usuario
                        text = stringResource(R.string.greeting_user, "Iker"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayMid
                    )
                    Text(
                        text = stringResource(R.string.title_mis_viajes),
                        style = MaterialTheme.typography.headlineLarge,
                        color = White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(colors = listOf(TurquoisePrimary, TurquoiseLight)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "IV", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

/**
 * TripCard con botones de editar/eliminar y fechas desde LocalDate.
 */
@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = listOf(NavyLight, Color(0xFF1A2E42))))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = trip.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = trip.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(
                                    R.string.trip_card_dates_nights,
                                    trip.startDate.format(cardDateFormatter),
                                    trip.endDate.format(cardDateFormatter),
                                    trip.getNights()
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayMid
                            )
                        }
                    }
                    Row {
                        IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Edit, stringResource(R.string.cd_editar), tint = TurquoisePrimary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Delete, stringResource(R.string.cd_eliminar), tint = ErrorRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (trip.budget > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.format_budget, trip.budget.toInt()),
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.format_percent, trip.getBudgetProgressPercent()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TurquoisePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (trip.budgetSpent / trip.budget).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = TurquoisePrimary,
                        trackColor = GrayDark,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
fun TripsEmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🗺️", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.empty_state_title), style = MaterialTheme.typography.titleMedium, color = White, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(R.string.empty_state_desc), style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        }
    }
}

/**
 * BottomNavBar — mantenida en HomeScreen.kt como en Sprint 01.
 * Importada por el resto de pantallas desde aquí.
 */
@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = NavyMid, tonalElevation = 0.dp) {
        val items = listOf(
            Triple(stringResource(R.string.nav_inicio), Icons.Default.Home, "home"),
            Triple(stringResource(R.string.nav_actividades), Icons.Default.DateRange, "activities"),
            Triple(stringResource(R.string.nav_galeria), Icons.Default.DateRange, "gallery"),
            Triple(stringResource(R.string.nav_ajustes), Icons.Default.Settings, "settings")
        )
        items.forEachIndexed { index, (label, icon, _) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TurquoisePrimary,
                    selectedTextColor = TurquoisePrimary,
                    unselectedIconColor = GrayMid,
                    unselectedTextColor = GrayMid,
                    indicatorColor = NavyLight
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    IridaTheme {
        HomeScreen()
    }
}