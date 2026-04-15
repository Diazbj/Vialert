package com.example.myapplication.features.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.features.homeuser.components.MainLayout

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val name by viewModel.name
    val title by viewModel.title
    val level by viewModel.level
    val points by viewModel.points
    val maxPoints by viewModel.maxPoints
    val nextLevel by viewModel.nextLevel
    val stats by viewModel.stats
    val logros = viewModel.logros

    MainLayout(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)) // Fondo muy claro
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Avatar, Name, Badge
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(140.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.size(32.dp).border(2.dp, Color.White, CircleShape),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF6366F1), // Indigo/Purple
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Nivel $level • $points pts",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleEditing() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Perfil")
                }

                Button(
                    onClick = { viewModel.onLogout() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEDE9FE),
                        contentColor = Color(0xFF7C3AED)
                    )
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("ACTIVOS", stats.active.toString(), Icons.Default.Campaign, Color(0xFFF5F3FF), Color(0xFF8B5CF6), Modifier.weight(1f))
                StatCard("FINALIZADOS", stats.completed.toString(), Icons.Default.CheckCircle, Color(0xFFF0FDF4), Color(0xFF22C55E), Modifier.weight(1f))
                StatCard("VERIFICADOS", stats.verified.toString(), Icons.Default.Verified, Color(0xFFEFF6FF), Color(0xFF3B82F6), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reputation Progress
            ReputationCard(points, maxPoints, nextLevel)

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            var selectedTab by remember { mutableStateOf(0) }
            val tabs = listOf("Logros", "Mis Publicaciones")

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF7C3AED),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF7C3AED)
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF7C3AED) else Color(0xFF64748B)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            if (selectedTab == 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    logros.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { logro ->
                                Box(modifier = Modifier.weight(1f)) {
                                    AchievementItem(logro)
                                }
                            }
                            // Add empty boxes if the row is not full to maintain spacing
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No hay publicaciones aún", color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Surface(
                    shape = CircleShape,
                    color = bgColor,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(6.dp))
                }
            }
        }
    }
}

@Composable
fun ReputationCard(current: Int, max: Int, nextLevel: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progreso de Reputación", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text("$current / $max", fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = current.toFloat() / max,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(0xFF7C3AED),
                trackColor = Color(0xFFE2E8F0)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Te faltan ${max - current} pts para subir al nivel $nextLevel",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun AchievementItem(logro: Achievement) {
    val icon = when (logro.iconType) {
        "trophy" -> Icons.Default.EmojiEvents
        "shield" -> Icons.Default.VerifiedUser
        "fire" -> Icons.Default.Whatshot
        "handshake" -> Icons.Default.Handshake
        "lock" -> Icons.Default.Lock
        else -> Icons.Default.Star
    }

    val iconColor = if (logro.isLocked) Color(0xFF94A3B8) else Color(0xFF7C3AED)
    val bgColor = if (logro.isLocked) Color(0xFFF1F5F9) else Color(0xFFF5F3FF)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.aspectRatio(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Surface(
                    shape = CircleShape,
                    color = bgColor,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = logro.title,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            color = if (logro.isLocked) Color(0xFF94A3B8) else Color(0xFF475569),
            fontWeight = FontWeight.Medium
        )
    }
}
