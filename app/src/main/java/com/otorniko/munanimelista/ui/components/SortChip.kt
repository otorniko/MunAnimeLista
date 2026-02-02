package com.otorniko.munanimelista.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.otorniko.munanimelista.ui.theme.BrandDarkBlue
import com.otorniko.munanimelista.ui.theme.Transparent

@Composable
fun SortChip(
    label: String,
    isSelected: Boolean,
    isAscending: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        // 1. Remove border when selected
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Transparent else MaterialTheme.colorScheme.outlineVariant,
            borderWidth = 1.dp
        ),

        // 2. Add a soft background fill when selected
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BrandDarkBlue.copy(alpha = 0.1f),
            selectedLabelColor = BrandDarkBlue, // Dark blue text
            selectedLeadingIconColor = BrandDarkBlue,
            containerColor = BrandDarkBlue.copy(alpha = 0.1f),
            labelColor = BrandDarkBlue
            //labelColor = BrandDarkBlue
        ),
        shape = CircleShape
    )
}