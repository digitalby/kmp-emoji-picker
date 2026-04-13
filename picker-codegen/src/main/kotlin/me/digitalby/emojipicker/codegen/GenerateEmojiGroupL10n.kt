package me.digitalby.emojipicker.codegen

import java.io.File

/**
 * Generates per-locale GRP1 binary blobs with translated emoji category
 * labels, sourced from applelocalization.com (EmojiFoundation.framework).
 *
 * Usage: GenerateEmojiGroupL10n <cache-dir> <output-resources-dir>
 *
 * Output: one `<locale>.bin` file per picker-supported locale, each
 * containing 9 Unicode group-id -> localized label entries.
 */
fun main(args: Array<String>) {
    require(args.size == 2) {
        "Usage: GenerateEmojiGroupL10n <cache-dir> <output-resources-dir>"
    }
    val cacheDir = File(args[0])
    val outDir = File(args[1])
    outDir.mkdirs()

    val downloader = AppleGroupDownloader(cacheDir)

    // Apple keys + the English search query we use to hit the fulltext API.
    // Query strings were chosen to return a manageable set; source-exact
    // filter inside AppleGroupDownloader.fetch() removes collisions like
    // "Objects Category Fallback" vs "Objects Category".
    val appleFetches: List<Triple<String, String, String>> = listOf(
        // unicode group id, Apple source key, search query hint
        Triple("people_body", "People Category Fallback", "People"),
        Triple("animals_nature", "Nature Category", "Animals & Nature"),
        Triple("food_drink", "Food & Drink Category", "Food & Drink"),
        Triple("travel_places", "Travel & Places Category", "Travel & Places"),
        Triple("activities", "Activity Category", "Activity"),
        Triple("objects", "Objects Category", "Objects"),
        Triple("symbols", "Symbols Category", "Symbols"),
        Triple("flags", "Flags Category", "Flags"),
    )

    // picker locale id -> Apple language code
    val pickerToApple: Map<String, String> = linkedMapOf(
        "en" to "en",
        "ru" to "ru",
        "uk" to "uk",
        "be" to "ru", // Belarusian absent from Apple; cascade to Russian
        "zh" to "zh_CN",
        "zh-hant" to "zh_TW",
        "es" to "es",
        "fr" to "fr",
        "de" to "de",
        "pt" to "pt_BR",
        "ja" to "ja",
        "ko" to "ko",
        "it" to "it",
        "ar" to "ar",
        "hi" to "hi",
        "tr" to "tr",
        "pl" to "pl",
        "nl" to "nl",
    )

    // Hand-authored translations for Unicode's "Smileys & Emotion" group, which
    // has no Apple equivalent (Apple bundles it into "Smileys & People" under
    // "People Category"). Sourced from common system emoji keyboards and
    // Unicode CLDR naming conventions.
    val smileysEmotionHand: Map<String, String> = linkedMapOf(
        "en" to "Smileys & Emotion",
        "ru" to "Смайлики и эмоции",
        "uk" to "Смайлики та емоції",
        "be" to "Смайлікі і эмоцыі",
        "zh" to "表情与情感",
        "zh-hant" to "表情與情感",
        "es" to "Emoticonos y emociones",
        "fr" to "Smileys et émotions",
        "de" to "Smileys & Emotionen",
        "pt" to "Emoticons e emoções",
        "ja" to "スマイリーと感情",
        "ko" to "이모티콘 및 감정",
        "it" to "Smiley ed emozioni",
        "ar" to "الوجوه المبتسمة والمشاعر",
        "hi" to "स्माइली और भावनाएं",
        "tr" to "İfadeler ve Duygular",
        "pl" to "Buźki i emocje",
        "nl" to "Smileys en emoties",
    )

    println("Generating emoji group l10n blobs from Apple localization data")

    // Fetch each Apple category across all languages; index by (groupId, language).
    val translations: MutableMap<String, MutableMap<String, String>> = HashMap()
    for ((unicodeGroup, appleKey, query) in appleFetches) {
        val rows = downloader.fetch(query, appleKey)
        check(rows.isNotEmpty()) { "No Apple rows for key '$appleKey' (query '$query')" }
        val byLang = HashMap<String, String>(rows.size)
        for (row in rows) {
            byLang.putIfAbsent(row.language, row.target)
        }
        translations[unicodeGroup] = byLang
        println("  fetched $appleKey -> ${byLang.size} languages")
    }

    // Emit one blob per picker locale.
    for ((pickerLocale, appleLocale) in pickerToApple) {
        val entries = linkedMapOf<String, String>()
        entries["smileys_emotion"] = smileysEmotionHand[pickerLocale]
            ?: humanizeGroupId("smileys_emotion")
        for ((unicodeGroup, _, _) in appleFetches) {
            val langMap = translations.getValue(unicodeGroup)
            val label = langMap[appleLocale]
                ?: langMap["en"]
                ?: error("Missing English fallback for $unicodeGroup")
            entries[unicodeGroup] = label
        }
        val blob = GroupBlobWriter.write(entries)
        val outFile = File(outDir, "$pickerLocale.bin")
        outFile.writeBytes(blob)
        println("  wrote ${outFile.name}: ${entries.size} entries, ${blob.size} bytes")
    }
    println("Done.")
}

private fun humanizeGroupId(raw: String): String = raw
    .split('_')
    .joinToString(separator = " & ") { token ->
        token.replaceFirstChar { it.titlecaseChar() }
    }
