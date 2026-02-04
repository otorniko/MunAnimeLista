package com.otorniko.munanimelista.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.otorniko.munanimelista.data.AnimeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
        title: String,
        viewModel: AnimeViewModel,
        onOpenDrawer: () -> Unit,
        onAnimeClick: (Int) -> Unit
                ) {
    val animeList by viewModel.browseList.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }
    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                                                                               ),
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = onOpenDrawer) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                                      )
            },
            floatingActionButton = {
                if (showScrollToTop) {
                    FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ) {
                        Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Scroll to Top"
                            )
                    }
                }
            }
            ) { padding ->
        Surface(
                modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
               ) {
            Column(
                    modifier = Modifier
                            .fillMaxSize()
                  ) {
                LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f)
                          ) {
                    itemsIndexed(animeList) { index, anime ->
                        AnimeRow(anime = anime, onClick = { onAnimeClick(anime.id) })
                        if (index >= animeList.lastIndex && !viewModel.isBrowseLoading) {
                            LaunchedEffect(Unit) {
                                viewModel.loadMoreBrowse()
                            }
                        }
                    }
                    if (viewModel.isBrowseLoading) {
                        item {
                            Box(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                    contentAlignment = Alignment.Center
                               ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}