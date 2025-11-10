plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // REMOVED: alias(libs.plugins.kotlin.parcelize) - It's already included in the kotlin-android plugin
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.justbaat.mindoro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.justbaat.mindoro"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
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
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.code.gson:gson:2.10.1")
    // Coil for image loading
    implementation("io.coil-kt:coil:2.5.0")

    // Your existing dependencies...
    implementation ("androidx.core:core-ktx:1.12.0")

}