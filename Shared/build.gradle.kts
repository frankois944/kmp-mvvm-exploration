import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

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
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // iOS
    listOf(
        // iosX64(), // uncomment if you're working on mac intel
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "Shared"
            isStatic = false
            export(libs.kermit.simple)
            export(libs.androidx.lifecycle.viewmodel)
            binaryOption("bundleId", "fr.frankois944.kmpviewmodel.shared")
        }
    }

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.database)
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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.okio.fakefilesystem)
        }
        iosMain.dependencies {
            api(libs.kermit.simple)
        }
        androidMain.dependencies {
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

tasks.withType(KotlinCompilationTask::class.java).configureEach {
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
