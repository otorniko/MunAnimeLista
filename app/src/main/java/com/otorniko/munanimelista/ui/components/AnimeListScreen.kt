package com.otorniko.munanimelista.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.otorniko.munanimelista.data.AnimeViewModel
import com.otorniko.munanimelista.data.ListStatus
import com.otorniko.munanimelista.data.MyListTab
import com.otorniko.munanimelista.data.SortType
import com.otorniko.munanimelista.ui.theme.BrandDarkBlue
import com.otorniko.munanimelista.ui.theme.BrandLightBlue
import com.otorniko.munanimelista.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(
    viewModel: AnimeViewModel = viewModel(),
    onAnimeClick: (Int) -> Unit,
    initialTab: MyListTab = MyListTab.ALL,
    onOpenDrawer: () -> Unit
) {
    val animeList by viewModel.visibleList.collectAsState()
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val tabs = listOf("All") + ListStatus.entries.map { it.label }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(initialTab) {
        viewModel.filterByStatus(initialTab.status)
    }
    Scaffold(
        containerColor = BrandLightBlue,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BrandDarkBlue,
                    titleContentColor = White,
                    navigationIconContentColor = White,
                    actionIconContentColor = White
                ),
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchText,
                            onValueChange = { /* ... */ },
                            placeholder = { Text("Search...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )
                    } else {
                        Text(
                            "Mun Anime Lista",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    if (isSearching) {
                        IconButton(onClick = {
                            isSearching = false
                            searchText = ""
                            viewModel.clearSearch()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Search")
                        }
                    } else {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        },
        bottomBar = {
            val currentFilter by viewModel.currentFilter.collectAsState()
            val selectedTabIndex = if (currentFilter == null) {
                0
            } else {
                ListStatus.entries.indexOf(currentFilter) + 1
            }
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = BrandDarkBlue,
                contentColor = White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            val newFilter = if (index == 0) null else ListStatus.entries[index - 1]
                            viewModel.filterByStatus(newFilter)
                        },
                        text = { Text(title) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                SortChip(
                    label = "Title",
                    isSelected = viewModel.currentSortType == SortType.TITLE,
                    isAscending = viewModel.isSortAscending,
                    onClick = { viewModel.onSortClicked(SortType.TITLE) }
                )

                SortChip(
                    label = "Score",
                    isSelected = viewModel.currentSortType == SortType.SCORE,
                    isAscending = viewModel.isSortAscending,
                    onClick = { viewModel.onSortClicked(SortType.SCORE) }
                )
                SortChip(
                    label = "Last Updated",
                    isSelected = viewModel.currentSortType == SortType.LAST_UPDATED,
                    isAscending = viewModel.isSortAscending,
                    onClick = { viewModel.onSortClicked(SortType.LAST_UPDATED) }
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(animeList) { anime ->
                    AnimeRow(
                        anime = anime,
                        onClick = {
                            onAnimeClick(anime.id)
                        })
                }
            }
        }
    }
}
