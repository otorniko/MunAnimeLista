package com.otorniko.munanimelista.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.otorniko.munanimelista.data.AnimeDetailsViewModel
import com.otorniko.munanimelista.data.MediaType
import com.otorniko.munanimelista.utils.getDisplayTitles
import com.otorniko.munanimelista.utils.getSeasonString
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailsScreen(
    animeId: Int,
    onBackClick: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    onStatusChanged: () -> Unit,
    viewModel: AnimeDetailsViewModel = viewModel()
) {
    LaunchedEffect(animeId) { viewModel.loadAnime(animeId) }
    val animeState by viewModel.animeDetails.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val anime = animeState
    val (title, subtitle) = remember(anime) { anime.getDisplayTitles() }

    Scaffold(topBar = {
        TopAppBar(title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Row() {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                }
                if (subtitle != null) {
                    Row() {
                        Text(subtitle)

                    }
                }
            }

        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        })
    }, floatingActionButton = {
        if (anime != null) {
            ExtendedFloatingActionButton(text = {
                Text(if (anime.myListStatus == null) "Add to List" else "Edit Status")
            }, icon = { Icon(Icons.Default.Edit, null) }, onClick = { showSheet = true })
        }
    }

    ) { innerPadding ->
        if (animeState == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val anime = animeState!!
            val safeRelatedAnime = remember(anime.relatedAnime) {
                anime.relatedAnime.filter { item ->
                    val type = item.relationType.lowercase()
                    type != "adaptation" && type != "source" && item.node.mediaType != MediaType.MUSIC
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.height(200.dp)) {
                    AsyncImage(
                        model = anime.mainPicture?.large ?: anime.mainPicture?.medium,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(0.7f) // Standard poster ratio
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {

                        Text(
                            text = "Score: ${anime.mean ?: "N/A"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )


                        Spacer(modifier = Modifier.height(8.dp))
                        val rankText = "Rank: #${anime.rank ?: "N/A"}"
                        val popText = "Popularity: #${anime.popularity ?: "N/A"}"
                        Text(
                            text = "$rankText  •  $popText",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        var displayText = "Unaired"
                        if (anime.startDate != null) {
                            val startStr = remember(anime.startSeason) {
                                anime.startSeason?.let {
                                    "${it.season} ${it.year}"
                                }
                            }
                            val endStr = remember(anime.endDate) {
                                getSeasonString(anime.endDate)
                            }
                            displayText = when {
                                endStr == null -> "$startStr - Present"
                                startStr == endStr -> startStr
                                else -> "$startStr - $endStr"
                            }
                            Text(text = displayText)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SuggestionChip(onClick = {}, label = {
                                val type = anime.mediaType?.name?.uppercase() ?: "TV"
                                val eps = anime.numEpisodes
                                val showCount = type != "MOVIE" && eps != 1

                                Text(
                                    text = if (showCount) "$type • ${eps ?: "?"} eps" else type
                                )
                            })

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround // Spreads them evenly
                        ) {
                            // Studio
                            val studioName = anime.studios?.firstOrNull()?.name ?: "Unknown"
                            AnimeInfoItem(
                                icon = Icons.Default.Business, label = "Studio", value = studioName
                            )

                            // Source
                            val sourceName = anime.source?.replace("_", " ")
                                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                ?: "-"
                            AnimeInfoItem(
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                label = "Source",
                                value = sourceName
                            )

                            val ratingName = anime.rating?.label?.split(" -")?.get(0) ?: "-"
                            AnimeInfoItem(
                                icon = Icons.Default.Warning, // Or Icons.Default.Explicit
                                label = "Rating", value = ratingName
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // 1. GENRES (The "Tags" look)
                if (!anime.genres.isNullOrEmpty()) {

                    // The Scrollable Row
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp), // Adds padding to the start/end of the list
                        horizontalArrangement = Arrangement.spacedBy(8.dp)  // Spacing between chips
                    ) {
                        items(anime.genres) { genre ->
                            SuggestionChip(
                                onClick = { /* Todo: Navigate */ },
                                label = { Text(genre.name) },
                                // Optional: Make them look slightly smaller/sleeker
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                                ),
                                border = null // Removes the outline for a cleaner look
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Text(
                    text = "Synopsis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                val cleanSynopsis = remember(anime.synopsis) {
                    anime.synopsis?.replace("[Written by MAL Rewrite]", "")
                        ?.replace(Regex("\\(Source: .*\\)"), "")?.trim() ?: "No synopsis available."
                }

                ExpandableText(
                    text = cleanSynopsis, modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(24.dp))

                if (safeRelatedAnime.isNotEmpty()) {
                    Text(
                        text = "Related Anime",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(safeRelatedAnime) { related ->
                            Column(
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable {
                                        onAnimeClick(related.node.id)
                                    }) {
                                AsyncImage(
                                    model = related.node.mainPicture?.medium,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = related.node.alternativeTitles?.en ?: related.node.title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = related.relationTypeFormatted ?: related.relationType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
                // TODO
                // No title == not anime?
                if (anime.recommendations.isNotEmpty()) {
                    Text(
                        text = "Recommendations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(anime.recommendations) { rec ->
                            Column(
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable {
                                        onAnimeClick(rec.node.id)
                                    }) {
                                AsyncImage(
                                    model = rec.node.mainPicture?.medium,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = rec.node.alternativeTitles?.en ?: rec.node.title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                if (showSheet && anime != null) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false }, sheetState = sheetState
                    ) {
                        EditStatusSheet(
                            initialStatus = anime.myListStatus?.status,
                            initialScore = anime.myListStatus?.score ?: 0,
                            initialProgress = anime.myListStatus?.numEpisodesWatched ?: 0,
                            maxEpisodes = anime.numEpisodes ?: 0,
                            onDismiss = { showSheet = false },
                            onSave = { status, score, progress ->
                                viewModel.updateStatus(status, score, progress)
                                onStatusChanged()
                                showSheet = false
                            },
                            onDelete = {
                                viewModel.removeAnime(anime.id) {
                                    showSheet = false
                                    onStatusChanged()
                                }
                            })
                    }
                }
            }
        }
    }
}


@Composable
fun ExpandableText(
    text: String, modifier: Modifier = Modifier, minimizedMaxLines: Int = 4
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { isExpanded = !isExpanded }) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                    isOverflowing = true
                }
            })

        if (isOverflowing && !isExpanded) {
            Text(
                text = "Read more...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        } else if (isExpanded) {
            Text(
                text = "Show less",
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun AnimeInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}