package me.digitalby.emojipicker.internal

import org.kodein.emoji.Emoji

internal fun matchesQuery(emoji: Emoji, queryLower: String): Boolean {
    if (queryLower.isEmpty()) return true
    val details = emoji.details
    if (details.description.contains(queryLower, ignoreCase = true)) return true
    if (details.aliases.any { it.contains(queryLower, ignoreCase = true) }) return true
    if (details.emoticons.any { it.contains(queryLower, ignoreCase = false) }) return true
    return false
}

internal fun filterEmojis(source: List<Emoji>, query: String): List<Emoji> {
    if (query.isBlank()) return source
    val q = query.trim().lowercase()
    return source.filter { matchesQuery(it, q) }
}
