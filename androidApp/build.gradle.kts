plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "fr.frankois944.kmpviewmodel.android"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()
    defaultConfig {
        applicationId = "fr.frankois944.kmpviewmodel.android"
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "boolean",
            "IS_PRODUCTION",
            "false",
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_11}"
    }
    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation("io.kotzilla:cloud-inject:0.10.0-Beta3")

    implementation(libs.androidx.compose.activity)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.lifecycle.compose)

    implementation(platform(libs.koin.bom))
    implementation(platform(libs.koin.annotation.bom))
    implementation(libs.bundles.koin.android)
    ksp(platform(libs.koin.annotation.bom))
    ksp(libs.koin.annotation.ksp)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization)
    implementation(libs.kermit)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.kotlin.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(platform(libs.koin.bom))
    androidTestImplementation(libs.koin.test)
}
