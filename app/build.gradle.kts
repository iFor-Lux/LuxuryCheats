plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    alias(libs.plugins.google.services)
}

// Standardize to JVM 17 for better compatibility

android {
    namespace = "com.luxury.cheats"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.luxury.cheats"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // resConfigs("es", "en") // Alternative if needed
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        // 🔥 Lint estricto (Google-level)
        abortOnError = false
        warningsAsErrors = true
        checkReleaseBuilds = false // Deshabilitado por bug de compatibilidad en Kotlin 2.1
        disable += "NullSafeMutableLiveData"
    }

    packaging {
        resources {
            excludes += "**/.agent/**"
            excludes += "**/.agents/**"
            excludes += "**/.cursor/**"
            excludes += "**/agents.md"
            excludes += "**/architecture.md"
            excludes += "**/firebase.md"
            excludes += "**/project_memory.md"
            excludes += "**/readme.md"
            excludes += "**/license"
            excludes += "**/gradlew-fix.ps1"
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    autoCorrect = true

    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    // Excluir carpetas de tests
    source.setFrom(files("src/main/java", "src/main/kotlin"))
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
}

dependencies {

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.graphics.shapes)
    
    // Backdrop library (local module)
    implementation(project(":backdrop"))

    // Debug only (NO se va a release)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.4.0")
}
