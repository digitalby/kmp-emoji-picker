package me.digitalby.emojipicker.codegen

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnnotationsParserTest {
    @Test
    fun parsesPrimaryAndKeywords() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ldml>
              <annotations>
                <annotation cp="😀">face | grin | grinning face</annotation>
                <annotation cp="😀" type="tts">grinning face</annotation>
                <annotation cp="❤️">heart | love</annotation>
                <annotation cp="❤️" type="tts">red heart</annotation>
              </annotations>
            </ldml>
        """.trimIndent()
        val parsed = AnnotationsParser.parse(xml)
        assertEquals(2, parsed.size)
        assertEquals("grinning face", parsed["😀"]?.tts)
        assertEquals(listOf("face", "grin", "grinning face"), parsed["😀"]?.keywords)
    }

    @Test
    fun mergePrefersPrimaryTtsOverDerived() {
        val primary = AnnotationsParser.parse(
            """
            <ldml><annotations>
              <annotation cp="🙂">smile</annotation>
              <annotation cp="🙂" type="tts">slight smile</annotation>
            </annotations></ldml>
            """.trimIndent(),
        )
        val derived = AnnotationsParser.parse(
            """
            <ldml><annotations>
              <annotation cp="🙂">face | happy</annotation>
              <annotation cp="🙂" type="tts">wrong name</annotation>
            </annotations></ldml>
            """.trimIndent(),
        )
        val merged = AnnotationsParser.merge(primary, derived)
        assertEquals(1, merged.size)
        assertEquals("slight smile", merged[0].name)
        assertTrue(merged[0].keywords.containsAll(listOf("smile", "face", "happy")))
    }

    @Test
    fun writerRoundTripsWithReader() {
        val entries = listOf(
            AnnotationEntry("😀", "улыбающееся лицо", listOf("лицо", "улыбка")),
            AnnotationEntry("❤️", "красное сердце", listOf("сердце", "любовь")),
            AnnotationEntry("笑", "笑脸", listOf("笑", "脸", "高兴")),
        )
        val blob = BlobWriter.write(entries)
        // Sanity check header: "EMJ1" + 4-byte count
        assertEquals('E'.code.toByte(), blob[0])
        assertEquals('M'.code.toByte(), blob[1])
        assertEquals('J'.code.toByte(), blob[2])
        assertEquals('1'.code.toByte(), blob[3])
        assertEquals(3, blob[7].toInt())
    }
}
