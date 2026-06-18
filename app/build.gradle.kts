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

        // Version 1.20.0 - Build 31 - Adaptive UI & Grid Improvements
        // Added: Responsive grid layouts for Jobs, Housing, Professionals, and Services
        // Added: Adaptive card designs for mobile, tablet, and desktop
        // Added: Chevron icons for mobile navigation
        // Added: Window size class adaptive layouts
        versionCode = 31
        versionName = "1.20.0"

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
          PIVOTACONNECT v1.20.0 - ADAPTIVE UI & GRID IMPROVEMENTS
================================================================

This release introduces a fully adaptive UI with responsive 
grid layouts and improved card designs across all device sizes.

================================================================
NEW FEATURES
================================================================

ADAPTIVE GRID LAYOUTS
- Jobs: 1-3 columns based on screen size (small phones to tablets)
- Housing: 1-3 columns with adaptive card sizes
- Professionals: 1-3 columns with responsive layouts
- Services: 3-6 columns depending on device width
- Consistent grid behavior across all content sections

RESPONSIVE CARD DESIGNS
- Mobile cards: Compact with essential information
- Tablet cards: Medium size with full features
- Desktop cards: Full featured with decorative elements
- Cards adapt seamlessly as window size changes

IMPROVED NAVIGATION
- Chevron icons replace arrows on mobile for better UX
- Consistent iconography across all card types
- Better touch targets for mobile interaction

================================================================
ADAPTIVE WINDOW SIZING
================================================================

WINDOW SIZE CLASS SUPPORT
- EXPANDED: 3 columns for Jobs, Housing, Professionals
- MEDIUM: 2 columns for Jobs, Housing, Professionals
- COMPACT (Landscape): 2 columns for most content
- COMPACT (Portrait, ≥480dp): 2 columns
- COMPACT (Portrait, <480dp): 1 column for clean readability

ORIENTATION AWARENESS
- Layouts adapt to landscape mode on phones
- Better use of screen real estate in landscape
- Consistent experience across rotations

================================================================
CARD ENHANCEMENTS
================================================================

MODERN JOB CARD V2
- Three variants: Desktop, Medium, Mobile
- Adaptive image sizes based on screen
- Contextual information display
- Clean typography and spacing

MODERN HOUSING CARD V2
- Responsive image and content layout
- Smart badge display based on screen size
- Property features shown appropriately
- Verified status visible on all variants

MODERN PROFESSIONAL CARD V2
- Adaptive profile image sizes
- Rating and job count display optimized
- Professional type badges responsive
- Clean layout for all screen sizes

================================================================
TECHNICAL IMPROVEMENTS
================================================================

ADAPTIVE COMPOSABLES
- Used currentWindowAdaptiveInfo() for window sizing
- Implemented WindowWidthSizeClass detection
- Orientation-aware layouts
- Consistent spacing across all screen sizes

PERFORMANCE OPTIMIZATIONS
- Lazy loading for grid content
- Optimized image loading with Coil
- Efficient recomposition with remember
- Smooth scrolling performance

CODE CLEANUP
- Removed duplicate adaptive logic
- Centralized grid column calculations
- Consistent spacing values
- Better code organization

================================================================
COMPATIBILITY
================================================================

SUPPORTED DEVICES
- Small phones (compact, portrait): 1 column
- Large phones (compact, portrait ≥480dp): 2 columns
- Phones in landscape: 2 columns
- Tablets (medium): 2 columns
- Large tablets/desktops (expanded): 3 columns

ANDROID VERSION SUPPORT
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 36 (Android 16)
- Full material3 adaptive support

================================================================
TESTING SCENARIOS
================================================================

1. TEST RESPONSIVE GRID
   - Open Discover Screen on small phone
   - Verify 1 column for Jobs, Housing, Professionals
   - Verify 3 columns for Services
   - Rotate to landscape, verify 2 columns
   - Test on tablet, verify 2-3 columns

2. TEST CARD VARIANTS
   - Compare mobile vs tablet card designs
   - Verify compact cards on phone
   - Check full cards on tablet/desktop
   - Ensure consistent information hierarchy

3. TEST NAVIGATION
   - Tap chevron icons on mobile cards
   - Verify navigation works correctly
   - Check touch targets are adequate
   - Test on different screen sizes

4. TEST PERFORMANCE
   - Scroll through content sections
   - Verify smooth scrolling
   - Check image loading performance
   - Monitor memory usage

================================================================
KNOWN ISSUES
================================================================

- Professional contact info not yet available from backend
- Contact dialog shows alternative options until backend provides data
- Payment processing not implemented
- Push notifications pending integration
- Booking cancellation flow enhancements in progress
- Some emulators may have slower animation performance

================================================================
COMING IN V1.21.0
================================================================

- Professional contact information from backend
- My Bookings management screen
- Enhanced offline data synchronization
- Professional profile pages
- Service offering sharing functionality
- Improved image caching

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.20.0!
Your feedback helps us create a better user experience.

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