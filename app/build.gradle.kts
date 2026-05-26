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
        versionCode = 6
        versionName = "1.0.6"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("staging") {
            initWith(getByName("debug"))

            versionNameSuffix = "-staging"

            firebaseAppDistribution {
                artifactType = "APK"
                testers = "allanmathenge22@gmail.com, allanmathenge67@gmail.com, allanmathenge319@gmail.com, stepenjuguna9010@gmail.com, s9010901090109010@gmail.com, martinmichuki8@gmail.com, brianmulimuteti@gmail.com, carolkim194@gmail.com, allanmathenge82@gmail.com, janenyambura4272@gmail.com, allaneditor67@gmail.com, kelvijames2023@gmail.com, deniskiplimo816@gmail.com"
                releaseNotes = """
PivotaConnect v1.0.6 (Build 6)

PREMIUM CARD REDESIGN - Complete Visual Overhaul (Major Update)

All listing cards have been completely redesigned with a modern, premium look:

NEW CARD DESIGNS:

Service Offering Card:
- Professional avatar with gradient verification ring for verified professionals
- Category badge and verified badge
- Rating stars with 5-star system (full and half-star support)
- Years of experience badge
- Location with icon
- Premium price display
- "View service →" text link
- Subtle dot pattern decoration
- Gradient divider line
- Enhanced visual hierarchy

Job Card (ModernJobCardV2):
- Company logo with gradient ring
- Employment type badge (Formal/Informal)
- Job type badge (Remote, Full-time, Contract, Gig, etc.)
- Job title and company name
- Location with icon
- "View details →" text link
- Posted time integrated at bottom
- Premium card styling

Housing Card (ModernHousingCardV2):
- Property image with gradient ring
- Property type badge (Apartment, House, Bedsitter, Room)
- Listing type badge (For Rent/For Sale) with color coding
- Price, location, and property features (bedrooms, bathrooms, sqm)
- "View details →" text link
- Premium visual treatment

Professional Card (ModernProfessionalCardV2):
- Circular profile image with gradient ring
- "SERVICE" badge and Individual/Company badge
- Professional name and profession
- Rating with star icon and jobs completed count
- "View profile →" text link
- Premium card design with professional layout

Visual Enhancements Across All Cards:
- Dotted border pattern for premium feel
- Decorative dot pattern in top-right corner
- Sweep gradient rings for featured/verified items
- Subtle linear gradient backgrounds
- Gradient divider lines separating content
- Consistent elevation (2dp default, 6dp on press)
- Uniform icon sizes (11-12dp)
- Better typography and spacing
- Dark/Light theme support
- Improved touch feedback

Technical Improvements:
- All cards are fully clickable with no button UI
- Consistent design language across all listing types
- Proper theming using MaterialTheme colors
- Smooth image loading with Coil
- Proper placeholder and error handling for images

Bug Fixes (from v1.0.5):
- Fixed DayAvailability mapping issues
- Fixed Moshi serialization for availability data
- Fixed logout flow with proper cleanup
- Fixed navigation after logout

Previous Features:
- Service Offerings flow with search, filter, and pagination
- Skeleton loading for better UX
- Offline support for categories
- Tablet support improvements
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
    //implementation(libs.androidx.compose.remote.creation.compose)

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

    // Credential Manager (as per documentation)
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.0")

    // Source: https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.1")

    implementation("com.airbnb.android:lottie-compose:6.4.0")

    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))

    implementation("com.google.firebase:firebase-analytics")

    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")  // For codegen (no reflection)

    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")
}