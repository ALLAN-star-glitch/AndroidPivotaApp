plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Kotlin serialization plugin for type safe routes and navigation arguments
    kotlin("plugin.serialization") version "2.0.21"

    alias(libs.plugins.ksp)

    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.pivota"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pivota"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    val nav_version = "2.9.0"
    val room_version = "2.8.4"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //For Navigation - Jetpack Compose
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Testing Navigation.
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    //Navigation Suite
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite-android:1.3.0-beta01")

    //For Adaptability
    implementation("androidx.compose.material3.adaptive:adaptive:1.2.0-alpha04")
    implementation ("androidx.compose.material3.adaptive:adaptive-layout:1.2.0-alpha04")
    implementation ("androidx.compose.material3.adaptive:adaptive-navigation:1.2.0-alpha04")

    //Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8")

    //Coil for images
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    
    // Add this line to get FavoriteBorder and others
    implementation("androidx.compose.material:material-icons-extended")

    implementation("com.google.dagger:hilt-android:2.54")
    ksp("com.google.dagger:hilt-android-compiler:2.54")



    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${room_version}")




}