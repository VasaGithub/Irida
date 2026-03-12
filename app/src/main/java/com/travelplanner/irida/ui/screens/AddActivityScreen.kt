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
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.ui.theme.*
import com.travelplanner.irida.ui.viewmodels.TripDetailViewModel
import com.travelplanner.irida.ui.viewmodels.TripDetailUiState
import com.travelplanner.irida.ui.viewmodels.TripListUiState
import com.travelplanner.irida.ui.viewmodels.TripListViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val ADD_ACT_TAG = "AddActivityScreen"
private val actDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val actTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

// ── Tabs ───────────────────────────────────────────────────────────────────
private enum class ActivityTab(val label: String, val emoji: String) {
    ADD("Añadir", "➕"),
    VIEW("Ver actividades", "📋")
}

@Composable
fun AddActivityScreen(
    onNavigate: (String) -> Unit = {},
    tripListViewModel: TripListViewModel = viewModel(),
    tripDetailViewModel: TripDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val tripsState by tripListViewModel.uiState.collectAsState()
    val validationErrors by tripDetailViewModel.validationErrors.collectAsState()
    val tripDetailState by tripDetailViewModel.uiState.collectAsState()

    // Tab activa
    var selectedTab by remember { mutableStateOf(ActivityTab.ADD) }

    // Estado del selector de viaje (compartido entre tabs)
    var selectedTrip by remember { mutableStateOf<Trip?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Campos del formulario (tab Añadir)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf<LocalTime?>(null) }

    // Feedback de éxito
    var showSuccess by remember { mutableStateOf(false) }

    // Lista de viajes disponibles
    val trips = (tripsState as? TripListUiState.Success)?.trips ?: emptyList()

    // Limpiar errores y formulario al cambiar de viaje
    LaunchedEffect(selectedTrip) {
        tripDetailViewModel.clearValidationErrors()
        title = ""
        description = ""
        date = null
        time = null
        showSuccess = false
        selectedTrip?.let {
            Log.d(ADD_ACT_TAG, "Viaje seleccionado: ${it.id} - ${it.title}")
            tripDetailViewModel.loadTrip(it.id)
        }
    }

    // DatePickerDialog
    val datePicker = selectedTrip?.let { trip ->
        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = LocalDate.of(year, month + 1, day)
                Log.d(ADD_ACT_TAG, "Fecha seleccionada: $date")
            },
            trip.startDate.year,
            trip.startDate.monthValue - 1,
            trip.startDate.dayOfMonth
        ).apply {
            datePicker.minDate = trip.startDate
                .atStartOfDay()
                .toInstant(java.time.ZoneOffset.UTC)
                .toEpochMilli()
            datePicker.maxDate = trip.endDate
                .atStartOfDay()
                .toInstant(java.time.ZoneOffset.UTC)
                .toEpochMilli()
        }
    }

    // TimePickerDialog
    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            time = LocalTime.of(hour, minute)
            Log.d(ADD_ACT_TAG, "Hora seleccionada: $time")
        },
        9, 0, true
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues)
        ) {
            // ── Header ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Actividades",
                    style = MaterialTheme.typography.headlineMedium,
                    color = White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Gestiona las actividades de tus viajes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
            }

            // ── Tab selector ───────────────────────────────────────────────
            ActivityTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Contenido según tab activa ─────────────────────────────────
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    ActivityTab.ADD -> AddActivityTabContent(
                        trips = trips,
                        selectedTrip = selectedTrip,
                        dropdownExpanded = dropdownExpanded,
                        onDropdownExpandedChange = { dropdownExpanded = it },
                        onTripSelected = { selectedTrip = it },
                        title = title,
                        onTitleChange = { title = it },
                        description = description,
                        onDescriptionChange = { description = it },
                        date = date,
                        time = time,
                        validationErrors = validationErrors,
                        showSuccess = showSuccess,
                        onDatePickerClick = { datePicker?.show() },
                        onTimePickerClick = { timePicker.show() },
                        onSave = {
                            val success = tripDetailViewModel.addActivity(
                                title = title,
                                description = description,
                                date = date,
                                time = time
                            )
                            if (success) {
                                title = ""; description = ""; date = null; time = null
                                showSuccess = true
                            }
                        }
                    )

                    ActivityTab.VIEW -> ViewActivitiesTabContent(
                        trips = trips,
                        selectedTrip = selectedTrip,
                        dropdownExpanded = dropdownExpanded,
                        onDropdownExpandedChange = { dropdownExpanded = it },
                        onTripSelected = { selectedTrip = it },
                        tripDetailState = tripDetailState
                    )
                }
            }
        }
    }
}

// ── Tab Row ────────────────────────────────────────────────────────────────

@Composable
private fun ActivityTabRow(
    selectedTab: ActivityTab,
    onTabSelected: (ActivityTab) -> Unit
) {
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
                    .background(
                        color = if (isSelected) TurquoisePrimary else NavyLight,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = tab.emoji, fontSize = 14.sp)
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

// ── Tab: Añadir actividad ──────────────────────────────────────────────────

@Composable
private fun AddActivityTabContent(
    trips: List<Trip>,
    selectedTrip: Trip?,
    dropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    onTripSelected: (Trip) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    date: LocalDate?,
    time: LocalTime?,
    validationErrors: Map<String, String>,
    showSuccess: Boolean,
    onDatePickerClick: () -> Unit,
    onTimePickerClick: () -> Unit,
    onSave: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Paso 1: Selector de viaje
        item { FormSectionLabel(text = "PASO 1 · SELECCIONA UN VIAJE") }

        item {
            if (trips.isEmpty()) {
                EmptyTripsCard()
            } else {
                TripSelectorDropdown(
                    trips = trips,
                    selectedTrip = selectedTrip,
                    expanded = dropdownExpanded,
                    onExpandedChange = onDropdownExpandedChange,
                    onTripSelected = onTripSelected
                )
            }
        }

        // Paso 2: Formulario (visible solo si hay viaje seleccionado)
        item {
            AnimatedVisibility(
                visible = selectedTrip != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    FormSectionLabel(text = "PASO 2 · DATOS DE LA ACTIVIDAD")

                    // Rango de fechas del viaje
                    selectedTrip?.let { trip ->
                        TripDateRangeCard(trip = trip)
                    }

                    // Título
                    TripFormField(
                        label = "Título *",
                        value = title,
                        onValueChange = onTitleChange,
                        placeholder = "Ej: Visita al Templo Senso-ji",
                        error = validationErrors["title"]
                    )

                    // Descripción
                    TripFormField(
                        label = "Descripción *",
                        value = description,
                        onValueChange = onDescriptionChange,
                        placeholder = "Describe la actividad",
                        error = validationErrors["description"],
                        singleLine = false,
                        minLines = 3
                    )

                    // Fecha y Hora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DatePickerField(
                            modifier = Modifier.weight(1f),
                            label = "Fecha *",
                            date = date,
                            error = validationErrors["date"],
                            onClick = onDatePickerClick
                        )
                        TimePickerField(
                            modifier = Modifier.weight(1f),
                            label = "Hora *",
                            time = time,
                            error = validationErrors["time"],
                            onClick = onTimePickerClick
                        )
                    }

                    // Mensaje de éxito
                    AnimatedVisibility(visible = showSuccess) {
                        SuccessBanner(text = "Actividad añadida correctamente")
                    }

                    // Botón guardar
                    Button(
                        onClick = onSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TurquoisePrimary,
                            contentColor = NavyDeep
                        )
                    ) {
                        Text(
                            text = "Guardar actividad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ── Tab: Ver actividades ───────────────────────────────────────────────────

@Composable
private fun ViewActivitiesTabContent(
    trips: List<Trip>,
    selectedTrip: Trip?,
    dropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    onTripSelected: (Trip) -> Unit,
    tripDetailState: TripDetailUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Selector de viaje
        item { FormSectionLabel(text = "SELECCIONA UN VIAJE") }

        item {
            if (trips.isEmpty()) {
                EmptyTripsCard()
            } else {
                TripSelectorDropdown(
                    trips = trips,
                    selectedTrip = selectedTrip,
                    expanded = dropdownExpanded,
                    onExpandedChange = onDropdownExpandedChange,
                    onTripSelected = onTripSelected
                )
            }
        }

        // Lista de actividades
        if (selectedTrip != null) {
            item { FormSectionLabel(text = "ACTIVIDADES DEL VIAJE") }

            when (tripDetailState) {
                is TripDetailUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TurquoisePrimary)
                        }
                    }
                }

                is TripDetailUiState.Success -> {
                    val activities = tripDetailState.trip.activities
                    if (activities.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = NavyLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text("🗓️", fontSize = 24.sp)
                                    Text(
                                        text = "Este viaje aún no tiene actividades. ¡Añade la primera!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GrayMid
                                    )
                                }
                            }
                        }
                    } else {
                        items(activities.sortedWith(compareBy({ it.date }, { it.time }))) { activity ->
                            ActivityListCard(activity = activity)
                        }
                    }
                }

                is TripDetailUiState.Error -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("⚠️", fontSize = 20.sp)
                                Text(
                                    text = tripDetailState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ErrorRed
                                )
                            }
                        }
                    }
                }

                else -> { /* idle, no mostrar nada */ }
            }
        }
    }
}

// ── Tarjeta de actividad ───────────────────────────────────────────────────

@Composable
private fun ActivityListCard(activity: com.travelplanner.irida.domain.Activity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Columna de fecha/hora
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(52.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            TurquoisePrimary.copy(alpha = 0.15f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = activity.date?.dayOfMonth?.toString() ?: "--",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TurquoisePrimary
                        )
                        Text(
                            text = activity.date?.format(
                                DateTimeFormatter.ofPattern("MMM").withLocale(java.util.Locale("es"))
                            )?.uppercase() ?: "--",
                            style = MaterialTheme.typography.labelSmall,
                            color = TurquoisePrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activity.time?.format(actTimeFormatter) ?: "--:--",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayMid
                )
            }

            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                if (activity.description.isNotBlank()) {
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayMid,
                        maxLines = 3
                    )
                }
            }
        }
    }
}

// ── Componentes auxiliares ─────────────────────────────────────────────────

@Composable
private fun EmptyTripsCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🗺️", fontSize = 24.sp)
            Text(
                text = "No tienes viajes. Crea uno primero desde Inicio.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid
            )
        }
    }
}

@Composable
private fun TripDateRangeCard(trip: Trip) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = TurquoisePrimary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📅", fontSize = 16.sp)
            Text(
                text = "Rango del viaje: ${trip.startDate.format(actDateFormatter)} – ${trip.endDate.format(actDateFormatter)}",
                style = MaterialTheme.typography.bodySmall,
                color = TurquoisePrimary
            )
        }
    }
}

@Composable
private fun SuccessBanner(text: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuccessGreen.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("✅", fontSize = 16.sp)
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = SuccessGreen
            )
        }
    }
}

// ── Componentes existentes ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSelectorDropdown(
    trips: List<Trip>,
    selectedTrip: Trip?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTripSelected: (Trip) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedTrip?.let { "${it.emoji} ${it.title}" } ?: "Selecciona un viaje...",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TurquoisePrimary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = if (selectedTrip != null) White else GrayDark,
                focusedBorderColor = TurquoisePrimary,
                unfocusedBorderColor = GrayDark,
                focusedContainerColor = NavyLight,
                unfocusedContainerColor = NavyLight,
                cursorColor = TurquoisePrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(NavyLight)
        ) {
            trips.forEach { trip ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = trip.emoji, fontSize = 20.sp)
                            Column {
                                Text(
                                    text = trip.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${trip.startDate.format(actDateFormatter)} – ${trip.endDate.format(actDateFormatter)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayMid
                                )
                            }
                        }
                    },
                    onClick = { onTripSelected(trip) },
                    modifier = Modifier.background(
                        if (selectedTrip?.id == trip.id) TurquoisePrimary.copy(alpha = 0.1f)
                        else NavyLight
                    )
                )
            }
        }
    }
}

@Composable
fun TimePickerField(
    modifier: Modifier = Modifier,
    label: String,
    time: LocalTime?,
    error: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (error != null) ErrorRed else GrayMid,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyLight, RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time?.format(actTimeFormatter) ?: "HH:mm",
                    color = if (time != null) White else GrayDark,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Seleccionar hora",
                    tint = if (error != null) ErrorRed else TurquoisePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddActivityScreenPreview() {
    IridaTheme {
        AddActivityScreen()
    }
}