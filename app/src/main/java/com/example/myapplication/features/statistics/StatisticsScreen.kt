package com.example.myapplication.features.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.theme.VialertPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController?) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics_topbar_title), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.statistics_back), tint = Color(0xFF1E293B))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.statistics_options), tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Main Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(stringResource(R.string.statistics_reports_by_month), fontSize = 12.sp, color = Color(0xFF64748B))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("142", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("~ 12%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Text(stringResource(R.string.statistics_total_accumulated), fontSize = 11.sp, color = Color(0xFF94A3B8))
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Custom Bar Chart Representation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        ChartBar(height = 120.dp, label = stringResource(R.string.statistics_week_1))
                        ChartBar(height = 90.dp, label = stringResource(R.string.statistics_week_2))
                        ChartBar(height = 50.dp, label = stringResource(R.string.statistics_week_3))
                        ChartBar(height = 160.dp, label = stringResource(R.string.statistics_week_4))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.statistics_activity_summary), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            // Summary List
            ActivitySummaryItem(
                icon = Icons.Default.Assignment,
                iconColor = VialertPurple,
                title = stringResource(R.string.statistics_total_reports),
                value = "142",
                badgeText = "+5%",
                badgeColor = Color(0xFF10B981),
                badgeBg = Color(0xFFD1FAE5)
            )
            
            ActivitySummaryItem(
                icon = Icons.Default.CheckCircle,
                iconColor = VialertPurple,
                title = stringResource(R.string.statistics_resolved),
                value = "98",
                badgeText = "-2%",
                badgeColor = Color(0xFFF59E0B),
                badgeBg = Color(0xFFFEF3C7)
            )

            ActivitySummaryItem(
                icon = Icons.Default.Pending,
                iconColor = VialertPurple,
                title = stringResource(R.string.statistics_in_progress),
                value = "44",
                badgeText = "+8%",
                badgeColor = Color(0xFF10B981),
                badgeBg = Color(0xFFD1FAE5)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(stringResource(R.string.statistics_by_category), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            CategoryProgress(stringResource(R.string.statistics_category_security), "64%", 0.64f, VialertPurple)
            CategoryProgress(stringResource(R.string.statistics_category_transit), "36%", 0.36f, Color(0xFFB794F6))
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ChartBar(height: androidx.compose.ui.unit.Dp, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(VialertPurple)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
    }
}

@Composable
fun ActivitySummaryItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    badgeText: String,
    badgeColor: Color,
    badgeBg: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, color = Color(0xFF64748B))
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeBg,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryProgress(title: String, percentage: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 14.sp, color = Color(0xFF475569))
            Text(percentage, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}
