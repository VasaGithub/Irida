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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.travelplanner.irida.ui.viewmodels.TripListUiState
import com.travelplanner.irida.ui.viewmodels.TripListViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val ADD_ACT_TAG = "ActivitiesScreen"
private val actDateFormatter  = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val actTimeFormatter  = DateTimeFormatter.ofPattern("HH:mm")

// ── Modos de la pantalla ───────────────────────────────────────────────────
private enum class ScreenMode {
    LIST, FORM, DETAIL
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

    var currentMode      by remember { mutableStateOf(ScreenMode.LIST) }
    var selectedTrip     by remember { mutableStateOf<Trip?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Estados para la gestión del CRUD y Vistas
    var activityToDelete by remember { mutableStateOf<Activity?>(null) }
    var activeActivity   by remember { mutableStateOf<Activity?>(null) } // Usado para Editar o Ver detalle

    // Campos del formulario
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date        by remember { mutableStateOf<LocalDate?>(null) }
    var time        by remember { mutableStateOf<LocalTime?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    val trips = (tripsState as? TripListUiState.Success)?.trips ?: emptyList()

    // Cargar viaje al seleccionarlo
    LaunchedEffect(selectedTrip) {
        selectedTrip?.let {
            Log.d(ADD_ACT_TAG, "loadTrip: ${it.id}")
            tripDetailViewModel.loadTrip(it.id)
            currentMode = ScreenMode.LIST // Asegurar que volvemos a la lista si cambiamos de viaje
        }
    }

    // Preparar el formulario al entrar en modo FORM
    LaunchedEffect(currentMode, activeActivity) {
        if (currentMode == ScreenMode.FORM) {
            tripDetailViewModel.clearValidationErrors()
            if (activeActivity != null) {
                // Modo Edición
                title = activeActivity!!.title
                description = activeActivity!!.description
                date = activeActivity!!.date
                time = activeActivity!!.time
            } else {
                // Modo Crear Nuevo
                title = ""
                description = ""
                date = null
                time = null
            }
        }
    }

    // Autodestrucción del mensaje verde a los 3 segundos
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            kotlinx.coroutines.delay(3000)
            showSuccess = false
        }
    }

    // DatePicker restringido al rango del viaje
    val datePicker = selectedTrip?.let { trip ->
        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = LocalDate.of(year, month + 1, day)
            },
            trip.startDate.year, trip.startDate.monthValue - 1, trip.startDate.dayOfMonth
        ).apply {
            datePicker.minDate = trip.startDate.atStartOfDay(java.time.ZoneOffset.UTC.normalized() as java.time.ZoneId).toInstant().toEpochMilli()
            datePicker.maxDate = trip.endDate.atStartOfDay(java.time.ZoneOffset.UTC.normalized() as java.time.ZoneId).toInstant().toEpochMilli()
        }
    }

    val timePicker = TimePickerDialog(context, { _, h, m -> time = LocalTime.of(h, m) }, 9, 0, true)

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
            // Header Principal (Siempre visible en la lista)
            AnimatedVisibility(visible = currentMode == ScreenMode.LIST) {
                Column(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 24.dp, bottom = 16.dp)) {
                    Text(stringResource(R.string.header_act), style = MaterialTheme.typography.headlineMedium, color = White, fontWeight = FontWeight.ExtraBold)
                    Text(stringResource(R.string.header_info), style = MaterialTheme.typography.bodyMedium, color = GrayMid)
                    Spacer(Modifier.height(16.dp))

                    // Dropdown debajo del título
                    if (trips.isEmpty()) {
                        ActEmptyTrips()
                    } else {
                        ActLabel(stringResource(R.string.select_trip))
                        Spacer(Modifier.height(8.dp))
                        ActDropdown(trips, selectedTrip, dropdownExpanded, { dropdownExpanded = it }) {
                            selectedTrip = it
                            dropdownExpanded = false
                        }
                    }
                }
            }

            // Contenido dinámico
            AnimatedContent(
                targetState = currentMode,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                },
                label = "screen_mode"
            ) { mode ->
                when (mode) {
                    ScreenMode.LIST -> {
                        // LA LISTA DE ACTIVIDADES
                        if (selectedTrip != null) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                // Mensaje de éxito flotante en la lista
                                if (showSuccess) {
                                    item {
                                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f))) {
                                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Text("✅", fontSize = 16.sp)
                                                Text(stringResource(R.string.act_upp_good), style = MaterialTheme.typography.bodyMedium, color = SuccessGreen)
                                            }
                                        }
                                    }
                                }

                                // Botón Añadir Actividad
                                item {
                                    Button(
                                        onClick = {
                                            activeActivity = null
                                            currentMode = ScreenMode.FORM
                                        },
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary, contentColor = NavyDeep)
                                    ) {
                                        Text("➕ Añadir actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Lista de actividades ordenadas
                                when (detailState) {
                                    is TripDetailUiState.Loading -> item {
                                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = TurquoisePrimary)
                                        }
                                    }
                                    is TripDetailUiState.Success -> {
                                        // ORDENAR POR DÍA Y LUEGO POR HORA
                                        val sortedActivities = (detailState as TripDetailUiState.Success).activities
                                            .sortedWith(compareBy({ it.date }, { it.time }))

                                        if (sortedActivities.isEmpty()) {
                                            item {
                                                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NavyLight)) {
                                                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                        Text("🗓️", fontSize = 24.sp)
                                                        Text(stringResource(R.string.trip_act_add), style = MaterialTheme.typography.bodyMedium, color = GrayMid)
                                                    }
                                                }
                                            }
                                        } else {
                                            items(sortedActivities) { activity ->
                                                ActActivityCardEditable(
                                                    activity = activity,
                                                    onCardClick = {
                                                        activeActivity = activity
                                                        currentMode = ScreenMode.DETAIL
                                                    },
                                                    onEdit = {
                                                        activeActivity = activity
                                                        currentMode = ScreenMode.FORM
                                                    },
                                                    onDelete = { activityToDelete = activity }
                                                )
                                            }
                                        }
                                    }
                                    is TripDetailUiState.Error -> item {
                                        Text((detailState as TripDetailUiState.Error).message, color = ErrorRed)
                                    }
                                }
                            }
                        }
                    }

                    ScreenMode.FORM -> {
                        // FORMULARIO (Crear o Editar)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                        ) {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { currentMode = ScreenMode.LIST }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = White)
                                    }
                                    Text(
                                        text = if (activeActivity != null) "Editar Actividad" else "Nueva Actividad",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            item {
                                selectedTrip?.let { trip ->
                                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = TurquoisePrimary.copy(alpha = 0.1f))) {
                                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text("📍", fontSize = 16.sp)
                                            Text("Viaje: ${trip.title}", style = MaterialTheme.typography.bodySmall, color = TurquoisePrimary)
                                        }
                                    }
                                }
                            }

                            item {
                                ActField(stringResource(R.string.act_title_ex), title, { title = it }, stringResource(R.string.act_text_ex), valErrors["title"])
                            }
                            item {
                                ActField(stringResource(R.string.act_extr_ex), description, { description = it }, stringResource(R.string.act_desc_ex), valErrors["description"], singleLine = false, minLines = 3)
                            }

                            item {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ActDateField(Modifier.weight(1f), stringResource(R.string.act_date_label), date, valErrors["date"]) { datePicker?.show() }
                                    TimePickerField(Modifier.weight(1f), stringResource(R.string.act_time_label), time, valErrors["time"]) { timePicker.show() }
                                }
                            }

                            item {
                                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedButton(
                                        onClick = { currentMode = ScreenMode.LIST },
                                        modifier = Modifier.weight(1f).height(52.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GrayMid)
                                    ) {
                                        Text("Cancelar")
                                    }
                                    Button(
                                        onClick = {
                                            val ok = if (activeActivity != null) {
                                                tripDetailViewModel.updateActivity(activeActivity!!.id, title, description, date, time)
                                            } else {
                                                tripDetailViewModel.addActivity(title, description, date, time)
                                            }
                                            if (ok) {
                                                showSuccess = true
                                                currentMode = ScreenMode.LIST
                                            }
                                        },
                                        modifier = Modifier.weight(1.5f).height(52.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary, contentColor = NavyDeep)
                                    ) {
                                        Text(if (activeActivity != null) "Actualizar" else "Guardar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    ScreenMode.DETAIL -> {
                        // VISTA DE DETALLE (Solo lectura)
                        activeActivity?.let { activity ->
                            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { currentMode = ScreenMode.LIST }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = White)
                                    }
                                    Text("Detalles", style = MaterialTheme.typography.titleLarge, color = White, fontWeight = FontWeight.Bold)
                                }

                                Spacer(Modifier.height(24.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = NavyLight)
                                ) {
                                    Column(Modifier.padding(24.dp)) {
                                        Text(activity.title, style = MaterialTheme.typography.headlineMedium, color = TurquoisePrimary, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(16.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("📅", fontSize = 20.sp)
                                            Text("${activity.date.format(actDateFormatter)} a las ${activity.time.format(actTimeFormatter)}", style = MaterialTheme.typography.bodyLarge, color = White)
                                        }

                                        if (activity.description.isNotBlank()) {
                                            Spacer(Modifier.height(24.dp))
                                            Text("📝 Notas", style = MaterialTheme.typography.labelMedium, color = GrayMid)
                                            Spacer(Modifier.height(4.dp))
                                            Text(activity.description, style = MaterialTheme.typography.bodyMedium, color = White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DIÁLOGO DE CONFIRMACIÓN DE BORRADO ---
        if (activityToDelete != null) {
            AlertDialog(
                onDismissRequest = { activityToDelete = null },
                containerColor = NavyLight,
                title = { Text("Eliminar actividad", color = White, fontWeight = FontWeight.Bold) },
                text = { Text("¿Estás seguro de que quieres eliminar '${activityToDelete?.title}'? Esta acción no se puede deshacer.", color = GrayMid) },
                confirmButton = {
                    TextButton(onClick = {
                        activityToDelete?.let { tripDetailViewModel.deleteActivity(it.id) }
                        activityToDelete = null
                    }) {
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
    }
}

// ── Tarjeta de actividad con Acciones (Clickable) ──────────────────────────

@Composable
private fun ActActivityCardEditable(
    activity: Activity,
    onCardClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() } // CLIC EN LA TARJETA PARA VER DETALLE
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Badge fecha + hora
            Box(
                modifier = Modifier
                    .background(TurquoisePrimary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
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
                        text = activity.date.format(DateTimeFormatter.ofPattern("MMM", java.util.Locale(stringResource(R.string.lenguaje_act)))).uppercase(),
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

            // Textos y Botones
            Column(Modifier.weight(1f)) {
                Text(activity.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = White)
                if (activity.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(activity.description, style = MaterialTheme.typography.bodySmall, color = GrayMid, maxLines = 2)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción (Evitamos que el clic en el botón active el de la tarjeta entera)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TurquoisePrimary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ErrorRed, modifier = Modifier.size(20.dp))
                    }
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
            Text(stringResource(R.string.trip_create), style = MaterialTheme.typography.bodyMedium, color = GrayMid)
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(NavyLight, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)) {
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
            value = selectedTrip?.let { "${it.emoji} ${it.title}" } ?: stringResource(R.string.select_trip_2),
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
                                Text("${trip.startDate.format(actDateFormatter)} – ${trip.endDate.format(actDateFormatter)}", style = MaterialTheme.typography.bodySmall, color = GrayMid)
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

@Composable
fun TimePickerField(modifier: Modifier = Modifier, label: String, time: LocalTime?, error: String? = null, onClick: () -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = if (error != null) ErrorRed else GrayMid, fontWeight = FontWeight.Medium)
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(NavyLight, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(time?.format(actTimeFormatter) ?: "HH:mm", color = if (time != null) White else GrayDark, style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.Default.DateRange, stringResource(R.string.select_time), tint = if (error != null) ErrorRed else TurquoisePrimary, modifier = Modifier.size(20.dp))
            }
        }
        if (error != null) Text(error, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
    }
}

@Preview(showBackground = true)
@Composable
fun AddActivityScreenPreview() {
    IridaTheme {
        AddActivityScreen()
    }
}