package com.otorniko.munanimelista.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.otorniko.munanimelista.data.ListStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditStatusSheet(
    initialStatus: ListStatus?,
    initialScore: Int,
    initialProgress: Int,
    maxEpisodes: Int,
    onDismiss: () -> Unit,
    onSave: (ListStatus, Int, Int) -> Unit,
    onDelete: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(initialStatus ?: ListStatus.PlanToWatch) }
    var score by remember { mutableIntStateOf(initialScore) }
    var progress by remember { mutableIntStateOf(initialProgress) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text("Update Status", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("Status", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ListStatus.entries.forEach { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = {
                        selectedStatus = status

                        if (status == ListStatus.Completed && maxEpisodes > 0) {
                            progress = maxEpisodes
                        }
                    },
                    label = { Text(status.label) }
                )
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 16.dp))
        if (selectedStatus != ListStatus.PlanToWatch) {
            Text("Score: $score", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (score > 0) score-- }) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }
                Slider(
                    value = score.toFloat(),
                    onValueChange = { score = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { score++ }) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))
            if (maxEpisodes != 1) {
                Text(
                    "Progress: $progress / ${if (maxEpisodes > 0) maxEpisodes else "?"}",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (progress > 0) progress-- }) {
                        Text("-", style = MaterialTheme.typography.titleLarge)
                    }
                    Slider(
                        value = progress.toFloat(),
                        onValueChange = { progress = it.toInt() },
                        valueRange = 0f..(if (maxEpisodes > 0) maxEpisodes.toFloat() else 100f),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { progress++ }) {
                        Text("+", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onSave(selectedStatus, score, progress) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (initialStatus != null) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDelete,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Remove from List")
            }
        }
    }
}