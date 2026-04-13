package me.digitalby.emojipicker.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import org.kodein.emoji.Emoji
import org.kodein.emoji.allGroups
import org.kodein.emoji.allOf

internal data class EmojiCatalog(
    val groups: List<String>,
    val emojisByGroup: Map<String, List<Emoji>>,
) {
    companion object {
        val Empty = EmojiCatalog(emptyList(), emptyMap())
    }
}

private const val COMPONENT_GROUP = "Component"

@Composable
internal fun rememberEmojiCatalog(): State<EmojiCatalog> = produceState(initialValue = EmojiCatalog.Empty) {
    value = withContext(Dispatchers.Default) {
        val groups = Emoji.allGroups().filter { it != COMPONENT_GROUP }
        val map = groups.associateWith { group -> Emoji.allOf(group) }
        EmojiCatalog(groups, map)
    }
}

internal fun buildCategories(
    groups: List<String>,
    showRecent: Boolean,
    hasRecent: Boolean,
    recentLabel: String,
): List<CategoryEntry> = buildList {
    if (showRecent && hasRecent) {
        add(CategoryEntry(RECENT_CATEGORY_ID, recentLabel))
    }
    groups.forEach { add(CategoryEntry(it, it)) }
}

internal fun selectSourceEmojis(
    catalog: EmojiCatalog,
    recent: List<Emoji>,
    selectedCategory: String,
): List<Emoji> = when (selectedCategory) {
    RECENT_CATEGORY_ID -> recent
    else -> catalog.emojisByGroup[selectedCategory].orEmpty()
}
