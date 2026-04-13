package me.digitalby.emojipicker

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.kodein.emoji.Emoji

public interface RecentEmojiStore {
    public val recent: StateFlow<List<Emoji>>

    public suspend fun record(emoji: Emoji)

    public companion object {
        public fun inMemory(capacity: Int = 32): RecentEmojiStore = InMemoryRecentEmojiStore(capacity)
    }
}

private class InMemoryRecentEmojiStore(private val capacity: Int) : RecentEmojiStore {
    private val state = MutableStateFlow<List<Emoji>>(emptyList())
    override val recent: StateFlow<List<Emoji>> = state

    override suspend fun record(emoji: Emoji) {
        state.update { current ->
            val key = emoji.details.string
            val without = current.filter { it.details.string != key }
            (listOf(emoji) + without).take(capacity)
        }
    }
}
