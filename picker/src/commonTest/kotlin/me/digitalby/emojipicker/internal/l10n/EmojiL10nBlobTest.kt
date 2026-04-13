package me.digitalby.emojipicker.internal.l10n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EmojiL10nBlobTest {
    @Test
    fun roundTripsSingleEntry() {
        val bytes = blobOf(
            listOf(
                Entry(emoji = "😀", name = "grinning face", keywords = listOf("face", "grin")),
            ),
        )
        val parsed = EmojiL10nBlob.parse(bytes)
        assertEquals(1, parsed.size)
        val got = parsed.getValue("😀")
        assertEquals("grinning face", got.name)
        assertEquals(listOf("face", "grin"), got.keywords)
    }

    @Test
    fun roundTripsCyrillicAndCjk() {
        val bytes = blobOf(
            listOf(
                Entry("😀", "улыбающееся лицо", listOf("лицо", "улыбка")),
                Entry("❤️", "красное сердце", listOf("сердце", "любовь")),
                Entry("笑", "笑脸", listOf("笑", "脸")),
            ),
        )
        val parsed = EmojiL10nBlob.parse(bytes)
        assertEquals(3, parsed.size)
        assertEquals("улыбающееся лицо", parsed.getValue("😀").name)
        assertEquals(listOf("сердце", "любовь"), parsed.getValue("❤️").keywords)
        assertEquals("笑脸", parsed.getValue("笑").name)
    }

    @Test
    fun handlesEmptyKeywordList() {
        val bytes = blobOf(listOf(Entry("🙂", "slight smile", emptyList())))
        val parsed = EmojiL10nBlob.parse(bytes)
        assertTrue(parsed.getValue("🙂").keywords.isEmpty())
    }

    @Test
    fun rejectsBadMagic() {
        val bogus = ByteArray(8) { 0 }
        assertFailsWith<IllegalArgumentException> {
            EmojiL10nBlob.parse(bogus)
        }
    }

    private data class Entry(val emoji: String, val name: String, val keywords: List<String>)

    private fun blobOf(entries: List<Entry>): ByteArray {
        val out = ArrayList<Byte>()
        out.addAll(
            byteArrayOf(
                'E'.code.toByte(),
                'M'.code.toByte(),
                'J'.code.toByte(),
                '1'.code.toByte(),
            ).toList(),
        )
        out.addAll(intBe(entries.size).toList())
        for (entry in entries) {
            writeString(out, entry.emoji)
            writeString(out, entry.name)
            out.add(entry.keywords.size.toByte())
            for (kw in entry.keywords) writeString(out, kw)
        }
        return out.toByteArray()
    }

    private fun writeString(out: ArrayList<Byte>, value: String) {
        val bytes = value.encodeToByteArray()
        out.addAll(shortBe(bytes.size).toList())
        out.addAll(bytes.toList())
    }

    private fun shortBe(value: Int): ByteArray = byteArrayOf(((value ushr 8) and 0xFF).toByte(), (value and 0xFF).toByte())

    private fun intBe(value: Int): ByteArray = byteArrayOf(
        ((value ushr 24) and 0xFF).toByte(),
        ((value ushr 16) and 0xFF).toByte(),
        ((value ushr 8) and 0xFF).toByte(),
        (value and 0xFF).toByte(),
    )
}
