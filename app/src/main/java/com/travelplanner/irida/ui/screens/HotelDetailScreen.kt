package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.SingleBed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.travelplanner.irida.domain.Hotel
import com.travelplanner.irida.ui.components.HotelAsyncImage
import com.travelplanner.irida.domain.Room
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GoldAccent
import com.travelplanner.irida.ui.theme.GrayDark
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.NavyMid
import com.travelplanner.irida.ui.theme.SuccessGreen
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.HotelSearchViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val detailDateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HotelDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: HotelSearchViewModel = hiltViewModel()
) {
    val hotel         by viewModel.selectedHotel.collectAsState()
    val startDate     by viewModel.startDate.collectAsState()
    val endDate       by viewModel.endDate.collectAsState()
    val reserveState  by viewModel.reserveUiState.collectAsState()

    // Room selected for reservation (local UI state)
    var roomToReserve by remember { mutableStateOf<Room?>(null) }

    // Auto-dismiss dialog on success, reset state when dialog closes
    LaunchedEffect(reserveState) {
        if (reserveState is HotelSearchViewModel.ReserveUiState.Success) {
            // Keep dialog open to show success; user dismisses manually
        }
    }

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        hotel?.name ?: "Detalle del hotel",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
        if (hotel == null) {
            Box(
                modifier = Modifier.fillMaxSize().background(NavyDeep),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = TurquoisePrimary) }
            return@Scaffold
        }

        val h = hotel!!
        val galleryImages = buildList {
            if (h.imageUrl.isNotBlank()) add(h.imageUrl)
            h.rooms.forEach { room -> room.images.forEach { img -> if (img.isNotBlank()) add(img) } }
        }.ifEmpty { listOf("") }   // at least one page (placeholder)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // ── Galería de imágenes ───────────────────────────────────────
            item {
                ImageGallery(images = galleryImages)
            }

            // ── Info del hotel ────────────────────────────────────────────
            item {
                HotelInfoCard(hotel = h)
            }

            // ── Fechas de búsqueda ────────────────────────────────────────
            if (startDate != null && endDate != null) {
                item {
                    DatesCard(startDate = startDate!!, endDate = endDate!!)
                }
            }

            // ── Habitaciones ──────────────────────────────────────────────
            item {
                Text(
                    "Habitaciones disponibles",
                    color = White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            itemsIndexed(h.rooms) { _, room ->
                RoomDetailCard(
                    room = room,
                    nights = if (startDate != null && endDate != null)
                        ChronoUnit.DAYS.between(startDate, endDate).toInt()
                    else 1,
                    onReserveClick = { roomToReserve = room }
                )
            }
        }
    }

    // ── Diálogo de reserva ────────────────────────────────────────────────
    if (roomToReserve != null) {
        ReservationDialog(
            hotel       = hotel!!,
            room        = roomToReserve!!,
            startDate   = startDate,
            endDate     = endDate,
            initialEmail = viewModel.currentGuestEmail,
            reserveState = reserveState,
            onConfirm   = { name, email ->
                viewModel.reserve(roomToReserve!!.id, name, email)
            },
            onDismiss   = {
                roomToReserve = null
                viewModel.resetReserveState()
            }
        )
    }
}

// ── ImageGallery ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGallery(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            HotelAsyncImage(
                url = images[page],
                contentDescription = "Imagen del hotel",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NavyDeep.copy(alpha = 0f), NavyDeep)
                    )
                )
        )

        // Page indicators
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .background(
                                color = if (pagerState.currentPage == index) TurquoisePrimary
                                        else White.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

// ── HotelInfoCard ─────────────────────────────────────────────────────────────

@Composable
private fun HotelInfoCard(hotel: Hotel) {
    Card(
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hotel.name,
                    color = White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(12.dp))
                // Rating badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${hotel.rating}/5",
                        color = GoldAccent,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = hotel.address,
                color = GrayMid,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ── DatesCard ─────────────────────────────────────────────────────────────────

@Composable
private fun DatesCard(startDate: LocalDate, endDate: LocalDate) {
    val nights = ChronoUnit.DAYS.between(startDate, endDate).toInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(NavyLight, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Entrada", color = GrayMid, style = MaterialTheme.typography.labelSmall)
            Text(startDate.format(detailDateFmt), color = White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$nights noche${if (nights == 1) "" else "s"}",
                color = TurquoisePrimary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Salida", color = GrayMid, style = MaterialTheme.typography.labelSmall)
            Text(endDate.format(detailDateFmt), color = White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── RoomDetailCard ────────────────────────────────────────────────────────────

@Composable
private fun RoomDetailCard(
    room: Room,
    nights: Int,
    onReserveClick: () -> Unit
) {
    val label     = roomTypeLabel(room.roomType)
    val color     = roomTypeColor(room.roomType)
    val icon      = roomTypeIcon(room.roomType)
    val total     = room.price * nights
    val roomImage = room.images.firstOrNull() ?: ""

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Column {
            // Imagen de la habitación
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                HotelAsyncImage(url = roomImage, contentDescription = "Imagen de la habitación", modifier = Modifier.fillMaxSize())

                // Tipo badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(color = NavyDeep.copy(alpha = 0.75f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Info y botón
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            label,
                            color = White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "%.0f €/noche".format(room.price),
                            color = GrayMid,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Total",
                            color = GrayMid,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            "%.0f €".format(total),
                            color = color,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (nights > 1) {
                            Text(
                                "$nights noches",
                                color = GrayMid,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = GrayDark.copy(alpha = 0.4f))
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onReserveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TurquoisePrimary,
                        contentColor = NavyDeep
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reservar", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

// ── ReservationDialog ─────────────────────────────────────────────────────────

@Composable
private fun ReservationDialog(
    hotel: Hotel,
    room: Room,
    startDate: LocalDate?,
    endDate: LocalDate?,
    initialEmail: String,
    reserveState: HotelSearchViewModel.ReserveUiState,
    onConfirm: (guestName: String, guestEmail: String) -> Unit,
    onDismiss: () -> Unit
) {
    val label   = roomTypeLabel(room.roomType)
    val nights  = if (startDate != null && endDate != null)
        ChronoUnit.DAYS.between(startDate, endDate).toInt() else 1
    val total   = room.price * nights

    var guestName  by remember { mutableStateOf("") }
    var guestEmail by remember { mutableStateOf(initialEmail) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = TurquoisePrimary,
        unfocusedBorderColor = GrayDark,
        focusedTextColor     = White,
        unfocusedTextColor   = White,
        cursorColor          = TurquoisePrimary,
        focusedContainerColor   = NavyDeep,
        unfocusedContainerColor = NavyDeep,
        focusedLabelColor    = TurquoisePrimary,
        unfocusedLabelColor  = GrayMid
    )
    val fieldShape = RoundedCornerShape(10.dp)

    AlertDialog(
        onDismissRequest = { if (reserveState !is HotelSearchViewModel.ReserveUiState.Loading) onDismiss() },
        containerColor = NavyLight,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Confirmar reserva",
                color = White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                when (reserveState) {

                    // ── Formulario ────────────────────────────────────────
                    is HotelSearchViewModel.ReserveUiState.Idle,
                    is HotelSearchViewModel.ReserveUiState.Error -> {

                        // Resumen hotel + habitación
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyDeep, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SummaryRow("Hotel",        hotel.name)
                            SummaryRow("Habitación",   label)
                            if (startDate != null) SummaryRow("Entrada", startDate.format(detailDateFmt))
                            if (endDate   != null) SummaryRow("Salida",  endDate.format(detailDateFmt))
                            SummaryRow("Noches",       "$nights")
                            HorizontalDivider(color = GrayDark.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total", color = White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("%.0f €".format(total), color = TurquoisePrimary, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
                            }
                        }

                        // Campos del huésped
                        OutlinedTextField(
                            value = guestName,
                            onValueChange = { guestName = it },
                            label = { Text("Nombre del huésped") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = fieldColors,
                            shape = fieldShape
                        )
                        OutlinedTextField(
                            value = guestEmail,
                            onValueChange = { guestEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = fieldColors,
                            shape = fieldShape
                        )

                        if (reserveState is HotelSearchViewModel.ReserveUiState.Error) {
                            Text(
                                reserveState.message,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // ── Cargando ──────────────────────────────────────────
                    is HotelSearchViewModel.ReserveUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                CircularProgressIndicator(color = TurquoisePrimary)
                                Text("Procesando reserva…", color = GrayMid, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // ── Éxito ─────────────────────────────────────────────
                    is HotelSearchViewModel.ReserveUiState.Success -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                "¡Reserva confirmada!",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Reserva nº ${reserveState.reservation.id}",
                                color = GrayMid,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "${hotel.name} · $label",
                                color = White,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            when (reserveState) {
                is HotelSearchViewModel.ReserveUiState.Success -> {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Cerrar", color = NavyDeep, fontWeight = FontWeight.Bold) }
                }
                is HotelSearchViewModel.ReserveUiState.Loading -> { /* no button */ }
                else -> {
                    Button(
                        onClick = { onConfirm(guestName.trim(), guestEmail.trim()) },
                        enabled = guestName.isNotBlank() && guestEmail.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TurquoisePrimary,
                            contentColor = NavyDeep,
                            disabledContainerColor = GrayDark,
                            disabledContentColor = GrayMid
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Confirmar", fontWeight = FontWeight.Bold) }
                }
            }
        },
        dismissButton = {
            if (reserveState !is HotelSearchViewModel.ReserveUiState.Loading &&
                reserveState !is HotelSearchViewModel.ReserveUiState.Success) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = GrayMid)
                }
            }
        }
    )
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = GrayMid, style = MaterialTheme.typography.bodySmall)
        Text(value, color = White, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ── Helpers (shared with HotelSearchScreen) ───────────────────────────────────

private fun roomTypeLabel(type: String): String = when (type.lowercase()) {
    "single"  -> "Individual"
    "double"  -> "Doble"
    "suite"   -> "Suite"
    "twin"    -> "Twin"
    "triple"  -> "Triple"
    "family"  -> "Familiar"
    "deluxe"  -> "Deluxe"
    else      -> type.replaceFirstChar { it.uppercase() }
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
