plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

val generatedResourcesDir =
    rootProject
        .layout
        .projectDirectory
        .dir("picker/src/commonMain/composeResources/files/emoji-l10n")

val generatedGroupResourcesDir =
    rootProject
        .layout
        .projectDirectory
        .dir("picker/src/commonMain/composeResources/files/emoji-group-l10n")

val cldrCacheDir = layout.buildDirectory.dir("cldr")
val appleCacheDir = layout.buildDirectory.dir("apple")

tasks.register<JavaExec>("generateEmojiL10n") {
    group = "codegen"
    description = "Download CLDR annotations for the configured locales and emit binary l10n blobs."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("me.digitalby.emojipicker.codegen.GenerateEmojiL10nKt")
    args =
        listOf(
            cldrCacheDir.get().asFile.absolutePath,
            generatedResourcesDir.asFile.absolutePath,
        )
    outputs.dir(generatedResourcesDir)
    outputs.cacheIf { true }
}

tasks.register<JavaExec>("generateEmojiGroupL10n") {
    group = "codegen"
    description =
        "Download Apple EmojiFoundation category translations and emit binary group l10n blobs."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("me.digitalby.emojipicker.codegen.GenerateEmojiGroupL10nKt")
    args =
        listOf(
            appleCacheDir.get().asFile.absolutePath,
            generatedGroupResourcesDir.asFile.absolutePath,
        )
    outputs.dir(generatedGroupResourcesDir)
    outputs.cacheIf { true }
}
