package com.travelplanner.irida.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.travelplanner.irida.R
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripImage
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GrayDark
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.IridaTheme
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.NavyMid
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.TripDetailUiState
import com.travelplanner.irida.ui.viewmodels.TripDetailViewModel
import java.io.File
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onAddActivity: () -> Unit = {},
    onEditActivity: (String) -> Unit = {},
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val images  by viewModel.images.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    var activityToDelete by remember { mutableStateOf<Activity?>(null) }
    var imageToDelete    by remember { mutableStateOf<TripImage?>(null) }

    // PhotoPicker launcher — copies selected URI into filesDir automatically
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.addImageFromUri(context, it) }
    }

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
                Box(modifier = Modifier.fillMaxSize().background(NavyDeep), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TurquoisePrimary)
                }
            }
            is TripDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().background(NavyDeep), contentAlignment = Alignment.Center) {
                    Text(state.message, color = ErrorRed)
                }
            }
            is TripDetailUiState.Success -> {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavyDeep)
                        .padding(paddingValues)
                ) {
                    item { TripDetailHeader(trip = state.trip, onBack = onBack) }
                    item { TripStatsRow(trip = state.trip, activityCount = state.activities.size) }

                    item {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor   = NavyDeep,
                            contentColor     = TurquoisePrimary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color    = TurquoisePrimary
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick  = { selectedTab = index },
                                    text = {
                                        Text(
                                            text       = title,
                                            color      = if (selectedTab == index) TurquoisePrimary else GrayMid,
                                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }
                    }

                    when (selectedTab) {
                        // ── Tab 0: Itinerary ───────────────────────────────
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
                                                    .withLocale(java.util.Locale.getDefault())
                                            )
                                        )
                                    }
                                    items(activitiesOfDay) { activity ->
                                        ActivityCard(
                                            activity = activity,
                                            onEdit   = { onEditActivity(activity.id) },
                                            onDelete = { activityToDelete = activity }
                                        )
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick   = onAddActivity,
                                    modifier  = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 24.dp)
                                        .height(52.dp),
                                    shape  = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TurquoisePrimary,
                                        contentColor   = NavyDeep
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Añadir actividad",
                                        style      = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // ── Tab 1: Gallery ─────────────────────────────────
                        1 -> {
                            item {
                                GallerySection(
                                    images        = images,
                                    onAddClick    = {
                                        pickMedia.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    onDeleteImage = { imageToDelete = it }
                                )
                            }
                        }

                        // ── Tab 2: Notes ───────────────────────────────────
                        2 -> item { NotesTabPlaceholder() }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // ── Delete activity dialog ─────────────────────────────────
                if (activityToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { activityToDelete = null },
                        containerColor   = NavyLight,
                        title = { Text("Eliminar actividad", color = White, fontWeight = FontWeight.Bold) },
                        text  = {
                            Text(
                                "¿Estás seguro de que quieres eliminar '${activityToDelete?.title}'? Esta acción no se puede deshacer.",
                                color = GrayMid
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                activityToDelete?.let { viewModel.deleteActivity(it.id) }
                                activityToDelete = null
                            }) { Text("Eliminar", color = ErrorRed, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            TextButton(onClick = { activityToDelete = null }) {
                                Text(stringResource(R.string.btn_cerrar), color = TurquoisePrimary)
                            }
                        }
                    )
                }

                // ── Delete image dialog ────────────────────────────────────
                if (imageToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { imageToDelete = null },
                        containerColor   = NavyLight,
                        title = { Text("Eliminar foto", color = White, fontWeight = FontWeight.Bold) },
                        text  = {
                            Text(
                                "¿Quieres eliminar esta foto del viaje? Esta acción no se puede deshacer.",
                                color = GrayMid
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                imageToDelete?.let { viewModel.deleteImage(it.id) }
                                imageToDelete = null
                            }) { Text("Eliminar", color = ErrorRed, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            TextButton(onClick = { imageToDelete = null }) {
                                Text(stringResource(R.string.btn_cerrar), color = TurquoisePrimary)
                            }
                        }
                    )
                }
            }
        }
    }
}

// ── Gallery tab ───────────────────────────────────────────────────────────────

@Composable
fun GallerySection(
    images: List<TripImage>,
    onAddClick: () -> Unit,
    onDeleteImage: (TripImage) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {

        // Header row: title + add button
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "Fotos del viaje",
                style      = MaterialTheme.typography.titleMedium,
                color      = White,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick    = onAddClick,
                shape      = RoundedCornerShape(12.dp),
                colors     = ButtonDefaults.buttonColors(
                    containerColor = TurquoisePrimary,
                    contentColor   = NavyDeep
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Añadir foto", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (images.isEmpty()) {
            GalleryEmptyState()
        } else {
            // Manual 3-column grid — LazyVerticalGrid inside LazyColumn is not supported,
            // so we chunk the list and render rows manually.
            val rows = images.chunked(3)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                rows.forEach { rowImages ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowImages.forEach { image ->
                            GalleryThumbnail(
                                image         = image,
                                onLongPress   = { onDeleteImage(image) },
                                modifier      = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining columns if the last row has fewer than 3 items
                        repeat(3 - rowImages.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryThumbnail(
    image: TripImage,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(NavyMid)
            .combinedClickable(onLongClick = onLongPress, onClick = {})
    ) {
        val file = remember(image.filePath) { File(image.filePath) }
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(file)
                .crossfade(200)
                .build(),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize(),
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color       = TurquoisePrimary,
                        strokeWidth = 2.dp,
                        modifier    = Modifier.size(20.dp)
                    )
                }
            },
            error = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Default.Image,
                        contentDescription = null,
                        tint               = TurquoisePrimary.copy(alpha = 0.4f),
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }
        )

        // Long-press hint: delete icon badge in top-right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                .clickable { onLongPress() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.Delete,
                contentDescription = "Eliminar foto",
                tint               = White,
                modifier           = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun GalleryEmptyState() {
    Box(
        modifier         = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🖼️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text  = "Aún no hay fotos para este viaje",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "Pulsa \"Añadir foto\" para importar imágenes de tu galería",
                style = MaterialTheme.typography.bodySmall,
                color = GrayDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Trip header / stats ───────────────────────────────────────────────────────

@Composable
fun TripDetailHeader(trip: Trip, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 220.dp)
            .background(Brush.verticalGradient(colors = listOf(Color(0xFF1A3A5C), NavyDeep)))
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_volver), tint = White)
        }
        Column(
            modifier              = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 20.dp),
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Text(text = trip.emoji, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = trip.title,
                style      = MaterialTheme.typography.headlineMedium,
                color      = White,
                fontWeight = FontWeight.ExtraBold,
                textAlign  = TextAlign.Center
            )
            if (trip.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text      = trip.description,
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines  = 3,
                    overflow  = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text  = stringResource(
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
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
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
        text      = day.uppercase(),
        style     = MaterialTheme.typography.labelSmall,
        color     = TurquoisePrimary,
        letterSpacing = 2.sp,
        modifier  = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun ActivityCard(activity: Activity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
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
            shape    = RoundedCornerShape(16.dp),
            colors   = CardDefaults.cardColors(containerColor = NavyLight)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = activity.title, style = MaterialTheme.typography.titleMedium, color = White, fontWeight = FontWeight.SemiBold)
                    if (activity.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = activity.description, style = MaterialTheme.typography.bodyMedium, color = GrayMid)
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TurquoisePrimary)
                }
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
    IridaTheme { TripDetailScreen(tripId = "1") }
}
