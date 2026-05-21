package com.example.myapplication.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryStat(
    val category: ReportCategory,
    val total: Int,
    val pending: Int,
    val inProgress: Int,
    val resolved: Int,
    val fraction: Float
)

data class AdminCategoriesUiState(
    val stats: List<CategoryStat> = emptyList(),
    val totalReports: Int = 0
)

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCategoriesUiState())
    val uiState: StateFlow<AdminCategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            reportRepository.reports.collect { reports ->
                val active = reports.filter { it.status != ReportStatus.DELETED }
                val total = active.size
                val stats = ReportCategory.entries.map { cat ->
                    val group = active.filter { it.type == cat.displayName }
                    CategoryStat(
                        category = cat,
                        total = group.size,
                        pending = group.count { it.status == ReportStatus.PENDING },
                        inProgress = group.count { it.status == ReportStatus.IN_PROGRESS },
                        resolved = group.count { it.status == ReportStatus.RESOLVED },
                        fraction = if (total > 0) group.size.toFloat() / total else 0f
                    )
                }.sortedByDescending { it.total }
                _uiState.value = AdminCategoriesUiState(stats = stats, totalReports = total)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriesScreen(
    viewModel: AdminCategoriesViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Categorías", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF7C3AED))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("${state.totalReports} reportes en total", fontSize = 13.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (state.stats.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("Sin datos de categorías", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    }
                }
            } else {
                items(state.stats) { stat ->
                    CategoryStatCard(stat = stat)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun CategoryStatCard(stat: CategoryStat) {
    val catColor = stat.category.color
    val pct = (stat.fraction * 100).toInt()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = catColor.copy(alpha = 0.12f), modifier = Modifier.size(44.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = catColor, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stat.category.displayName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text("${stat.total} reportes · $pct% del total", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                Surface(shape = RoundedCornerShape(10.dp), color = catColor.copy(alpha = 0.12f)) {
                    Text("$pct%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = catColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { stat.fraction },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = catColor,
                trackColor = Color(0xFFF1F5F9)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniStatusChip("Pendiente", stat.pending, Color(0xFFEF4444), Modifier.weight(1f))
                MiniStatusChip("Revisión", stat.inProgress, Color(0xFFF59E0B), Modifier.weight(1f))
                MiniStatusChip("Resuelto", stat.resolved, Color(0xFF10B981), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniStatusChip(label: String, count: Int, color: Color, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.08f), modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 9.sp, color = color.copy(alpha = 0.8f))
        }
    }
}
