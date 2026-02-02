package com.otorniko.munanimelista.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.otorniko.munanimelista.data.MyListTab
import com.otorniko.munanimelista.data.RankingCategory

@Composable
fun AppDrawerContent(
    onCategoryClick: (RankingCategory) -> Unit,
    onMyListClick: (MyListTab) -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    var isMyListExpanded by remember { mutableStateOf(false) }
    var isBrowseExpanded by remember { mutableStateOf(false) }
    val drawerItemColors = NavigationDrawerItemDefaults.colors(
        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
        unselectedIconColor = MaterialTheme.colorScheme.onSurface
    )

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,

        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Spacer(
                modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Mun Anime Lista",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Your Anime List",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(MaterialTheme.shapes.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onMyListClick(MyListTab.ALL) }
                        .padding(start = 16.dp, end = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("My List")
                }
                IconButton(onClick = { isMyListExpanded = !isMyListExpanded }) {
                    Icon(
                        imageVector =
                            if (isMyListExpanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand"
                    )
                }
            }

            AnimatedVisibility(visible = isMyListExpanded) {
                Column {
                    MyListTab.entries.filter { it != MyListTab.ALL }.forEach { tab ->
                        NavigationDrawerItem(
                            label = { Text(tab.label) },
                            selected = false,
                            onClick = { onMyListClick(tab) },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                .padding(start = 16.dp),
                            colors = drawerItemColors
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(MaterialTheme.shapes.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onCategoryClick(RankingCategory.ALL) }
                        .padding(start = 16.dp, end = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Top Anime",
                    )
                }

                IconButton(
                    onClick = { isBrowseExpanded = !isBrowseExpanded }
                ) {
                    Icon(
                        imageVector =
                            if (isBrowseExpanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                    )
                }
            }

            AnimatedVisibility(
                visible = isBrowseExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    RankingCategory.entries.forEach { category ->
                        NavigationDrawerItem(
                            label = { Text(category.label) },
                            selected = false,
                            onClick = { onCategoryClick(category) },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                .padding(start = 16.dp),
                            colors = drawerItemColors
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Advanced Search (WIP)") },
                selected = false,
                onClick = {
                    Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = drawerItemColors
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Profile (WIP)") },
                selected = false,
                onClick = {
                    Toast.makeText(context, "Profile not ready yet", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = drawerItemColors
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                onClick = onSettingsClick,
                icon = { Icon(Icons.Default.Settings, null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = drawerItemColors
            )
        }
    }
}