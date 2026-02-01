package com.otorniko.munanimelista.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.otorniko.munanimelista.data.AnimeNode
import com.otorniko.munanimelista.data.ListStatus
import com.otorniko.munanimelista.utils.getDisplayTitles


@Composable
fun AnimeRow(anime: AnimeNode, onClick: () -> Unit) {

    val displayTitle = anime.getDisplayTitles().first

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = anime.mainPicture?.medium,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleMedium,
                //maxLines = 1 // Optional
            )

            Text(
                text = "Score: ${anime.mean} | Me: ${anime.myListStatus?.score ?: "-"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val status = anime.myListStatus?.status
            if (status != null) {
                Spacer(modifier = Modifier.height(4.dp))

                // Dynamic color based on status
                val statusColor = when (status) {
                    ListStatus.Watching -> MaterialTheme.colorScheme.primary
                    ListStatus.Completed -> MaterialTheme.colorScheme.secondary
                    ListStatus.Dropped -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }

                Text(
                    text = status.label, // Uses your Enum's .label (e.g. "Plan to Watch")
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}