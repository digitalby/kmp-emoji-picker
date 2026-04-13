package me.digitalby.emojipicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.digitalby.emojipicker.internal.CategoryTabs
import me.digitalby.emojipicker.internal.EmojiGrid
import me.digitalby.emojipicker.internal.SearchField
import me.digitalby.emojipicker.internal.buildCategories
import me.digitalby.emojipicker.internal.filterEmojis
import me.digitalby.emojipicker.internal.l10n.EmojiL10n
import me.digitalby.emojipicker.internal.l10n.LocalizedEntry
import me.digitalby.emojipicker.internal.l10n.rememberCurrentInputLocale
import me.digitalby.emojipicker.internal.rememberEmojiCatalog
import me.digitalby.emojipicker.internal.selectSourceEmojis
import org.kodein.emoji.Emoji
import org.kodein.emoji.compose.EmojiService

public object EmojiPickerDefaults {
    public const val DEFAULT_COLUMNS: Int = 8
    public const val RECENT_TAB_LABEL: String = "Recent"
}

@Composable
public fun EmojiPicker(
    onEmojiSelected: (Emoji) -> Unit,
    modifier: Modifier = Modifier,
    state: EmojiPickerState = rememberEmojiPickerState(),
    columns: Int = EmojiPickerDefaults.DEFAULT_COLUMNS,
    showSearch: Boolean = true,
    showRecent: Boolean = true,
) {
    LaunchedEffect(Unit) { EmojiService.initialize() }

    val catalog by rememberEmojiCatalog()
    val recent by state.recentStore.recent.collectAsState()
    val scope = rememberCoroutineScope()

    val categories = remember(catalog.groups, showRecent, recent.isNotEmpty()) {
        buildCategories(
            groups = catalog.groups,
            showRecent = showRecent,
            hasRecent = recent.isNotEmpty(),
            recentLabel = EmojiPickerDefaults.RECENT_TAB_LABEL,
        )
    }

    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && categories.none { it.id == state.selectedCategory }) {
            state.selectedCategory = categories.first().id
        }
    }

    val sourceEmojis by remember(catalog, state.selectedCategory, recent) {
        derivedStateOf { selectSourceEmojis(catalog, recent, state.selectedCategory) }
    }

    val inputLocale = rememberCurrentInputLocale()
    val localizedEntries by produceState<Map<String, LocalizedEntry>?>(
        initialValue = null,
        key1 = inputLocale,
    ) {
        value = EmojiL10n.load(inputLocale)
    }

    val visibleEmojis by remember(sourceEmojis, state.query, localizedEntries) {
        derivedStateOf { filterEmojis(sourceEmojis, state.query, localizedEntries) }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (showSearch) {
                SearchField(query = state.query, onQueryChange = { state.query = it })
            }
            CategoryTabs(
                categories = categories,
                selected = state.selectedCategory,
                onSelect = { state.selectedCategory = it },
                modifier = Modifier.fillMaxWidth(),
            )
            EmojiGrid(
                emojis = visibleEmojis,
                state = state,
                columns = columns,
                onEmojiSelected = { selected ->
                    scope.launch { state.recentStore.record(selected) }
                    onEmojiSelected(selected)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 240.dp),
            )
        }
    }
}
