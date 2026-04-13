package me.digitalby.emojipicker.internal.l10n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EmojiGroupL10nBlobTest {
    @Test
    fun roundTripNineGroups() {
        val source = linkedMapOf(
            "smileys_emotion" to "Smileys & Emotion",
            "people_body" to "People",
            "animals_nature" to "Animals & Nature",
            "food_drink" to "Food & Drink",
            "travel_places" to "Travel & Places",
            "activities" to "Activity",
            "objects" to "Objects",
            "symbols" to "Symbols",
            "flags" to "Flags",
        )
        val bytes = EmojiGroupL10nBlob.serialize(source)
        val parsed = EmojiGroupL10nBlob.parse(bytes)
        assertEquals(source, parsed)
    }

    @Test
    fun cyrillicAndCjkRoundTrip() {
        val source = linkedMapOf(
            "people_body" to "Люди",
            "smileys_emotion" to "表情与情感",
            "flags" to "العلم",
        )
        val parsed = EmojiGroupL10nBlob.parse(EmojiGroupL10nBlob.serialize(source))
        assertEquals(source, parsed)
    }

    @Test
    fun emptyBlobParses() {
        val bytes = EmojiGroupL10nBlob.serialize(emptyMap())
        assertEquals(emptyMap(), EmojiGroupL10nBlob.parse(bytes))
    }

    @Test
    fun badMagicThrows() {
        val bytes = EmojiGroupL10nBlob.serialize(mapOf("a" to "b"))
        bytes[0] = 0
        assertFailsWith<IllegalArgumentException> { EmojiGroupL10nBlob.parse(bytes) }
    }

    @Test
    fun tooSmallThrows() {
        assertFailsWith<IllegalArgumentException> {
            EmojiGroupL10nBlob.parse(ByteArray(4))
        }
    }

    @Test
    fun preservesOrder() {
        val source = linkedMapOf("c" to "C", "a" to "A", "b" to "B")
        val parsed = EmojiGroupL10nBlob.parse(EmojiGroupL10nBlob.serialize(source))
        assertTrue(parsed is LinkedHashMap<String, String>)
        assertEquals(listOf("c", "a", "b"), parsed.keys.toList())
    }
}
