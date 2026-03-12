package com.travelplanner.irida.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.travelplanner.irida.ui.viewmodels.TripListUiState
import com.travelplanner.irida.ui.viewmodels.TripListViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val ADD_ACT_TAG = "AddActivityScreen"
private val actDateFormatter  = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val actTimeFormatter  = DateTimeFormatter.ofPattern("HH:mm")

// ── Tabs ───────────────────────────────────────────────────────────────────

private enum class ActivityTab(val label: String, val emoji: String) {
    ADD("Añadir", "➕"),
    VIEW("Ver actividades", "📋")
}

// ── Screen ─────────────────────────────────────────────────────────────────

@Composable
fun AddActivityScreen(
    onNavigate: (String) -> Unit = {},
    tripListViewModel: TripListViewModel = viewModel(),
    tripDetailViewModel: TripDetailViewModel = viewModel()
) {
    val context      = LocalContext.current
    val tripsState   by tripListViewModel.uiState.collectAsState()
    val detailState  by tripDetailViewModel.uiState.collectAsState()
    val valErrors    by tripDetailViewModel.validationErrors.collectAsState()

    var selectedTab      by remember { mutableStateOf(ActivityTab.ADD) }
    var selectedTrip     by remember { mutableStateOf<Trip?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Campos del formulario
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date        by remember { mutableStateOf<LocalDate?>(null) }
    var time        by remember { mutableStateOf<LocalTime?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    val trips = (tripsState as? TripListUiState.Success)?.trips ?: emptyList()

    // Cada vez que cambia el viaje seleccionado → cargar sus actividades
    LaunchedEffect(selectedTrip) {
        tripDetailViewModel.clearValidationErrors()
        title = ""; description = ""; date = null; time = null
        showSuccess = false
        selectedTrip?.let {
            Log.d(ADD_ACT_TAG, "loadTrip: ${it.id}")
            tripDetailViewModel.loadTrip(it.id)
        }
    }

    // DatePicker restringido al rango del viaje
    val datePicker = selectedTrip?.let { trip ->
        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = LocalDate.of(year, month + 1, day)
                Log.d(ADD_ACT_TAG, "Fecha: $date")
            },
            trip.startDate.year,
            trip.startDate.monthValue - 1,
            trip.startDate.dayOfMonth
        ).apply {
            datePicker.minDate = trip.startDate
                .atStartOfDay(java.time.ZoneOffset.UTC.normalized() as java.time.ZoneId)
                .toInstant().toEpochMilli()
            datePicker.maxDate = trip.endDate
                .atStartOfDay(java.time.ZoneOffset.UTC.normalized() as java.time.ZoneId)
                .toInstant().toEpochMilli()
        }
    }

    val timePicker = TimePickerDialog(context, { _, h, m ->
        time = LocalTime.of(h, m)
        Log.d(ADD_ACT_TAG, "Hora: $time")
    }, 9, 0, true)

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 24.dp, bottom = 16.dp)) {
                Text("Actividades", style = MaterialTheme.typography.headlineMedium, color = White, fontWeight = FontWeight.ExtraBold)
                Text("Gestiona las actividades de tus viajes", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
            }

            // Tab selector
            ActTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Spacer(Modifier.height(8.dp))

            // Contenido animado
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal)
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    else
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    ActivityTab.ADD -> AddTabContent(
                        trips            = trips,
                        selectedTrip     = selectedTrip,
                        dropdownExpanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        onTripSelected   = { selectedTrip = it; dropdownExpanded = false },
                        title            = title,
                        onTitleChange    = { title = it },
                        description      = description,
                        onDescChange     = { description = it },
                        date             = date,
                        time             = time,
                        valErrors        = valErrors,
                        showSuccess      = showSuccess,
                        onDateClick      = { datePicker?.show() },
                        onTimeClick      = { timePicker.show() },
                        onSave           = {
                            val ok = tripDetailViewModel.addActivity(title, description, date, time)
                            if (ok) {
                                title = ""; description = ""; date = null; time = null
                                showSuccess = true
                            }
                        }
                    )
                    ActivityTab.VIEW -> ViewTabContent(
                        trips            = trips,
                        selectedTrip     = selectedTrip,
                        dropdownExpanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        onTripSelected   = { selectedTrip = it; dropdownExpanded = false },
                        detailState      = detailState
                    )
                }
            }
        }
    }
}

// ── Tab Row ────────────────────────────────────────────────────────────────

@Composable
private fun ActTabRow(selectedTab: ActivityTab, onTabSelected: (ActivityTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(NavyLight, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ActivityTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (isSelected) TurquoisePrimary else NavyLight, RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(tab.emoji, fontSize = 14.sp)
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NavyDeep else GrayMid
                    )
                }
            }
        }
    }
}

// ── Tab: Añadir ────────────────────────────────────────────────────────────

@Composable
private fun AddTabContent(
    trips: List<Trip>,
    selectedTrip: Trip?,
    dropdownExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTripSelected: (Trip) -> Unit,
    title: String, onTitleChange: (String) -> Unit,
    description: String, onDescChange: (String) -> Unit,
    date: LocalDate?, time: LocalTime?,
    valErrors: Map<String, String>,
    showSuccess: Boolean,
    onDateClick: () -> Unit, onTimeClick: () -> Unit,
    onSave: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { ActLabel("PASO 1 · SELECCIONA UN VIAJE") }
        item {
            if (trips.isEmpty()) ActEmptyTrips()
            else ActDropdown(trips, selectedTrip, dropdownExpanded, onExpandedChange, onTripSelected)
        }

        item {
            AnimatedVisibility(visible = selectedTrip != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Spacer(Modifier.height(4.dp))
                    ActLabel("PASO 2 · DATOS DE LA ACTIVIDAD")

                    // Rango del viaje
                    selectedTrip?.let { trip ->
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = TurquoisePrimary.copy(alpha = 0.1f))) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("📅", fontSize = 16.sp)
                                Text(
                                    "Rango del viaje: ${trip.startDate.format(actDateFormatter)} – ${trip.endDate.format(actDateFormatter)}",
                                    style = MaterialTheme.typography.bodySmall, color = TurquoisePrimary
                                )
                            }
                        }
                    }

                    ActField("Título *", title, onTitleChange, "Ej: Visita al Templo Senso-ji", valErrors["title"])
                    ActField("Descripción *", description, onDescChange, "Describe la actividad", valErrors["description"], singleLine = false, minLines = 3)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActDateField(Modifier.weight(1f), "Fecha *", date, valErrors["date"], onDateClick)
                        TimePickerField(Modifier.weight(1f), "Hora *", time, valErrors["time"], onTimeClick)
                    }

                    AnimatedVisibility(visible = showSuccess) {
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f))) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("✅", fontSize = 16.sp)
                                Text("Actividad añadida correctamente", style = MaterialTheme.typography.bodyMedium, color = SuccessGreen)
                            }
                        }
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary, contentColor = NavyDeep)
                    ) {
                        Text("Guardar actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── Tab: Ver actividades ───────────────────────────────────────────────────

@Composable
private fun ViewTabContent(
    trips: List<Trip>,
    selectedTrip: Trip?,
    dropdownExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTripSelected: (Trip) -> Unit,
    detailState: TripDetailUiState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { ActLabel("SELECCIONA UN VIAJE") }
        item {
            if (trips.isEmpty()) ActEmptyTrips()
            else ActDropdown(trips, selectedTrip, dropdownExpanded, onExpandedChange, onTripSelected)
        }

        if (selectedTrip != null) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    ActLabel("ACTIVIDADES DEL VIAJE")
                    // Contador de actividades cuando el estado es Success
                    val count = (detailState as? TripDetailUiState.Success)?.activities?.size ?: 0
                    if (count > 0) Text("$count actividades", style = MaterialTheme.typography.labelSmall, color = GrayMid)
                }
            }

            when (detailState) {
                is TripDetailUiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TurquoisePrimary)
                    }
                }

                is TripDetailUiState.Success -> {
                    val activities = detailState.activities  // ya vienen ordenadas del ViewModel
                    if (activities.isEmpty()) {
                        item {
                            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NavyLight)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("🗓️", fontSize = 24.sp)
                                    Text("Este viaje aún no tiene actividades. ¡Añade la primera!", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
                                }
                            }
                        }
                    } else {
                        items(activities, key = { it.id }) { activity ->
                            ActActivityCard(activity)
                        }
                    }
                }

                is TripDetailUiState.Error -> item {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f))) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("⚠️", fontSize = 20.sp)
                            Text(detailState.message, style = MaterialTheme.typography.bodyMedium, color = ErrorRed)
                        }
                    }
                }

                else -> { /* Loading inicial — no mostrar nada */ }
            }
        }
    }
}

// ── Tarjeta de actividad ───────────────────────────────────────────────────

@Composable
private fun ActActivityCard(activity: Activity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
            // Badge fecha + hora
            Box(
                modifier = Modifier.background(TurquoisePrimary.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activity.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TurquoisePrimary
                    )
                    Text(
                        text = activity.date.format(DateTimeFormatter.ofPattern("MMM", java.util.Locale("es"))).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TurquoisePrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = activity.time.format(actTimeFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = GrayMid
                    )
                }
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(activity.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = White)
                if (activity.description.isNotBlank()) {
                    Text(activity.description, style = MaterialTheme.typography.bodySmall, color = GrayMid, maxLines = 3)
                }
            }
        }
    }
}

// ── Componentes UI reutilizables ───────────────────────────────────────────

@Composable
private fun ActLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = TurquoisePrimary, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun ActEmptyTrips() {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NavyLight)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("🗺️", fontSize = 24.sp)
            Text("No tienes viajes. Crea uno primero desde Inicio.", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        }
    }
}

@Composable
private fun ActField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String, error: String? = null,
    singleLine: Boolean = true, minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = if (error != null) ErrorRed else GrayMid, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = GrayDark) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine, minLines = minLines, isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White, unfocusedTextColor = White,
                focusedBorderColor = TurquoisePrimary, unfocusedBorderColor = GrayDark,
                errorBorderColor = ErrorRed, focusedContainerColor = NavyLight,
                unfocusedContainerColor = NavyLight, errorContainerColor = NavyLight,
                cursorColor = TurquoisePrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        if (error != null) Text(error, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
    }
}

@Composable
private fun ActDateField(modifier: Modifier, label: String, date: LocalDate?, error: String?, onClick: () -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = if (error != null) ErrorRed else GrayMid, fontWeight = FontWeight.Medium)
        Box(modifier = Modifier.fillMaxWidth().background(NavyLight, RoundedCornerShape(12.dp)).clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(date?.format(actDateFormatter) ?: "dd/MM/yyyy", color = if (date != null) White else GrayDark, style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.Default.DateRange, null, tint = if (error != null) ErrorRed else TurquoisePrimary, modifier = Modifier.size(20.dp))
            }
        }
        if (error != null) Text(error, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActDropdown(
    trips: List<Trip>, selectedTrip: Trip?,
    expanded: Boolean, onExpandedChange: (Boolean) -> Unit,
    onTripSelected: (Trip) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = selectedTrip?.let { "${it.emoji} ${it.title}" } ?: "Selecciona un viaje...",
            onValueChange = {}, readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = TurquoisePrimary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White, unfocusedTextColor = if (selectedTrip != null) White else GrayDark,
                focusedBorderColor = TurquoisePrimary, unfocusedBorderColor = GrayDark,
                focusedContainerColor = NavyLight, unfocusedContainerColor = NavyLight, cursorColor = TurquoisePrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }, modifier = Modifier.background(NavyLight)) {
            trips.forEach { trip ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(trip.emoji, fontSize = 20.sp)
                            Column {
                                Text(trip.title, style = MaterialTheme.typography.bodyMedium, color = White, fontWeight = FontWeight.Medium)
                                Text(
                                    "${trip.startDate.format(actDateFormatter)} – ${trip.endDate.format(actDateFormatter)}",
                                    style = MaterialTheme.typography.bodySmall, color = GrayMid
                                )
                            }
                        }
                    },
                    onClick = { onTripSelected(trip) },
                    modifier = Modifier.background(if (selectedTrip?.id == trip.id) TurquoisePrimary.copy(alpha = 0.1f) else NavyLight)
                )
            }
        }
    }
}

// TimePickerField — público para uso desde otras pantallas si se necesita
@Composable
fun TimePickerField(modifier: Modifier = Modifier, label: String, time: LocalTime?, error: String? = null, onClick: () -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = if (error != null) ErrorRed else GrayMid, fontWeight = FontWeight.Medium)
        Box(modifier = Modifier.fillMaxWidth().background(NavyLight, RoundedCornerShape(12.dp)).clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(time?.format(actTimeFormatter) ?: "HH:mm", color = if (time != null) White else GrayDark, style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.Default.DateRange, "Seleccionar hora", tint = if (error != null) ErrorRed else TurquoisePrimary, modifier = Modifier.size(20.dp))
            }
        }
        if (error != null) Text(error, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun AddActivityScreenPreview() {
    IridaTheme {
        AddActivityScreen()
    }
}