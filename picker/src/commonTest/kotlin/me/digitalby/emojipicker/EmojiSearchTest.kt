package me.digitalby.emojipicker

import me.digitalby.emojipicker.internal.filterEmojis
import me.digitalby.emojipicker.internal.l10n.LocalizedEntry
import me.digitalby.emojipicker.internal.matchesQuery
import org.kodein.emoji.Emoji
import org.kodein.emoji.allGroups
import org.kodein.emoji.allOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmojiSearchTest {
    private val sample: List<Emoji> by lazy {
        Emoji.allGroups().flatMap { Emoji.allOf(it) }
    }

    @Test
    fun emptyQueryReturnsAll() {
        val subset = sample.take(50)
        assertEquals(subset, filterEmojis(subset, ""))
    }

    @Test
    fun whitespaceOnlyQueryReturnsAll() {
        val subset = sample.take(50)
        assertEquals(subset, filterEmojis(subset, "   "))
        assertEquals(subset, filterEmojis(subset, "\t\n"))
    }

    @Test
    fun descriptionMatchIsCaseInsensitive() {
        val grinning = sample.first { it.details.description == "grinning face" }
        assertTrue(matchesQuery(grinning, "grinning"))
        assertTrue(matchesQuery(grinning, "GRIN"))
        assertTrue(matchesQuery(grinning, "GriNnInG"))
    }

    @Test
    fun queryIsTrimmedAndLowercased() {
        val grinning = sample.first { it.details.description == "grinning face" }
        val result = filterEmojis(listOf(grinning), "  GRINNING  ")
        assertEquals(1, result.size)
    }

    @Test
    fun noMatchReturnsEmpty() {
        val subset = sample.take(50)
        assertTrue(filterEmojis(subset, "zzzzzznonexistentqqqqq").isEmpty())
    }

    @Test
    fun matchesByAliasCaseInsensitive() {
        val target = sample.firstOrNull { it.details.aliases.isNotEmpty() }
            ?: error("expected at least one emoji with aliases")
        val alias = target.details.aliases.first()
        assertTrue(matchesQuery(target, alias))
        assertTrue(matchesQuery(target, alias.uppercase()))
    }

    @Test
    fun filterNarrowsSet() {
        val source = sample.take(500)
        val result = filterEmojis(source, "smile")
        assertTrue(result.isNotEmpty())
        assertTrue(result.size < source.size)
        assertTrue(result.all { matchesQuery(it, "smile") })
    }

    @Test
    fun nonMatchingEmojiExcluded() {
        val grinning = sample.first { it.details.description == "grinning face" }
        assertFalse(matchesQuery(grinning, "automobile"))
    }

    @Test
    fun localizedNameMatches() {
        val grinning = sample.first { it.details.description == "grinning face" }
        val entry = LocalizedEntry(name = "улыбающееся лицо", keywords = listOf("лицо", "улыбка"))
        assertTrue(matchesQuery(grinning, "улыба", entry))
        assertTrue(matchesQuery(grinning, "УЛЫБА", entry))
    }

    @Test
    fun localizedKeywordMatches() {
        val grinning = sample.first { it.details.description == "grinning face" }
        val entry = LocalizedEntry(name = "笑脸", keywords = listOf("笑", "脸", "高兴"))
        assertTrue(matchesQuery(grinning, "笑", entry))
        assertTrue(matchesQuery(grinning, "高兴", entry))
        assertFalse(matchesQuery(grinning, "哭", entry))
    }

    @Test
    fun localizedEntryIsOptional() {
        val grinning = sample.first { it.details.description == "grinning face" }
        assertTrue(matchesQuery(grinning, "grin", localized = null))
    }

    @Test
    fun filterEmojisAppliesLocalizedMap() {
        val grinning = sample.first { it.details.description == "grinning face" }
        val heart = sample.first { it.details.description.contains("red heart") }
        val source = listOf(grinning, heart)
        val map = mapOf(
            grinning.details.string to LocalizedEntry("улыбающееся лицо", listOf("улыбка")),
            heart.details.string to LocalizedEntry("красное сердце", listOf("сердце", "любовь")),
        )
        val matches = filterEmojis(source, "сердце", map)
        assertEquals(listOf(heart), matches)
    }
}
