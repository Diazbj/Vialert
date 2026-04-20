package com.example.myapplication.features.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserLevel
import com.example.myapplication.domain.model.Report
import com.example.myapplication.features.homeuser.components.MainLayout
import com.example.myapplication.core.navigation.EditProfile
import com.example.myapplication.core.navigation.Statistics
import com.example.myapplication.core.navigation.AdminControlPanel
import com.example.myapplication.domain.model.UserRole

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    
    if (uiState.user?.role == UserRole.ADMIN) {
        AdminProfileContent(uiState = uiState, viewModel = viewModel, navController = navController)
    } else {
        UserProfileContent(uiState = uiState, viewModel = viewModel, navController = navController)
    }
}

@Composable
fun UserProfileContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    navController: NavController? = null
) {
    val user = uiState.user
    val level = uiState.userLevel
    val stats = uiState.stats
    val progress = uiState.pointsProgress
    val logros = viewModel.logros

    MainLayout(navController = navController) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(140.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                    }
                }
                
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
                text = "${user?.firstName ?: ""} ${user?.lastName ?: stringResource(R.string.profile_loading_name)}",
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
                    text = level.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = stringResource(R.string.profile_score, user?.score ?: 0),
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
                    onClick = { navController?.navigate(EditProfile) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_btn_edit))
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
                    Text(stringResource(R.string.profile_btn_logout))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(stringResource(R.string.profile_stats_pending), stats.pending.toString(), Icons.Default.Campaign, Color(0xFFFFF3CD), Color(0xFFFFA000), Modifier.weight(1f).clickable { navController?.navigate(Statistics) })
                StatCard(stringResource(R.string.profile_stats_verified), stats.verified.toString(), Icons.Default.Verified, Color(0xFFE8E8FF), Color(0xFF1976D2), Modifier.weight(1f).clickable { navController?.navigate(Statistics) })
                StatCard(stringResource(R.string.profile_stats_resolved), stats.resolved.toString(), Icons.Default.CheckCircle, Color(0xFFDFF6DD), Color(0xFF388E3C), Modifier.weight(1f).clickable { navController?.navigate(Statistics) })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reputation Progress
            ReputationCard(
                current = user?.score ?: 0, 
                max = level.maxPoints, 
                nextLevel = UserLevel.entries.getOrNull(level.ordinal + 1)?.displayName ?: stringResource(R.string.profile_reputation_max),
                progress = progress
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            var selectedTab by remember { mutableStateOf(0) }
            val tabs = listOf(stringResource(R.string.profile_tab_achievements), stringResource(R.string.profile_tab_publications))

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
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                if (uiState.myReports.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.profile_no_publications), color = Color(0xFF64748B))
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.myReports.forEach { report ->
                            ReportSummaryItem(report)
                        }
                    }
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
fun ReputationCard(current: Int, max: Int, nextLevel: String, progress: Float) {
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
                Text(stringResource(R.string.profile_reputation_title), fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text("$current / $max", fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(0xFF7C3AED),
                trackColor = Color(0xFFE2E8F0)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.profile_reputation_next_level, max - current, nextLevel),
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

@Composable
fun ReportSummaryItem(report: Report) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = report.status.color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Feed, contentDescription = null, tint = report.status.color, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = report.status.displayName, fontSize = 12.sp, color = report.status.color, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun AdminProfileContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    navController: NavController?
) {
    val user = uiState.user

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.profile_admin_back), tint = Color(0xFF7C3AED))
                }
                Text(stringResource(R.string.profile_admin_title), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.profile_admin_settings), tint = Color(0xFF7C3AED))
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(120.dp)) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(4.dp, Color(0xFFF3E8FF)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(16.dp), tint = Color.LightGray)
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFBBF24),
                    modifier = Modifier.size(32.dp).border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("${user?.firstName ?: ""} ${user?.lastName ?: ""}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.profile_admin_role), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
            }
            
            Text(stringResource(R.string.profile_admin_region, user?.id?.take(4) ?: "8824"), fontSize = 12.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { navController?.navigate(EditProfile) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E8FF), contentColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_btn_edit))
                }

                Button(
                    onClick = { viewModel.onLogout() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF64748B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_btn_logout))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController?.navigate(AdminControlPanel) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.profile_admin_control_panel), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard(title = stringResource(R.string.profile_admin_stat_reports), value = "124", icon = Icons.Default.InsertChartOutlined, iconTint = Color(0xFF9333EA), iconBg = Color(0xFFF3E8FF), modifier = Modifier.weight(1f), onClick = { navController?.navigate(com.example.myapplication.core.navigation.AdminDataAnalysis) })
                AdminStatCard(title = stringResource(R.string.profile_admin_stat_users), value = "1.2k", icon = Icons.Default.PeopleAlt, iconTint = Color(0xFF7C3AED), iconBg = Color(0xFFF3E8FF), modifier = Modifier.weight(1f))
                AdminStatCard(title = stringResource(R.string.profile_admin_stat_alerts), value = "8", icon = Icons.Default.WarningAmber, iconTint = Color(0xFFEF4444), iconBg = Color(0xFFFEE2E2), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFAFF)),
                border = BorderStroke(1.dp, Color(0xFFF3E8FF))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.profile_admin_resolution_title), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("92%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = 0.92f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = Color(0xFF7C3AED),
                        trackColor = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.profile_admin_performance), fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            }

            var selectedAdminTab by remember { mutableStateOf(0) }

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.weight(1f)
                        .clickable { selectedAdminTab = 0 }
                        .border(BorderStroke(if (selectedAdminTab == 0) 1.dp else 0.dp, if (selectedAdminTab == 0) Color(0xFF7C3AED) else Color.Transparent), shape = RoundedCornerShape(topStart = 8.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.profile_admin_tab_activity), fontSize = 12.sp, fontWeight = if (selectedAdminTab == 0) FontWeight.Bold else FontWeight.Normal, color = if (selectedAdminTab == 0) Color(0xFF7C3AED) else Color(0xFF64748B))
                    HorizontalDivider(color = if (selectedAdminTab == 0) Color(0xFF7C3AED) else Color(0xFFE2E8F0), thickness = if (selectedAdminTab == 0) 2.dp else 1.dp, modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
                }
                Box(
                    modifier = Modifier.weight(1f)
                        .clickable { selectedAdminTab = 1 }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.profile_admin_tab_new_users), fontSize = 12.sp, fontWeight = if (selectedAdminTab == 1) FontWeight.Bold else FontWeight.Normal, color = if (selectedAdminTab == 1) Color(0xFF7C3AED) else Color(0xFF64748B))
                    HorizontalDivider(color = if (selectedAdminTab == 1) Color(0xFF7C3AED) else Color(0xFFE2E8F0), thickness = if (selectedAdminTab == 1) 2.dp else 1.dp, modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
                }
            }
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedAdminTab == 0) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AdminRoleCard(stringResource(R.string.profile_admin_role_moderator), Icons.Default.Security, Modifier.weight(1f))
                    AdminRoleCard(stringResource(R.string.profile_admin_role_verifier), Icons.Default.VerifiedUser, Modifier.weight(1f))
                    AdminRoleCard(stringResource(R.string.profile_admin_role_founder), Icons.Default.RocketLaunch, Modifier.weight(1f))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.allUsers.forEach { listedUser ->
                        AdminUserItem(user = listedUser)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, icon: ImageVector, iconTint: Color, iconBg: Color, modifier: Modifier, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.aspectRatio(0.85f).clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.weight(1f))
                Surface(shape = CircleShape, color = iconBg, modifier = Modifier.size(24.dp)) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun AdminRoleCard(title: String, icon: ImageVector, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.aspectRatio(0.80f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFF3E8FF),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
        }
    }
}

@Composable
fun AdminUserItem(user: User) {
    val roleLabel = when (user.role) {
        UserRole.ADMIN -> stringResource(R.string.profile_admin_user_role_admin)
        UserRole.USER -> stringResource(R.string.profile_admin_user_role_user)
    }
    val roleBg = when (user.role) {
        UserRole.ADMIN -> Color(0xFFF3E8FF)
        UserRole.USER -> Color(0xFFEDE9FE)
    }
    val roleColor = when (user.role) {
        UserRole.ADMIN -> Color(0xFF9333EA)
        UserRole.USER -> Color(0xFF6366F1)
    }
    val initials = "${user.firstName.firstOrNull()?.uppercase() ?: ""}${user.lastName.firstOrNull()?.uppercase() ?: ""}"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle with initials
            Surface(
                shape = CircleShape,
                color = Color(0xFFF3E8FF),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name & email
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.email,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Role badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = roleBg
            ) {
                Text(
                    text = roleLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = roleColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
