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

        // Version 1.5.0 - Build 15 - Enhanced Category Search with Full-Screen Bottom Sheet
        versionCode = 15
        versionName = "1.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Enable R8 for APK size reduction
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("staging") {
            initWith(getByName("debug"))

            versionNameSuffix = "-staging"

            firebaseAppDistribution {
                artifactType = "APK"

                testers = "allanmathenge22@gmail.com, allanmathenge67@gmail.com, allanmathenge319@gmail.com, stepenjuguna9010@gmail.com, s9010901090109010@gmail.com, martinmichuki8@gmail.com, brianmulimuteti@gmail.com, carolkim194@gmail.com, allanmathenge82@gmail.com, janenyambura4272@gmail.com, allaneditor67@gmail.com, kelvijames2023@gmail.com, deniskiplimo816@gmail.com"

                releaseNotes = """
PivotaConnect v1.5.0 (Build 15)

ENHANCED CATEGORY SEARCH WITH FULL-SCREEN BOTTOM SHEET

This release significantly improves the category selection experience with a redesigned bottom sheet and powerful search functionality.

CATEGORY SELECTION IMPROVEMENTS:

1. FULL-SCREEN BOTTOM SHEET
   - Bottom sheet now opens in full screen by default
   - Provides more space to view and search categories
   - Can still be pulled down to dismiss
   - Drag handle remains for intuitive dismissal

2. NATURAL LANGUAGE SEARCH
   - Fuzzy matching - finds categories even with partial matches
   - Example: "Arch" finds "Architects", "Commercial Architects", "Landscape Architects"
   - Multiple word search (order doesn't matter)
   - Intelligent word boundary detection

3. SEARCH ENHANCEMENTS
   - Real-time filtering as you type
   - Clear button to reset search
   - "No matching categories found" message for empty results
   - Relevance sorting (exact matches appear first)

4. USER EXPERIENCE IMPROVEMENTS
   - Larger search bar with better visibility
   - Improved visual hierarchy
   - Faster category discovery
   - Reduced cognitive load when selecting categories

TECHNICAL IMPLEMENTATION:

- Fuzzy search algorithm with word prefix matching
- Multi-word search support
- Relevance-based sorting
- Full-screen ModalBottomSheet configuration
- Performance-optimized filtering with remember

BEFORE (v1.4.0):
- Bottom sheet showed only 3/4 of screen
- Basic contains-only search
- No relevance sorting
- Limited visibility for long category lists

AFTER (v1.5.0):
- Full-screen bottom sheet (100% height)
- Natural language fuzzy search
- Relevance-sorted results
- Better category discovery experience

USER BENEFITS:

- Find categories faster with flexible search
- See more categories at once with full-screen view
- Type naturally without worrying about exact wording
- Reduced time to select a category

BUG FIXES:

- Fixed bottom sheet initial height issues
- Improved search performance with large category lists
- Fixed keyboard covering search results
- Better handling of special characters in search

TESTING INSTRUCTIONS:

To test enhanced category search:
1. Go to Post Service screen
2. Tap on Category dropdown
3. Bottom sheet opens full screen
4. Type partial category names (e.g., "arch")
5. Observe fuzzy matching results
6. Try multi-word searches (e.g., "repair air")
7. Results are sorted by relevance

PERFORMANCE IMPROVEMENTS:
- Search filtering is memoized for performance
- LazyColumn for efficient scrolling
- Stable keys for list items

MIGRATION NOTES:
- No database migration required
- No breaking changes
- Fully backward compatible with v1.4.0

UPCOMING IN v1.6.0:
- Category icons in dropdown
- Recent categories section
- Favorite/pinned categories
- Category suggestions based on user history

Thank you for testing PivotaConnect v1.5.0 with enhanced category search!
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

    // Coil - Optimized for performance
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

    // Credential Manager
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.0")

    // JWT
    implementation("com.auth0:java-jwt:4.5.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Moshi
    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")
}