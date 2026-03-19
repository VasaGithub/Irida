package com.travelplanner.irida.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelplanner.irida.R
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
    var activityToDelete by remember { mutableStateOf<Activity?>(null) }
    var activityToEdit by remember { mutableStateOf<Activity?>(null) }

    // Nombres de las pestañas traducidos
    val tabs = listOf(
        stringResource(R.string.tab_itinerario),
        stringResource(R.string.tab_galeria),
        stringResource(R.string.tab_notas)
    )

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
                        // Pasamos la cantidad real de actividades desde el estado
                        TripStatsRow(trip = state.trip, activityCount = state.activities.size)
                    }

                    item {
                        // Cambiado de ScrollableTabRow a TabRow para que ocupe todo el ancho
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = NavyDeep,
                            contentColor = TurquoisePrimary,
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
                                        // Locale.getDefault() adapta el nombre del día ("Lun", "Mon", "Dl") al idioma del móvil
                                        DayHeader(
                                            day = date.format(
                                                DateTimeFormatter.ofPattern("EEE dd MMM")
                                                    .withLocale(java.util.Locale.getDefault())
                                            )
                                        )
                                    }
                                    items(activitiesOfDay) { activity ->
                                        ActivityCard(
                                            activity = activity,
                                            onEdit = {
                                                activityToEdit = activity
                                            },
                                            onDelete = {
                                                activityToDelete = activity
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        1 -> item { GalleryTabPlaceholder() }
                        2 -> item { NotesTabPlaceholder() }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // --- DIÁLOGO DE CONFIRMACIÓN DE BORRADO ---
                if (activityToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { activityToDelete = null },
                        containerColor = NavyLight,
                        title = {
                            Text("Eliminar actividad", color = White, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Text(
                                "¿Estás seguro de que quieres eliminar '${activityToDelete?.title}'? Esta acción no se puede deshacer.",
                                color = GrayMid
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    activityToDelete?.let { viewModel.deleteActivity(it.id) }
                                    activityToDelete = null
                                }
                            ) {
                                Text("Eliminar", color = ErrorRed, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { activityToDelete = null }) {
                                Text(stringResource(R.string.btn_cerrar), color = TurquoisePrimary)
                            }
                        }
                    )
                }

                // --- DIÁLOGO DE EDICIÓN DE ACTIVIDAD ---
                activityToEdit?.let { act ->
                    val context = LocalContext.current

                    var editTitle by remember(act) { mutableStateOf(act.title) }
                    var editDesc by remember(act) { mutableStateOf(act.description) }
                    var editDate by remember(act) { mutableStateOf(act.date) }
                    var editTime by remember(act) { mutableStateOf(act.time) }

                    val datePicker = DatePickerDialog(
                        context,
                        { _, year, month, day -> editDate = LocalDate.of(year, month + 1, day) },
                        editDate.year, editDate.monthValue - 1, editDate.dayOfMonth
                    ).apply {
                        datePicker.minDate = state.trip.startDate.atStartOfDay(java.time.ZoneOffset.UTC.normalized() as java.time.ZoneId).toInstant().toEpochMilli()
                        datePicker.maxDate = state.trip.endDate.atStartOfDay(java.time.ZoneOffset.UTC.normalized() as java.time.ZoneId).toInstant().toEpochMilli()
                    }

                    val timePicker = TimePickerDialog(
                        context,
                        { _, h, m -> editTime = LocalTime.of(h, m) },
                        editTime.hour, editTime.minute, true
                    )

                    AlertDialog(
                        onDismissRequest = { activityToEdit = null },
                        containerColor = NavyLight,
                        title = { Text("Editar Actividad", color = TurquoisePrimary, fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = editTitle, onValueChange = { editTitle = it },
                                    label = { Text("Título", color = GrayMid) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = White, unfocusedTextColor = White,
                                        focusedBorderColor = TurquoisePrimary, unfocusedBorderColor = GrayDark
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = editDesc, onValueChange = { editDesc = it },
                                    label = { Text("Descripción", color = GrayMid) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = White, unfocusedTextColor = White,
                                        focusedBorderColor = TurquoisePrimary, unfocusedBorderColor = GrayDark
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { datePicker.show() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark)
                                    ) {
                                        Text(editDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                    }
                                    OutlinedButton(
                                        onClick = { timePicker.show() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark)
                                    ) {
                                        Text(editTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.updateActivity(act.id, editTitle, editDesc, editDate, editTime)
                                    activityToEdit = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary, contentColor = NavyDeep)
                            ) {
                                Text("Actualizar", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { activityToEdit = null }) {
                                Text("Cancelar", color = White)
                            }
                        }
                    )
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
            .defaultMinSize(minHeight = 220.dp) // Altura dinámica por si la descripción es muy larga
            .background(Brush.verticalGradient(colors = listOf(Color(0xFF1A3A5C), NavyDeep)))
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_volver), tint = White)
        }

        Column(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 20.dp).padding(top = 24.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = trip.emoji, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trip.title,
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            // Si hay descripción, la mostramos aquí en el centro
            if (trip.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = trip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(
                    R.string.trip_card_dates_nights,
                    trip.startDate.format(dateFormatter),
                    trip.endDate.format(dateFormatter),
                    trip.getNights()
                ),
                style = MaterialTheme.typography.labelLarge,
                color = TurquoisePrimary
            )
        }
    }
}

@Composable
fun TripStatsRow(trip: Trip, activityCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(value = "${trip.getNights()}", label = stringResource(R.string.stat_noches), emoji = "🌙")
        StatDivider()
        StatItem(value = "$activityCount", label = stringResource(R.string.stat_actividades), emoji = "📍")
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
fun ActivityCard(
    activity: Activity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = activity.title, style = MaterialTheme.typography.titleMedium, color = White, fontWeight = FontWeight.SemiBold)
                    if (activity.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = activity.description, style = MaterialTheme.typography.bodyMedium, color = GrayMid)
                    }
                }

                // Botón Editar
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TurquoisePrimary)
                }

                // Botón Eliminar
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ErrorRed)
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
            Text(text = stringResource(R.string.empty_activities_title), style = MaterialTheme.typography.bodyMedium, color = GrayMid)
            Text(text = stringResource(R.string.empty_activities_desc), style = MaterialTheme.typography.bodySmall, color = GrayDark)
        }
    }
}

@Composable
fun GalleryTabPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🖼️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.placeholder_gallery), style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        }
    }
}

@Composable
fun NotesTabPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📝", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.placeholder_notes), style = MaterialTheme.typography.bodyMedium, color = GrayMid)
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