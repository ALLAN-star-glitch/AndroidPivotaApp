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

        // Version 1.18.0 - Build 28 - Token Management & Offline Recovery
        // Added: Automatic token refresh, network recovery, offline banner
        versionCode = 28
        versionName = "1.18.0"

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
          PIVOTACONNECT v1.18.0 - TOKEN MANAGEMENT & OFFLINE RECOVERY
================================================================

This release introduces a comprehensive token management system
with automatic refresh, intelligent offline handling, and seamless
connection recovery when network or backend service is restored.

================================================================
TOKEN MANAGEMENT SYSTEM
================================================================

AUTO TOKEN REFRESH
- Tokens automatically refresh every 12 minutes
- Prevents session expiration during extended app usage
- No user interruption during refresh
- Seamless background operation

SMART REFRESH SCHEDULING
- Refreshes token 5 minutes before expiration
- Avoids unnecessary API calls
- Reduces server load
- Optimized refresh intervals

CONCURRENT REFRESH PROTECTION
- Prevents multiple simultaneous refresh attempts
- Mutex locks ensure thread safety
- Handles race conditions gracefully
- Maintains token integrity

================================================================
NETWORK & BACKEND RECOVERY
================================================================

INTELLIGENT ERROR CLASSIFICATION
- Distinguishes between network and backend errors
- ConnectException = Backend unreachable (internet works)
- UnknownHostException = No internet connection
- Timeout = Server not responding

AUTOMATIC RECOVERY
- Health check runs every 30 seconds
- Detects when backend comes back online
- Auto-refreshes token on recovery
- Seamless user experience

NETWORK CALLBACK MONITORING
- Instant detection when network returns
- Uses Android ConnectivityManager
- Falls back to health check if callback unavailable
- Works reliably on all devices

================================================================
OFFLINE BANNER SYSTEM
================================================================

ELEGANT BANNER DESIGN
- Gradient background for modern look
- Animated warning icon with pulse effect
- Smooth slide-in/out animations
- Rounded corners (20dp) with elevation

SMART MESSAGING
- "No internet connection" for network issues
- "Service temporarily unavailable" for backend issues
- Clear, user-friendly error messages
- Actionable guidance for users

MANUAL RETRY BUTTON
- User can force retry at any time
- Visual feedback during retry
- Full-screen loading animation
- Updates banner with result

================================================================
SESSION MANAGEMENT
================================================================

FORCE LOGOUT ON AUTH FAILURE
- Detects invalid/expired refresh tokens
- Clears all session data
- Navigates to login screen
- Prevents unauthorized access

CLEAN SESSION CLEARING
- Clears DataStore preferences
- Removes Room database entries
- Stops auto-refresh jobs
- Resets all failure counters

================================================================
OFFLINE DATA ACCESS
================================================================

CACHED PROFILE DISPLAY
- Shows cached profile when offline
- Timestamp indicates cache age
- Warning for stale data (>24 hours)
- Transparent offline experience

BACKGROUND PROFILE REFRESH
- Attempts refresh when online
- Updates cache on success
- Preserves user experience
- No UI blocking

================================================================
BANNER UI ENHANCEMENTS
================================================================

GRADIENT BACKGROUND
- Horizontal gradient for visual appeal
- Surface container colors adapt to theme
- Proper dark mode support
- Professional appearance

ANIMATED ICON
- Pulsing Wi-Fi Off icon
- Smooth infinite animation
- Error tint for visibility
- 24dp size with scale transform

RESPONSIVE BUTTONS
- Dismiss and Retry options
- Equal width with proper spacing
- Rounded corners (12dp)
- Primary color for Retry button

LOADING STATE
- Circular progress indicator
- "Attempting to reconnect..." message
- Buttons hidden during retry
- Smooth transition

================================================================
CODE IMPROVEMENTS
================================================================

TOKEN MANAGER
- Seamless class with comprehensive error handling
- Recovery events for UI updates
- Backend status tracking
- Thread-safe operations

NETWORK EXCEPTION HANDLER
- Accurate network state detection
- Uses ConnectivityManager when available
- Smart fallback for error classification
- Proper error message extraction

DASHBOARD VIEWMODEL
- Centralized retry logic
- Offline state management
- Recovery event handling
- Clean state flows

================================================================
PERFORMANCE OPTIMIZATIONS
================================================================

EFFICIENT HEALTH CHECKS
- 5-second timeout for health checks
- Minimal battery impact
- Non-blocking coroutines
- Dispatchers.IO for network operations

OPTIMIZED STATE FLOWS
- SharedFlow for events
- StateFlow for UI states
- Proper scope management
- No memory leaks

================================================================
BUG FIXES
================================================================

- Fixed token refresh not retrying after network recovery
- Fixed banner not appearing on second backend failure
- Fixed manual retry not refreshing profile
- Resolved duplicate loading indicators
- Fixed offline state persistence across screen rotations
- Corrected error message for backend vs network issues

================================================================
TESTING SCENARIOS
================================================================

1. TEST TOKEN AUTO-REFRESH
   - Log in and leave app running for 12 minutes
   - Verify token refreshes without interruption
   - Check logs for successful refresh
   - Confirm no user-facing errors

2. TEST BACKEND DOWNTIME
   - Kill backend server while app is running
   - Verify banner appears: "Service temporarily unavailable"
   - Wait 30 seconds, restart backend
   - Verify banner disappears and data reloads
   - Check auto-refresh on recovery

3. TEST NETWORK DOWNTIME
   - Turn on Airplane mode
   - Verify banner appears: "No internet connection"
   - Turn off Airplane mode
   - Verify banner disappears within 2-5 seconds
   - Check data reloads automatically

4. TEST MANUAL RETRY
   - With backend down, click "Retry" button
   - Verify full-screen loading appears
   - Verify banner updates message on failure
   - Start backend, click retry again
   - Verify success and banner disappears

5. TEST SESSION EXPIRY
   - Manually invalidate refresh token on backend
   - Verify app detects auth error
   - Verify force logout occurs
   - Verify navigation to login screen

6. TEST OFFLINE CACHE
   - Turn off network completely
   - Kill and restart app
   - Verify cached profile displays
   - Verify "Using cached data" message appears
   - Verify timestamp shows cache age

================================================================
KNOWN ISSUES
================================================================

- Payment processing not implemented (v1.19.0)
- Push notifications pending integration
- Booking cancellation flow enhancements in progress
- Emulator network callbacks may be slower than physical devices

================================================================
COMING IN V1.19.0
================================================================

- Escrow payment integration
- Professional counter-offer system
- My Bookings management screen
- Push notifications for booking updates
- SMS notifications for urgent updates
- Enhanced offline data synchronization

================================================================
TECHNICAL DETAILS
================================================================

TOKEN REFRESH FLOW
1. Auto-refresh job runs every 12 minutes
2. Checks token expiration (5 minutes before expiry)
3. Calls refreshToken API with current refresh token
4. On success, saves new tokens with timestamp
5. On failure, classifies error (network/backend/auth)
6. Retries with exponential backoff

HEALTH CHECK SYSTEM
1. Runs every 30 seconds when network available
2. Attempts quick token validation
3. Detects backend availability changes
4. Emits recovery events when backend returns

ERROR CLASSIFICATION
- No internet: UnknownHostException, No route to host
- Backend down: ConnectException, Connection refused
- Timeout: SocketTimeoutException, Read timeout
- Auth error: 401, "Invalid token", "Session expired"

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.18.0!
Your feedback helps us create a more reliable app.

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