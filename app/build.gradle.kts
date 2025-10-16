plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // REMOVED: alias(libs.plugins.kotlin.parcelize) - It's already included in the kotlin-android plugin
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.justbaatai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.justbaatai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        viewBinding = true
    }
}

dependencies {
    // Core & UI bundle (includes core-ktx, appcompat, material, constraintlayout, etc.)
    implementation(libs.bundles.androidx.essentials)

    // Lifecycle & Navigation bundles
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.navigation.fragment)

    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room for Local Database (if you use it)
    // implementation(libs.bundles.room)
    // ksp(libs.androidx.room.compiler)

    // Splash Screen (now from the version catalog)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)
}