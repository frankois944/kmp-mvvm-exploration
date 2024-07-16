import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.skie)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinParcelize)
}

kotlin {
    // every class, method, property must declare there visibility
    explicitApi()

    // Android
    androidTarget {
        // https://youtrack.jetbrains.com/issue/KT-66448
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // iOS
    listOf(
        // iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(libs.kermit.simple)
            export(libs.androidx.lifecycle.viewmodel)
            binaryOption("bundleId", "fr.frankois944.kmpviewmodel.shared")
        }
    }

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }

    sourceSets {
        commonMain.configure {
            compilerOptions {
                // https://kotlinlang.org/docs/native-objc-interop.html#provide-documentation-with-kdoc-comments
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
        commonMain.dependencies {
            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.serialization)
            implementation(libs.kotlin.datetime)
            implementation(project.dependencies.platform(libs.koin.annotation.bom))
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin.kmp)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.collection)
            implementation(libs.okio)
            implementation(libs.kermit)
            implementation(libs.kermit.koin)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.cloud.inject)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.okio.fakefilesystem)
        }
        iosMain.dependencies {
            implementation(libs.bundles.koin.kmp)
            api(libs.kermit.simple)
        }
        androidMain.dependencies {
            implementation(libs.bundles.koin.android)
        }
    }
}

android {
    namespace = "fr.frankois944.kmpviewmodel"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    add("kspCommonMainMetadata", project.dependencies.platform(libs.koin.annotation.bom))
    add("kspCommonMainMetadata", libs.koin.annotation.ksp)
}

tasks.withType<KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

ksp {
    // https://insert-koin.io/docs/reference/koin-annotations/start#compile-safety---check-your-koin-config-at-compile-time-since-130
    arg("KOIN_CONFIG_CHECK", "true")
}

kover {
    useJacoco()
    reports {
        filters {
            excludes {
                packages("org.koin.ksp.generated")
            }
        }
    }
}

skie {
    features {
        // https://skie.touchlab.co/features/flows-in-swiftui
        enableSwiftUIObservingPreview = true
        // https://skie.touchlab.co/features/combine
        enableFutureCombineExtensionPreview = true
        enableFlowCombineConvertorPreview = true
    }
}
