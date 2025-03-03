import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import kotlin.jvm.java

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {

    explicitApi()

    androidTarget {
        // https://youtrack.jetbrains.com/issue/KT-66448
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // iosX64() // uncomment if you're working on mac intel
    iosArm64()
    iosSimulatorArm64()

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.annotation.bom))
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin.kmp)
            api(libs.androidx.room.runtime)
            implementation(libs.kotlin.serialization)
            implementation(libs.kotlin.coroutines)
            implementation(libs.sqlite.bundled)
        }

        commonTest.dependencies {
        }

        androidMain.dependencies {
        }

        iosMain.dependencies {
        }
    }
}

android {
    namespace = "fr.frankois944.kmpviewmodel.database"
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
    // Koin Annotation
    add("kspCommonMainMetadata", project.dependencies.platform(libs.koin.annotation.bom))
    add("kspCommonMainMetadata", libs.koin.annotation.ksp)
    add("kspAndroid", project.dependencies.platform(libs.koin.annotation.bom))
    add("kspAndroid", libs.koin.annotation.ksp)
    add("kspIosSimulatorArm64", project.dependencies.platform(libs.koin.annotation.bom))
    add("kspIosSimulatorArm64", libs.koin.annotation.ksp)
//    add("kspIosX64", libs.koin.annotation.ksp) // uncomment if you're working on mac intel
    add("kspIosArm64", project.dependencies.platform(libs.koin.annotation.bom))
    add("kspIosArm64", libs.koin.annotation.ksp)
    // Room
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
//    add("kspIosX64", libs.androidx.room.compiler) // uncomment if you're working on mac intel
    add("kspIosArm64", libs.androidx.room.compiler)
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

room {
    schemaDirectory("$projectDir/schemas")
}
