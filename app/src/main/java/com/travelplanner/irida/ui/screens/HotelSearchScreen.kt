package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SingleBed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.travelplanner.irida.domain.Hotel
import com.travelplanner.irida.domain.Room
import com.travelplanner.irida.ui.components.HotelAsyncImage
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GoldAccent
import com.travelplanner.irida.ui.theme.GrayDark
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.NavyMid
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.HotelSearchViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateDisplayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun millisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()

// ── Room type helpers ─────────────────────────────────────────────────────────

private fun roomTypeLabel(type: String): String = when (type.lowercase()) {
    "single"       -> "Individual"
    "double"       -> "Doble"
    "suite"        -> "Suite"
    "twin"         -> "Twin"
    "triple"       -> "Triple"
    "family"       -> "Familiar"
    "deluxe"       -> "Deluxe"
    else           -> type.replaceFirstChar { it.uppercase() }
}

private fun roomTypeColor(type: String) = when (type.lowercase()) {
    "suite", "deluxe" -> GoldAccent
    "double", "twin"  -> TurquoisePrimary
    else              -> GrayMid
}

private fun roomTypeIcon(type: String) = when (type.lowercase()) {
    "single" -> Icons.Filled.SingleBed
    else     -> Icons.Filled.KingBed
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchScreen(
    onNavigateBack: () -> Unit,
    onHotelClick: (Hotel) -> Unit = {},
    viewModel: HotelSearchViewModel = hiltViewModel()
) {
    val uiState    by viewModel.uiState.collectAsState()
    val selectedCity by viewModel.city.collectAsState()
    val startDate  by viewModel.startDate.collectAsState()
    val endDate    by viewModel.endDate.collectAsState()
    val canSearch  by viewModel.canSearch.collectAsState()

    val cities = listOf("Londres", "París", "Barcelona")

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker   by remember { mutableStateOf(false) }

    val startPickerState = rememberDatePickerState()
    val endPickerState   = rememberDatePickerState()

    // ── DatePicker · entrada ──────────────────────────────────────────────
    if (showStartPicker) {
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startPickerState.selectedDateMillis?.let { millis ->
                        viewModel.onStartDateSelected(millisToLocalDate(millis))
                    }
                    showStartPicker = false
                }) { Text("Aceptar", color = TurquoisePrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text("Cancelar", color = GrayMid)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = NavyMid)
        ) {
            DatePicker(
                state = startPickerState,
                title = { Text("Fecha de entrada", color = GrayMid, modifier = Modifier.padding(start = 24.dp, top = 16.dp)) },
                colors = DatePickerDefaults.colors(
                    containerColor = NavyMid,
                    titleContentColor = GrayMid,
                    headlineContentColor = TurquoisePrimary,
                    weekdayContentColor = GrayMid,
                    subheadContentColor = GrayMid,
                    navigationContentColor = White,
                    yearContentColor = White,
                    currentYearContentColor = TurquoisePrimary,
                    selectedYearContentColor = NavyDeep,
                    selectedYearContainerColor = TurquoisePrimary,
                    dayContentColor = White,
                    selectedDayContentColor = NavyDeep,
                    selectedDayContainerColor = TurquoisePrimary,
                    todayContentColor = TurquoisePrimary,
                    todayDateBorderColor = TurquoisePrimary
                )
            )
        }
    }

    // ── DatePicker · salida ───────────────────────────────────────────────
    if (showEndPicker) {
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endPickerState.selectedDateMillis?.let { millis ->
                        viewModel.onEndDateSelected(millisToLocalDate(millis))
                    }
                    showEndPicker = false
                }) { Text("Aceptar", color = TurquoisePrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text("Cancelar", color = GrayMid)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = NavyMid)
        ) {
            DatePicker(
                state = endPickerState,
                title = { Text("Fecha de salida", color = GrayMid, modifier = Modifier.padding(start = 24.dp, top = 16.dp)) },
                colors = DatePickerDefaults.colors(
                    containerColor = NavyMid,
                    titleContentColor = GrayMid,
                    headlineContentColor = TurquoisePrimary,
                    weekdayContentColor = GrayMid,
                    subheadContentColor = GrayMid,
                    navigationContentColor = White,
                    yearContentColor = White,
                    currentYearContentColor = TurquoisePrimary,
                    selectedYearContentColor = NavyDeep,
                    selectedYearContainerColor = TurquoisePrimary,
                    dayContentColor = White,
                    selectedDayContentColor = NavyDeep,
                    selectedDayContainerColor = TurquoisePrimary,
                    todayContentColor = TurquoisePrimary,
                    todayDateBorderColor = TurquoisePrimary
                )
            )
        }
    }

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Buscar hoteles",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Selector de ciudad ────────────────────────────────────────
            item {
                Text(
                    "Ciudad de destino",
                    color = GrayMid,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    cities.forEach { city ->
                        val selected = selectedCity == city
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.onCitySelected(city) },
                            label = { Text(city, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TurquoisePrimary,
                                selectedLabelColor = NavyDeep,
                                containerColor = NavyLight,
                                labelColor = White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = TurquoisePrimary,
                                borderColor = GrayDark
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // ── Fechas ────────────────────────────────────────────────────
            item {
                Text(
                    "Fechas de estancia",
                    color = GrayMid,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fecha de entrada
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = startDate?.format(dateDisplayFormatter) ?: "",
                            onValueChange = {},
                            label = { Text("Entrada") },
                            placeholder = { Text("dd/MM/yyyy", color = GrayMid) },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.DateRange,
                                    contentDescription = "Seleccionar fecha de entrada",
                                    tint = TurquoisePrimary
                                )
                            },
                            enabled = false,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = if (startDate != null) TurquoisePrimary else GrayDark,
                                disabledTextColor = White,
                                disabledLabelColor = GrayMid,
                                disabledContainerColor = NavyLight,
                                disabledTrailingIconColor = TurquoisePrimary,
                                disabledPlaceholderColor = GrayMid
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        // Overlay para capturar el click
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showStartPicker = true }
                        )
                    }

                    // Fecha de salida
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = endDate?.format(dateDisplayFormatter) ?: "",
                            onValueChange = {},
                            label = { Text("Salida") },
                            placeholder = { Text("dd/MM/yyyy", color = GrayMid) },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.DateRange,
                                    contentDescription = "Seleccionar fecha de salida",
                                    tint = TurquoisePrimary
                                )
                            },
                            enabled = false,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = if (endDate != null) TurquoisePrimary else GrayDark,
                                disabledTextColor = White,
                                disabledLabelColor = GrayMid,
                                disabledContainerColor = NavyLight,
                                disabledTrailingIconColor = TurquoisePrimary,
                                disabledPlaceholderColor = GrayMid
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        // Overlay para capturar el click
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showEndPicker = true }
                        )
                    }
                }
            }

            // ── Botón de búsqueda ─────────────────────────────────────────
            item {
                Button(
                    onClick = { viewModel.search() },
                    enabled = canSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TurquoisePrimary,
                        contentColor = NavyDeep,
                        disabledContainerColor = NavyLight,
                        disabledContentColor = GrayMid
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Buscar hoteles",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // ── Resultados ────────────────────────────────────────────────
            when (val state = uiState) {

                is HotelSearchViewModel.UiState.Idle -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏨", fontSize = 52.sp)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Selecciona una ciudad y las fechas\npara encontrar hoteles disponibles",
                                    color = GrayMid,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                is HotelSearchViewModel.UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = TurquoisePrimary) }
                    }
                }

                is HotelSearchViewModel.UiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                state.message,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                is HotelSearchViewModel.UiState.Success -> {
                    if (state.hotels.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("😕", fontSize = 40.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "No hay hoteles disponibles\npara estas fechas",
                                        color = GrayMid,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            HorizontalDivider(color = GrayDark.copy(alpha = 0.5f))
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Hoteles disponibles",
                                    color = White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${state.hotels.size} resultado${if (state.hotels.size == 1) "" else "s"}",
                                    color = TurquoisePrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        items(state.hotels, key = { it.id }) { hotel ->
                            HotelCard(hotel = hotel, onClick = { onHotelClick(hotel) })
                        }
                    }
                }
            }
        }
    }
}

// ── HotelCard ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HotelCard(hotel: Hotel, onClick: () -> Unit = {}) {
    val visibleRooms = hotel.rooms.take(3)
    val extraRooms   = hotel.rooms.size - visibleRooms.size

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {

            // ── Imagen del hotel ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                HotelAsyncImage(
                    url = hotel.imageUrl,
                    contentDescription = hotel.name,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient fade into card background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(NavyLight.copy(alpha = 0f), NavyLight)
                            )
                        )
                )
                // Rating badge overlaid on the image
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    RatingBadge(rating = hotel.rating)
                }
            }

            // ── Cabecera del hotel ────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = hotel.name,
                    color = White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = hotel.address,
                    color = GrayMid,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {

            // ── Lista de habitaciones ─────────────────────────────────────
            if (visibleRooms.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = GrayDark.copy(alpha = 0.4f))
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Habitaciones disponibles",
                        color = GrayMid,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${hotel.rooms.size} tipo${if (hotel.rooms.size == 1) "" else "s"}",
                        color = GrayMid,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    visibleRooms.forEach { room ->
                        RoomRow(room = room)
                    }
                }

                if (extraRooms > 0) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "+$extraRooms habitación${if (extraRooms == 1) "" else "es"} más",
                        color = TurquoisePrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            }
        }
    }
}

// ── RoomRow ───────────────────────────────────────────────────────────────────

@Composable
private fun RoomRow(room: Room) {
    val label     = roomTypeLabel(room.roomType)
    val color     = roomTypeColor(room.roomType)
    val icon      = roomTypeIcon(room.roomType)
    val thumbUrl  = room.images.firstOrNull() ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = NavyDeep.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Thumbnail: imagen real si existe, icono de fallback si no
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                if (thumbUrl.isNotBlank()) {
                    HotelAsyncImage(
                        url = thumbUrl,
                        contentDescription = label,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                color = White,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "%.0f €/noche".format(room.price),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── RatingBadge ───────────────────────────────────────────────────────────────

@Composable
private fun RatingBadge(rating: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = GoldAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Rating",
            tint = GoldAccent,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = "$rating",
            color = GoldAccent,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
