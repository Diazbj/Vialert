package com.example.myapplication.features.homeuser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.myapplication.R
import com.example.myapplication.core.components.StatusCategoryChip
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportCategory

@Composable
fun ReportCard(
    report: Report,
    timeLabel: String,
    importantCount: Int,
    hasVoted: Boolean,
    onImportantClick: (Report) -> Unit,
    onShareClick: (Report) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(report.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = report.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categoryEnum = ReportCategory.entries.find { it.displayName == report.type }
                StatusCategoryChip(
                    text = categoryEnum?.displayName ?: report.type,
                    baseColor = categoryEnum?.color ?: Color.Gray
                )
                
                StatusCategoryChip(
                    text = report.status.displayName,
                    baseColor = report.status.color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (!hasVoted) onImportantClick(report) },
                    enabled = !hasVoted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasVoted) Color(0xFF6A1B9A).copy(alpha = 0.5f) else Color(0xFF6A1B9A),
                        disabledContainerColor = Color(0xFF6A1B9A).copy(alpha = 0.5f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (hasVoted)
                            "✓ Importante ($importantCount)"
                        else
                            stringResource(id = R.string.home_user_report_important, importantCount)
                    )
                }

                IconButton(
                    onClick = { onShareClick(report) },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color(0xFF6A1B9A)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(id = R.string.home_user_share_content_description)
                    )
                }
            }
        }
    }
}
