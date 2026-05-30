import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.appdistribution")
}

android {
    namespace = "com.example.pivota"
    compileSdk = 36

    kotlin {
        jvmToolchain(17)
    }

    defaultConfig {
        applicationId = "com.example.pivota"
        minSdk = 24
        targetSdk = 36

        // Version Management - Increment for each release
        // Version 1.1.0 - Build 11 - Shimmer Animation Enhancement
        versionCode = 11
        versionName = "1.1.1"

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

        create("staging") {
            initWith(getByName("debug"))

            versionNameSuffix = "-staging"

            // Firebase App Distribution Configuration
            // Distribution Method: APK uploaded via Gradle task
            // Command to distribute: ./gradlew assembleStaging appDistributionUploadStaging
            firebaseAppDistribution {
                // Distribution group: Internal Testers
                // All testers have been pre-added to Firebase Console
                artifactType = "APK"

                // Tester emails - Internal testing team
                testers = "allanmathenge22@gmail.com, allanmathenge67@gmail.com, allanmathenge319@gmail.com, stepenjuguna9010@gmail.com, s9010901090109010@gmail.com, martinmichuki8@gmail.com, brianmulimuteti@gmail.com, carolkim194@gmail.com, allanmathenge82@gmail.com, janenyambura4272@gmail.com, allaneditor67@gmail.com, kelvijames2023@gmail.com, deniskiplimo816@gmail.com"

                // Release notes - Updated for version 1.1.1
                releaseNotes = """
PivotaConnect v1.1.1 (Build 11)

NEW: ENHANCED SHIMMER ANIMATIONS
- Added prominent shimmer effects to loading skeletons
- Improved visibility of loading states across all screens
- Implemented colored shimmer that matches category themes
- Added shimmer effect to ServiceOfferingsScreen loading state
- Added shimmer effect to SubcategoriesScreen loading state
- Optimized animation timing for smoother visual feedback

ENHANCEMENTS:
- Shimmer now uses higher alpha values (0.8) for better visibility
- Added colored shimmer for icon placeholders matching pillar colors
- Implemented custom Modifier.shimmerEffect() for reusable animations
- Grid skeletons now adapt to screen size (3-12 items based on columns)
- Improved loading state UX with visual feedback

UI IMPROVEMENTS:
- Service offerings skeleton cards now show proper shimmer
- Subcategories grid skeleton matches actual layout structure
- Circle avatars in skeletons show colored shimmer
- Text placeholders show neutral white/gray shimmer
- Animation duration optimized to 1000ms for noticeable effect

TECHNICAL UPDATES:
- Created prominentShimmerEffect() modifier function
- Created coloredShimmerEffect() for themed animations
- Implemented SubcategoriesLoadingSkeleton composable
- Added ServiceOfferingsLoadingSkeleton with shimmer
- Proper onGloballyPositioned usage for size-aware gradients
- Optimized infiniteTransition for smooth animations

BUG FIXES:
- Fixed shimmer not being visible due to low alpha values
- Fixed gradient not updating during animation
- Fixed shimmer not covering entire element width
- Fixed memory leaks in animation composition

PREVIOUS FEATURES (still available):
- Post professional services with 5-step wizard
- Dynamic pricing based on category
- Set working hours per day
- AM/PM time picker with manual input
- Dark/Light theme support
- Tablet optimized layout
- Success dialog with Lottie animation
- Professional profile detection
- Enhanced snackbar with action buttons
                """.trimIndent()
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    //implementation(libs.androidx.compose.remote.creation.compose)

    val nav_version = "2.9.0"
    val room_version = "2.8.4"
    val ktorVersion = "3.0.0"

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

    // Navigation
    implementation("androidx.navigation:navigation-compose:$nav_version")
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite-android:1.3.0-beta01")

    // Adaptability
    implementation("androidx.compose.material3.adaptive:adaptive:1.2.0-alpha04")
    implementation("androidx.compose.material3.adaptive:adaptive-layout:1.2.0-alpha04")
    implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.2.0-alpha04")

    // Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8")

    // Coil
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    implementation("androidx.compose.material:material-icons-extended")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:${room_version}")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.datastore:datastore:1.2.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    // Ktor
    implementation("io.ktor:ktor-client-android:${ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("io.ktor:ktor-client-logging:${ktorVersion}")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Credential Manager (as per documentation)
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.0")

    // Source: https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.1")

    implementation("com.airbnb.android:lottie-compose:6.4.0")

    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))

    implementation("com.google.firebase:firebase-analytics")

    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")  // For codegen (no reflection)
}