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

        // Version 1.9.0 - Build 19 - Booking Bottom Sheet & Service Coverage Areas
        versionCode = 19
        versionName = "1.9.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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
PivotaConnect v1.9.0 (Build 19)

BOOKING BOTTOM SHEET & SERVICE COVERAGE AREAS

This release introduces a completely new booking flow with a modern bottom sheet design and enhances service offerings with multi-area coverage support.

NEW FEATURES:

1. BOOKING BOTTOM SHEET
   • Brand new booking interface that slides up from bottom
   • Clean, modern design with smooth animations
   • Real-time price calculation based on duration
   • Responsive layout for both mobile and tablet
   • Modal bottom sheet with drag-to-dismiss
   • Price breakdown card showing calculation details
   • Comprehensive validation before submission

2. BOOKING FORM COMPONENTS
   • Date picker with calendar icon and formatted display
   • Dynamic duration fields (hours/days based on pricing unit)
   • Location dropdown for multiple service areas
   • Multi-line notes field for special requests
   • Real-time total price updates as user enters duration
   • Disabled submit button until required fields are filled
   • Cancel and Confirm action buttons

3. SERVICE COVERAGE AREAS (Backend Integration)
   • Services can now be offered in multiple locations
   • Coverage areas displayed as list in service details
   • First area shown with +X badge for additional areas
   • Location dropdown in booking sheet for area selection
   • Validation ensures selected location is within coverage

4. SERVICE OFFERING DETAILS ENHANCEMENTS
   • Display all coverage areas in Service Details section
   • Location card shows primary area with badge
   • Improved visual hierarchy for service areas
   • Better handling of multiple locations

5. PRICE CALCULATION IMPROVEMENTS
   • Automatic total price calculation based on duration
   • Support for PER_HOUR and PER_DAY pricing units
   • Price breakdown card shows calculation formula
   • FIXED price services skip duration field
   • Currency formatting with proper KES display

6. FORM VALIDATION
   • Date selection required
   • Duration required for hourly/daily services
   • Real-time validation feedback
   • Submit button enabled only when all required fields filled
   • Clear error states and hints

7. UI/UX IMPROVEMENTS
   • Drag handle at top for easy dismissal
   • Rounded corners (12-16dp) throughout
   • Subtle shadows for depth
   • Proper spacing and padding
   • Loading states for submission
   • Success/error snackbar feedback

8. TABLET OPTIMIZATION
   • Bottom sheet centered with max-width 600dp on tablets
   • Properly scaled form fields
   • Optimized layout for larger screens
   • Consistent spacing across devices

TECHNICAL IMPLEMENTATION:

• ModalBottomSheet with custom drag handle
• State management for form fields
• Real-time price calculation with derived state
• Comprehensive input validation
• Responsive design using WindowSizeClass
• Proper keyboard handling
• Scrollable content for smaller screens

BOOKING FLOW:

1. User taps "Book Service" button on service details screen
2. Bottom sheet slides up with booking form
3. User selects date and duration (if applicable)
4. User optionally selects location (if multiple coverage areas)
5. User adds optional notes
6. User reviews price breakdown
7. User taps "Confirm Booking" to submit
8. Success/failure feedback shown via snackbar

SERVICE COVERAGE AREAS:

Services can now be offered in multiple cities/neighborhoods:
• Coverage areas stored as JSON array in backend
• Displayed in service details as comma-separated list
• First area shown in location card with +X badge
• Booking sheet validates selected location
• Improves service discoverability by area

BENEFITS:

• Faster booking process with inline form
• Clear price transparency with breakdown
• Reduced form abandonment with validation
• Better tablet experience with centered sheet
• Support for multi-location service providers
• Improved conversion for hourly/daily services

TESTING INSTRUCTIONS:

To test Booking Bottom Sheet:
1. Navigate to any service offering details
2. Tap "Book Service" button at bottom
3. Verify bottom sheet slides up smoothly
4. Test date picker functionality
5. For hourly services, enter hours and verify price updates
6. For daily services, enter days and verify price updates
7. Add notes in optional field
8. Test submit button (enabled/disabled states)
9. Cancel and verify sheet dismisses

To test Coverage Areas:
1. View service offering with multiple coverage areas
2. Verify all areas listed in Service Details section
3. Open booking bottom sheet
4. Location dropdown should show all coverage areas
5. Select different location and verify it's saved

To test Price Calculation:
1. Open booking sheet for hourly service
2. Enter 2 hours → total price should be 2 × base price
3. Enter 5 hours → total price updates automatically
4. Price breakdown card shows calculation
5. FIXED price services should show no duration field

To test Tablet Layout:
1. Run on tablet or large-screen device
2. Bottom sheet should be centered with max 600dp width
3. All form fields properly sized
4. Date picker and dropdowns work correctly

BUG FIXES:

• Fixed location display in service details
• Improved text contrast in booking form
• Fixed keyboard covering input fields
• Corrected price formatting for KES
• Fixed date picker timezone issues
• Resolved bottom sheet animation glitches

KNOWN ISSUES:

• Escrow integration pending (Phase 2)
• Payment processing not yet implemented
• Booking confirmation screen coming in next release
• Email/SMS notifications not yet active

MIGRATION NOTES:
• Existing services with single location will show coverageAreas as single-item list
• Backward compatible with previous API versions
• No database migration required
• Users can update existing services to add multiple coverage areas

COMING IN V1.10.0:
• Booking confirmation screen
• My Bookings list
• Booking status tracking
• Push notifications for booking updates
• Escrow payment integration
• Provider availability calendar

Thank you for testing PivotaConnect v1.9.0!
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