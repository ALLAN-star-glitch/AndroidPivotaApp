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

        versionCode = 36
        versionName = "1.25.0"

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
          PIVOTACONNECT v1.25.0 - UI ENHANCEMENTS & OFFLINE STATUS
================================================================

This release introduces significant UI improvements including an 
online/offline status indicator on the user avatar, optional page 
titles in the header, and visual refinements to the header component.

================================================================
NEW FEATURES
================================================================

ONLINE/OFFLINE STATUS INDICATOR
- Green dot on avatar when user is online (connected to backend)
- Gray dot on avatar when user is offline (no network/backend issues)
- Uses Material Theme colors (SuccessGreen / onSurfaceVariant)
- Smooth transitions between states
- Professional visual indicator with shadow and border
- Status reflects real network connectivity state

OPTIONAL PAGE TITLES
- Page title is now optional with default null
- Title section gracefully hides when not provided
- Maintains professional spacing without title
- Backward compatible with existing usages
- Smooth animations for title show/hide

HEADER UI REFINEMENTS
- Avatar size increased from 48dp to 54dp for better visibility
- Status dot positioned at bottom-right corner of avatar
- Dot size 16dp with 2.5dp border for clear visibility
- Shadow effect on status dot for depth
- Professional visual separation between online/offline states

================================================================
UI/UX IMPROVEMENTS
================================================================

AVATAR ENHANCEMENTS
- Larger avatar size (54dp) for better visibility
- Status dot positioned outside avatar bounds
- Clean circular border around avatar
- Consistent spacing in header layout
- Professional visual hierarchy

HEADER LAYOUT
- Optional page title with automatic hiding
- Smooth title animations (fade + slide)
- Preserved spacing without title
- Clean separation line below header
- Professional elevation shadow

STATUS INDICATOR BEHAVIOR
- Online: Green dot (SuccessGreen)
- Offline: Gray dot (onSurfaceVariant)
- Real-time status updates
- Network state changes reflected immediately
- No visual glitches during transitions

================================================================
FIXES & IMPROVEMENTS
================================================================

HEADER COMPONENT
- Fixed: Page title now optional with null default
- Fixed: Status dot clipping on avatar
- Fixed: Status dot positioning at bottom-right
- Fixed: Status dot visibility on different backgrounds
- Fixed: Status dot color matching Material Theme

UI CONSISTENCY
- All header elements use Material Theme colors
- Consistent spacing throughout header
- Professional visual hierarchy
- Smooth animations for all transitions
- Proper dark/light mode support

NETWORK STATUS
- Real-time online/offline detection
- Proper banner state management
- Status dot reflects actual connectivity
- Network recovery detection
- Automatic status updates

================================================================
CODE IMPROVEMENTS
================================================================

REUSABLE HEADER
- pageTitle parameter now optional (String? = null)
- Status dot using Material Theme colors
- Derived state for online/offline status
- Comprehensive logging for debugging
- Proper state observation with collectAsState()

UI COMPONENTS
- Status dot with shadow and border
- Optional title section with animations
- Consistent theming across components
- Clean separation of concerns
- Reusable and maintainable code

================================================================
DOCUMENTATION
================================================================

NEW COMPONENT: STATUS DOT
- Position: Bottom-right corner of avatar
- Size: 16dp with 2.5dp border
- Colors: SuccessGreen (online) / onSurfaceVariant (offline)
- Shadow: 3dp elevation with color-matched glow
- Animation: Smooth transitions

HEADER PARAMETER CHANGES
- pageTitle: String? = null (previously required)
- All other parameters unchanged
- Backward compatible

USAGE EXAMPLES
- With title: ReusableHeader(pageTitle = "Dashboard")
- Without title: ReusableHeader() // title hidden
- Status dot: Automatic based on connectivity

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
COMING IN V1.26.0
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
- Status dot animation enhancements
- Avatar status indicator customization

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.25.0!
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