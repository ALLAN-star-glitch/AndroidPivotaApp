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

        // Version 1.21.0 - Build 32 - Jobs API Integration & Enhanced Filtering
        // Added: Real jobs API integration with JobPostsViewModel
        // Added: 6 jobs limit on Discover screen with skeleton loading
        // Added: Job card improvements with dynamic data formatting
        // Added: Advanced job filtering on JobListingsScreen
        // Added: Pagination support for job listings
        // Fixed: Job card image placeholder handling
        // Fixed: Employment type and commitment label formatting
        versionCode = 32
        versionName = "1.21.0"

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
          PIVOTACONNECT v1.21.0 - JOBS API INTEGRATION
================================================================

This release integrates real job data from the API with enhanced
filtering, pagination, and improved job card displays.

================================================================
NEW FEATURES
================================================================

REAL JOBS API INTEGRATION
- Discover screen now loads 6 real jobs from the API
- JobListingsScreen fetches all jobs with pagination
- Job data includes: title, company, location, pay, commitment
- Proper employment type mapping (Formal/Informal)
- Commitment label formatting (Full Time, Part Time, etc.)

JOBS SKELETON LOADING
- Beautiful skeleton loaders while jobs are fetching
- 6 skeleton cards on Discover screen
- Responsive skeleton grid matching content layout
- Smooth loading experience with no layout shifts

ADVANCED JOB FILTERING
- Filter by job type: All, Full Time, Part Time, Contract, Internship
- Filter by salary range (min/max)
- Filter by employer type: All, Companies, Individuals
- Filter by listing status
- Active filter count badge
- Search by title, company, location, or description

JOB LISTINGS PAGINATION
- Load more jobs when scrolling to bottom
- Loading indicator at bottom of list
- Infinite scroll with smooth loading
- Preserves scroll position

================================================================
JOB CARD ENHANCEMENTS
================================================================

DYNAMIC DATA FORMATTING
- Posted time: "Just now", "5m ago", "2h ago", "1d ago", etc.
- Employment type: Formal (Permanent/Contract) or Informal
- Commitment: Full Time, Part Time, Project Based, On Call
- Company profile images from API

RESPONSIVE JOB CARDS
- Desktop: Full featured with gradient ring and decorative dots
- Tablet: Medium layout with badges
- Mobile: Compact layout optimized for small screens
- Two-column compact mode for larger phones in landscape

================================================================
UI IMPROVEMENTS
================================================================

JOB LISTINGS HEADER
- Clean header with back button
- Title and subtitle
- Search bar with voice input
- Category filter pills
- Sticky search bar when scrolling

FILTER MODAL
- Adaptive bottom sheet for phones
- Alert dialog for tablets
- Filter by: Job Type, Salary, Employer Type, Status
- Reset and Apply actions
- Real-time filter preview

EMPTY STATES
- No jobs found with filters
- No jobs available at all
- Clear filter button
- Post job CTA

================================================================
TECHNICAL IMPROVEMENTS
================================================================

VIEWMODEL INTEGRATION
- JobPostsViewModel with state management
- JobsUiState: Loading, Success, Error
- Lifecycle-aware state collection
- Proper error handling

PAGINATION SUPPORT
- loadMore() function for infinite scroll
- SnapshotFlow to detect scroll position
- Prevents duplicate loading requests
- Smooth user experience

OPTIMIZED PERFORMANCE
- LazyVerticalGrid for efficient rendering
- Cached image requests with Coil
- Debounced search to reduce API calls
- Remembered filtered results

================================================================
CODE CLEANUP
================================================================

REMOVED DUPLICATE CODE
- Removed hardcoded jobItems from DiscoverScreen
- Removed duplicate job formatting functions
- Centralized job data mapping
- Consistent helper functions across screens

IMPROVED ORGANIZATION
- Clear separation of concerns
- Reusable composable functions
- Consistent naming conventions
- Better code maintainability

================================================================
COMPATIBILITY
================================================================

ANDROID VERSION SUPPORT
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 36 (Android 16)
- Full material3 adaptive support

SUPPORTED DEVICES
- All Android devices running API 24+
- Optimized for phones, tablets, and desktop

================================================================
TESTING SCENARIOS
================================================================

1. TEST JOBS LOADING ON DISCOVER SCREEN
   - Open Discover screen
   - Verify 6 skeleton cards appear
   - Wait for jobs to load
   - Verify jobs display with correct data
   - Check formatting: time, type, commitment

2. TEST JOB LISTINGS SCREEN
   - Navigate to Job Listings
   - Verify all jobs load
   - Scroll to bottom, verify pagination
   - Use search to filter jobs
   - Apply various filters
   - Verify filter count badge updates

3. TEST JOB CARD VARIANTS
   - Check cards on phone (1 column)
   - Check cards on tablet (2 columns)
   - Check cards on desktop (3 columns)
   - Verify image placeholders work
   - Check all data displays correctly

4. TEST FILTERING
   - Filter by Full Time jobs
   - Filter by salary range
   - Filter by Companies only
   - Combine multiple filters
   - Clear filters
   - Verify no results state

5. TEST EMPTY STATES
   - Search for non-existent job
   - Verify no results message
   - Click "Clear Filters"
   - Verify jobs reload

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
COMING IN V1.22.0
================================================================

- Professional contact information from backend
- My Bookings management screen
- Enhanced offline data synchronization
- Professional profile pages
- Service offering sharing functionality
- Improved image caching
- Job application flow
- Saved jobs feature

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.21.0!
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

    // Lifecycle utilities for Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

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