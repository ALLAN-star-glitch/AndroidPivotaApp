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

        // Version 1.17.0 - Build 27 - Header UI Enhancements
        // Added: Rounded card header, primary color avatar border, admin role display
        versionCode = 27
        versionName = "1.17.0"

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
          PIVOTACONNECT v1.17.0 - HEADER UI ENHANCEMENTS
================================================================

This release improves the app header with a modern card design,
better visual hierarchy, and improved user information display.

================================================================
HEADER UI IMPROVEMENTS
================================================================

ROUNDED CARD DESIGN
- Header now uses a fully rounded card (28dp corners)
- White background with subtle elevation
- Dynamic elevation based on scroll position (4dp → 8dp → 12dp)
- Professional shadow effects for depth
- Consistent with modern app design patterns

AVATAR ENHANCEMENTS
- Primary color border around all avatars (2.5dp)
- Consistent styling for all users (not just verified)
- Improved gradient backgrounds for guest mode
- Better shadow and visual hierarchy

ADMIN ROLE DISPLAY
- System administrators now see "Admin" badge
- No truncation for admin role (previously "PlatformSy...")
- Primary color styling for admin badge
- Professional pill design with icon

VERIFIED USER BADGE
- Verified icon now appears next to user name
- Tertiary color for verification badge
- Only shown for verified users
- Clear visual indicator of trust status

================================================================
HEADER STRUCTURE
================================================================

PERSISTENT HEADER
- Avatar and action icons always visible
- Page title only hides on scroll (standard behavior)
- Smooth fade and slide animations
- No disappearing header issues

RESPONSIVE DESIGN
- Proper truncation for long names (15 chars)
- Plan names truncated to 10 characters
- Member fallback for unknown roles
- Consistent layout across screen sizes

================================================================
COLOR SCHEME UPDATES
================================================================

THEME COLORS
- Avatar border: Primary color
- Admin badge: Primary color
- Verified icon: Tertiary color
- Plan pills: Color-coded by plan type
- Surface variant backgrounds for icons

DARK MODE SUPPORT
- Fully compatible with dark theme
- Proper color inversions
- Shadows visible in both modes
- Consistent visual hierarchy

================================================================
VISUAL ENHANCEMENTS
================================================================

ACTION ICONS
- Gradient backgrounds for modern look
- Press animation with scale effect (0.92x)
- Dynamic shadows on interaction
- Consistent 38dp sizing

NOTIFICATION BADGE
- Gradient background (Red to Red-80%)
- Proper positioning with offset
- "99+" handling for large counts
- Rounded pill design

PROFILE MENU
- Bottom sheet with professional styling
- Smooth animations
- Clear menu items with icons
- Logout option with destructive styling

================================================================
CODE IMPROVEMENTS
================================================================

PERFORMANCE
- Optimized recompositions
- Efficient animation triggers
- Cached user data for smooth scrolling
- Reduced unnecessary redraws

STATE MANAGEMENT
- Proper elevation state handling
- Smooth scroll offset tracking
- Clean LaunchedEffect usage
- No state leaks

================================================================
BUG FIXES
================================================================

- Fixed header disappearing on scroll
- Fixed admin role truncation issue
- Fixed avatar border visibility for all users
- Resolved elevation animation glitches
- Fixed theme switching color updates

================================================================
TESTING SCENARIOS
================================================================

1. TEST HEADER VISUALS
   - Launch app on different screen sizes
   - Verify rounded card corners (28dp)
   - Check avatar border (primary color)
   - Test light and dark themes
   - Verify proper truncation

2. TEST SCROLL BEHAVIOR
   - Scroll slowly through content
   - Verify header card elevation increases
   - Check title fade animations
   - Verify header never disappears
   - Test smooth transitions

3. TEST USER ROLES
   - Login as System Admin
   - Verify "Admin" badge appears
   - Check no truncation of role name
   - Test Business user plan display
   - Verify Member fallback for unknown roles

4. TEST INTERACTIONS
   - Press action icons for scale animation
   - Tap avatar to open profile menu
   - Scroll header to see elevation changes
   - Toggle theme to verify colors
   - Click notification badge

================================================================
KNOWN ISSUES
================================================================

- Payment processing not implemented (v1.18.0)
- Push notifications pending integration
- Booking cancellation flow enhancements in progress

================================================================
COMING IN V1.18.0
================================================================

- Escrow payment integration
- Professional counter-offer system
- My Bookings management screen
- Push notifications for booking updates
- SMS notifications for urgent updates

================================================================
HOW TO TEST THIS RELEASE
================================================================

1. VISUAL TESTING
   - Check header curvature on different devices
   - Verify primary color avatar border
   - Test dark mode appearance
   - Check admin badge display

2. SCROLL TESTING
   - Scroll through content
   - Verify header remains visible
   - Check elevation transitions
   - Test title hide/show animations

3. ROLE TESTING
   - Test with different user roles
   - Verify admin displays correctly
   - Check business plan pills
   - Test member fallback

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.17.0!
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