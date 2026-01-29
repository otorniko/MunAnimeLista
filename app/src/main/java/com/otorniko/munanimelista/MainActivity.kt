package com.otorniko.munanimelista

// EXPLICIT IMPORTS FOR MATERIAL 3
// --------------------------------
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.otorniko.munanimelista.data.AnimeNode
import com.otorniko.munanimelista.data.AnimeSortOrder
import com.otorniko.munanimelista.data.AnimeViewModel
import com.otorniko.munanimelista.data.ListStatus
import kotlinx.serialization.json.Json


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimeListScreen()
        }
    }
}

// 1. We fix the JSON warning by creating this once, outside the function.
private val json = Json { ignoreUnknownKeys = true }

// 2. We fix the SmallTopAppBar error with this annotation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(
    viewModel: AnimeViewModel = viewModel()
) {
    val animeList by viewModel.animeList.collectAsState()
    val tabs = listOf("All") + ListStatus.entries.map { it.label }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mun Anime Lista") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        bottomBar = {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index

                            // LOGIC: If index is 0, filter is NULL (All).
                            // Otherwise, get the corresponding Enum (index - 1).
                            val filter = if (index == 0) null else ListStatus.entries[index - 1]
                            viewModel.filterByStatus(filter)
                        },
                        text = { Text(title) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        // 1. PARENT COLUMN: Stacks the buttons and the list vertically
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply the Scaffold padding here
                .fillMaxSize()
        ) {
            // 2. BUTTONS (Put them first so they are at the top)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly // Space them out nicely
            ) {
                Button(onClick = { viewModel.changeSortOrder(AnimeSortOrder.TITLE) }) {
                    Text("A-Z")
                }
                Button(onClick = { viewModel.changeSortOrder(AnimeSortOrder.SCORE) }) {
                    Text("Score")
                }
            }

            // 3. THE LIST
            // IMPORTANT: Modifier.weight(1f) tells the list to take up ALL remaining space
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(animeList) { anime ->
                    AnimeRow(anime = anime)
                }
            }
        }
    }
}

@Composable
fun AnimeRow(anime: AnimeNode) {
    // LOGIC: Use English title if not empty, otherwise use default
    val displayTitle = if (!anime.alternativeTitles?.en.isNullOrBlank()) {
        anime.alternativeTitles!!.en!!
    } else {
        anime.title
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            modifier = Modifier.weight(1f) // Take up remaining width
        ) {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleMedium,
                //maxLines = 1 // Optional: Limit title to 1 line if you want
            )

            Text(
                text = "Score: ${anime.mean } | Me: ${anime.myListStatus?.score ?: "-"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Make it slightly gray
            )
        }
    }
}

