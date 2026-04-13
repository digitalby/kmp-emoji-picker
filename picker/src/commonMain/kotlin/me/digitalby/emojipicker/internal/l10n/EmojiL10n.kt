package me.digitalby.emojipicker.internal.l10n

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.digitalby.emojipicker.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal object EmojiL10n {
    internal val SUPPORTED_LOCALES: Set<String> = linkedSetOf(
        "en",
        "ru",
        "uk",
        "be",
        "zh",
        "zh-hant",
        "es",
        "fr",
        "de",
        "pt",
        "ja",
        "ko",
        "it",
        "ar",
        "hi",
        "tr",
        "pl",
        "nl",
    )

    private val cache: MutableMap<String, Map<String, LocalizedEntry>> = mutableMapOf()
    private val mutex: Mutex = Mutex()

    @OptIn(ExperimentalResourceApi::class)
    suspend fun load(locale: String): Map<String, LocalizedEntry> {
        val resolved = resolveLocale(locale)
        cache[resolved]?.let { return it }
        return mutex.withLock {
            cache[resolved]?.let { return@withLock it }
            val bytes = Res.readBytes("files/emoji-l10n/$resolved.bin")
            val parsed = EmojiL10nBlob.parse(bytes)
            cache[resolved] = parsed
            parsed
        }
    }

    fun resolveLocale(requested: String): String {
        val normalized = requested.lowercase().replace('_', '-')
        if (normalized in SUPPORTED_LOCALES) return normalized
        val primary = normalized.substringBefore('-')
        if (primary == "zh") {
            val isTraditional = normalized.contains("hant") ||
                normalized.contains("-tw") ||
                normalized.contains("-hk") ||
                normalized.contains("-mo")
            return if (isTraditional) "zh-hant" else "zh"
        }
        if (primary in SUPPORTED_LOCALES) return primary
        return "en"
    }
}
