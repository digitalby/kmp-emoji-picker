package me.digitalby.emojipicker

import kotlinx.coroutines.test.runTest
import org.kodein.emoji.Emoji
import org.kodein.emoji.allGroups
import org.kodein.emoji.allOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecentEmojiStoreTest {
    private val samples: List<Emoji> by lazy {
        Emoji.allGroups().flatMap { Emoji.allOf(it) }.take(10)
    }

    @Test
    fun recordMovesEmojiToFront() = runTest {
        val store = RecentEmojiStore.inMemory(capacity = 5)
        samples.take(3).forEach { store.record(it) }
        assertEquals(samples[2].details.string, store.recent.value.first().details.string)

        store.record(samples[0])
        assertEquals(samples[0].details.string, store.recent.value.first().details.string)
        assertEquals(3, store.recent.value.size)
    }

    @Test
    fun capacityIsEnforced() = runTest {
        val store = RecentEmojiStore.inMemory(capacity = 3)
        samples.take(5).forEach { store.record(it) }
        assertEquals(3, store.recent.value.size)
        assertEquals(samples[4].details.string, store.recent.value.first().details.string)
    }

    @Test
    fun recordingSameEmojiTwiceKeepsOneEntry() = runTest {
        val store = RecentEmojiStore.inMemory(capacity = 5)
        store.record(samples[0])
        store.record(samples[0])
        assertEquals(1, store.recent.value.size)
    }

    @Test
    fun capacityZeroRetainsNothing() = runTest {
        val store = RecentEmojiStore.inMemory(capacity = 0)
        store.record(samples[0])
        assertTrue(store.recent.value.isEmpty())
    }

    @Test
    fun initialRecentIsEmpty() = runTest {
        val store = RecentEmojiStore.inMemory()
        assertTrue(store.recent.value.isEmpty())
    }
}
