package me.digitalby.emojipicker

import me.digitalby.emojipicker.internal.filterEmojis
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
        assertEquals(subset, filterEmojis(subset, "   "))
    }

    @Test
    fun matchesByDescription() {
        val grinning = sample.first { it.details.description == "grinning face" }
        assertTrue(matchesQuery(grinning, "grinning"))
        assertTrue(matchesQuery(grinning, "GRIN"))
        assertFalse(matchesQuery(grinning, "xyz_no_match"))
    }

    @Test
    fun matchesByAlias() {
        val target = sample.firstOrNull { it.details.aliases.isNotEmpty() }
            ?: error("expected at least one emoji with aliases")
        val alias = target.details.aliases.first()
        assertTrue(matchesQuery(target, alias))
    }

    @Test
    fun filterNarrowsSet() {
        val source = sample.take(500)
        val result = filterEmojis(source, "smile")
        assertTrue(result.isNotEmpty())
        assertTrue(result.size < source.size)
        assertTrue(result.all { matchesQuery(it, "smile") })
    }
}
