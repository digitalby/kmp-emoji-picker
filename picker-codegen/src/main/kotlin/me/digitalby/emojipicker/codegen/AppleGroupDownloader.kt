package me.digitalby.emojipicker.codegen

import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * Fetches emoji category translations from https://applelocalization.com,
 * which mirrors macOS/iOS framework .strings files. The backing source is
 * `EmojiFoundation.framework/Localizable.loctable` on iOS 26.
 *
 * Responses are cached to disk so the codegen is reproducible offline once
 * the cache is warm.
 */
internal class AppleGroupDownloader(private val cacheDir: File) {
    private val client: HttpClient = HttpClient
        .newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    init {
        cacheDir.mkdirs()
    }

    /**
     * Returns rows from the search API filtered to only those whose `source`
     * equals [exactKey]. Key collisions between the fuzzy fulltext search and
     * siblings like `Objects Category Fallback` are removed here.
     */
    fun fetch(searchQuery: String, exactKey: String): List<AppleRow> {
        val slug = searchQuery.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
        val cacheFile = File(cacheDir, "$slug.json")
        val body = if (cacheFile.exists() && cacheFile.length() > 0) {
            cacheFile.readText(Charsets.UTF_8)
        } else {
            val encodedQ = URLEncoder.encode(searchQuery, Charsets.UTF_8)
            val url = "https://applelocalization.com/api/ios/search" +
                "?q=$encodedQ&b=EmojiFoundation.framework&size=200"
            val request = HttpRequest
                .newBuilder(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(60))
                .header("User-Agent", "kmp-emoji-picker-codegen/1.0")
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString(Charsets.UTF_8))
            check(response.statusCode() == 200) {
                "Apple l10n fetch failed for $url: HTTP ${response.statusCode()}"
            }
            cacheFile.writeText(response.body(), Charsets.UTF_8)
            response.body()
        }
        return parseRows(body).filter { it.source == exactKey }
    }

    /**
     * Minimal JSON parser for the fixed shape the applelocalization.com API
     * returns. We avoid pulling in a dependency — the response is an object
     * with a `data` array of flat records keyed by `source`, `target`,
     * `language`, `file_name`, `bundle_name`.
     */
    private fun parseRows(body: String): List<AppleRow> {
        val result = ArrayList<AppleRow>()
        // Locate the "data" array.
        val dataStart = body.indexOf("\"data\":[")
        if (dataStart == -1) return emptyList()
        var i = dataStart + "\"data\":[".length
        while (i < body.length) {
            val c = body[i]
            if (c == ']') break
            if (c != '{') {
                i++
                continue
            }
            val objEnd = findObjectEnd(body, i)
            val obj = body.substring(i, objEnd + 1)
            val source = extractJsonString(obj, "source")
            val target = extractJsonString(obj, "target")
            val language = extractJsonString(obj, "language")
            if (source != null && target != null && language != null) {
                result.add(AppleRow(source = source, target = target, language = language))
            }
            i = objEnd + 1
        }
        return result
    }

    private fun findObjectEnd(body: String, start: Int): Int {
        var depth = 0
        var i = start
        var inString = false
        while (i < body.length) {
            val c = body[i]
            if (inString) {
                if (c == '\\') {
                    i += 2
                    continue
                }
                if (c == '"') inString = false
                i++
                continue
            }
            when (c) {
                '"' -> inString = true
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return i
                }
            }
            i++
        }
        error("Unterminated JSON object starting at $start")
    }

    private fun extractJsonString(obj: String, key: String): String? {
        val needle = "\"$key\":\""
        val start = obj.indexOf(needle)
        if (start == -1) return null
        val valueStart = start + needle.length
        val sb = StringBuilder()
        var i = valueStart
        while (i < obj.length) {
            val c = obj[i]
            if (c == '\\' && i + 1 < obj.length) {
                val esc = obj[i + 1]
                when (esc) {
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    '/' -> sb.append('/')
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    'r' -> sb.append('\r')
                    'b' -> sb.append('\b')
                    'f' -> sb.append('\u000c')
                    'u' -> {
                        val hex = obj.substring(i + 2, i + 6)
                        sb.append(hex.toInt(16).toChar())
                        i += 4
                    }
                    else -> sb.append(esc)
                }
                i += 2
                continue
            }
            if (c == '"') return sb.toString()
            sb.append(c)
            i++
        }
        return null
    }
}

internal data class AppleRow(
    val source: String,
    val target: String,
    val language: String,
)
