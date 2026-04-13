package me.digitalby.emojipicker.codegen

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

/**
 * Binary layout (big-endian):
 *
 *   magic    : 4 bytes "EMJ1"
 *   count    : uint32 number of entries
 *   entries  : count records, each:
 *     emojiLen : uint16 (UTF-8 byte length of emoji)
 *     emoji    : UTF-8 bytes
 *     nameLen  : uint16 (UTF-8 byte length of tts name)
 *     name     : UTF-8 bytes
 *     kwCount  : uint8  (number of keywords)
 *     keywords : for each keyword — uint16 length + UTF-8 bytes
 */
internal object BlobWriter {
    private const val MAGIC = "EMJ1"

    fun write(entries: List<AnnotationEntry>): ByteArray {
        val bytes = ByteArrayOutputStream()
        val out = DataOutputStream(bytes)
        out.writeBytes(MAGIC)
        out.writeInt(entries.size)
        for (entry in entries) {
            val emojiBytes = entry.emoji.toByteArray(Charsets.UTF_8)
            require(emojiBytes.size <= UShort.MAX_VALUE.toInt()) {
                "Emoji byte length too large: ${entry.emoji}"
            }
            out.writeShort(emojiBytes.size)
            out.write(emojiBytes)

            val nameBytes = entry.name.toByteArray(Charsets.UTF_8)
            require(nameBytes.size <= UShort.MAX_VALUE.toInt()) {
                "Name byte length too large: ${entry.name}"
            }
            out.writeShort(nameBytes.size)
            out.write(nameBytes)

            val keywords = entry.keywords.take(255)
            out.writeByte(keywords.size)
            for (keyword in keywords) {
                val kwBytes = keyword.toByteArray(Charsets.UTF_8)
                val truncated = if (kwBytes.size > UShort.MAX_VALUE.toInt()) {
                    kwBytes.copyOf(UShort.MAX_VALUE.toInt())
                } else {
                    kwBytes
                }
                out.writeShort(truncated.size)
                out.write(truncated)
            }
        }
        out.flush()
        return bytes.toByteArray()
    }
}
