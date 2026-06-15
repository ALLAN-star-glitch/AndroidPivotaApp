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

        // Version 1.19.0 - Build 29 - All Services Screen UI Overhaul
        // Added: Segmented switch, enhanced search bars, responsive layouts
        versionCode = 29
        versionName = "1.19.0"

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
          PIVOTACONNECT v1.19.0 - ALL SERVICES UI OVERHAUL
================================================================

This release introduces a complete redesign of the All Services
screen with modern UI components, improved navigation, and a
seamless experience for browsing both services and categories.

================================================================
ALL SERVICES SCREEN - COMPLETE REDESIGN
================================================================

SEGMENTED SWITCH (SERVICES / CATEGORIES)
- Modern iOS-style segmented control with smooth animation
- Clean underline indicator that slides between options
- Professional typography with proper font weights
- Services tab shows all service offerings across categories
- Categories tab displays the organized category grid

STICKY HEADER WITH COLLAPSIBLE TITLE
- Title collapses when scrolling for more content space
- Back button and title in same row for cleaner layout
- Smooth fade animation when title collapses
- Compact header appears with just back button when scrolling

ANIMATED SEGMENT INDICATOR
- Spring animation for smooth sliding between tabs
- 250ms transition with FastOutSlowInEasing
- Gradient underline for visual appeal
- Proper alignment based on selected tab

================================================================
SERVICES TAB ENHANCEMENTS
================================================================

ALL OFFERINGS API INTEGRATION
- New GetAllOfferings endpoint fetches all services
- Pagination support for infinite scrolling
- Load more button at bottom for better UX
- Pull-to-refresh support for fresh data

ENHANCED SEARCH BAR
- Modern rounded design with subtle shadow
- Clear button for easy query reset
- Loading indicator during search
- Persistent filter button with active count badge

SERVICE OFFERING FILTERS
- Sort by: Recent, Price Low-High, Price High-Low, Highest Rated
- Price range filter (min/max in KES)
- Verified professionals only toggle
- Minimum rating filter (3★, 4★, 5★)
- Active filters summary with clear display

IMPROVED FILTER BOTTOM SHEET
- Clean modal design with rounded corners
- Real-time preview of active filters
- Reset button to clear all filters
- Apply button with primary theme color
- Smooth drag handle for dismissing

================================================================
CATEGORIES TAB ENHANCEMENTS
================================================================

UNIFIED SEARCH BAR DESIGN
- Same modern design as Services tab
- Consistent spacing and styling
- Filter button with active count badge

HORIZONTAL FILTER PILLS
- Scrolling row of category filters
- "All Services", "Property Services", "Career Services", "Community Support"
- Color-coded chips matching vertical pillars
- Selected state with vibrant background

RESPONSIVE GRID LAYOUT
- Adaptive columns: 3 on phone, 4 on medium, 6 on expanded
- Proper spacing between items (12-20dp based on screen size)
- Circle icons with colored backgrounds
- Subcategory badge indicator (📁) for parent categories

================================================================
SERVICE OFFERING CARD IMPROVEMENTS
================================================================

IMPROVED CARD DESIGN
- Modern rounded corners (16dp)
- Consistent elevation with subtle shadow
- Clean typography with proper hierarchy
- Verified badge for trusted professionals

ENHANCED PRICE DISPLAY
- Negotiable pricing indicator
- Custom booking fee display when applicable
- Price per unit (hour, day, month, etc.)
- Currency formatting with KES support

PROFESSIONAL INFORMATION
- Professional name and avatar
- Years of experience display
- Location/coverage areas
- Average rating with star icon

================================================================
SHIMMER LOADING ANIMATIONS
================================================================

ENHANCED SKELETON LOADING
- Smooth shimmer effect for all loading states
- Color-matched shimmer for category icons
- Proper card skeleton matching actual content
- Infinite animation until data loads

RESPONSIVE SKELETON LAYOUT
- Grid adjusts based on screen size
- Proper number of skeleton items per row
- No layout shift when actual content loads
- Graceful loading experience

================================================================
RESPONSIVE DESIGN IMPROVEMENTS
================================================================

TABLET OPTIMIZATION
- 2-column grid for services on tablets
- 4-6 column grid for categories on tablets
- Proper padding and spacing for larger screens
- Maintains readability on all devices

PHONE OPTIMIZATION
- Single column for services on phones
- 3-column grid for categories on phones
- Optimized touch targets for thumb reach
- Compact but usable interface

DYNAMIC PADDING
- 16dp on phones, 24dp on medium, 32dp on expanded
- Ensures content doesn't touch screen edges
- Consistent with Material Design guidelines
- Adapts to window size classes

================================================================
NAVIGATION ENHANCEMENTS
================================================================

SMOOTH BACK NAVIGATION
- Proper popBackStack handling
- State preservation when returning
- No duplicate navigation entries

SERVICE DETAILS NAVIGATION
- Clicking card navigates to ServiceOfferingDetailsScreen
- Full service information displayed
- Book Service button for making bookings
- Contact Provider option (with improved UX)

BOOKING FLOW INTEGRATION
- ProfessionalServiceBooking screen for scheduling
- Handles service offering and contractor IDs
- Smooth transition from details to booking

================================================================
ANIMATION IMPROVEMENTS
================================================================

TAB TRANSITIONS
- 250ms spring animation for indicator
- Smooth crossfade between content
- No jarring jumps or layout shifts
- Professional feel

SEARCH BAR ANIMATIONS
- Smooth appearance of clear button
- Progress indicator with fade-in
- Filter button badge animation

SCROLL BEHAVIOR
- Sticky search bar on both tabs
- Content scrolls independently
- No conflicts with lazy grids
- Consistent across platforms

================================================================
PERFORMANCE OPTIMIZATIONS
================================================================

LAZY LOADING
- LazyVerticalGrid for efficient rendering
- Only composables visible on screen are rendered
- Smooth scrolling with 60fps
- Recycler-like performance

STATE MANAGEMENT
- Proper remember and derivedStateOf usage
- No unnecessary recompositions
- Stable keys for list items
- Efficient filter calculations

CACHE INTEGRATION
- Individual service offerings cached for offline access
- In-memory cache for quick navigation
- Room database for persistent storage
- Automatic cache invalidation on updates

================================================================
BUG FIXES
================================================================

- Fixed LazyVerticalGrid inside verticalScroll causing crash
- Fixed duplicate ServicesContent function definitions
- Resolved shimmer animation not playing on skeletons
- Fixed filter counts not updating correctly
- Corrected navigation back stack behavior
- Fixed tablet layout spacing issues
- Resolved category grid column calculation on rotation
- Fixed search debouncing for both tabs

================================================================
UI/UX IMPROVEMENTS
================================================================

COLOR SCHEME CONSISTENCY
- Proper use of MaterialTheme colorScheme
- Surface containers for cards and backgrounds
- Primary, secondary, tertiary colors for pillars
- Dark mode fully supported

TYPOGRAPHY ENHANCEMENTS
- Proper font scales (sp)
- Appropriate font weights (Medium, SemiBold, Bold)
- Line heights for readability
- Text overflow handling with ellipsis

EMPTY STATES
- Professional empty state illustrations
- Clear CTAs for clearing filters
- Friendly messaging for no results
- Maintains brand consistency

ERROR HANDLING
- User-friendly error messages
- Retry buttons for failed loads
- Offline detection and messaging
- Graceful degradation

================================================================
CODE QUALITY IMPROVEMENTS
================================================================

CLEAN ARCHITECTURE
- Domain models separated from DTOs
- Use cases for business logic
- Repository pattern for data access
- ViewModels for state management

COMPOSE BEST PRACTICES
- Proper state hoisting
- Reusable composable functions
- Modifier parameter for flexibility
- No side effects in composables

TESTABILITY
- ViewModels with constructor injection
- Repositories with dependency injection
- Use cases easily mockable
- Separation of concerns

================================================================
TESTING SCENARIOS
================================================================

1. TEST SEGMENTED SWITCH
   - Tap between Services and Categories
   - Verify smooth animation
   - Check content updates correctly
   - Verify state persists on rotation

2. TEST SEARCH FUNCTIONALITY
   - Type in search bar on Services tab
   - Verify results filter in real-time
   - Clear search with X button
   - Test search on Categories tab
   - Verify debouncing works (300ms delay)

3. TEST FILTERS ON SERVICES TAB
   - Tap filter button
   - Apply multiple filters (sort, price range, rating)
   - Verify active count badge updates
   - Reset filters and verify clearing
   - Apply filters and close sheet

4. TEST FILTERS ON CATEGORIES TAB
   - Tap filter pills (Property Services, Career Services, etc.)
   - Verify grid updates with filtered categories
   - Tap "All Services" to reset
   - Verify active filter count badge

5. TEST SERVICE CARD CLICKS
   - Click any service offering card
   - Verify navigation to ServiceOfferingDetailsScreen
   - Check back button returns correctly
   - Verify bookmark state persists

6. TEST PAGINATION
   - Scroll to bottom of services
   - Click "Load More" button
   - Verify more services load
   - Continue until no more data

7. TEST RESPONSIVE LAYOUT
   - Test on phone (small screen)
   - Test on tablet (medium screen)
   - Test on large tablet/desktop (expanded)
   - Verify grid columns adjust correctly
   - Check padding and spacing

8. TEST OFFLINE BEHAVIOR
   - Turn off internet
   - Open All Services screen
   - Verify error message
   - Turn on internet and retry
   - Verify data loads

9. TEST SHIMMER ANIMATION
   - Slow network connection
   - Observe skeleton loading
   - Verify shimmer animation plays
   - Check content loads smoothly

================================================================
KNOWN ISSUES
================================================================

- Professional contact info not yet available from backend
- Contact dialog shows alternative options until backend provides data
- Payment processing not implemented (v1.20.0)
- Push notifications pending integration
- Booking cancellation flow enhancements in progress
- Some emulators may have slower animation performance

================================================================
COMING IN V1.20.0
================================================================

- Escrow payment integration
- Professional counter-offer system
- My Bookings management screen
- Push notifications for booking updates
- SMS notifications for urgent updates
- Enhanced offline data synchronization
- Professional profile pages
- Service offering sharing

================================================================
TECHNICAL DETAILS
================================================================

ALL OFFERINGS API
- Endpoint: GET /v1/contractors-module/service-offerings/all
- Pagination with limit/offset
- Filters: price range, rating, verified only
- Sorting: recent, price_asc, price_desc, rating

COMPOSE STRUCTURE
- AllServicesScreen: Main container with state management
- ServicesContent: Services tab with search and filters
- CategoriesContent: Categories tab with filter pills
- AnimatedSegmentedSwitch: Custom tab component
- ServicesSearchBarWithFilter: Reusable search component

STATE MANAGEMENT
- categoriesActiveFilterCount: Tracks active category filters
- servicesActiveFilterCount: Tracks active service filters
- debouncedCategoriesQuery: Delayed search for categories
- debouncedServicesQuery: Delayed search for services

================================================================
SUPPORT & FEEDBACK
================================================================

For issues, bug reports, or feature requests:
Email: allanmathenge22@gmail.com

Thank you for testing PivotaConnect v1.19.0!
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