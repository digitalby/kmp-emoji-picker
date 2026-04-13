package me.digitalby.emojipicker.internal.l10n

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.digitalby.emojipicker.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal object EmojiGroupL10n {
    private val cache: MutableMap<String, Map<String, String>> = mutableMapOf()
    private val mutex: Mutex = Mutex()

    @OptIn(ExperimentalResourceApi::class)
    suspend fun load(locale: String): Map<String, String> {
        val resolved = EmojiL10n.resolveLocale(locale)
        cache[resolved]?.let { return it }
        return mutex.withLock {
            cache[resolved]?.let { return@withLock it }
            val bytes = try {
                Res.readBytes("files/emoji-group-l10n/$resolved.bin")
            } catch (t: Throwable) {
                emptyMap<String, String>().also { cache[resolved] = it }
                return@withLock emptyMap()
            }
            val parsed = EmojiGroupL10nBlob.parse(bytes)
            cache[resolved] = parsed
            parsed
        }
    }
}
