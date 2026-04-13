plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.roborazzi)
}

kotlin {
    explicitApi()

    androidTarget {
        publishLibraryVariants("release")
    }
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            api(libs.kodein.emoji.kt)
            api(libs.kodein.emoji.compose.m3)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.roborazzi.compose.desktop)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.robolectric)
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.core)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
                implementation("androidx.compose.ui:ui-test-manifest:1.8.0")
            }
        }
    }
}

android {
    namespace = "me.digitalby.emojipicker"
    compileSdk =
        libs.versions.android.compile.sdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.min.sdk
                .get()
                .toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "me.digitalby.emojipicker.generated.resources"
    generateResClass = auto
}

// Force the detected input locale for the unit-test task so screenshot goldens
// are reproducible across developer machines. CI runners are en_US already,
// but a developer's macOS regional override (e.g. en_US@rg=plzzzz) and active
// Cocoa input source can otherwise leak through rememberCurrentInputLocale()
// and drift the goldens.
tasks.withType<Test>().configureEach {
    jvmArgumentProviders.add(
        CommandLineArgumentProvider {
            listOf("-Demojipicker.forceLocale=en")
        },
    )
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/digitalby/kmp-emoji-picker")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
