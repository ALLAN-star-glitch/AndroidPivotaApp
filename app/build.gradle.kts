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

        // Version 1.8.0 - Build 18 - Service Offerings UI Improvements
        versionCode = 18
        versionName = "1.8.0"

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
PivotaConnect v1.8.0 (Build 18)

SERVICE OFFERINGS UI IMPROVEMENTS

This release focuses on improving the Service Offerings screens with better visual design, enhanced text visibility, and improved user experience.

IMPROVEMENTS:

1. SERVICE OFFERING DETAILS SCREEN REDESIGN
   • Completely redesigned layout with card-based design
   • Improved text visibility with better contrast ratios
   • Fixed font size issues throughout the screen
   • Proper scrollable content on both mobile and tablet
   • Two-pane layout for tablets with independent scrolling
   • Enhanced readability with semantic color usage

2. BOTTOM BAR OVERLAP FIX
   • Added proper padding to prevent bottom bar overlap
   • Bottom bar now has proper elevation and shadow
   • Adjusted button heights for better touch targets
   • Content no longer hides behind navigation buttons

3. TEXT VISIBILITY ENHANCEMENTS
   • All primary text now uses onSurface color for maximum contrast
   • Labels use primary color for better emphasis
   • Increased font sizes for critical information
   • Added proper text overflow handling
   • Improved color contrast throughout the screen

4. CATEGORY AND LOCATION DISPLAY
   • Now displayed in dedicated cards with solid backgrounds
   • Added labels for better context
   • Larger icons for better visibility
   • Proper spacing and visual hierarchy

5. SERVICE PROVIDER CARD IMPROVEMENTS
   • Added proper avatar display with initials fallback
   • Name capitalization for consistent formatting
   • Clickable card with chevron indicator
   • Combined experience and verification status
   • Cleaner layout with better visual separation

6. LOADING STATES ENHANCEMENT
   • More visible shimmer effect with stronger contrast
   • Smooth 60fps skeleton loading animation
   • Properly sized skeleton elements
   • Better visual feedback during loading

7. FILTER BOTTOM SHEET IMPROVEMENTS
   • Better checkbox alignment and visibility
   • Improved filter chip styling
   • Clear visual feedback for active filters
   • Proper spacing and layout

8. SEARCH AND FILTER HEADER
   • Redesigned header with better visual hierarchy
   • Improved search field styling
   • Active filter badge with count
   • Better spacing and shadows

9. EMPTY AND ERROR STATES
   • Redesigned empty state with better iconography
   • Clear action buttons
   • Improved error state messaging
   • Better visual feedback for users

TECHNICAL IMPLEMENTATION:

• Card-based layout for all content sections
• Proper Material 3 color scheme usage
• Responsive design for mobile and tablet
• Enhanced shimmer effect for loading states
• Proper padding handling for system bars
• Two-pane layout for tablet optimization
• Independent scrolling for each pane
• Improved color contrast throughout

BEFORE (v1.7.0):
• Font sizes were inconsistent
• Some text was not visible due to low contrast
• Bottom bar overlapped content
• Category and location text hard to read
• Provider card had unclear action
• Loading shimmer was barely visible
• Tablet layout was not optimized

AFTER (v1.8.0):
• Consistent and readable font sizes
• High contrast text throughout
• Proper padding prevents bottom bar overlap
• Clear category and location display
• Clickable provider card with chevron
• Highly visible shimmer loading effect
• Optimized two-pane tablet layout
• All text now properly visible

AFFECTED SCREENS:

• Service Offerings List Screen
• Service Offering Details Screen (Mobile)
• Service Offering Details Screen (Tablet)
• Service Provider Card Component
• Filter Bottom Sheet
• Search and Filter Header
• Loading Skeleton States
• Empty and Error States

CHANGES TO SERVICE OFFERING DETAILS SCREEN:

Mobile Layout:
• Price displayed in highlighted card at top
• Key stats with circular icon backgrounds
• Content grouped in themed cards
• Subtle dividers with proper spacing
• Bottom padding to prevent bar overlap
• Smooth scrolling experience

Tablet Layout:
• Two-pane layout with left and right sections
• Both panes independently scrollable
• Left pane shows pricing, title, category, location, and quick info
• Right pane shows description, service details, location, availability, and provider
• Optimized spacing for larger screens

CHANGES TO SERVICE PROVIDER CARD:
• Avatar with initials fallback when image unavailable
• Proper name capitalization (First Last)
• Combined experience and verification display
• Clickable entire card with chevron icon
• Card lifts on press for visual feedback

TESTING INSTRUCTIONS:

To test Service Offering Details:
1. Navigate to any category in the listings
2. Tap on any service offering card
3. Verify details screen opens with proper layout
4. Check all text is clearly visible
5. Scroll through content - no bottom bar overlap
6. On tablet, verify two-pane layout works
7. Both panes should scroll independently
8. Provider card should be clickable with chevron

To test Loading States:
1. Navigate to Service Offerings list
2. Observe shimmer loading effect
3. Verify skeleton elements are properly sized
4. Shimmer should be clearly visible

To test Search and Filter:
1. Use search field to find services
2. Tap filter button to open bottom sheet
3. Apply various filters
4. Verify active filter badge shows count
5. Clear filters and verify functionality

To test Provider Card:
1. Go to Service Offering Details
2. Scroll to Service Provider section
3. Card should show provider name, experience, verification
4. Tap card - should be clickable
5. Chevron icon indicates clickable action

BUG FIXES:

• Fixed text visibility issues throughout
• Resolved bottom bar overlap problem
• Fixed font size inconsistencies
• Corrected color contrast issues
• Fixed tablet layout scrolling
• Resolved category text visibility
• Fixed provider image display
• Corrected shimmer effect visibility

MIGRATION NOTES:
• No database migration required
• No breaking changes
• Fully backward compatible
• No action required from users

Thank you for testing PivotaConnect v1.8.0!
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