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

        versionCode = 35
        versionName = "1.24.0"

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
          PIVOTACONNECT v1.24.0 - ENHANCED OFFLINE & ERROR HANDLING
================================================================

This release introduces a comprehensive offline banner system with
different states for internet and backend issues, DNS-based health 
checks, duplicate message prevention, and significant improvements 
to network/error handling.

================================================================
NEW FEATURES
================================================================

OFFLINE BANNER SYSTEM
- Comprehensive banner system with different states:
  • NO_INTERNET - Red banner with Wi-Fi off icon
  • BACKEND_DOWN - Orange banner with warning icon
  • RECOVERING - Green banner with refresh icon
  • BACKEND_RECOVERED - Green banner with check icon
- Retry button only shown for BACKEND_DOWN state
- Dismiss button always available
- Swipe to dismiss functionality
- Animated banner entrance/exit with fade and slide
- Professional "technical downtime" messaging

DNS-BASED HEALTH CHECK
- Instant network detection using DNS resolution
- 30-second cache for DNS results
- No token refresh on health checks (separate from HTTP)
- Faster detection of network issues

DUPLICATE MESSAGE PREVENTION
- Prevents duplicate "Network restored!" messages
- Tracks last message to avoid repeats
- Clean status transitions
- Prevents RECOVERING banner from being overridden

ENHANCED TOKEN MANAGEMENT
- 30-second cooldown ONLY for token refresh operations
- Always refresh token on network/backend recovery
- Grace period for backend recovery (5 seconds)
- Prevent multiple refresh attempts
- Automatic token refresh on recovery

================================================================
UI/UX IMPROVEMENTS
================================================================

BANNER BEHAVIOR
- Banner stays visible on retry failure
- Retry button shows loading state with spinner
- Auto-dismiss after 5 seconds for RECOVERING state
- No auto-dismiss for error states (user must dismiss)
- Swipe to dismiss with threshold (200px)
- Smooth animations for banner transitions
- Different colors for different states:
  • NO_INTERNET - Red (#E53935)
  • BACKEND_DOWN - Orange (#FF9800)
  • RECOVERING - Green (#4CAF50)

ERROR HANDLING
- Proper distinction between internet and backend issues
- Clear, professional error messages
- Graceful degradation during network issues
- Proper state management for error recovery
- "We are experiencing technical downtime" messaging

================================================================
FIXES & IMPROVEMENTS
================================================================

NETWORK DETECTION
- Instant detection (no cooldown on network state changes)
- Proper internet vs backend distinction
- DNS-based health check for faster detection
- Network callback for instant recovery

TOKEN MANAGEMENT
- Fixed: Double "Reconnecting" messages when network comes back
- Fixed: Offline banner disappearing on retry failure
- Fixed: Retry spinner getting stuck when backend is down
- Fixed: RECOVERING banner being overridden by BACKEND_DOWN
- Fixed: Session REVOKED errors from duplicate refreshes
- 30-second cooldown ONLY for token refresh operations
- Always refresh token on network/backend recovery

BANNER BEHAVIOR
- Banner stays visible on retry failure
- Retry button shows loading state
- Auto-dismiss after 5 seconds for RECOVERING state
- No auto-dismiss for error states (user must dismiss)
- Swipe to dismiss with threshold

USER EXPERIENCE
- Clear distinction between internet and backend issues
- Different icons and colors for different states
- Professional "technical downtime" messaging
- Seamless transitions between states
- Proper offline mode with cached data

================================================================
CODE IMPROVEMENTS
================================================================

TOKEN MANAGER
- Improved checkBackendHealth() with network-first approach
- Grace period for backend recovery
- Proper status tracking (AVAILABLE, INTERNET_DOWN, BACKEND_DOWN, RECOVERING)
- Duplicate message prevention
- Network callback for instant detection

VIEWMODEL
- isRecoveringBannerShowing flag for duplicate prevention
- lastNetworkMessage tracking
- Proper state management for banner types
- Reset flags on state changes

UI COMPONENTS
- OfflineWarningBanner with swipe to dismiss
- Different banner styles for different states
- Proper loading states for retry button
- Animated transitions

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
COMING IN V1.25.0
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

Thank you for testing PivotaConnect v1.24.0!
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