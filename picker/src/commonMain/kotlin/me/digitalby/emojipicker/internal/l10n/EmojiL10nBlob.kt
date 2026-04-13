package me.digitalby.emojipicker.internal.l10n

internal object EmojiL10nBlob {
    private const val MAGIC: Int = 0x454D4A31 // "EMJ1"

    fun parse(bytes: ByteArray): Map<String, LocalizedEntry> {
        val reader = Reader(bytes)
        val magic = reader.readInt()
        require(magic == MAGIC) { "Bad emoji l10n blob magic: $magic" }
        val count = reader.readInt()
        val result = HashMap<String, LocalizedEntry>(count)
        repeat(count) {
            val emoji = reader.readString16()
            val name = reader.readString16()
            val kwCount = reader.readUByte()
            val keywords = if (kwCount == 0) {
                emptyList()
            } else {
                ArrayList<String>(kwCount).also { list ->
                    repeat(kwCount) { list.add(reader.readString16()) }
                }
            }
            result[emoji] = LocalizedEntry(name = name, keywords = keywords)
        }
        return result
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

        fun readUByte(): Int {
            val b = bytes[pos].toInt() and 0xFF
            pos += 1
            return b
        }

        fun readString16(): String {
            val length = readShort()
            val str = bytes.decodeToString(pos, pos + length)
            pos += length
            return str
        }
    }
}
