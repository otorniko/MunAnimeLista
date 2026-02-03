package com.otorniko.munanimelista.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.otorniko.munanimelista.data.AnimeViewModel
import com.otorniko.munanimelista.data.ListStatus
import com.otorniko.munanimelista.data.MyListTab
import com.otorniko.munanimelista.data.SortType
import com.otorniko.munanimelista.ui.theme.BrandDarkBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(
    viewModel: AnimeViewModel = viewModel(),
    onAnimeClick: (Int) -> Unit,
    initialTab: MyListTab = MyListTab.ALL,
    onOpenDrawer: () -> Unit
) {
    val animeList by viewModel.visibleList.collectAsState()

    val searchState by viewModel.searchState.collectAsState()
    val isSearchOpen by viewModel.isSearchBarVisible.collectAsState()

    val tabs = listOf("All") + ListStatus.entries.map { it.label }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }
    LaunchedEffect(isSearchOpen) {
        if (isSearchOpen) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    LaunchedEffect(initialTab) {
        viewModel.filterByStatus(initialTab.status)
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
                title = {
                    if (isSearchOpen) {
                        BasicTextField(
                            value = searchState,
                            onValueChange = { newTextFieldValue ->
                                viewModel.onSearchStateChange(newTextFieldValue)
                                viewModel.search(newTextFieldValue.text)
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),

                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            decorationBox = { innerTextField ->
                                Row(

                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (searchState.text.isEmpty()) {
                                            Text(
                                                text = "Search...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            }
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
                    if (isSearchOpen) {
                        IconButton(onClick = {
                            viewModel.clearSearch()
                            viewModel.toggleSearchBar(false)
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Search")
                        }
                    } else {
                        IconButton(onClick = { viewModel.toggleSearchBar(true) }) {
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
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.navigationBarsPadding()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            val newFilter = if (index == 0) null else ListStatus.entries[index - 1]
                            viewModel.filterByStatus(newFilter)
                        },
                        text = {
                            Surface(
                                shape = CircleShape,
                                color = if (selectedTabIndex == index) Color.White else Color.Transparent,
                                contentColor = if (selectedTabIndex == index) BrandDarkBlue else Color.White
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
            }
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
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
        Column(
            modifier = Modifier
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
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                items(animeList) { anime ->
                    AnimeRow(
                        anime = anime,
                        onClick = {
                            onAnimeClick(anime.id)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
    }
}
