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

        // Version 1.13.0 - Build 23 - Professional Header UI Enhancements
        // Added: Curved header design, dynamic elevation, text truncation
        versionCode = 23
        versionName = "1.13.0"

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
          PIVOTACONNECT v1.13.0 - PROFESSIONAL HEADER DESIGN
================================================================

This release enhances the app header with a modern, premium 
design featuring curved edges, dynamic shadows, and professional 
text handling.

================================================================
HEADER UI ENHANCEMENTS
================================================================

CURVED HEADER DESIGN
- Modern bottom-curved header (28dp rounded corners)
- Professional gradient backgrounds
- Smooth dynamic elevation based on scroll position
- Elegant shadow effects with ambient and spot lighting
- Subtle bottom separator line for visual hierarchy

DYNAMIC ELEVATION SYSTEM
- 4dp elevation at rest for subtle depth
- 8dp elevation when partially scrolled
- 12dp elevation when sticky/full scrolled
- Smooth animated transitions between states
- Proper shadow casting for premium feel

TEXT TRUNCATION IMPROVEMENTS
- First names truncated to 15 characters
- Role names truncated to 12 characters
- Plan names truncated to 10 characters
- Professional "..." ellipsis for overflow
- Smart truncation preserving context

================================================================
VISUAL ENHANCEMENTS
================================================================

PROFILE AVATAR UPGRADES
- Added shadow to avatar for depth
- Gradient background for guest mode
- Enhanced verified badge with thicker border
- Professional loading states
- Smooth image loading with crossfade

ACTION ICONS REFINEMENT
- Press animation with scale effect (0.92x)
- Gradient backgrounds for modern look
- Dynamic shadows on interaction
- Smooth 100ms feedback animations
- Consistent 38dp sizing

NOTIFICATION BADGE
- Gradient background (Red to Red-80%)
- Improved positioning with offset
- Better text scaling for numbers
- "99+" handling for large counts
- Rounded pill design

================================================================
USER INFO DISPLAY
================================================================

PROFESSIONAL PILL DESIGN
- Rounded corners (20dp) for modern look
- Semi-transparent backgrounds (12% opacity)
- Icon + text combination
- Color-coded by plan type
- Proper spacing and padding

ROLE BADGES
- System admin role with shield icon
- Business plan pills with plan icons
- Consistent styling across scopes
- Proper truncation for long names
- Hover and click states

================================================================
ANIMATION IMPROVEMENTS
================================================================

SMOOTH TRANSITIONS
- Fade + slide for page title visibility
- 300ms entrance animations
- 200ms exit animations
- Rotating dropdown arrow (180deg)
- Icon scale feedback on press

SCROLL BEHAVIOR
- Page title hides on scroll
- Header elevation increases with scroll
- Smooth alpha transitions
- Performance-optimized animations
- No jank or stuttering

================================================================
RESPONSIVE DESIGN
================================================================

SCREEN SIZE ADAPTATION
- Proper truncation on all screen sizes
- No text overflow on small screens
- Maintains visual hierarchy on tablets
- Consistent padding across devices
- Adaptive icon sizing

DARK MODE SUPPORT
- Fully compatible with dark theme
- Proper color inversions
- Shadows visible in both modes
- Gradient adaptations
- Verified badge color consistency

================================================================
CODE IMPROVEMENTS
================================================================

PERFORMANCE
- Optimized recompositions
- Efficient truncation logic
- Cached user data for smooth scrolling
- Reduced unnecessary redraws
- Memory-efficient animations

STATE MANAGEMENT
- Proper elevation state handling
- Smooth scroll offset tracking
- Efficient animation triggers
- Clean LaunchedEffect usage
- No state leaks

================================================================
BUG FIXES
================================================================

- Fixed header text overflow on long names
- Resolved shadow clipping on curved edges
- Fixed badge positioning on different DPIs
- Corrected animation timing inconsistencies
- Fixed theme switching color updates
- Resolved profile image border rendering

================================================================
TESTING SCENARIOS
================================================================

1. TEST HEADER VISUALS
   - Launch app on different screen sizes
   - Verify curved bottom corners
   - Check shadow depth and direction
   - Test light and dark themes
   - Verify proper truncation

2. TEST SCROLL BEHAVIOR
   - Scroll slowly through content
   - Verify elevation increases
   - Check title fade animations
   - Test sticky header behavior
   - Verify smooth transitions

3. TEST LONG NAMES
   - Use account with long name (25+ chars)
   - Verify truncation with "..."
   - Check tooltip or full name on click
   - Test role name truncation
   - Verify no layout breaking

4. TEST INTERACTIONS
   - Press action icons for scale animation
   - Tap avatar to open menu
   - Scroll header to see elevation changes
   - Toggle theme to verify colors
   - Click notification badge

================================================================
KNOWN ISSUES
================================================================

- Payment processing not implemented (v1.14.0)
- Push notifications pending integration
- Booking cancellation flow in progress
- Professional calendar view coming soon
- In-app messaging system planned

================================================================
COMING IN V1.14.0
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

1. VISUAL TESTING
   - Check header curvature on different devices
   - Verify shadows and elevation
   - Test dark mode appearance
   - Check text truncation with long names

2. PERFORMANCE TESTING
   - Scroll rapidly to check frame drops
   - Test on older devices (API 24+)
   - Verify memory usage
   - Check animation smoothness

3. USABILITY TESTING
   - Navigate through all screens
   - Test all header interactions
   - Verify badge updates correctly
   - Check tooltip accessibility

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.13.0!
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