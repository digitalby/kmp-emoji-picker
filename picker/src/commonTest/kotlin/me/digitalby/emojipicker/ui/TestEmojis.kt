package me.digitalby.emojipicker.ui

import me.digitalby.emojipicker.internal.EmojiCatalog
import org.kodein.emoji.Emoji
import org.kodein.emoji.SkinTone1Emoji
import org.kodein.emoji.SkinTone2Emoji
import org.kodein.emoji.allGroups
import org.kodein.emoji.allOf

internal object TestEmojis {
    val allGroupNames: List<String> by lazy {
        Emoji.allGroups().filter { it != "Component" }
    }
    val all: List<Emoji> by lazy { allGroupNames.flatMap { Emoji.allOf(it) } }
    val plain: Emoji by lazy { all.first { it !is SkinTone1Emoji && it !is SkinTone2Emoji } }
    val plain2: Emoji by lazy {
        all.asSequence()
            .filter { it !is SkinTone1Emoji && it !is SkinTone2Emoji }
            .drop(1)
            .first()
    }
    val tonable1: Emoji by lazy { all.first { it is SkinTone1Emoji && it !is SkinTone2Emoji } }
    val tonable2: Emoji by lazy { all.first { it is SkinTone2Emoji } }

    val smallCatalog: EmojiCatalog by lazy {
        val first = allGroupNames.first()
        val second = allGroupNames.getOrNull(1) ?: first
        EmojiCatalog(
            groups = listOf(first, second),
            emojisByGroup = mapOf(
                first to Emoji.allOf(first).take(6),
                second to Emoji.allOf(second).take(6),
            ),
        )
    }
}
