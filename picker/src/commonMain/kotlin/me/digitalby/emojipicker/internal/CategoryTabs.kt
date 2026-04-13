package me.digitalby.emojipicker.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal data class CategoryEntry(val id: String, val label: String)

@Composable
internal fun CategoryTabs(
    categories: List<CategoryEntry>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.height(56.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = categories, key = { it.id }) { cat ->
            FilterChip(
                selected = cat.id == selected,
                onClick = { onSelect(cat.id) },
                label = { Text(cat.label) },
            )
        }
    }
}
