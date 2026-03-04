package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.ui.theme.*

data class TeamMember(
    val initials: String,
    val name: String,
    val role: String,
    val color: androidx.compose.ui.graphics.Color
)

val mockTeamMembers = listOf(
    TeamMember("IV", "Iker Vazquez", "Backend · Frontend · Arquitectura y Desarrollo", TurquoisePrimary),
    TeamMember("RR", "Raul Rubio", "Logo · Diseño gráfico", GoldAccent)
)

@Composable
fun AboutScreen(
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        containerColor = NavyDeep,
        bottomBar = {
            BottomNavBar(selectedTab = 3, onTabSelected = { tab ->
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
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero section
            item {
                AboutHero()
            }

            // Team section
            item {
                PreferenceSectionHeader(emoji = "👥", title = "EQUIPO DE DESARROLLO")
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    mockTeamMembers.forEach { member ->
                        TeamMemberCard(member = member)
                    }
                }
            }

            // Technical info section
            item {
                PreferenceSectionHeader(emoji = "ℹ️", title = "INFORMACIÓN TÉCNICA")
            }
            item {
                PreferenceCard {
                    TechInfoRow("Versión", "1.0.0")
                    PreferenceDivider()
                    TechInfoRow("Sprint", "01")
                    PreferenceDivider()
                    TechInfoRow("Android mín.", "API 26 (8.0)")
                    PreferenceDivider()
                    TechInfoRow("Kotlin", "2.0.0")
                    PreferenceDivider()
                    TechInfoRow("Jetpack Compose", "BOM 2024.x")
                }
            }

            // License section
            item {
                PreferenceSectionHeader(emoji = "📄", title = "LICENCIA")
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "MIT License",
                            style = MaterialTheme.typography.titleMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Copyright © 2025 Irida Team · Campus Igualada · Universitat de Lleida",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayMid
                        )
                    }
                }
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Desarrollado en Campus Igualada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayDark,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Universitat de Lleida · 2025",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayDark,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AboutHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyMid, NavyDeep)
                )
            )
            .padding(vertical = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(TurquoisePrimary, TurquoiseLight)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✈", fontSize = 40.sp, color = NavyDeep)
            }

            Text(
                text = "Irida",
                style = MaterialTheme.typography.headlineLarge,
                color = White,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Travel Planner",
                style = MaterialTheme.typography.titleMedium,
                color = TurquoisePrimary,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = TurquoisePrimary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "v1.0.0 · Sprint 01",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TurquoisePrimary,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Plan your adventures, your way.\nDesarrollado en Campus Igualada.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TeamMemberCard(member: TeamMember) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(member.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.initials,
                    color = member.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Column {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = member.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
            }
        }
    }
}

@Composable
fun TechInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMid
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    IridaTheme {
        AboutScreen()
    }
}