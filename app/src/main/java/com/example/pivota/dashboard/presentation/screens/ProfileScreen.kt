package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.dashboard.presentation.composables.ReusableHeader
import com.example.pivota.ui.theme.*

// ==================== ENUMS ====================

enum class ListingType {
    PROPERTY,
    JOB,
    PROFESSIONAL
}

enum class EntityType {
    INDIVIDUAL,
    ORGANIZATION
}

enum class VerificationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED
}

enum class AccountType {
    INDIVIDUAL,
    ORGANIZATION
}

enum class ProfileType {
    ACCOUNT,
    PROFESSIONAL,
    AGENT,
    EMPLOYER,
    JOB_SEEKER,
    PROPERTY_OWNER,
    PROPERTY_SEEKER,
    SERVICE_PROVIDER,
    BENEFICIARY
}

// ==================== DATA CLASSES ====================

data class AccountData(
    val id: String,
    val uuid: String,
    val accountCode: String,
    val name: String?,
    val type: AccountType,
    val status: String,
    val isVerified: Boolean,
    val verifiedFeatures: List<String>,
    val createdAt: String,
    val updatedAt: String
)

data class UserData(
    val uuid: String,
    val userCode: String,
    val email: String,
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
    val roleName: String,
    val profileImage: String?,
    val status: String
)

data class IndividualProfileData(
    val firstName: String?,
    val lastName: String?,
    val bio: String?,
    val gender: String?,
    val dateOfBirth: String?,
    val nationalId: String?,
    val profileImage: String?
)

data class OrganizationProfileData(
    val name: String,
    val type: String?,
    val registrationNo: String?,
    val kraPin: String?,
    val officialEmail: String?,
    val officialPhone: String?,
    val website: String?,
    val about: String?,
    val logo: String?,
    val physicalAddress: String?,
    val members: List<TeamMemberData> = emptyList(),
    val pendingInvitations: List<PendingInvitationData> = emptyList()
)

data class ProfessionalProfileData(
    val uuid: String,
    val title: String?,
    val specialties: List<String>,
    val serviceAreas: List<String>,
    val yearsExperience: Int?,
    val licenseNumber: String?,
    val insuranceInfo: String?,
    val hourlyRate: Float?,
    val paymentTerms: String?,
    val isVerified: Boolean,
    val averageRating: Float,
    val totalReviews: Int,
    val completedJobs: Int,
    val portfolioImages: List<String>
)

data class AgentProfileData(
    val uuid: String,
    val agentType: String,
    val specializations: List<String>,
    val licenseNumber: String?,
    val licenseBody: String?,
    val yearsExperience: Int?,
    val agencyName: String?,
    val serviceAreas: List<String>,
    val commissionRate: Float?,
    val feeStructure: String?,
    val minimumFee: Float?,
    val isVerified: Boolean,
    val averageRating: Float,
    val totalReviews: Int,
    val completedDeals: Int,
    val about: String?,
    val profileImage: String?,
    val contactEmail: String?,
    val contactPhone: String?,
    val website: String?
)

data class EmployerProfileData(
    val companyName: String?,
    val industry: String?,
    val companySize: String?,
    val foundedYear: Int?,
    val description: String?,
    val logo: String?,
    val preferredSkills: List<String>,
    val remotePolicy: String?,
    val isVerifiedEmployer: Boolean
)

data class JobSeekerProfileData(
    val headline: String?,
    val isActivelySeeking: Boolean,
    val skills: List<String>,
    val industries: List<String>,
    val jobTypes: List<String>,
    val seniorityLevel: String?,
    val noticePeriod: String?,
    val expectedSalary: Float?,
    val workAuthorization: List<String>,
    val cvUrl: String?,
    val cvLastUpdated: String?,
    val linkedInUrl: String?,
    val portfolioUrl: String?,
    val githubUrl: String?
)

data class PropertyOwnerProfileData(
    val isProfessional: Boolean,
    val licenseNumber: String?,
    val companyName: String?,
    val yearsInBusiness: Int?,
    val preferredPropertyTypes: List<String>,
    val serviceAreas: List<String>,
    val isVerifiedOwner: Boolean
)

data class PropertySeekerProfileData(
    val minBedrooms: Int,
    val maxBedrooms: Int,
    val minBudget: Float?,
    val maxBudget: Float?,
    val preferredTypes: List<String>,
    val preferredCities: List<String>,
    val preferredNeighborhoods: List<String>,
    val moveInDate: String?,
    val leaseDuration: String?,
    val householdSize: Int,
    val hasPets: Boolean?,
    val petDetails: String?
)

data class ServiceProviderProfileData(
    val providerType: String,
    val servicesOffered: List<String>,
    val targetBeneficiaries: List<String>,
    val serviceAreas: List<String>,
    val isVerified: Boolean,
    val verifiedBy: String?,
    val about: String?,
    val website: String?,
    val contactEmail: String?,
    val contactPhone: String?,
    val officeHours: String?,
    val peopleServed: Int?,
    val yearEstablished: Int?,
    val acceptsDonations: Boolean,
    val needsVolunteers: Boolean
)

data class BeneficiaryProfileData(
    val needs: List<String>,
    val urgentNeeds: List<String>,
    val familySize: Int?,
    val dependents: Int?,
    val vulnerabilityFactors: List<String>,
    val city: String?,
    val neighborhood: String?,
    val prefersAnonymity: Boolean,
    val languagePreference: List<String>
)

data class TeamMemberData(
    val userUuid: String,
    val userName: String,
    val userEmail: String,
    val userImage: String?,
    val roleName: String
)

data class PendingInvitationData(
    val id: String,
    val email: String,
    val status: String,
    val expiresAt: String
)

data class VerificationItemData(
    val type: String,
    val status: VerificationStatus,
    val documentUrl: String?,
    val rejectionReason: String?,
    val verifiedAt: String?,
    val expiresAt: String?
)

data class Review(
    val id: String,
    val reviewerName: String,
    val reviewerImage: String,
    val rating: Float,
    val comment: String,
    val date: String
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: (ProfileType) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToTeamManagement: () -> Unit = {},
    onNavigateToVerification: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToPaymentMethods: () -> Unit = {},
    onNavigateToBillingHistory: () -> Unit = {},
    onSignOut: () -> Unit = {},
    user: User? = null,
    isGuestMode: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    // 📱 Orientation & Window Size Logic
    val configuration = LocalConfiguration.current
    val windowSizeClass = androidx.compose.material3.adaptive.currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    // 🎨 Brand Palette
    val primaryTeal = colorScheme.primary
    val goldenAccent = colorScheme.tertiary
    val softBackground = colorScheme.background
    val purpleAccent = PurpleAccent

    // Track scroll offset for header
    val listState = rememberLazyListState()
    val scrollOffset by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                Float.MAX_VALUE
            }
        }
    }

    // Check if title/subtitle should be hidden (scrolled)
    val isScrolled = scrollOffset > 20f

    // Mock Data - Modify for guest mode
    val accountData = remember {
        if (isGuestMode) {
            mockGuestAccountData()
        } else {
            mockAccountData()
        }
    }
    val userData = remember {
        if (isGuestMode) null else mockUserData()
    }
    val individualProfileData = remember {
        if (isGuestMode) null else mockIndividualProfileData()
    }
    val organizationProfileData = remember {
        if (isGuestMode) null else mockOrganizationProfileData()
    }
    val professionalProfileData = remember {
        if (isGuestMode) null else mockProfessionalProfileData()
    }
    val agentProfileData = remember {
        if (isGuestMode) null else mockAgentProfileData()
    }
    val employerProfileData = remember {
        if (isGuestMode) null else mockEmployerProfileData()
    }
    val jobSeekerProfileData = remember {
        if (isGuestMode) null else mockJobSeekerProfileData()
    }
    val propertyOwnerProfileData = remember {
        if (isGuestMode) null else mockPropertyOwnerProfileData()
    }
    val propertySeekerProfileData = remember {
        if (isGuestMode) null else mockPropertySeekerProfileData()
    }
    val serviceProviderProfileData = remember {
        if (isGuestMode) null else mockServiceProviderProfileData()
    }
    val beneficiaryProfileData = remember {
        if (isGuestMode) null else mockBeneficiaryProfileData()
    }
    val verifications = remember {
        if (isGuestMode) emptyList() else mockVerifications()
    }
    val reviews = remember {
        if (isGuestMode) emptyList() else mockReviews()
    }

    // Dynamically build tabs based on available profiles
    val tabs = buildList {
        add(ProfileType.ACCOUNT to "Account")
        if (!isGuestMode) {
            if (professionalProfileData != null) add(ProfileType.PROFESSIONAL to "Professional Profile")
            if (agentProfileData != null) add(ProfileType.AGENT to "Agent Profile")
            if (employerProfileData != null) add(ProfileType.EMPLOYER to "Employer Profile")
            if (jobSeekerProfileData != null) add(ProfileType.JOB_SEEKER to "Job Seeker Profile")
            if (propertyOwnerProfileData != null) add(ProfileType.PROPERTY_OWNER to "Property Owner Profile")
            if (propertySeekerProfileData != null) add(ProfileType.PROPERTY_SEEKER to "Property Seeker Profile")
            if (serviceProviderProfileData != null) add(ProfileType.SERVICE_PROVIDER to "Service Provider Profile")
            if (beneficiaryProfileData != null) add(ProfileType.BENEFICIARY to "Beneficiary Profile")
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 0.dp)
            ) {
                // Non-sticky Header (scrolls away)
                item {
                    ReusableHeader(
                        colorScheme = colorScheme,
                        pageTitle = "Profile",
                        pageSubtitle = if (isGuestMode) "Sign in to access your account" else "Manage your identity",
                        user = user,
                        isGuestMode = isGuestMode,
                        isSticky = false,
                        scrollOffset = scrollOffset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // STICKY TABS - touches top edge when sticky
                stickyHeader(key = "tabRow") {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                            color = softBackground,
                            tonalElevation = 2.dp
                        ) {
                            // Apply status bar padding to the content inside when scrolled
                            // This pushes the tab content down, but the background touches the edge
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (isScrolled) {
                                            Modifier.statusBarsPadding()
                                        } else {
                                            Modifier
                                        }
                                    )
                            ) {
                                ScrollableTabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    containerColor = Color.Transparent,
                                    edgePadding = 16.dp,
                                    divider = {},
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                            color = primaryTeal,
                                            height = 3.dp
                                        )
                                    }
                                ) {
                                    tabs.forEachIndexed { index, (_, title) ->
                                        Tab(
                                            selected = selectedTabIndex == index,
                                            onClick = { selectedTabIndex = index },
                                            text = {
                                                Text(
                                                    text = title,
                                                    fontWeight = if (selectedTabIndex == index)
                                                        FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 14.sp,
                                                    color = if (selectedTabIndex == index)
                                                        primaryTeal else colorScheme.onSurfaceVariant
                                                )
                                            },
                                            selectedContentColor = primaryTeal,
                                            unselectedContentColor = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB CONTENT
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        if (tabs.isNotEmpty()) {
                            when (tabs[selectedTabIndex].first) {
                                ProfileType.ACCOUNT -> AccountTabContent(
                                    accountData = accountData,
                                    userData = userData,
                                    individualProfileData = individualProfileData,
                                    organizationProfileData = organizationProfileData,
                                    verifications = verifications,
                                    primaryColor = primaryTeal,
                                    goldenAccent = goldenAccent,
                                    purpleAccent = purpleAccent,
                                    warningColor = WarningAmber,
                                    successColor = SuccessGreen,
                                    colorScheme = colorScheme,
                                    onNavigateToSettings = onNavigateToSettings,
                                    onNavigateToTeamManagement = onNavigateToTeamManagement,
                                    onNavigateToVerification = onNavigateToVerification,
                                    onNavigateToSubscription = onNavigateToSubscription,
                                    onNavigateToPaymentMethods = onNavigateToPaymentMethods,
                                    onNavigateToBillingHistory = onNavigateToBillingHistory,
                                    onNavigateToHelpCenter = onNavigateToHelpCenter,
                                    onSignOut = onSignOut,
                                    onEditPersonalInfo = { onNavigateToEditProfile(ProfileType.ACCOUNT) },
                                    onEditVerification = { onNavigateToEditProfile(ProfileType.ACCOUNT) },
                                    isGuestMode = isGuestMode
                                )

                                ProfileType.PROFESSIONAL -> ProfessionalTabContent(
                                    professionalProfileData = professionalProfileData!!,
                                    primaryColor = primaryTeal,
                                    goldenAccent = goldenAccent,
                                    colorScheme = colorScheme,
                                    onEditOverview = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                    onEditSpecialties = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                    onEditServiceAreas = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                    onEditBusinessDetails = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                    onEditPortfolio = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) }
                                )
                                // ... other tabs remain the same
                                else -> {}
                            }
                        } else {
                            // Empty state for guest mode
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.PersonOutline,
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sign in to view your profile",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Create an account or log in to access your personal information, listings, and more.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
// Add guest mode mock data
fun mockGuestAccountData(): AccountData {
    return AccountData(
        id = "",
        uuid = "",
        accountCode = "",
        name = "Guest User",
        type = AccountType.INDIVIDUAL,
        status = "GUEST",
        isVerified = false,
        verifiedFeatures = emptyList(),
        createdAt = "",
        updatedAt = ""
    )
}



// ==================== PROFILE HEADER - exactly like Dashboard ====================

@Composable
fun ProfileHeroHeader(
    primaryColor: Color,
    accentColor: Color,
    height: Dp,
    collapseFraction: Float,
    userName: String,
    userEmail: String,
    isVerified: Boolean,
    titlesDisplay: String,
    profileImage: String?,
    onEditProfileClick: () -> Unit,
    colorScheme: ColorScheme,
    isWide: Boolean,
    isGuestMode: Boolean = false,  // Add this parameter
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = if (isWide) 40.sp else 32.sp
    val minFontSize = if (isWide) 28.sp else 22.sp

    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) primaryColor.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    val titleFontSize = ((maxFontSize.value - collapseFraction * (maxFontSize.value - minFontSize.value))).sp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shadowElevation = if (collapsed) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background image (visible when expanded) - hide for guest mode
            if (!isGuestMode) {
                AnimatedVisibility(
                    visible = !collapsed,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.dashbaordd)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Background color animation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient overlay (visible when expanded) - hide for guest mode
            if (!isGuestMode) {
                AnimatedVisibility(
                    visible = !collapsed,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        primaryColor.copy(0.9f),
                                        primaryColor.copy(0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            // Top row with avatar and icons (visible when expanded)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                AnimatedVisibility(
                    visible = !collapsed,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                HeaderAvatarProfile(profileImage, colorScheme, isGuestMode)
                                if (!isGuestMode) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(colorScheme.tertiary, CircleShape)
                                            .border(1.5.dp, primaryColor, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))

                            Column {
                                Text(
                                    userName,
                                    color = if (isGuestMode) primaryColor else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    if (isGuestMode) "Sign in for full access" else titlesDisplay,
                                    color = if (isGuestMode) primaryColor.copy(0.7f) else Color.White.copy(0.85f),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HeaderActionIconProfile(
                                icon = Icons.Rounded.Edit,
                                iconTint = if (isGuestMode) primaryColor else Color.White,
                                backgroundTint = if (isGuestMode) primaryColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f),
                                size = if (isWide) 44.dp else 36.dp,
                                onClick = onEditProfileClick
                            )
                        }
                    }
                }

                if (!collapsed) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            if (collapsed) {
                // Collapsed header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp, end = 16.dp)
                        .offset(y = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (isVerified && !isGuestMode) accentColor else colorScheme.surface.copy(0.3f),
                                    CircleShape
                                )
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileImage != null && !isGuestMode) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(profileImage)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    error = painterResource(id = R.drawable.ic_launcher_background)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    null,
                                    tint = if (isGuestMode) primaryColor else colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = userName,
                                color = if (isGuestMode) primaryColor else colorScheme.onPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = if (isGuestMode) "Guest" else titlesDisplay,
                                color = if (isGuestMode) primaryColor.copy(0.7f) else colorScheme.onPrimary.copy(0.8f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = if (isGuestMode) primaryColor else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = titleFontSize
                        )
                    )
                }
            } else {
                // Expanded header - centered content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 24.dp)
                        .statusBarsPadding()
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = if (isGuestMode) primaryColor else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = titleFontSize
                        )
                    )

                    Text(
                        text = if (isGuestMode) "Sign in to access your account" else "Manage your identity",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isGuestMode) primaryColor.copy(0.8f) else Color.White.copy(0.9f),
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderAvatarProfile(
    profileImage: String?,
    colorScheme: ColorScheme,
    isGuestMode: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(colorScheme.onSurface.copy(0.2f), CircleShape)
            .border(1.5.dp, colorScheme.onSurface.copy(0.5f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (profileImage != null && !isGuestMode) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(id = R.drawable.ic_launcher_background)
            )
        } else {
            Icon(
                imageVector = Icons.Default.PersonOutline,
                contentDescription = "User Avatar",
                tint = colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun HeaderActionIconProfile(
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    backgroundTint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    size: Dp = 40.dp,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(size)
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.8f)
                .background(backgroundTint, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}

// ==================== TAB 1: ACCOUNT ====================

@Composable
fun AccountTabContent(
    accountData: AccountData,
    userData: UserData?,
    individualProfileData: IndividualProfileData?,
    organizationProfileData: OrganizationProfileData?,
    verifications: List<VerificationItemData>,
    primaryColor: Color,
    goldenAccent: Color,
    purpleAccent: Color,
    warningColor: Color,
    successColor: Color,
    colorScheme: ColorScheme,
    onNavigateToSettings: () -> Unit,
    onNavigateToTeamManagement: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToBillingHistory: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onSignOut: () -> Unit,
    onEditPersonalInfo: () -> Unit,
    onEditVerification: () -> Unit,
    isGuestMode: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        // If guest mode, show sign in prompt
        if (isGuestMode) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.PersonOutline,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sign in to your account",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to view and manage your profile, listings, and account settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onSignOut,  // This will trigger navigation to login
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In", color = Color.White)
                }
            }
            return
        }
        // Account Overview
        ProfileSection(
            title = "Account Overview",
            action = {
                IconButton(onClick = onEditPersonalInfo) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.AccountCircle,
                label = "Account Code",
                subtitle = accountData.accountCode,
                onClick = {},
                iconColor = primaryColor,
                showDivider = true
            )
            ProfileItem(
                icon = Icons.Default.Category,
                label = "Account Type",
                subtitle = accountData.type.name,
                onClick = {},
                iconColor = primaryColor,
                showDivider = true
            )
            ProfileItem(
                icon = Icons.Default.Info,
                label = "Status",
                subtitle = accountData.status,
                onClick = {},
                iconColor = when (accountData.status) {
                    "ACTIVE" -> successColor
                    "PENDING_PAYMENT" -> warningColor
                    else -> errorLight
                },
                showDivider = false
            )
            ProfileItem(
                icon = Icons.Default.CalendarToday,
                label = "Member Since",
                subtitle = accountData.createdAt,
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Verification & Badges
        ProfileSection(
            title = "Verification & Badges",
            action = {
                Row {
                    IconButton(onClick = onEditVerification) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    TextButton(onClick = onNavigateToVerification) {
                        Text("Verify", color = primaryColor)
                    }
                }
            }
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                if (accountData.verifiedFeatures.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        items(accountData.verifiedFeatures) { feature ->
                            when (feature) {
                                "IDENTITY" -> BadgeItem(
                                    icon = Icons.Default.Verified,
                                    label = "ID Verified",
                                    color = successColor,
                                    small = true
                                )
                                "BUSINESS" -> BadgeItem(
                                    icon = Icons.Default.Business,
                                    label = "Business",
                                    color = primaryColor,
                                    small = true
                                )
                                "PROFESSIONAL_LICENSE" -> BadgeItem(
                                    icon = Icons.Default.MilitaryTech,
                                    label = "Licensed",
                                    color = goldenAccent,
                                    small = true
                                )
                                "AGENT_LICENSE" -> BadgeItem(
                                    icon = Icons.Default.Person,
                                    label = "Agent",
                                    color = purpleAccent,
                                    small = true
                                )
                                "NGO_REGISTRATION" -> BadgeItem(
                                    icon = Icons.Default.VolunteerActivism,
                                    label = "NGO",
                                    color = Color(0xFF4CAF50),
                                    small = true
                                )
                                else -> BadgeItem(
                                    icon = Icons.Default.Verified,
                                    label = feature.replace("_", " "),
                                    color = primaryColor,
                                    small = true
                                )
                            }
                        }
                    }
                }

                verifications.filter { it.status == VerificationStatus.PENDING }.forEach { verification ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToVerification() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(warningColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = warningColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                when (verification.type) {
                                    "IDENTITY" -> "ID Verification"
                                    "BUSINESS" -> "Business Verification"
                                    "PROFESSIONAL_LICENSE" -> "Professional License"
                                    "AGENT_LICENSE" -> "Agent License"
                                    "NGO_REGISTRATION" -> "NGO Registration"
                                    else -> verification.type.replace("_", " ")
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                "Pending verification",
                                fontSize = 13.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider(
                        color = colorScheme.outlineVariant,
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp)
                    )
                }
            }
        }

        if (accountData.type == AccountType.INDIVIDUAL) {
            // Individual Profile Section
            ProfileSection(
                title = "Personal Information",
                action = {
                    IconButton(onClick = onEditPersonalInfo) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                ProfileItem(
                    icon = Icons.Default.Person,
                    label = "Full Name",
                    subtitle = userData?.let { "${it.firstName} ${it.lastName}" } ?: accountData.name ?: "Not set",
                    onClick = {},
                    iconColor = primaryColor
                )
                ProfileItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    subtitle = userData?.email ?: "Not set",
                    onClick = {},
                    iconColor = primaryColor
                )
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    subtitle = userData?.phone ?: "Not set",
                    onClick = {},
                    iconColor = primaryColor
                )
                if (individualProfileData?.dateOfBirth != null) {
                    ProfileItem(
                        icon = Icons.Default.Cake,
                        label = "Date of Birth",
                        subtitle = individualProfileData.dateOfBirth,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (individualProfileData?.gender != null) {
                    ProfileItem(
                        icon = Icons.Default.Wc,
                        label = "Gender",
                        subtitle = individualProfileData.gender,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (individualProfileData?.nationalId != null) {
                    ProfileItem(
                        icon = Icons.Default.Badge,
                        label = "National ID",
                        subtitle = individualProfileData.nationalId,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }

            if (!individualProfileData?.bio.isNullOrBlank()) {
                ProfileSection(
                    title = "About Me",
                    action = {
                        IconButton(onClick = onEditPersonalInfo) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = individualProfileData?.bio ?: "",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            // Organization Profile Section
            organizationProfileData?.let { orgData ->
                ProfileSection(
                    title = "Organization Information",
                    action = {
                        IconButton(onClick = onEditPersonalInfo) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                ) {
                    ProfileItem(
                        icon = Icons.Default.Business,
                        label = "Organization Name",
                        subtitle = orgData.name,
                        onClick = {},
                        iconColor = primaryColor
                    )
                    ProfileItem(
                        icon = Icons.Default.Email,
                        label = "Official Email",
                        subtitle = orgData.officialEmail ?: "Not set",
                        onClick = {},
                        iconColor = primaryColor
                    )
                    ProfileItem(
                        icon = Icons.Default.Phone,
                        label = "Official Phone",
                        subtitle = orgData.officialPhone ?: "Not set",
                        onClick = {},
                        iconColor = primaryColor
                    )
                    ProfileItem(
                        icon = Icons.Default.Description,
                        label = "Registration No",
                        subtitle = orgData.registrationNo ?: "Not set",
                        onClick = {},
                        iconColor = primaryColor
                    )
                    ProfileItem(
                        icon = Icons.Default.Receipt,
                        label = "KRA PIN",
                        subtitle = orgData.kraPin ?: "Not set",
                        onClick = {},
                        iconColor = primaryColor
                    )
                    ProfileItem(
                        icon = Icons.Default.Category,
                        label = "Organization Type",
                        subtitle = orgData.type ?: "Not set",
                        onClick = {},
                        iconColor = primaryColor
                    )
                    ProfileItem(
                        icon = Icons.Default.Language,
                        label = "Website",
                        subtitle = orgData.website ?: "Not set",
                        onClick = {},
                        iconColor = primaryColor
                    )
                    ProfileItem(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        subtitle = orgData.physicalAddress ?: "Not set",
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }

                if (!orgData.about.isNullOrBlank()) {
                    ProfileSection(
                        title = "About",
                        action = {
                            IconButton(onClick = onEditPersonalInfo) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = orgData.about,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                // Team Snapshot (count only)
                if (orgData.members.isNotEmpty() || orgData.pendingInvitations.isNotEmpty()) {
                    ProfileSection(
                        title = "Team",
                        action = {
                            Row {
                                IconButton(onClick = { /* Edit team */ }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                TextButton(onClick = onNavigateToTeamManagement) {
                                    Text("Manage", color = primaryColor)
                                }
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            if (orgData.members.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(primaryColor.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Group,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            "Team Members",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colorScheme.onSurface
                                        )
                                        Text(
                                            "${orgData.members.size} members",
                                            fontSize = 13.sp,
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = primaryColor.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            "View",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            fontSize = 12.sp,
                                            color = primaryColor
                                        )
                                    }
                                }
                            }

                            if (orgData.pendingInvitations.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(warningColor.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.HourglassEmpty,
                                            contentDescription = null,
                                            tint = warningColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            "Pending Invitations",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colorScheme.onSurface
                                        )
                                        Text(
                                            "${orgData.pendingInvitations.size} awaiting response",
                                            fontSize = 13.sp,
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Preferences & Settings
        ProfileSection(
            title = "Preferences & Settings",
            action = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                subtitle = "Email, Push, SMS",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Language,
                label = "Language",
                subtitle = "English",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Palette,
                label = "Theme",
                subtitle = "System default",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Lock,
                label = "Privacy & Security",
                subtitle = "2FA enabled",
                onClick = onNavigateToSettings,
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Subscription & Billing
        ProfileSection(
            title = "Subscription & Billing",
            action = {
                IconButton(onClick = onNavigateToSubscription) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Payment,
                label = "Subscription Plan",
                subtitle = "Pro Plan • 1,500 KES/month",
                onClick = onNavigateToSubscription,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.CreditCard,
                label = "Payment Methods",
                subtitle = "M-Pesa • 2 cards",
                onClick = onNavigateToPaymentMethods,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.History,
                label = "Billing History",
                subtitle = "View past invoices",
                onClick = onNavigateToBillingHistory,
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Support & Info
        ProfileSection(title = "Support") {
            ProfileItem(
                icon = Icons.Default.HelpOutline,
                label = "Help Center",
                subtitle = "FAQs, Guides, Tutorials",
                onClick = onNavigateToHelpCenter,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Info,
                label = "About PivotaConnect",
                subtitle = "Version 1.0.0 • MVP1",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Description,
                label = "Terms of Service",
                subtitle = "Read our terms",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.PrivacyTip,
                label = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Sign Out
        ProfileSection(title = "Account") {
            ProfileItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Sign Out",
                subtitle = "",
                onClick = onSignOut,
                iconColor = errorLight,
                textColor = errorLight,
                showDivider = false
            )
        }
    }
}

// ==================== TAB 2: PROFESSIONAL ====================

@Composable
fun ProfessionalTabContent(
    professionalProfileData: ProfessionalProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditSpecialties: () -> Unit,
    onEditServiceAreas: () -> Unit,
    onEditBusinessDetails: () -> Unit,
    onEditPortfolio: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Professional Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!professionalProfileData.title.isNullOrBlank()) {
                    Text(
                        text = professionalProfileData.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Experience",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${professionalProfileData.yearsExperience ?: 0}+ years",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            "Rating",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = goldenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                String.format("%.1f", professionalProfileData.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                " (${professionalProfileData.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column {
                        Text(
                            "Jobs",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${professionalProfileData.completedJobs}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                }

                if (professionalProfileData.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Professional",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Specialties
        if (professionalProfileData.specialties.isNotEmpty()) {
            ProfileSection(
                title = "Specialties",
                action = {
                    IconButton(onClick = onEditSpecialties) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(professionalProfileData.specialties) { specialty ->
                            AssistChip(
                                onClick = {},
                                label = { Text(specialty) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Service Areas
        if (professionalProfileData.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditServiceAreas) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(professionalProfileData.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Business Details
        ProfileSection(
            title = "Business Details",
            action = {
                IconButton(onClick = onEditBusinessDetails) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!professionalProfileData.licenseNumber.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "License Number",
                    subtitle = professionalProfileData.licenseNumber,
                    onClick = {},
                    iconColor = if (professionalProfileData.isVerified) SuccessGreen else primaryColor
                )
            }
            if (!professionalProfileData.insuranceInfo.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Security,
                    label = "Insurance",
                    subtitle = professionalProfileData.insuranceInfo,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (professionalProfileData.hourlyRate != null) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Hourly Rate",
                    subtitle = "KES ${professionalProfileData.hourlyRate}",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!professionalProfileData.paymentTerms.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Payment,
                    label = "Payment Terms",
                    subtitle = professionalProfileData.paymentTerms,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Portfolio
        if (professionalProfileData.portfolioImages.isNotEmpty()) {
            ProfileSection(
                title = "Portfolio",
                action = {
                    IconButton(onClick = onEditPortfolio) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(professionalProfileData.portfolioImages.take(5)) { imageUrl ->
                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                error = painterResource(id = R.drawable.ic_launcher_background)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 3: AGENT ====================

@Composable
fun AgentTabContent(
    agentProfileData: AgentProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditSpecializations: () -> Unit,
    onEditLicense: () -> Unit,
    onEditServiceAreas: () -> Unit,
    onEditCommission: () -> Unit,
    onEditContact: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Agent Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = agentProfileData.agentType.replace("_", " "),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Experience",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${agentProfileData.yearsExperience ?: 0}+ years",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            "Rating",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = goldenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                String.format("%.1f", agentProfileData.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                " (${agentProfileData.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column {
                        Text(
                            "Deals",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${agentProfileData.completedDeals}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                }

                if (agentProfileData.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Agent",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Specializations
        if (agentProfileData.specializations.isNotEmpty()) {
            ProfileSection(
                title = "Specializations",
                action = {
                    IconButton(onClick = onEditSpecializations) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(agentProfileData.specializations) { spec ->
                            AssistChip(
                                onClick = {},
                                label = { Text(spec) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // License & Credentials
        if (!agentProfileData.licenseNumber.isNullOrBlank()) {
            ProfileSection(
                title = "License & Credentials",
                action = {
                    IconButton(onClick = onEditLicense) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "License Number",
                    subtitle = agentProfileData.licenseNumber,
                    onClick = {},
                    iconColor = primaryColor
                )
                if (!agentProfileData.licenseBody.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Business,
                        label = "Issuing Body",
                        subtitle = agentProfileData.licenseBody,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!agentProfileData.agencyName.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Apartment,
                        label = "Agency",
                        subtitle = agentProfileData.agencyName,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Service Areas
        if (agentProfileData.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditServiceAreas) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(agentProfileData.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Commission & Fees
        ProfileSection(
            title = "Commission & Fees",
            action = {
                IconButton(onClick = onEditCommission) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (agentProfileData.commissionRate != null) {
                ProfileItem(
                    icon = Icons.Default.Percent,
                    label = "Commission Rate",
                    subtitle = "${agentProfileData.commissionRate}%",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfileData.feeStructure.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Fee Structure",
                    subtitle = agentProfileData.feeStructure,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (agentProfileData.minimumFee != null) {
                ProfileItem(
                    icon = Icons.Default.Payment,
                    label = "Minimum Fee",
                    subtitle = "KES ${agentProfileData.minimumFee}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Contact Info
        ProfileSection(
            title = "Contact Information",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!agentProfileData.contactEmail.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    subtitle = agentProfileData.contactEmail,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfileData.contactPhone.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    subtitle = agentProfileData.contactPhone,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfileData.website.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Language,
                    label = "Website",
                    subtitle = agentProfileData.website,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // About
        if (!agentProfileData.about.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditContact) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = agentProfileData.about,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==================== TAB 4: EMPLOYER ====================

@Composable
fun EmployerTabContent(
    employerProfileData: EmployerProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditDescription: () -> Unit,
    onEditPreferences: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Company Overview
        ProfileSection(
            title = "Company Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = employerProfileData.companyName ?: "Company Name",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!employerProfileData.industry.isNullOrBlank()) {
                        Column {
                            Text(
                                "Industry",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                employerProfileData.industry,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    if (!employerProfileData.companySize.isNullOrBlank()) {
                        Column {
                            Text(
                                "Size",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                employerProfileData.companySize,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    if (employerProfileData.foundedYear != null) {
                        Column {
                            Text(
                                "Founded",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${employerProfileData.foundedYear}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (employerProfileData.isVerifiedEmployer) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Employer",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Description
        if (!employerProfileData.description.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditDescription) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = employerProfileData.description,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Hiring Preferences
        if (employerProfileData.preferredSkills.isNotEmpty()) {
            ProfileSection(
                title = "Preferred Skills",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(employerProfileData.preferredSkills) { skill ->
                            AssistChip(
                                onClick = {},
                                label = { Text(skill) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        if (!employerProfileData.remotePolicy.isNullOrBlank()) {
            ProfileSection(
                title = "Work Policy",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                ProfileItem(
                    icon = Icons.Default.Work,
                    label = "Remote Policy",
                    subtitle = employerProfileData.remotePolicy,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}

// ==================== TAB 5: JOB SEEKER ====================

@Composable
fun JobSeekerTabContent(
    jobSeekerProfileData: JobSeekerProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditBranding: () -> Unit,
    onEditSkills: () -> Unit,
    onEditIndustries: () -> Unit,
    onEditPreferences: () -> Unit,
    onEditWorkAuth: () -> Unit,
    onEditCV: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Professional Branding
        ProfileSection(
            title = "Professional Profile",
            action = {
                IconButton(onClick = onEditBranding) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!jobSeekerProfileData.headline.isNullOrBlank()) {
                    Text(
                        text = jobSeekerProfileData.headline,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (jobSeekerProfileData.isActivelySeeking)
                            SuccessGreen.copy(alpha = 0.1f)
                        else
                            colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Accessibility,
                                contentDescription = null,
                                tint = if (jobSeekerProfileData.isActivelySeeking) SuccessGreen else colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (jobSeekerProfileData.isActivelySeeking) "Actively Seeking" else "Open to Opportunities",
                                fontSize = 12.sp,
                                color = if (jobSeekerProfileData.isActivelySeeking) SuccessGreen else colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Skills
        if (jobSeekerProfileData.skills.isNotEmpty()) {
            ProfileSection(
                title = "Skills",
                action = {
                    IconButton(onClick = onEditSkills) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jobSeekerProfileData.skills) { skill ->
                            AssistChip(
                                onClick = {},
                                label = { Text(skill) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Industries
        if (jobSeekerProfileData.industries.isNotEmpty()) {
            ProfileSection(
                title = "Industries",
                action = {
                    IconButton(onClick = onEditIndustries) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jobSeekerProfileData.industries) { industry ->
                            AssistChip(
                                onClick = {},
                                label = { Text(industry) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Job Preferences
        ProfileSection(
            title = "Job Preferences",
            action = {
                IconButton(onClick = onEditPreferences) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (jobSeekerProfileData.jobTypes.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Work,
                    label = "Job Types",
                    subtitle = jobSeekerProfileData.jobTypes.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.seniorityLevel.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.TrendingUp,
                    label = "Seniority Level",
                    subtitle = jobSeekerProfileData.seniorityLevel,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.noticePeriod.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Schedule,
                    label = "Notice Period",
                    subtitle = jobSeekerProfileData.noticePeriod,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (jobSeekerProfileData.expectedSalary != null) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Expected Salary",
                    subtitle = "KES ${jobSeekerProfileData.expectedSalary}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Work Authorization
        if (jobSeekerProfileData.workAuthorization.isNotEmpty()) {
            ProfileSection(
                title = "Work Authorization",
                action = {
                    IconButton(onClick = onEditWorkAuth) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jobSeekerProfileData.workAuthorization) { auth ->
                            AssistChip(
                                onClick = {},
                                label = { Text(auth) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // CV & Links
        ProfileSection(
            title = "CV & Professional Links",
            action = {
                IconButton(onClick = onEditCV) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!jobSeekerProfileData.cvUrl.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "CV/Resume",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        if (jobSeekerProfileData.cvLastUpdated != null) {
                            Text(
                                "Updated ${jobSeekerProfileData.cvLastUpdated}",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Rounded.Download,
                        contentDescription = null,
                        tint = primaryColor
                    )
                }
            }

            if (!jobSeekerProfileData.linkedInUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Info,
                    label = "LinkedIn",
                    subtitle = "View Profile",
                    onClick = {},
                    iconColor = Color(0xFF0077B5)
                )
            }
            if (!jobSeekerProfileData.portfolioUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Link,
                    label = "Portfolio",
                    subtitle = "View Website",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.githubUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Code,
                    label = "GitHub",
                    subtitle = "View Profile",
                    onClick = {},
                    iconColor = Color.Black,
                    showDivider = false
                )
            }
        }
    }
}

// ==================== TAB 6: PROPERTY OWNER ====================

@Composable
fun PropertyOwnerTabContent(
    propertyOwnerProfileData: PropertyOwnerProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditProfessionalDetails: () -> Unit,
    onEditPreferences: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Property Owner Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Type",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (propertyOwnerProfileData.isProfessional) "Professional" else "Individual Owner",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    if (propertyOwnerProfileData.yearsInBusiness != null) {
                        Column {
                            Text(
                                "Years in Business",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${propertyOwnerProfileData.yearsInBusiness}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (propertyOwnerProfileData.isVerifiedOwner) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Owner",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // License & Company
        if (propertyOwnerProfileData.isProfessional) {
            ProfileSection(
                title = "Professional Details",
                action = {
                    IconButton(onClick = onEditProfessionalDetails) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                if (!propertyOwnerProfileData.licenseNumber.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Badge,
                        label = "License Number",
                        subtitle = propertyOwnerProfileData.licenseNumber,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!propertyOwnerProfileData.companyName.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Business,
                        label = "Company",
                        subtitle = propertyOwnerProfileData.companyName,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Property Preferences
        if (propertyOwnerProfileData.preferredPropertyTypes.isNotEmpty()) {
            ProfileSection(
                title = "Property Types",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(propertyOwnerProfileData.preferredPropertyTypes) { type ->
                            AssistChip(
                                onClick = {},
                                label = { Text(type) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Service Areas
        if (propertyOwnerProfileData.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(propertyOwnerProfileData.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 7: PROPERTY SEEKER ====================

@Composable
fun PropertySeekerTabContent(
    propertySeekerProfileData: PropertySeekerProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditHousing: () -> Unit,
    onEditLocation: () -> Unit,
    onEditMoveIn: () -> Unit,
    onEditHousehold: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Housing Preferences
        ProfileSection(
            title = "Housing Preferences",
            action = {
                IconButton(onClick = onEditHousing) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Bed,
                label = "Bedrooms",
                subtitle = "${propertySeekerProfileData.minBedrooms} - ${propertySeekerProfileData.maxBedrooms}",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.AttachMoney,
                label = "Budget",
                subtitle = buildString {
                    if (propertySeekerProfileData.minBudget != null) {
                        append("KES ${propertySeekerProfileData.minBudget}")
                    }
                    if (propertySeekerProfileData.maxBudget != null) {
                        if (propertySeekerProfileData.minBudget != null) append(" - ")
                        append("KES ${propertySeekerProfileData.maxBudget}")
                    }
                },
                onClick = {},
                iconColor = primaryColor
            )
            if (propertySeekerProfileData.preferredTypes.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Category,
                    label = "Property Types",
                    subtitle = propertySeekerProfileData.preferredTypes.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Location Preferences
        ProfileSection(
            title = "Location Preferences",
            action = {
                IconButton(onClick = onEditLocation) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (propertySeekerProfileData.preferredCities.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.LocationCity,
                    label = "Cities",
                    subtitle = propertySeekerProfileData.preferredCities.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (propertySeekerProfileData.preferredNeighborhoods.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.LocationOn,
                    label = "Neighborhoods",
                    subtitle = propertySeekerProfileData.preferredNeighborhoods.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Move-in Details
        ProfileSection(
            title = "Move-in Details",
            action = {
                IconButton(onClick = onEditMoveIn) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!propertySeekerProfileData.moveInDate.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Move-in Date",
                    subtitle = propertySeekerProfileData.moveInDate,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!propertySeekerProfileData.leaseDuration.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Schedule,
                    label = "Lease Duration",
                    subtitle = propertySeekerProfileData.leaseDuration,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Household
        ProfileSection(
            title = "Household",
            action = {
                IconButton(onClick = onEditHousehold) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.People,
                label = "Household Size",
                subtitle = "${propertySeekerProfileData.householdSize}",
                onClick = {},
                iconColor = primaryColor
            )
            if (propertySeekerProfileData.hasPets != null) {
                ProfileItem(
                    icon = Icons.Default.Pets,
                    label = "Pets",
                    subtitle = if (propertySeekerProfileData.hasPets == true)
                        propertySeekerProfileData.petDetails ?: "Yes"
                    else
                        "No",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}

// ==================== TAB 8: SERVICE PROVIDER ====================

@Composable
fun ServiceProviderTabContent(
    serviceProviderProfileData: ServiceProviderProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditServices: () -> Unit,
    onEditBeneficiaries: () -> Unit,
    onEditAreas: () -> Unit,
    onEditContact: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Service Provider Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = serviceProviderProfileData.providerType.replace("_", " "),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (serviceProviderProfileData.yearEstablished != null) {
                        Column {
                            Text(
                                "Founded",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${serviceProviderProfileData.yearEstablished}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    if (serviceProviderProfileData.peopleServed != null) {
                        Column {
                            Text(
                                "People Served",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${serviceProviderProfileData.peopleServed}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (serviceProviderProfileData.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified by ${serviceProviderProfileData.verifiedBy ?: "Platform"}",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Services Offered
        if (serviceProviderProfileData.servicesOffered.isNotEmpty()) {
            ProfileSection(
                title = "Services Offered",
                action = {
                    IconButton(onClick = onEditServices) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviceProviderProfileData.servicesOffered) { service ->
                            AssistChip(
                                onClick = {},
                                label = { Text(service) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Target Beneficiaries
        if (serviceProviderProfileData.targetBeneficiaries.isNotEmpty()) {
            ProfileSection(
                title = "Target Beneficiaries",
                action = {
                    IconButton(onClick = onEditBeneficiaries) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviceProviderProfileData.targetBeneficiaries) { beneficiary ->
                            AssistChip(
                                onClick = {},
                                label = { Text(beneficiary) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Service Areas
        if (serviceProviderProfileData.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditAreas) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviceProviderProfileData.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // About
        if (!serviceProviderProfileData.about.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditContact) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = serviceProviderProfileData.about,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Contact Info
        ProfileSection(
            title = "Contact Information",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!serviceProviderProfileData.contactEmail.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    subtitle = serviceProviderProfileData.contactEmail,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!serviceProviderProfileData.contactPhone.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    subtitle = serviceProviderProfileData.contactPhone,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!serviceProviderProfileData.website.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Language,
                    label = "Website",
                    subtitle = serviceProviderProfileData.website,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!serviceProviderProfileData.officeHours.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Schedule,
                    label = "Office Hours",
                    subtitle = serviceProviderProfileData.officeHours,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Support Needs
        if (serviceProviderProfileData.acceptsDonations || serviceProviderProfileData.needsVolunteers) {
            ProfileSection(
                title = "Support Needs",
                action = {
                    IconButton(onClick = onEditContact) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                if (serviceProviderProfileData.acceptsDonations) {
                    ProfileItem(
                        icon = Icons.Default.Favorite,
                        label = "Accepts Donations",
                        subtitle = "Yes",
                        onClick = {},
                        iconColor = Color(0xFFE91E63)
                    )
                }
                if (serviceProviderProfileData.needsVolunteers) {
                    ProfileItem(
                        icon = Icons.Default.People,
                        label = "Needs Volunteers",
                        subtitle = "Yes",
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }
    }
}

// ==================== TAB 9: BENEFICIARY ====================

@Composable
fun BeneficiaryTabContent(
    beneficiaryProfileData: BeneficiaryProfileData,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditNeeds: () -> Unit,
    onEditHousehold: () -> Unit,
    onEditLocation: () -> Unit,
    onEditPrivacy: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Needs
        ProfileSection(
            title = "Needs",
            action = {
                IconButton(onClick = onEditNeeds) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (beneficiaryProfileData.needs.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfileData.needs) { need ->
                            AssistChip(
                                onClick = {},
                                label = { Text(need) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }

            if (beneficiaryProfileData.urgentNeeds.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    Text(
                        "Urgent Needs",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = errorLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfileData.urgentNeeds) { need ->
                            AssistChip(
                                onClick = {},
                                label = { Text(need) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = errorLight.copy(alpha = 0.1f),
                                    labelColor = errorLight
                                )
                            )
                        }
                    }
                }
            }
        }

        // Household
        ProfileSection(
            title = "Household",
            action = {
                IconButton(onClick = onEditHousehold) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (beneficiaryProfileData.familySize != null) {
                ProfileItem(
                    icon = Icons.Default.People,
                    label = "Family Size",
                    subtitle = "${beneficiaryProfileData.familySize}",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (beneficiaryProfileData.dependents != null) {
                ProfileItem(
                    icon = Icons.Default.ChildCare,
                    label = "Dependents",
                    subtitle = "${beneficiaryProfileData.dependents}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Vulnerability Factors
        if (beneficiaryProfileData.vulnerabilityFactors.isNotEmpty()) {
            ProfileSection(
                title = "Vulnerability Factors",
                action = {
                    IconButton(onClick = onEditNeeds) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfileData.vulnerabilityFactors) { factor ->
                            AssistChip(
                                onClick = {},
                                label = { Text(factor) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = errorLight.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Location
        if (!beneficiaryProfileData.city.isNullOrBlank() || !beneficiaryProfileData.neighborhood.isNullOrBlank()) {
            ProfileSection(
                title = "Location",
                action = {
                    IconButton(onClick = onEditLocation) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                if (!beneficiaryProfileData.city.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.LocationCity,
                        label = "City",
                        subtitle = beneficiaryProfileData.city,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!beneficiaryProfileData.neighborhood.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.LocationOn,
                        label = "Neighborhood",
                        subtitle = beneficiaryProfileData.neighborhood,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Privacy Settings
        ProfileSection(
            title = "Privacy Settings",
            action = {
                IconButton(onClick = onEditPrivacy) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.PrivacyTip,
                label = "Anonymous Profile",
                subtitle = if (beneficiaryProfileData.prefersAnonymity) "Yes" else "No",
                onClick = {},
                iconColor = primaryColor
            )
            if (beneficiaryProfileData.languagePreference.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Language,
                    label = "Language Preference",
                    subtitle = beneficiaryProfileData.languagePreference.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}

// ==================== HELPER COMPOSABLES ====================

@Composable
fun ProfileSection(
    title: String,
    action: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (action != null) {
                action()
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    label: String,
    subtitle: String = "",
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    showDivider: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant
            )
        }

        if (showDivider) {
            HorizontalDivider(
                color = colorScheme.outlineVariant,
                modifier = Modifier.padding(start = 72.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun BadgeItem(
    icon: ImageVector,
    label: String,
    color: Color,
    small: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(if (small) 60.dp else 70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (small) 40.dp else 48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(if (small) 20.dp else 24.dp)
            )
        }
        Text(
            text = label,
            fontSize = if (small) 9.sp else 10.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// ==================== MOCK DATA HELPERS ====================

fun mockAccountData(): AccountData {
    return AccountData(
        id = "1",
        uuid = "acc_123",
        accountCode = "ACC001",
        name = "Allan Mathenge",
        type = AccountType.INDIVIDUAL,
        status = "ACTIVE",
        isVerified = true,
        verifiedFeatures = listOf("IDENTITY", "PROFESSIONAL_LICENSE"),
        createdAt = "2024-01-15",
        updatedAt = "2024-03-20"
    )
}

fun mockUserData(): UserData {
    return UserData(
        uuid = "user_123",
        userCode = "USR001",
        email = "allan.mathenge@example.com",
        phone = "+254712345678",
        firstName = "Allan",
        lastName = "Mathenge",
        roleName = "Professional",
        profileImage = null,
        status = "ACTIVE"
    )
}

fun mockIndividualProfileData(): IndividualProfileData {
    return IndividualProfileData(
        firstName = "Allan",
        lastName = "Mathenge",
        bio = "Professional contractor with 5+ years of experience in electrical and plumbing services. Passionate about quality work and customer satisfaction.",
        gender = "Male",
        dateOfBirth = "1990-01-15",
        nationalId = "12345678",
        profileImage = null
    )
}

fun mockOrganizationProfileData(): OrganizationProfileData? {
    return null
}

fun mockProfessionalProfileData(): ProfessionalProfileData? {
    return ProfessionalProfileData(
        uuid = "prof_123",
        title = "Master Electrician & Plumber",
        specialties = listOf("Electrical", "Plumbing", "Solar Installation"),
        serviceAreas = listOf("Nairobi", "Kiambu", "Machakos"),
        yearsExperience = 5,
        licenseNumber = "ELC-2024-12345",
        insuranceInfo = "Fully Insured",
        hourlyRate = 1500f,
        paymentTerms = "Hourly or Fixed",
        isVerified = true,
        averageRating = 4.8f,
        totalReviews = 89,
        completedJobs = 47,
        portfolioImages = emptyList()
    )
}

fun mockAgentProfileData(): AgentProfileData? {
    return null
}

fun mockEmployerProfileData(): EmployerProfileData? {
    return null
}

fun mockJobSeekerProfileData(): JobSeekerProfileData? {
    return JobSeekerProfileData(
        headline = "Senior Full Stack Developer",
        isActivelySeeking = true,
        skills = listOf("Kotlin", "Jetpack Compose", "Firebase", "Spring Boot"),
        industries = listOf("FinTech", "HealthTech", "E-commerce"),
        jobTypes = listOf("FULL_TIME", "CONTRACT"),
        seniorityLevel = "SENIOR",
        noticePeriod = "1 Month",
        expectedSalary = 250000f,
        workAuthorization = listOf("Citizen", "Remote Only"),
        cvUrl = "https://example.com/cv.pdf",
        cvLastUpdated = "2024-03-15",
        linkedInUrl = "https://linkedin.com/in/allan",
        portfolioUrl = "https://allan.dev",
        githubUrl = "https://github.com/allan"
    )
}

fun mockPropertyOwnerProfileData(): PropertyOwnerProfileData? {
    return null
}

fun mockPropertySeekerProfileData(): PropertySeekerProfileData? {
    return PropertySeekerProfileData(
        minBedrooms = 2,
        maxBedrooms = 3,
        minBudget = 35000f,
        maxBudget = 50000f,
        preferredTypes = listOf("APARTMENT", "HOUSE"),
        preferredCities = listOf("Nairobi"),
        preferredNeighborhoods = listOf("Kilimani", "Lavington", "Westlands"),
        moveInDate = "2024-06-01",
        leaseDuration = "1 Year",
        householdSize = 2,
        hasPets = false,
        petDetails = null
    )
}

fun mockServiceProviderProfileData(): ServiceProviderProfileData? {
    return null
}

fun mockBeneficiaryProfileData(): BeneficiaryProfileData? {
    return null
}

fun mockTeamMembers(): List<TeamMemberData> {
    return emptyList()
}

fun mockPendingInvitations(): List<PendingInvitationData> {
    return emptyList()
}

fun mockVerifications(): List<VerificationItemData> {
    return listOf(
        VerificationItemData(
            type = "PROFESSIONAL_LICENSE",
            status = VerificationStatus.APPROVED,
            documentUrl = null,
            rejectionReason = null,
            verifiedAt = "2024-02-01",
            expiresAt = "2025-02-01"
        ),
        VerificationItemData(
            type = "BUSINESS",
            status = VerificationStatus.PENDING,
            documentUrl = null,
            rejectionReason = null,
            verifiedAt = null,
            expiresAt = null
        )
    )
}

fun mockReviews(): List<Review> {
    return listOf(
        Review("1", "Sarah Kimani", "", 5.0f, "Excellent work! Very professional and completed on time.", "2 days ago"),
        Review("2", "John Mburu", "", 4.5f, "Good quality work. Would recommend.", "1 week ago"),
        Review("3", "Mary Wanjiku", "", 5.0f, "Fixed our electrical issues quickly. Fair pricing.", "2 weeks ago")
    )
}