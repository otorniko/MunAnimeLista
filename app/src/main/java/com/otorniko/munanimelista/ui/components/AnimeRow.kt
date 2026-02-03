package com.otorniko.munanimelista.ui.components

import android.R.attr.maxLines
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.otorniko.munanimelista.data.AnimeNode
import com.otorniko.munanimelista.data.AnimeViewModel
import com.otorniko.munanimelista.data.ListStatus
import com.otorniko.munanimelista.ui.theme.LightGrey
import com.otorniko.munanimelista.utils.getDisplayTitles


@Composable
fun AnimeRow(
    anime: AnimeNode,
    onClick: () -> Unit,
    viewModel: AnimeViewModel = viewModel()
) {

    val preferEnglish by viewModel.preferEnglish.collectAsState()

    val (title, subtitle) = remember(anime, preferEnglish) {
        anime?.getDisplayTitles(preferEnglish) ?: ("" to null)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(IntrinsicSize.Min)
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = anime.mainPicture?.medium,
            contentDescription = anime.title,
            modifier = Modifier
                .width(70.dp)
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                //.size(64.dp)
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
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        //maxLines = 1 // Optional
                        //overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )}

                    if (subtitle != null) {
                        Row {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                //color = MaterialTheme.colorScheme.onSurfaceVariant, // Grey
                                color = LightGrey,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    //text = "Score: ${anime.mean} | Me: ${anime.myListStatus?.score ?: "-"}",
                    //style = MaterialTheme.typography.bodyMedium,
                    text = "★ ${anime.mean ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (anime.myListStatus?.score != null) {

                    Text(
                        //text = "Score: ${anime.mean} | Me: ${anime.myListStatus?.score ?: "-"}",
                        //style = MaterialTheme.typography.bodyMedium,
                        text = "Me: ${anime.myListStatus.score}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
}