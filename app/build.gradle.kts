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

        // Version 1.10.0 - Build 20 - Negotiable Pricing & Booking Fee
        versionCode = 20
        versionName = "1.10.0"

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
PivotaConnect v1.10.0 (Build 20)

NEGOTIABLE PRICING & BOOKING FEE SYSTEM

This release introduces flexible pricing options for professionals, allowing them to enable price negotiation and charge booking fees for their services.

NEW FEATURES:

1. NEGOTIABLE PRICING
   • Professionals can mark services as negotiable
   • Set minimum and maximum acceptable price range
   • Customers see "Negotiable" badge on service details
   • Price range displayed to customers (e.g., "KES 12,000 - 18,000")
   • Customer can propose custom price during booking
   • Real-time validation against min/max range
   • Automatic price calculation based on proposed price

2. BOOKING FEE (CALL-OUT FEE)
   • Professionals can charge an optional booking fee
   • Set custom amount per service offering
   • Add description explaining what fee covers
   • Toggle refundable status on cancellation
   • Displayed prominently on service details
   • Added to total price calculation during booking

3. ENHANCED SERVICE POSTING FLOW
   • New "Negotiable Pricing" section with toggle switch
   • Optional min/max price fields for negotiable services
   • New "Booking Fee" section with toggle switch
   • Amount field with validation (max 10,000 KES)
   • Optional description field for booking fee
   • Refundable toggle with explanatory text
   • Real-time validation and error messages

4. SERVICE DETAILS DISPLAY
   • "Negotiable" badge shown on price card
   • Negotiable price range displayed when applicable
   • Booking fee amount shown with currency
   • "Refundable" badge for refundable booking fees
   • Booking fee description displayed below amount
   • Separate card for negotiable pricing information

5. BOOKING SHEET ENHANCEMENTS
   • Proposed price field for negotiable services
   • Real-time validation against allowed range
   • Error messages for invalid proposals
   • Booking fee displayed separately
   • Total price calculation includes booking fee
   • Price breakdown card shows service price + booking fee
   • "Negotiated" label when customer proposes different price

6. ADMIN SERVICE DETAILS
   • Display negotiable status in overview
   • Show negotiable price range
   • Display booking fee amount and refund policy
   • Enhanced service cards with new pricing info
   • Improved analytics for negotiable services

7. VALIDATION & BUSINESS RULES
   • Min price cannot exceed max price
   • Min price cannot exceed base price
   • Max price cannot be less than base price
   • Negotiable range must be within platform min/max
   • Booking fee cannot exceed 10,000 KES
   • Booking fee validation against service price
   • All rules enforced on frontend and backend

8. UI/UX IMPROVEMENTS
   • Clean card-based sections for pricing options
   • Animated visibility for conditional fields
   • Switch toggles for enabling features
   • Consistent spacing and typography
   • Error messages with proper color coding
   • Help text explaining each feature
   • Responsive layout for all screen sizes

TECHNICAL IMPLEMENTATION:

• Backend DTOs updated with new fields
• Room database schema migration
• Validation logic in ViewModel
• Real-time price calculation with state management
• Proper handling of nullable fields
• Backward compatible API calls
• Offline support for cached data

NEGOTIABLE PRICING FLOW:

1. Professional enables negotiable pricing toggle
2. Sets min and max acceptable price (optional but recommended)
3. Service displayed with negotiable badge
4. Customer views service and sees negotiable range
5. During booking, customer can propose custom price
6. System validates proposal against min/max
7. If valid, total calculated with proposed price + booking fee
8. Booking created with negotiated price flag

BOOKING FEE FLOW:

1. Professional enables booking fee toggle
2. Sets amount (e.g., KES 500) and optional description
3. Chooses whether fee is refundable on cancellation
4. Service displays booking fee information
5. Customer sees fee breakdown during booking
6. Fee added to total calculation
7. Booking stores fee snapshot for reference
8. Cancellation policy applied based on refundable setting

DATABASE CHANGES:

• ServiceOfferingEntity: Added isNegotiable, minNegotiablePrice, maxNegotiablePrice
• ServiceOfferingEntity: Added useCustomBookingFee, customBookingFeeEnabled, customBookingFeeAmount, customBookingFeeCurrency, customBookingFeeDescription, customBookingFeeRefundable
• ServiceBookingEntity: Added proposedPrice, isNegotiated, bookingFeeAmount, bookingFeeCurrency, bookingFeeRefundable, totalAmount
• Room database version upgrade with migration

API INTEGRATION:

• CreateServiceOfferingRequestDto includes new fields
• UpdateServiceOfferingRequestDto includes new fields  
• ServiceOfferingDto includes new fields in responses
• Booking requests include proposedPrice
• Booking responses include pricing breakdown
• Backward compatible with older API versions

BENEFITS:

• Professionals can offer flexible pricing
• Customers can negotiate fair prices
• Booking fee compensates for travel/consultation
• Clear pricing transparency
• Reduced cancellations with booking fees
• Higher conversion for negotiable services
• Better matching of price expectations

TESTING INSTRUCTIONS:

To Test Negotiable Pricing (Professional):
1. Go to Post a Service screen
2. Fill basic service information
3. Navigate to Pricing step
4. Toggle "Negotiable Pricing" switch ON
5. Enter min price (e.g., 12000) and max price (e.g., 18000)
6. Verify validation (min ≤ max, within platform range)
7. Complete and post service

To Test Booking Fee (Professional):
1. In Pricing step, toggle "Booking Fee" switch ON
2. Enter amount (e.g., 500)
3. Add optional description
4. Toggle refundable status
5. Verify validation (amount > 0, ≤ 10000)
6. Complete and post service

To Test Customer Negotiation:
1. View service with negotiable pricing
2. Verify "Negotiable" badge displayed
3. Verify price range shown in details
4. Tap "Book Service"
5. Enter proposed price (e.g., 14000)
6. Verify validation (within min/max range)
7. See total price update (proposed + booking fee)
8. Submit booking

To Test Booking Fee Display:
1. View service with booking fee
2. Verify fee amount shown in price section
3. Open booking sheet
4. Verify fee added to total calculation
5. Check refundable status displayed

EDGE CASES TESTED:

• Min price > max price → error message
• Price outside platform range → validation error
• Booking fee > 10,000 KES → error message
• Negotiable toggle OFF → hide min/max fields
• Booking fee toggle OFF → hide amount field
• No coverage areas → location field hidden
• Offline mode → cached data still shows pricing

BUG FIXES:

• Fixed price unit display in booking sheet
• Corrected currency formatting for KES
• Fixed validation race conditions
• Improved error message clarity
• Fixed keyboard covering input fields
• Corrected total price calculation with booking fee

KNOWN ISSUES:

• Escrow integration pending (Phase 2)
• Payment processing not yet implemented
• Booking confirmation screen enhancements pending
• Email/SMS notifications for negotiated bookings

MIGRATION NOTES:

• Existing services will have isNegotiable = false by default
• No migration needed for existing bookings
• App will prompt for database upgrade on first launch
• Cache will be invalidated and refreshed

COMING IN V1.11.0:
• Counter-offer system (professional can counter customer's proposal)
• Booking confirmation screen with all details
• My Bookings list with negotiation status
• Push notifications for price proposals
• Escrow payment integration
• Provider availability calendar

Thank you for testing PivotaConnect v1.10.0!
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