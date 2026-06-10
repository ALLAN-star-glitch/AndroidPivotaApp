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

        // Version 1.11.0 - Build 21 - Professional Service Booking System
        // Added: Complete booking flow with date/time picker, negotiation, and booking fees
        versionCode = 21
        versionName = "1.11.0"

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
          PIVOTACONNECT v1.11.0 - PROFESSIONAL SERVICE BOOKING
================================================================

NEW FEATURE: COMPLETE BOOKING SYSTEM

This major release introduces a comprehensive booking system 
that allows customers to book professional services directly 
through the app.

================================================================
BOOKING FLOW
================================================================

- Professional Service Booking Screen
  - Clean two-step booking process
  - Date & Time selection with calendar picker
  - Smart duration selection (hours/days/weeks/months)
  - Location selection from service coverage areas
  - Optional price negotiation
  - Additional notes for special requests

- Review & Payment Step
  - Complete booking summary
  - Price breakdown with all charges
  - Service total calculation
  - Booking fee display
  - Grand total calculation
  - Booking protection information

- Responsive Layout
  - Adaptive design for phones and tablets
  - Wide layout with side-by-side form and summary
  - Narrow layout with step-by-step stepper
  - Smooth animations throughout

================================================================
PRICE NEGOTIATION SYSTEM
================================================================

- Customers can propose custom prices
- Real-time validation against professional's range
- Automatic total recalculation
- Negotiated price reflected in all summaries
- Clear indication when price is negotiated
- Original price shown with strikethrough

================================================================
BOOKING FEE MANAGEMENT
================================================================

- Booking fees automatically calculated
- Displayed separately in price breakdown
- Refundable status clearly indicated
- Added to grand total
- Shown in booking confirmation
- Consistent across all screens

================================================================
UI/UX ENHANCEMENTS
================================================================

- Theme-aware date and time picker
  - Uses Material 3 theme colors
  - Separate date and time selection
  - Number picker for hours/minutes
  - Visual feedback on selection

- Enhanced Form Fields
  - Animated form sections with expand/collapse
  - Real-time validation with error messages
  - Clear visual indicators for required fields
  - Smooth transitions between steps

- Professional Service Card
  - Hero card with service details
  - Price per unit display
  - Professional verification badge
  - Rating and review count
  - Years of experience

- Booking Summary Panel (Tablet)
  - Right-side preview panel
  - Real-time price calculations
  - Service total breakdown
  - Booking fee display
  - Location and date summary

================================================================
PERFORMANCE IMPROVEMENTS
================================================================

- Cached booking data for offline access
- Optimized network requests
- Reduced API calls with smart caching
- Smooth animations with Compose
- Fast date picker responses

================================================================
ERROR HANDLING
================================================================

- Graceful error messages
- Network error recovery
- Form validation with helpful hints
- Conflict detection for booked slots
- Clear guidance for fixing errors

================================================================
DEVICE SUPPORT
================================================================

- Phones (portrait and landscape)
- Tablets (adaptive layouts)
- Dark mode support
- All screen sizes supported
- Responsive typography

================================================================
BUG FIXES
================================================================

- Fixed date picker crash on older devices
- Corrected currency formatting for KES
- Fixed keyboard covering input fields
- Resolved conflict detection logic
- Fixed booking fee calculation errors
- Corrected total price display
- Fixed navigation back stack issues
- Resolved permission validation

================================================================
TECHNICAL IMPROVEMENTS
================================================================

- Complete booking repository implementation
- Room database with caching
- Real-time booking status updates
- Optimized SQL queries
- Improved state management with Compose
- Better error logging for debugging

================================================================
HOW TO TEST
================================================================

1. CREATE A SERVICE OFFERING
   - Navigate to Post a Service
   - Fill in service details
   - Set pricing (PER_HOUR, PER_DAY, etc.)
   - Define coverage areas
   - Set availability hours

2. BOOK A SERVICE
   - Browse available services
   - Tap on a service to view details
   - Click "Book Service"
   - Select date and time
   - Enter duration
   - Choose location
   - Review summary
   - Confirm booking

3. TEST NEGOTIATION
   - Enable negotiation when creating service
   - Set min/max price range
   - Customer proposes custom price
   - System validates automatically

4. TEST BOOKING FEE
   - Enable booking fee when creating service
   - Set fee amount
   - Choose refundable option
   - Fee appears in customer's total

================================================================
KNOWN ISSUES
================================================================

- Payment processing not implemented (coming in v1.12.0)
- Email notifications pending integration
- Contractor availability calendar enhancement pending
- Push notifications for booking updates coming soon

================================================================
COMING IN V1.12.0
================================================================

- Escrow payment integration
- Professional counter-offer system
- Booking confirmation screen
- My Bookings management
- Push notifications
- Availability calendar enhancements
- SMS notifications

================================================================
SUPPORT
================================================================

For issues or feedback, contact:
allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.11.0!
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