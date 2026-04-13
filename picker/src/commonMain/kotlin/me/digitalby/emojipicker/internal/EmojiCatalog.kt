package me.digitalby.emojipicker.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
