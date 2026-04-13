package me.digitalby.emojipicker.internal

import me.digitalby.emojipicker.internal.l10n.LocalizedEntry
import org.kodein.emoji.Emoji

internal fun matchesQuery(
    emoji: Emoji,
    queryLower: String,
    localized: LocalizedEntry? = null,
): Boolean {
    if (queryLower.isEmpty()) return true
    val details = emoji.details
    if (details.description.contains(queryLower, ignoreCase = true)) return true
    if (details.aliases.any { it.contains(queryLower, ignoreCase = true) }) return true
    if (details.emoticons.any { it.contains(queryLower, ignoreCase = false) }) return true
    if (localized != null) {
        if (localized.name.contains(queryLower, ignoreCase = true)) return true
        if (localized.keywords.any { it.contains(queryLower, ignoreCase = true) }) return true
    }
    return false
}

internal fun filterEmojis(
    source: List<Emoji>,
    query: String,
    localized: Map<String, LocalizedEntry>? = null,
): List<Emoji> {
    if (query.isBlank()) return source
    val q = query.trim().lowercase()
    return source.filter { emoji ->
        matchesQuery(emoji, q, localized?.get(emoji.details.string))
    }
}
