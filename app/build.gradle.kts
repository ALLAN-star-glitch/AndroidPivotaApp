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

        // Version 1.23.0 - Build 34 - Enhanced UI with Shimmer Effects & Lottie Error States
        // Added: Elegant shimmer loading effect for housing and job cards
        // Added: Lottie animation for error states with retry functionality
        // Added: Unified shimmer system using sliding light streak effect
        // Added: Connect logo in bottom navigation bar and tablet rail
        // Added: Smooth curve bump in bottom navigation for Connect item
        // Added: Shimmer effect for all skeleton loading states
        // Added: ErrorStateWithLottie composable for network failures
        // Fixed: Housing skeleton loading with proper shimmer effect
        // Fixed: Job skeleton loading with consistent shimmer
        // Fixed: Navigation rail visibility on tablet detail screens
        // Fixed: Bottom bar gap when system navigation is hidden
        // Fixed: Service details bottom bar positioning
        // Fixed: Housing cards shimmer effect matching AllServicesScreen style
        // Improved: Bottom navigation bar height and styling
        // Improved: Navigation rail with logo for Connect item
        versionCode = 34
        versionName = "1.23.0"

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

                testers = "allanmathenge22@gmail.com, allanmathenge67@gmail.com, allanmathenge319@gmail.com, stepenjuguna9010@gmail.com, s9010901090109010@gmail.com, martinmichuki8@gmail.com, brianmulimuteti@gmail.com, carolkim194@gmail.com, allanmathenge82@gmail.com, jananyambura4272@gmail.com, allaneditor67@gmail.com, kelvijames2023@gmail.com, deniskiplimo816@gmail.com"

                releaseNotes = """
================================================================
          PIVOTACONNECT v1.23.0 - ENHANCED UI WITH SHIMMER EFFECTS & ERROR STATES
================================================================

This release introduces elegant shimmer loading effects, Lottie 
animations for error states, and significant UI improvements across 
the entire app.

================================================================
NEW FEATURES
================================================================

UNIFIED SHIMMER EFFECT SYSTEM
- Implemented sliding light streak shimmer for all loading states
- Consistent shimmer across housing, jobs, and service cards
- Optimized animation speed and smoothness
- Dark mode compatible shimmer colors
- Reusable shimmer composable in general package
- Alpha-based pulsing shimmer option for simple elements

LOTTIE ERROR STATE ANIMATIONS
- Added ErrorStateWithLottie composable for network failures
- Beautiful Lottie animations for loading errors
- Retry button with proper refresh functionality
- Consistent error state across all sections
- Custom Lottie animations for jobs, housing, and services
- Seamless integration with existing error handling

CONNECT LOGO IN BOTTOM NAVIGATION
- Custom Connect logo icon in bottom navigation bar
- Bulging effect with shadow and glow
- Larger size when selected (56dp)
- Gradient ring animation for selected state
- Smooth curved bump in bottom navigation bar
- Consistent Connect logo in tablet navigation rail

ENHANCED SKELETON LOADING
- HousingSkeletonContent with shimmer effect
- ElegantHousingCardSkeleton matching card design
- JobCardSkeleton with sliding shimmer
- ServiceGridSkeleton with shimmer effect
- Proper responsive heights for all screen sizes
- Consistent loading experience

================================================================
UI/UX IMPROVEMENTS
================================================================

BOTTOM NAVIGATION BAR
- Increased height to 72dp for better visibility
- Smooth curve bump at Connect item position
- Larger Connect logo with glow effect
- Bold text for Connect label when selected
- Removed default indicator for cleaner look
- Consistent styling across all screens

NAVIGATION RAIL (TABLET)
- Connect logo displayed in rail
- Wider rail (80dp) for logo visibility
- Proper spacing and alignment
- Consistent with bottom navigation
- Only shows on main screens

ERROR HANDLING
- Proper visual feedback for network errors
- Lottie animations for engaging error states
- Clear retry functionality
- Consistent error messages
- Proper state management for error recovery

================================================================
PERFORMANCE IMPROVEMENTS
================================================================

OPTIMIZED SHIMMER ANIMATION
- Faster animation speed (800ms)
- Linear easing for smooth motion
- Wider gradient spread (400f)
- Better start/end positions
- Reduced CPU usage
- Animated with rememberInfiniteTransition

EFFICIENT SKELETON RENDERING
- Proper key management for skeleton items
- LazyColumn/LazyVerticalGrid for performance
- Cached shimmer compositions
- Reduced recompositions
- Optimized for large lists

================================================================
CODE IMPROVEMENTS
================================================================

UNIFIED SHIMMER UTILITY
- Single source of truth for shimmer effects
- Reusable shimmerBrush and shimmer modifier
- Consistent shimmer across all components
- Easy to customize shimmer colors
- Proper documentation

IMPROVED ERROR STATE HANDLING
- Common ErrorStateWithLottie composable
- Configurable Lottie animations
- Consistent error UI across screens
- Proper retry callbacks
- State management improvements

RESPONSIVE NAVIGATION
- Conditional navigation rail visibility
- Proper padding adjustments
- Consistent tablet experience
- Dynamic content width based on rail visibility

================================================================
FIXES & IMPROVEMENTS
================================================================

BOTTOM BAR FIXES
- Fixed gap when system navigation is hidden
- Proper navigationBarsPadding usage
- Consistent bottom bar positioning
- Fixed service details bottom bar
- Fixed house details bottom bar

SKELETON LOADING FIXES
- Fixed shimmer effect on all skeleton components
- Proper height for different screen sizes
- Consistent loading states
- Fixed skeleton layout issues

NAVIGATION FIXES
- Fixed rail showing on detail screens
- Proper isMainScreen logic
- Consistent navigation experience
- Fixed back navigation

================================================================
KNOWN ISSUES
================================================================

- Professional contact info not yet available from backend
- Contact dialog shows alternative options until backend provides data
- Payment processing not implemented
- Push notifications pending integration
- Booking cancellation flow enhancements in progress
- Some emulators may have slower animation performance
- Job images not yet available from API (using fallback)

================================================================
COMING IN V1.24.0
================================================================

- Professional contact information from backend
- My Bookings management screen
- Enhanced offline data synchronization
- Professional profile pages
- Service offering sharing functionality
- Improved image caching
- Job application flow
- Saved jobs feature
- Housing application flow
- Favorites management
- Enhanced search functionality
- Push notification integration

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.23.0!
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