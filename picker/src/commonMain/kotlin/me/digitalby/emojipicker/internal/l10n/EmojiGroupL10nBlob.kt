package me.digitalby.emojipicker.internal.l10n

internal object EmojiGroupL10nBlob {
    const val MAGIC: Int = 0x47525031 // "GRP1"

    fun parse(bytes: ByteArray): Map<String, String> {
        require(bytes.size >= 8) { "Group l10n blob too small: ${bytes.size}" }
        val reader = Reader(bytes)
        val magic = reader.readInt()
        require(magic == MAGIC) { "Bad group l10n blob magic: $magic" }
        val count = reader.readInt()
        val result = LinkedHashMap<String, String>(count)
        repeat(count) {
            val groupId = reader.readString16()
            val label = reader.readString16()
            result[groupId] = label
        }
        return result
    }

    fun serialize(entries: Map<String, String>): ByteArray {
        val out = ArrayList<Byte>(64 + entries.size * 32)
        out.writeInt(MAGIC)
        out.writeInt(entries.size)
        for ((groupId, label) in entries) {
            out.writeString16(groupId)
            out.writeString16(label)
        }
        return out.toByteArray()
    }

    private fun MutableList<Byte>.writeInt(value: Int) {
        add(((value ushr 24) and 0xFF).toByte())
        add(((value ushr 16) and 0xFF).toByte())
        add(((value ushr 8) and 0xFF).toByte())
        add((value and 0xFF).toByte())
    }

    private fun MutableList<Byte>.writeShort(value: Int) {
        require(value in 0..0xFFFF) { "short out of range: $value" }
        add(((value ushr 8) and 0xFF).toByte())
        add((value and 0xFF).toByte())
    }

    private fun MutableList<Byte>.writeString16(value: String) {
        val bytes = value.encodeToByteArray()
        writeShort(bytes.size)
        for (b in bytes) add(b)
    }

    private class Reader(private val bytes: ByteArray) {
        private var pos: Int = 0

        fun readInt(): Int {
            val b0 = bytes[pos].toInt() and 0xFF
            val b1 = bytes[pos + 1].toInt() and 0xFF
            val b2 = bytes[pos + 2].toInt() and 0xFF
            val b3 = bytes[pos + 3].toInt() and 0xFF
            pos += 4
            return (b0 shl 24) or (b1 shl 16) or (b2 shl 8) or b3
        }

        fun readShort(): Int {
            val b0 = bytes[pos].toInt() and 0xFF
            val b1 = bytes[pos + 1].toInt() and 0xFF
            pos += 2
            return (b0 shl 8) or b1
        }

        fun readString16(): String {
            val length = readShort()
            val str = bytes.decodeToString(pos, pos + length)
            pos += length
            return str
        }
    }
}
