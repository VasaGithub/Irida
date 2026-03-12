package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.ui.theme.*
import com.travelplanner.irida.ui.viewmodels.TripDetailUiState
import com.travelplanner.irida.ui.viewmodels.TripDetailViewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun TripDetailScreen(
    tripId: String,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: TripDetailViewModel = viewModel()
) {
    // Carga el viaje al entrar en la pantalla
    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Itinerario", "Galería", "Notas")

    Scaffold(
        containerColor = NavyDeep,
        bottomBar = {
            BottomNavBar(selectedTab = 1, onTabSelected = { tab ->
                when (tab) {
                    0 -> onNavigate("home")
                    1 -> onNavigate("activities")
                    2 -> onNavigate("gallery")
                    3 -> onNavigate("settings")
                }
            })
        }
    ) { paddingValues ->
        when (val state = uiState) {

            is TripDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(NavyDeep),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TurquoisePrimary)
                }
            }

            is TripDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(NavyDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = ErrorRed)
                }
            }

            is TripDetailUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavyDeep)
                        .padding(paddingValues)
                ) {
                    item {
                        TripDetailHeader(trip = state.trip, onBack = onBack)
                    }

                    item {
                        TripStatsRow(trip = state.trip)
                    }

                    item {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = NavyDeep,
                            contentColor = TurquoisePrimary,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = TurquoisePrimary
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            color = if (selectedTab == index) TurquoisePrimary else GrayMid,
                                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }
                    }

                    when (selectedTab) {
                        0 -> {
                            if (state.activities.isEmpty()) {
                                item { ActivityEmptyState() }
                            } else {
                                val grouped = state.activities.groupBy { it.date }
                                grouped.forEach { (date, activitiesOfDay) ->
                                    item {
                                        DayHeader(
                                            day = date.format(
                                                DateTimeFormatter.ofPattern("EEE dd MMM")
                                                    .withLocale(java.util.Locale("es"))
                                            )
                                        )
                                    }
                                    items(activitiesOfDay) { activity ->
                                        ActivityCard(activity = activity)
                                    }
                                }
                            }
                        }
                        1 -> item { GalleryTabPlaceholder() }
                        2 -> item { NotesTabPlaceholder() }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ── Componentes ────────────────────────────────────────────────────────────

@Composable
fun TripDetailHeader(trip: Trip, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Brush.verticalGradient(colors = listOf(Color(0xFF1A3A5C), NavyDeep)))
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = White)
        }

        Column(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = trip.emoji, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trip.title,
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "📅 ${trip.startDate.format(dateFormatter)} – ${trip.endDate.format(dateFormatter)} · ${trip.getNights()} noches",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid
            )
        }
    }
}

@Composable
fun TripStatsRow(trip: Trip) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(value = "${trip.getNights()}", label = "NOCHES", emoji = "🌙")
        StatDivider()
        StatItem(value = "${trip.activities.size}", label = "ACTIVIDADES", emoji = "📍")
        StatDivider()
        StatItem(
            value = if (trip.description.length > 12) trip.description.take(12) + "…" else trip.description,
            label = "DESCRIPCIÓN",
            emoji = "📝"
        )
    }
}

@Composable
fun StatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = White, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = GrayMid, letterSpacing = 1.sp)
    }
}

@Composable
fun StatDivider() {
    Box(modifier = Modifier.width(1.dp).height(40.dp).background(GrayDark))
}

@Composable
fun DayHeader(day: String) {
    Text(
        text = day.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TurquoisePrimary,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun ActivityCard(activity: Activity) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(52.dp)
        ) {
            Text(text = activity.time.format(timeFormatter), style = MaterialTheme.typography.labelSmall, color = GrayMid)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.size(8.dp).background(TurquoisePrimary, CircleShape))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyLight)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = activity.title, style = MaterialTheme.typography.titleMedium, color = White, fontWeight = FontWeight.SemiBold)
                if (activity.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = activity.description, style = MaterialTheme.typography.bodyMedium, color = GrayMid)
                }
            }
        }
    }
}

@Composable
fun ActivityEmptyState() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🗺️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("No hay actividades para este viaje.", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
            Text("Añade la primera actividad con el botón +", style = MaterialTheme.typography.bodySmall, color = GrayDark)
        }
    }
}

@Composable
fun GalleryTabPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🖼️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Galería disponible en la pantalla Galería de viaje", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        }
    }
}

@Composable
fun NotesTabPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📝", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Aún no hay notas para este viaje.", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripDetailScreenPreview() {
    IridaTheme {
        TripDetailScreen(tripId = "1")
    }
}