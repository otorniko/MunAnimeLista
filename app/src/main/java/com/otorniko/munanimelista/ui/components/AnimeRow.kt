package com.otorniko.munanimelista.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.otorniko.munanimelista.data.AnimeNode
import com.otorniko.munanimelista.data.AnimeViewModel
import com.otorniko.munanimelista.data.ListStatus
import com.otorniko.munanimelista.utils.getDisplayTitles


@Composable
fun AnimeRow(anime: AnimeNode, onClick: () -> Unit, viewModel: AnimeViewModel = viewModel()) {

    val preferEnglish by viewModel.preferEnglish.collectAsState()

    val (title, subtitle) = remember(anime, preferEnglish) {
        anime?.getDisplayTitles(preferEnglish) ?: ("" to null)
    }

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
            modifier = Modifier
                .size(64.dp)
                .aspectRatio(0.7f),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row {
                Column {
                    Row {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        //maxLines = 1 // Optional
                    )}

                    if (subtitle != null) {
                        Row {
                            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Text(
                text = "Score: ${anime.mean} | Me: ${anime.myListStatus?.score ?: "-"}",
                style = MaterialTheme.typography.bodyMedium,
            )

            val status = anime.myListStatus?.status
            if (status != null) {
                Spacer(modifier = Modifier.height(4.dp))

                val statusColor = when (status) {
                    ListStatus.Watching -> MaterialTheme.colorScheme.primary
                    ListStatus.Completed -> MaterialTheme.colorScheme.secondary
                    ListStatus.Dropped -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }

                Text(
                    text = status.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}