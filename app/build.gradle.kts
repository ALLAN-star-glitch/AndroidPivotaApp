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

        // Version 1.19.1 - Build 30 - Added Swipe Gesture Support
        // Added: Horizontal swipe between Services and Categories tabs
        versionCode = 30
        versionName = "1.19.1"

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
          PIVOTACONNECT v1.19.1 - SWIPE GESTURE SUPPORT
================================================================

This release adds a highly requested feature: swipe gestures
for navigating between tabs on the All Services screen.

================================================================
NEW FEATURES
================================================================

SWIPE GESTURE SUPPORT
- Swipe left or right to switch between Services and Categories
- Smooth, responsive animations when swiping
- Works seamlessly with the existing segmented switch
- Natural gesture interaction that users expect

IMPROVED TAB NAVIGATION
- Both tap and swipe interactions are supported
- The segmented switch updates to reflect the current tab
- Synchronized state between swipe and tap interactions
- Consistent and intuitive user experience

================================================================
TECHNICAL IMPROVEMENTS
================================================================

HORIZONTAL PAGER INTEGRATION
- Implemented using Compose Foundation's HorizontalPager
- Efficient rendering with lazy loading of tab content
- Proper state management between pager and switch
- Smooth physics and animations out of the box

STATE SYNCHRONIZATION
- LaunchedEffect observers keep pager and switch in sync
- No desync issues when switching via tap or swipe
- Page changes trigger immediate UI updates
- Reliable state management across configuration changes

================================================================
USER EXPERIENCE ENHANCEMENTS
================================================================

NATURAL INTERACTION
- Swipe gestures feel natural and responsive
- Tabs respond immediately to finger movement
- Smooth snap-to-page behavior
- No accidental page changes

TOUCH FEEDBACK
- Visual feedback during swipe interaction
- The indicator follows the swipe motion
- Responsive to both slow and fast swipes

================================================================
IMPLEMENTATION DETAILS
================================================================

TECHNICAL APPROACH
- Used androidx.compose.foundation.pager.HorizontalPager
- rememberPagerState for managing page state
- LaunchedEffect for bidirectional synchronization
- userScrollEnabled = true for swipe support

ARCHITECTURE
- Minimal changes to existing code structure
- Kept the same composable functions for content
- Pager wraps the existing tab content
- No duplication of code

================================================================
TESTING SCENARIOS
================================================================

1. TEST SWIPE GESTURE
   - Open All Services screen (default: Services tab)
   - Swipe left to navigate to Categories tab
   - Verify the segmented switch updates
   - Swipe right to return to Services tab
   - Verify content updates correctly

2. TEST TAP AND SWIPE SYNC
   - Tap "Categories" on the segmented switch
   - Verify the page animates to Categories
   - Swipe left to go to Categories
   - Tap "Services" on the switch
   - Verify the page animates back

3. TEST FAST SWIPES
   - Quickly swipe between tabs multiple times
   - Verify the app remains responsive
   - Check that the state is always correct
   - No crashes or glitches

4. TEST SLOW SWIPES
   - Slowly swipe halfway between tabs
   - Release the swipe
   - Verify the page snaps to the correct tab
   - Smooth snap animation

5. TEST ROTATION
   - Rotate the device while on either tab
   - Verify the current tab is preserved
   - Ensure the content displays correctly

================================================================
KNOWN ISSUES
================================================================

- Professional contact info not yet available from backend (v1.20.0)
- Contact dialog shows alternative options until backend provides data
- Payment processing not implemented (v1.21.0)
- Push notifications pending integration
- Booking cancellation flow enhancements in progress
- Some emulators may have slower animation performance

================================================================
COMING IN V1.20.0
================================================================

- Professional contact information from backend
- My Bookings management screen
- Enhanced offline data synchronization
- Professional profile pages
- Service offering sharing functionality

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.19.1!
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