package me.digitalby.emojipicker.codegen

import java.io.File

fun main(args: Array<String>) {
    require(args.size == 2) {
        "Usage: GenerateEmojiL10n <cldr-cache-dir> <output-resources-dir>"
    }
    val cacheDir = File(args[0])
    val outDir = File(args[1])
    outDir.mkdirs()

    val downloader = CldrDownloader(cacheDir)
    println("Generating emoji l10n blobs from CLDR $CLDR_RELEASE_TAG")

    for (locale in SUPPORTED_LOCALES) {
        val primaryXml = downloader.fetchAnnotations(locale)
        if (primaryXml == null) {
            System.err.println("  skipped $locale: no annotations")
            continue
        }
        val derivedXml = downloader.fetchAnnotationsDerived(locale)
        val primary = AnnotationsParser.parse(primaryXml)
        val derived = derivedXml?.let { AnnotationsParser.parse(it) } ?: emptyMap()
        val merged = AnnotationsParser.merge(primary, derived)
        val blob = BlobWriter.write(merged)
        val outFile = File(outDir, "${locale.lowercase().replace('_', '-')}.bin")
        outFile.writeBytes(blob)
        println("  wrote ${outFile.name}: ${merged.size} entries, ${blob.size} bytes")
    }
    println("Done.")
}
