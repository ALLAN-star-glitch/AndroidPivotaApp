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

        // Version 1.22.0 - Build 33 - Enhanced Housing Cards & Navigation Fixes
        // Added: Elegant housing card design with improved large screen support
        // Added: Favorite button and verified badge animations
        // Added: Professional navigation rail (only on main screens for tablet)
        // Added: Bottom bar navigation padding to prevent overlap with system buttons
        // Added: Service offering details bottom bar fix
        // Added: Housing ViewModel integration on Discover screen
        // Added: Real housing data fetching from backend
        // Fixed: Housing cards layout on large screens (better proportions, spacing)
        // Fixed: Navigation rail visibility on tablet (only shows on main screens)
        // Fixed: Bottom bars overlapping with system navigation buttons
        // Fixed: Sticky search bar behavior on housing listings
        versionCode = 33
        versionName = "1.22.0"

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
          PIVOTACONNECT v1.22.0 - ENHANCED HOUSING CARDS & NAVIGATION FIXES
================================================================

This release introduces elegant housing cards with improved visual
design, better large screen support, and critical navigation fixes.

================================================================
NEW FEATURES
================================================================

ELEGANT HOUSING CARDS
- Completely redesigned housing cards with modern aesthetic
- Gradient backgrounds and subtle shadows for premium feel
- Favorite button with heart icon toggle
- Animated verified badge with scale and fade effects
- Status badges (FOR SALE / FOR RENT) with proper color coding
- Feature chips with icons for bedrooms, bathrooms, sq. meters

LARGE SCREEN OPTIMIZATION
- Desktop cards: 20% larger images, more spacing, bigger typography
- Increased padding from 24-32dp to 32-40dp on large screens
- Corner radius: 20dp on desktop (was 16dp)
- Card elevation: 12dp on desktop (was 8dp)
- Feature chips with "large" variant for better visibility
- Content now center-aligned vertically for better balance

TABLEt NAVIGATION IMPROVEMENTS
- Navigation rail now only shows on main screens (Dashboard, Connect, Profile)
- Detail screens (HouseListings, JobListings, etc.) have full-width content
- Dynamic padding adjustment based on navigation rail visibility
- Consistent experience across all tablet screens

================================================================
UI/UX IMPROVEMENTS
================================================================

BOTTOM BAR FIXES
- Added navigationBarsPadding() to all sticky bottom elements
- Prevents overlap with system navigation buttons
- Fixes: Service offering details bottom bar
- Fixes: House details bottom bar
- Fixes: Main screen scaffold navigation bar
- Ensures proper positioning on all devices

HOUSING VIEWMODEL INTEGRATION
- Discover screen now uses HousingViewModel for real data
- Fetches 6 real housing listings from backend
- Elegant skeleton loading for housing cards
- Loading, success, and empty states handled properly
- Consistent with jobs implementation

ENHANCED FILTERING
- Category filter pills on housing listings
- Price range filtering (min/max)
- Status filtering (Available, Pending, Rented, Sold, Inactive)
- Active filter count badge on filter button
- Search debouncing for better performance

================================================================
CODE IMPROVEMENTS
================================================================

UNIFIED HOUSING EXTENSIONS
- Consistent helper functions across screens
- getMainImage(), getFormattedPrice(), getFormattedLocation()
- getPropertyTypeLabel(), getListingTypeLabel()
- getFormattedPostedTime() with proper time formatting
- Shared across HouseListingsScreen and DiscoverScreen

RESPONSIVE GRID LAYOUTS
- Housing grid: 1 column (phone) → 2 (tablet) → 3 (desktop)
- Adaptive spacing based on screen size
- Proper card sizing with weight distribution
- Consistent with jobs and professionals sections

IMPROVED SKELETON LOADING
- ElegantHousingCardSkeleton matching card design
- Desktop skeleton: 240dp height with proper layout
- Medium skeleton: compact row layout
- Mobile skeleton: optimized for small screens
- Shimmer effect for loading feedback

================================================================
NAVIGATION IMPROVEMENTS
================================================================

CONDITIONAL NAVIGATION RAIL
- isMainScreen flag determines navigation rail visibility
- Main screens: Dashboard, Connect, Profile
- Detail screens: Full-width content without rail
- Cleaner tablet experience

STICKY SEARCH BEHAVIOR
- Search bar pins when scrolling on housing listings
- Smooth transition between unpinned and pinned states
- Category pills remain accessible while scrolling
- Consistent with main Discover screen behavior

================================================================
HOUSING CARD VARIANTS
================================================================

DESKTOP VARIANT
- 200-240dp square image with 16dp corner radius
- Large title (24-28sp) and price (26-30sp)
- Feature chips with circle background
- View Property button with chevron
- Posted time with clock icon

MEDIUM/TABLET VARIANT
- 110dp square image with 12dp corner radius
- Compact row layout with badges
- 17sp title and 19sp price
- Feature chips compact style
- Chevron button for details

MOBILE VARIANT
- 80-95dp square image
- Optimized for one or two-column layouts
- Smaller typography for limited space
- Essential info only
- Clean and scannable

================================================================
TECHNICAL IMPROVEMENTS
================================================================

PERFORMANCE OPTIMIZATIONS
- LazyVerticalGrid for efficient rendering
- Cached image requests with Coil
- Debounced search to reduce API calls
- Remembered filtered results
- Proper key management for list items

STATE MANAGEMENT
- HousingUiState: Loading, Success, Error
- Lifecycle-aware state collection
- Proper error handling with retry
- Pagination support with loadMore()

RESPONSIVE BREAKPOINTS
- EXPANDED: 3+ columns, large padding
- MEDIUM: 2 columns, medium padding
- COMPACT: 1-2 columns, small padding
- Landscape detection for two-column compact

================================================================
BUG FIXES
================================================================

- Fixed: Housing cards overlapping on large screens
- Fixed: Navigation rail showing on detail screens
- Fixed: Bottom bars overlapping system navigation
- Fixed: Search bar pinning behavior
- Fixed: Filter count badge updating correctly
- Fixed: Empty states showing proper messages
- Fixed: Skeleton loading height consistency

================================================================
TESTING SCENARIOS
================================================================

1. TEST HOUSING CARDS ON DIFFERENT SCREENS
   - Phone (portrait): Single column layout
   - Phone (landscape): Two-column layout
   - Tablet (portrait): Two-column layout
   - Tablet (landscape): Three-column layout
   - Desktop: Three-column layout with larger cards

2. TEST NAVIGATION ON TABLET
   - Navigate to Dashboard → Navigation rail visible
   - Navigate to Connect → Navigation rail visible
   - Navigate to Profile → Navigation rail visible
   - Navigate to HouseListings → Navigation rail hidden
   - Navigate to JobListings → Navigation rail hidden
   - Navigate back → Rail reappears correctly

3. TEST BOTTOM BAR OVERLAP
   - Check service details bottom bar
   - Check house details bottom bar
   - Check main scaffold navigation bar
   - Verify no overlap with system navigation
   - Test on devices with gesture navigation
   - Test on devices with 3-button navigation

4. TEST REAL DATA INTEGRATION
   - Open Discover screen
   - Verify housing data loads from API
   - Check skeleton loading states
   - Verify all fields display correctly
   - Test empty state when no data

5. TEST FILTERING
   - Filter by category (Apartment, House, Studio, etc.)
   - Filter by price range (min/max)
   - Filter by status
   - Clear filters
   - Verify results update correctly

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
- Housing images: some listings may not have images

================================================================
COMING IN V1.23.0
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

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.22.0!
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