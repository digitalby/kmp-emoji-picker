package me.digitalby.emojipicker.codegen

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class CldrDownloader(private val cacheDir: File) {
    private val client: HttpClient = HttpClient
        .newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    init {
        cacheDir.mkdirs()
    }

    fun fetchAnnotations(locale: String): String? = fetch("annotations", locale)

    fun fetchAnnotationsDerived(locale: String): String? = fetch("annotationsDerived", locale)

    private fun fetch(folder: String, locale: String): String? {
        val cacheFile = File(cacheDir, "$folder-$locale.xml")
        if (cacheFile.exists() && cacheFile.length() > 0) {
            return cacheFile.readText(Charsets.UTF_8)
        }
        val url = "https://raw.githubusercontent.com/unicode-org/cldr/" +
            "$CLDR_RELEASE_TAG/common/$folder/$locale.xml"
        val request = HttpRequest
            .newBuilder(URI.create(url))
            .GET()
            .timeout(Duration.ofSeconds(60))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString(Charsets.UTF_8))
        return when (response.statusCode()) {
            200 -> {
                cacheFile.writeText(response.body(), Charsets.UTF_8)
                response.body()
            }
            404 -> null
            else -> error("CLDR fetch failed for $url: HTTP ${response.statusCode()}")
        }
    }
}
