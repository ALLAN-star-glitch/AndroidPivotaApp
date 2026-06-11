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

        // Version 1.12.0 - Build 22 - Enhanced Booking System & UI Improvements
        // Added: Fixed price booking, search functionality, sticky filters, UI enhancements
        versionCode = 22
        versionName = "1.12.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // Add your signing config here when ready for production
            // storeFile = file("keystore.jks")
            // storePassword = System.getenv("KEYSTORE_PASSWORD")
            // keyAlias = System.getenv("KEY_ALIAS")
            // keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // signingConfig = signingConfigs.getByName("release") // Uncomment when ready
        }

        create("staging") {
            initWith(getByName("debug"))

            versionNameSuffix = "-staging"

            firebaseAppDistribution {
                artifactType = "APK"

                testers = "allanmathenge22@gmail.com, allanmathenge67@gmail.com, allanmathenge319@gmail.com, stepenjuguna9010@gmail.com, s9010901090109010@gmail.com, martinmichuki8@gmail.com, brianmulimuteti@gmail.com, carolkim194@gmail.com, allanmathenge82@gmail.com, janenyambura4272@gmail.com, allaneditor67@gmail.com, kelvijames2023@gmail.com, deniskiplimo816@gmail.com"

                releaseNotes = """
================================================================
          PIVOTACONNECT v1.12.0 - ENHANCED BOOKING SYSTEM
================================================================

This release completes the booking system with FIXED price 
support, adds search functionality, and improves the UI with 
sticky filters and better navigation.

================================================================
BOOKING SYSTEM ENHANCEMENTS
================================================================

FIXED PRICE BOOKING SUPPORT
- Added proper handling for FIXED and PER_SESSION price units
- Fixed duration field validation for non-hourly services
- Corrected API request format for FIXED price bookings
- Duration fields now sent as null for FIXED price services
- Eliminated "durationHours must not be less than 0.5" error

PER_HOUR BOOKING IMPROVEMENTS
- Minimum duration validation (0.5 hours)
- Support for half-hour increments
- Proper Int type conversion for backend API
- Real-time price calculation for hourly services

BOOKING FEE MANAGEMENT
- Booking fees correctly applied to all booking types
- Refundable status displayed prominently
- Separate line item in price breakdown
- Accurate grand total calculation

PRICE NEGOTIATION
- Fixed negotiation flow for FIXED price services
- Real-time validation against professional's range
- Original price shown with strikethrough when negotiated
- Automatic total recalculation

================================================================
UI/UX IMPROVEMENTS
================================================================

SEARCH FUNCTIONALITY
- Added search icon to main header
- Sticky search bar appears when scrolling
- Voice search support (microphone icon)
- Real-time search filtering
- Clear search button for easy reset

STICKY FILTERS
- Category pills now stick to top when scrolling
- Enhanced visual design with improved shadows
- Smooth transition animations
- Better color contrast and selection states
- Close icon on selected filters for quick removal

FILTER PILL ENHANCEMENTS
- Rounded pill design with icons
- Selected state shows filled color with white text
- Hover and click animations
- Shadow elevation on selection
- Border stroke for unselected state

HEADER IMPROVEMENTS
- Added search icon alongside theme toggle
- Combined notification badge with message count
- Improved profile menu animation
- Better responsive layout for different screen sizes
- Smooth sticky header behavior

================================================================
SCREEN ENHANCEMENTS
================================================================

DISCOVER SCREEN
- Sticky filter section with search bar
- Improved scroll performance
- Dynamic service grid with 4-6 columns based on screen size
- Better loading states and error handling
- Animated content transitions

PROFESSIONAL SERVICE BOOKING SCREEN
- Fixed validation logic for FIXED price services
- Improved error messages for duration fields
- Better handling of null duration values
- Enhanced form state management
- Smooth step transitions

================================================================
BUG FIXES
================================================================

CRITICAL FIXES
- Fixed FIXED price booking error (durationHours validation)
- Corrected duration field types (Int vs Double)
- Fixed price calculation for non-hourly services
- Resolved negotiation price validation issues
- Fixed booking fee calculation errors

UI FIXES
- Fixed keyboard covering input fields
- Resolved scroll conflicts in booking form
- Corrected date picker display on older devices
- Fixed theme switching animation glitches
- Resolved navigation bar overlapping content

PERFORMANCE FIXES
- Reduced recompositions in booking flow
- Optimized image loading in grids
- Improved LazyColumn scrolling performance
- Fixed memory leaks in date picker
- Optimized network requests

================================================================
CODE IMPROVEMENTS
================================================================

TYPE SAFETY
- Changed durationHours from Double? to Int? for API compatibility
- Added proper null handling for duration fields
- Improved type conversions in ViewModel
- Enhanced data class definitions

STATE MANAGEMENT
- Better handling of loading states
- Improved error state recovery
- Optimized UI state updates
- Reduced unnecessary recompositions

NETWORK LAYER
- Improved request/response logging
- Better error message parsing
- Enhanced token refresh handling
- Optimized retry logic

================================================================
TESTING & VALIDATION
================================================================

BOOKING SCENARIOS TESTED
- FIXED price booking (Tire Change & Wheel Alignment)
- PER_HOUR booking with 0.5 hour minimum
- PER_DAY booking with daily rates
- Negotiated price booking
- Booking fee scenarios

EDGE CASES
- Empty duration fields
- Invalid date selection
- Network failures during booking
- Token expiration handling
- Concurrent booking attempts

================================================================
KNOWN ISSUES
================================================================

- Payment processing not yet implemented (v1.13.0)
- Push notifications for booking updates pending
- Email confirmation system in progress
- Professional availability calendar enhancement pending
- Booking cancellation flow coming soon

================================================================
COMING IN V1.13.0
================================================================

- Escrow payment integration
- Professional counter-offer system
- Booking confirmation with payment
- My Bookings management screen
- Push notifications for all booking events
- SMS notifications for critical updates
- Availability calendar enhancements
- Booking history and receipts

================================================================
HOW TO TEST UPDATES
================================================================

1. TEST FIXED PRICE BOOKING
   - Find a FIXED price service (e.g., Tire Change)
   - Complete booking without entering duration
   - Verify no "durationHours" error appears
   - Check booking is created successfully

2. TEST SEARCH FUNCTIONALITY
   - Scroll down on Discover screen
   - Sticky search bar should appear
   - Type to filter services
   - Use microphone for voice search

3. TEST STICKY FILTERS
   - Scroll through Discover screen
   - Category pills should stick to top
   - Tap filters to see selection states
   - Clear filters with close icon

4. TEST BOOKING VALIDATION
   - Try booking with invalid duration (0 hours)
   - Should show validation error
   - Try booking without date selection
   - Should prompt for required fields

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.12.0!
We appreciate your feedback and continued support.

================================================================
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