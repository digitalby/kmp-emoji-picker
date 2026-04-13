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

val cldrCacheDir = layout.buildDirectory.dir("cldr")

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
