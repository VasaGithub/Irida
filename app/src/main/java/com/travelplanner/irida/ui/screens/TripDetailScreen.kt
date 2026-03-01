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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.domain.ItineraryItem
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.ui.theme.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

// Mock itinerary data for Tokyo
val mockItinerary = listOf(
    ItineraryItem(
        id = "1",
        time = "08:00",
        title = "Flight BCN → NRT",
        description = "Vueling VY7182 · Terminal 1",
        location = "Aeroport Barcelona",
        cost = 420.0,
        emoji = "✈️",
        isBooked = true
    ),
    ItineraryItem(
        id = "2",
        time = "22:30",
        title = "Check-in · Shinjuku Hotel",
        description = "Shinjuku, Tokyo · 4★",
        location = "Shinjuku, Tokyo",
        cost = 95.0,
        emoji = "🏨",
        isBooked = true
    ),
    ItineraryItem(
        id = "3",
        time = "09:00",
        title = "Senso-ji Temple",
        description = "Asakusa · 2h visita",
        location = "Asakusa, Tokyo",
        cost = 0.0,
        emoji = "⛩️",
        isBooked = false
    ),
    ItineraryItem(
        id = "4",
        time = "13:00",
        title = "Ramen Ippudo",
        description = "Shibuya · Reserva hecha",
        location = "Shibuya, Tokyo",
        cost = 18.0,
        emoji = "🍜",
        isBooked = true
    ),
    ItineraryItem(
        id = "5",
        time = "15:30",
        title = "Shibuya Crossing",
        description = "Icònic creuament · 1h",
        location = "Shibuya, Tokyo",
        cost = 0.0,
        emoji = "🏙️",
        isBooked = false
    ),
    ItineraryItem(
        id = "6",
        time = "20:00",
        title = "Sushi Saito",
        description = "Restaurant omakase · Reserva obligatoria",
        location = "Roppongi, Tokyo",
        cost = 85.0,
        emoji = "🍣",
        isBooked = true
    )
)

val mockTripTokyo = Trip(
    id = "1",
    title = "Tokyo Adventure",
    destination = "Tokyo, Japan",
    startDate = "Mar 10",
    endDate = "Mar 18",
    nights = 8,
    budget = 1240.0,
    budgetSpent = 806.0,
    emoji = "🗼",
    activities = mockItinerary
)

@Composable
fun TripDetailScreen(
    trip: Trip = mockTripTokyo,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Itinerary", "Gallery", "Budget", "Notes")

    Scaffold(
        containerColor = NavyDeep,
        bottomBar = {
            BottomNavBar(selectedTab = 1, onTabSelected = { tab ->
                when (tab) {
                    0 -> onNavigate("home")
                    1 -> onNavigate("trips")
                    2 -> onNavigate("gallery")
                    3 -> onNavigate("settings")
                }
            })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues)
        ) {
            // Hero header
            item {
                TripDetailHeader(trip = trip, onBack = onBack)
            }

            // Stats row
            item {
                TripStatsRow(trip = trip)
            }

            // Tabs
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

            // Tab content
            when (selectedTab) {
                0 -> {
                    // Group by day
                    val day1 = mockItinerary.take(2)
                    val day2 = mockItinerary.drop(2)

                    item {
                        DayHeader(day = "DAY 1 · MAR 10")
                    }
                    items(day1) { item ->
                        ItineraryItemCard(item = item)
                    }
                    item {
                        DayHeader(day = "DAY 2 · MAR 11")
                    }
                    items(day2) { item ->
                        ItineraryItemCard(item = item)
                    }
                }
                1 -> {
                    item {
                        GalleryTabPlaceholder()
                    }
                }
                2 -> {
                    item {
                        BudgetTabContent(trip = trip)
                    }
                }
                3 -> {
                    item {
                        NotesTabPlaceholder()
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun TripDetailHeader(trip: Trip, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A3A5C), NavyDeep)
                )
            )
    ) {
        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
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
                text = "📅 ${trip.startDate} – ${trip.endDate} · ${trip.nights} nights",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid
            )
        }
    }
}

@Composable
fun TripStatsRow(trip: Trip) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(value = "${trip.nights}", label = "NIGHTS", emoji = "🌙")
        StatDivider()
        StatItem(value = "€${trip.budget.toInt()}", label = "BUDGET", emoji = "💰")
        StatDivider()
        StatItem(value = "${trip.activities.size}", label = "ACTIVITIES", emoji = "📍")
    }
}

@Composable
fun StatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = GrayMid,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(GrayDark)
    )
}

@Composable
fun DayHeader(day: String) {
    Text(
        text = day,
        style = MaterialTheme.typography.labelSmall,
        color = TurquoisePrimary,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun ItineraryItemCard(item: ItineraryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Time column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(52.dp)
        ) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.labelSmall,
                color = GrayMid
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(TurquoisePrimary, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = item.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayMid
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = item.getFormattedCost(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (item.cost == 0.0) SuccessGreen else GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isBooked) {
                        Text(
                            text = "✓ booked",
                            style = MaterialTheme.typography.labelSmall,
                            color = TurquoisePrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetTabContent(trip: Trip) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyLight)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Budget Overview", style = MaterialTheme.typography.titleMedium, color = White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                BudgetRow("Total Budget", "€${trip.budget.toInt()}", GoldAccent)
                BudgetRow("Spent", "€${trip.budgetSpent.toInt()}", ErrorRed)
                BudgetRow("Remaining", "€${trip.getRemainingBudget().toInt()}", SuccessGreen)
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (trip.budgetSpent / trip.budget).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = TurquoisePrimary,
                    trackColor = GrayDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${trip.getBudgetProgressPercent()}% spent",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayMid
                )
            }
        }
    }
}

@Composable
fun BudgetRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GalleryTabPlaceholder() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🖼️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Gallery available in Trip Gallery screen", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        }
    }
}

@Composable
fun NotesTabPlaceholder() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📝", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("No notes yet for this trip", style = MaterialTheme.typography.bodyMedium, color = GrayMid)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripDetailScreenPreview() {
    IridaTheme {
        TripDetailScreen()
    }
}