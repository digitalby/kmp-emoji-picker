package me.digitalby.emojipicker.codegen

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

/**
 * Binary layout (big-endian), must stay in sync with
 * `picker/src/commonMain/.../EmojiGroupL10nBlob.kt`:
 *
 *   magic    : 4 bytes "GRP1"
 *   count    : uint32
 *   entries  : count records, each:
 *     groupIdLen : uint16
 *     groupId    : UTF-8 bytes
 *     labelLen   : uint16
 *     label      : UTF-8 bytes
 */
internal object GroupBlobWriter {
    private const val MAGIC = "GRP1"

    fun write(entries: Map<String, String>): ByteArray {
        val bytes = ByteArrayOutputStream()
        val out = DataOutputStream(bytes)
        out.writeBytes(MAGIC)
        out.writeInt(entries.size)
        for ((groupId, label) in entries) {
            val idBytes = groupId.toByteArray(Charsets.UTF_8)
            require(idBytes.size <= UShort.MAX_VALUE.toInt()) {
                "Group id byte length too large: $groupId"
            }
            out.writeShort(idBytes.size)
            out.write(idBytes)
            val labelBytes = label.toByteArray(Charsets.UTF_8)
            require(labelBytes.size <= UShort.MAX_VALUE.toInt()) {
                "Label byte length too large: $label"
            }
            out.writeShort(labelBytes.size)
            out.write(labelBytes)
        }
        out.flush()
        return bytes.toByteArray()
    }
}
