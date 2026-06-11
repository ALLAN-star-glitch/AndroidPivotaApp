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

        // Version 1.14.0 - Build 24 - Adaptive Grid Layout & Search Improvements
        // Added: Adaptive grid layout for tablets, removed autofocus from search
        versionCode = 24
        versionName = "1.14.0"

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
          PIVOTACONNECT v1.14.0 - ADAPTIVE GRID LAYOUT
================================================================

This release introduces adaptive grid layouts for tablets and 
large screens, plus search bar improvements for better UX.

================================================================
ADAPTIVE GRID LAYOUTS
================================================================

SERVICE OFFERINGS SCREEN
- Phones: Single column list (optimal for small screens)
- Medium Tablets: 2-column grid (perfect for iPad Mini)
- Large Tablets/Desktop: 3-column grid (maximizes space)
- Automatic detection using window size classes
- Smooth transitions between layouts

BENEFITS
- See more offerings at once on tablets
- Better use of screen real estate
- Reduced scrolling on large displays
- Consistent card sizes across all devices
- Professional staggered grid presentation

================================================================
SEARCH BAR IMPROVEMENTS
================================================================

REMOVED AUTOFOCUS BEHAVIOR
- Search bar no longer auto-focuses on scroll
- Keyboard doesn't automatically appear
- Less intrusive user experience
- Users control when to search
- Cleaner scroll behavior

ENHANCED SEARCH INTERACTION
- Tap to focus and type
- Clear button hides keyboard
- Voice search still available
- Real-time filtering
- Debounced search for performance

================================================================
VISUAL ENHANCEMENTS
================================================================

GRID CARD DESIGN
- Cards maintain consistent sizing
- Proper spacing between grid items (12dp)
- Responsive padding based on screen size
- Smooth animations when loading
- Professional card elevation

SKELETON LOADING
- Adaptive skeleton based on grid columns
- 2 rows of skeletons for tablets
- 5 skeletons for phones
- Prominent shimmer effect
- Smooth loading transitions

================================================================
PERFORMANCE OPTIMIZATIONS
================================================================

RENDERING IMPROVEMENTS
- LazyVerticalGrid for efficient recycling
- Proper key handling for items
- Optimized recompositions
- Reduced overdraw on tablets
- Faster scrolling on large lists

MEMORY MANAGEMENT
- Efficient grid item recycling
- Proper state management
- Cached item keys
- Smooth infinite scroll
- Optimized filter operations

================================================================
SCREEN SIZE ADAPTATION
================================================================

WINDOW SIZE CLASSES
- COMPACT: Phones (1 column)
- MEDIUM: Small tablets (2 columns)
- EXPANDED: Large tablets/Desktop (3 columns)
- Automatic detection with adaptive info
- No manual configuration needed

RESPONSIVE PADDING
- Horizontal padding scales with screen size
- 16dp for phones
- 20dp for medium tablets
- 24dp for large tablets
- Consistent visual spacing

================================================================
CODE IMPROVEMENTS
================================================================

ARCHITECTURE
- Extracted grid column logic
- Reusable skeleton components
- Clean state management
- Proper error handling
- Efficient filter debouncing

TYPE SAFETY
- Strong typing for window classes
- Null-safe grid calculations
- Proper state propagation
- Safe viewModel usage

================================================================
BUG FIXES
================================================================

- Fixed search bar autofocus on scroll
- Corrected grid alignment on tablets
- Fixed card width issues on large screens
- Resolved keyboard showing unexpectedly
- Fixed scroll position reset on filter
- Corrected skeleton loading for grids

================================================================
TESTING SCENARIOS
================================================================

1. TEST TABLET LAYOUT
   - Run on medium tablet (iPad Mini size)
   - Verify 2-column grid appears
   - Check card sizes and spacing
   - Scroll to test performance
   - Rotate device to test responsiveness

2. TEST LARGE SCREEN
   - Run on large tablet or desktop
   - Verify 3-column grid appears
   - Check content density
   - Test landscape orientation
   - Verify no layout breaks

3. TEST SEARCH BEHAVIOR
   - Scroll to sticky search bar
   - Verify no auto-focus occurs
   - Tap search to type
   - Clear search hides keyboard
   - Test voice search button

4. TEST FILTERING
   - Open filter bottom sheet
   - Apply price range filter
   - Sort by different options
   - Verify grid updates correctly
   - Clear filters to reset

================================================================
DEVICE SUPPORT
================================================================

SUPPORTED SCREEN SIZES
- Phones (5-7 inches): 1 column
- Small tablets (7-9 inches): 2 columns
- Large tablets (10-13 inches): 3 columns
- Desktop/Chrome OS: 3 columns
- Foldables: Adaptive based on state

ORIENTATION SUPPORT
- Portrait and landscape modes
- Dynamic column adjustment
- Preserves scroll position
- Smooth transitions
- No layout shifts

================================================================
KNOWN ISSUES
================================================================

- Payment processing not implemented (v1.15.0)
- Push notifications pending integration
- Booking cancellation flow in progress
- Professional calendar view coming soon
- In-app messaging system planned

================================================================
COMING IN V1.15.0
================================================================

- Escrow payment integration
- Professional counter-offer system
- My Bookings management screen
- Push notifications for all events
- SMS notifications for updates
- Booking history and receipts
- Professional availability calendar
- In-app chat system

================================================================
HOW TO TEST THIS RELEASE
================================================================

1. TABLET TESTING
   - Install on Android tablet
   - Navigate to Service Offerings
   - Verify grid layout (2 columns on medium, 3 on large)
   - Scroll and load more items
   - Test filter and sort

2. PHONE TESTING
   - Install on phone
   - Verify single column list
   - Search bar behavior
   - Filter functionality
   - Smooth scrolling

3. SEARCH TESTING
   - Scroll down to sticky search
   - Verify no keyboard popup
   - Tap to search and type
   - Clear button functionality
   - Voice search button

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.14.0!
Your feedback helps us create a better experience.

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