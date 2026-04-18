package com.example.myapplication.features.detailreport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.core.components.StatusCategoryChip
import com.example.myapplication.domain.model.Comment
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.domain.model.ReportStatus
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailReportScreen(
    navController: NavController,
    viewModel: DetailReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Reporte", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.report != null) {
            val report = uiState.report!!
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ReportContent(report)
                    }
                    
                    item {
                        ImportanceSection(count = report.important)
                    }
                    
                    item {
                        LocationSection()
                    }
                    
                    item {
                        Text(
                            text = "Comentarios (${uiState.comments.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(uiState.comments) { comment ->
                        CommentItem(
                            comment = comment, 
                            userName = uiState.userNames[comment.userId] ?: "Usuario ${comment.userId}"
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                CommentInput(
                    text = uiState.commentText,
                    onTextChanged = viewModel::onCommentTextChanged,
                    onSend = viewModel::addComment
                )
            }
        }
    }
}

@Composable
fun ReportContent(report: Report) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
        AsyncImage(
            model = report.photoUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusCategoryChip(
                text = report.status.displayName,
                baseColor = report.status.color
            )
            
            val categoryEnum = ReportCategory.entries.find { it.displayName == report.type }
            if (categoryEnum != null) {
                StatusCategoryChip(
                    text = categoryEnum.displayName,
                    baseColor = categoryEnum.color
                )
            } else {
                StatusCategoryChip(
                    text = report.type,
                    baseColor = Color.Gray
                )
            }
        }
        
        Text(
            text = report.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        
        Text(
            text = report.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ImportanceSection(count: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF6A1B9A).copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¡Es Importante!",
                color = Color(0xFF6A1B9A),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Surface(
                color = Color(0xFF6A1B9A).copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A1B9A)
                )
            }
        }
    }
}

@Composable
fun LocationSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Ubicación",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Mapa disponible en futuras actualizaciones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun CommentItem(comment: Comment, userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF6A1B9A).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF6A1B9A)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = comment.createdAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CommentInput(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6A1B9A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "YO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = { Text("Escribe un comentario ...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                trailingIcon = {
                    IconButton(
                        onClick = onSend,
                        enabled = text.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = if (text.isNotBlank()) Color(0xFF6A1B9A) else Color.Gray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6A1B9A),
                    cursorColor = Color(0xFF6A1B9A)
                )
            )
        }
    }
}
