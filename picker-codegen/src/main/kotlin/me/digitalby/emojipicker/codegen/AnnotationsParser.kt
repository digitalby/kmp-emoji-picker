package me.digitalby.emojipicker.codegen

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

internal data class AnnotationEntry(
    val emoji: String,
    val name: String,
    val keywords: List<String>,
)

internal object AnnotationsParser {
    private val factory: SAXParserFactory = SAXParserFactory.newInstance().apply {
        isNamespaceAware = false
        setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        setFeature("http://xml.org/sax/features/external-general-entities", false)
        setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    }

    fun parse(xml: String): Map<String, RawAnnotation> {
        val handler = Handler()
        val parser = factory.newSAXParser()
        val reader = parser.xmlReader
        reader.contentHandler = handler
        reader.setEntityResolver { _, _ -> InputSource(StringReader("")) }
        reader.parse(InputSource(StringReader(xml)))
        return handler.result
    }

    fun merge(
        primary: Map<String, RawAnnotation>,
        derived: Map<String, RawAnnotation>,
    ): List<AnnotationEntry> {
        val merged = LinkedHashMap<String, RawAnnotation>(primary.size + derived.size)
        for ((key, value) in derived) {
            merged[key] = value
        }
        for ((key, value) in primary) {
            val existing = merged[key]
            if (existing == null) {
                merged[key] = value
            } else {
                merged[key] = RawAnnotation(
                    tts = value.tts ?: existing.tts,
                    keywords = (value.keywords + existing.keywords).distinct(),
                )
            }
        }
        return merged
            .mapNotNull { (cp, raw) ->
                val name = raw.tts ?: return@mapNotNull null
                AnnotationEntry(
                    emoji = cp,
                    name = name,
                    keywords = raw.keywords,
                )
            }
            .sortedBy { it.emoji }
    }

    internal data class RawAnnotation(
        val tts: String?,
        val keywords: List<String>,
    )

    private class Handler : DefaultHandler() {
        val result: MutableMap<String, RawAnnotation> = LinkedHashMap()
        private var currentCp: String? = null
        private var currentType: String? = null
        private val buffer = StringBuilder()

        override fun startElement(
            uri: String?,
            localName: String?,
            qName: String?,
            attributes: Attributes?,
        ) {
            if (qName == "annotation") {
                currentCp = attributes?.getValue("cp")
                currentType = attributes?.getValue("type")
                buffer.setLength(0)
            }
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
            if (currentCp != null && ch != null) {
                buffer.append(ch, start, length)
            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if (qName != "annotation") return
            val cp = currentCp ?: return
            val text = buffer.toString().trim()
            val existing = result[cp] ?: RawAnnotation(tts = null, keywords = emptyList())
            val updated = if (currentType == "tts") {
                existing.copy(tts = text)
            } else {
                val kws = text
                    .split('|')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                existing.copy(keywords = (existing.keywords + kws).distinct())
            }
            result[cp] = updated
            currentCp = null
            currentType = null
            buffer.setLength(0)
        }
    }
}
