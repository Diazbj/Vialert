package com.example.myapplication.features.homeuser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.domain.model.Report

@Composable
fun ReportCard(
    report: Report,
    timeLabel: String,
    importantCount: Int,
    onImportantClick: (Report) -> Unit,
    onShareClick: (Report) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        //colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = report.photoUrl,
                contentDescription = report.title,
                modifier = Modifier
                    .fillMaxWidth(),
                    //.height(160.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report.type.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF6A1B9A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = report.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 28.sp
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onImportantClick(report) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A1B9A),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(id = R.string.home_user_report_important, importantCount))
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

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}
