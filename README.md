# kmp-emoji-picker

A Compose Multiplatform emoji picker. Works on Android, iOS, JVM/Desktop, and wasmJs from a single codebase.

## Why

Research in April 2026 confirmed there is no Compose Multiplatform emoji picker. [kosi-libs/Emoji.kt](https://github.com/kosi-libs/Emoji.kt) provides the data layer and renderers across all CMP targets but does not ship a picker UI. Every existing "compose emoji picker" on GitHub (androidx.emoji2.emojipicker, Abhimanyu14/compose-emoji-picker, vanniktech/Emoji, and several others) is Android-only.

This library is a thin UI layer on top of `org.kodein.emoji:emoji-compose-m3`, giving you:

- Category tabs backed by CLDR emoji groups
- Searchable grid with description + shortcode + emoticon matching
- Skin tone selector for `SkinTone1Emoji` and `SkinTone2Emoji`
- Pluggable `RecentEmojiStore` (in-memory default, bring your own persistence)
- Noto SVG fallback on Wasm and Desktop where system fonts lack glyphs (inherited from kosi-libs)

## Install

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// picker/build.gradle.kts (commonMain)
dependencies {
    implementation("me.digitalby:kmp-emoji-picker:0.1.0")
}
```

## Usage

```kotlin
import me.digitalby.emojipicker.EmojiPicker
import me.digitalby.emojipicker.rememberEmojiPickerState

@Composable
fun MyScreen(onInsert: (String) -> Unit) {
    val state = rememberEmojiPickerState()
    EmojiPicker(
        state = state,
        onEmojiSelected = { emoji -> onInsert(emoji.details.string) },
    )
}
```

## Targets

- Android (`compileSdk = 35`, `minSdk = 24`)
- iOS (`iosX64`, `iosArm64`, `iosSimulatorArm64`)
- JVM (Desktop)
- wasmJs (browser)

## License

MIT
