package com.otorniko.munanimelista

// EXPLICIT IMPORTS FOR MATERIAL 3
// --------------------------------
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.otorniko.munanimelista.data.AnimeNode
import com.otorniko.munanimelista.data.AnimeViewModel
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
    // We inject the ViewModel here.
    // This 'viewModel()' function magically finds the existing instance
    // or creates a new one if it doesn't exist.
    viewModel: AnimeViewModel = viewModel()
) {
    // 1. OBSERVE: We "subscribe" to the data stream.
    // collectAsState() is like useSelector() in Redux.
    val animeList by viewModel.animeList.collectAsState()

    // 2. UI: Purely declarative now. No logic inside.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mun Anime Lista") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(animeList) { anime ->
                AnimeRow(anime = anime)
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

        Text(
            text = displayTitle, // Use our smart variable here
            style = MaterialTheme.typography.bodyLarge
        )
    }
}