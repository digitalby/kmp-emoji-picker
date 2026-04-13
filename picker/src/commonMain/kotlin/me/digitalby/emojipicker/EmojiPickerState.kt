package me.digitalby.emojipicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.kodein.emoji.Emoji
import org.kodein.emoji.SkinTone
import org.kodein.emoji.SkinTone1Emoji
import org.kodein.emoji.SkinTone2Emoji

public const val RECENT_CATEGORY_ID: String = "__recent__"

public class EmojiPickerState internal constructor(
    public val recentStore: RecentEmojiStore,
    initialCategory: String,
    initialQuery: String,
    initialSkinTone: SkinTone?,
) {
    public var query: String by mutableStateOf(initialQuery)
    public var selectedCategory: String by mutableStateOf(initialCategory)
    public var preferredSkinTone: SkinTone? by mutableStateOf(initialSkinTone)

    public fun resolveWithSkinTone(emoji: Emoji): Emoji {
        val tone = preferredSkinTone ?: return emoji
        return when (emoji) {
            is SkinTone2Emoji -> emoji.withSkinTone(tone, tone)
            is SkinTone1Emoji -> emoji.withSkinTone(tone)
            else -> emoji
        }
    }

    public companion object {
        internal fun Saver(recentStore: RecentEmojiStore): Saver<EmojiPickerState, *> = listSaver(
            save = { state ->
                listOf(state.selectedCategory, state.query, state.preferredSkinTone?.name ?: "")
            },
            restore = { list ->
                val toneName = list[2] as String
                EmojiPickerState(
                    recentStore = recentStore,
                    initialCategory = list[0] as String,
                    initialQuery = list[1] as String,
                    initialSkinTone = SkinTone.entries.firstOrNull { it.name == toneName },
                )
            },
        )
    }
}

@Composable
public fun rememberEmojiPickerState(
    recentStore: RecentEmojiStore = remember { RecentEmojiStore.inMemory() },
    initialCategory: String = RECENT_CATEGORY_ID,
    initialSkinTone: SkinTone? = null,
): EmojiPickerState = rememberSaveable(saver = EmojiPickerState.Saver(recentStore)) {
    EmojiPickerState(
        recentStore = recentStore,
        initialCategory = initialCategory,
        initialQuery = "",
        initialSkinTone = initialSkinTone,
    )
}
