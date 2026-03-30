package com.example.pivota.welcome.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.buttons.AuthGoogleButton
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSkipButton
import com.example.pivota.ui.theme.*


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PurposeSelectionScreenContent(
    onContinue: (purpose: String, purposeData: Map<String, Any>) -> Unit,
    onSkipToDashboard: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onJustExploring: () -> Unit, // New callback for just exploring
    currentStep: Int = 2,
    totalSteps: Int = 6,
    modifier: Modifier = Modifier
) {
    var selectedPurpose by remember { mutableStateOf<String?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Dynamic field states
    var jobSeekerData by remember { mutableStateOf(JobSeekerData()) }
    var skilledProfessionalData by remember { mutableStateOf(SkilledProfessionalData()) }
    var agentData by remember { mutableStateOf(AgentData()) }
    var housingSeekerData by remember { mutableStateOf(HousingSeekerData()) }
    var supportBeneficiaryData by remember { mutableStateOf(SupportBeneficiaryData()) }
    var employerData by remember { mutableStateOf(EmployerData()) }
    var propertyOwnerData by remember { mutableStateOf(PropertyOwnerData()) }

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    // Purpose options - Just Exploring moved to the top
    val purposeOptions = listOf(
        PurposeOption("Just Exploring", Icons.Default.Explore, "✨", "Explore what Pivota has to offer before deciding"),
        PurposeOption("Find a Job", Icons.Default.Work, "🔍", "Search and apply for jobs that match your skills"),
        PurposeOption("Offer Skilled Services", Icons.Default.Build, "🔧", "Showcase your expertise and get hired by clients"),
        PurposeOption("Work as Agent", Icons.Default.Person, "🤝", "Help others find opportunities and earn commissions"),
        PurposeOption("Find Housing", Icons.Default.Home, "🏠", "Discover rental properties, apartments, and houses"),
        PurposeOption("Get Social Support", Icons.Default.Favorite, "❤️", "Access community support services and resources"),
        PurposeOption("Hire Employees", Icons.Default.Business, "👔", "Find talented professionals for your business"),
        PurposeOption("List Properties", Icons.Default.House, "📋", "Rent out your properties to qualified tenants")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Headline
            Text(
                text = "What's your main goal?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Helper Text
            Text(
                text = "Choose your primary focus—you can add more roles later",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Professional Selection Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBottomSheet = true },
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Icon container
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedPurpose != null)
                                        colorScheme.primary.copy(alpha = 0.1f)
                                    else
                                        colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedPurpose != null) {
                                    purposeOptions.find { it.label == selectedPurpose }?.icon ?: Icons.Default.Info
                                } else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (selectedPurpose != null)
                                    colorScheme.primary
                                else
                                    colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (selectedPurpose != null) "Selected Purpose" else "Select Your Purpose",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = selectedPurpose ?: "Choose your primary focus",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (selectedPurpose != null) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selectedPurpose != null)
                                        colorScheme.primary
                                    else
                                        colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Select purpose",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Helper text below selection
            Text(
                text = "You can add more roles from your dashboard later",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Fields based on selection (but not for Just Exploring)
            AnimatedContent(
                targetState = selectedPurpose,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) +
                            slideInVertically(initialOffsetY = { 20 }) togetherWith
                            fadeOut(animationSpec = tween(150)) +
                            slideOutVertically(targetOffsetY = { -20 })
                }
            ) { purpose ->
                when (purpose) {
                    "Find a Job" -> JobSeekerFields(
                        data = jobSeekerData,
                        onDataChange = { jobSeekerData = it }
                    )
                    "Offer Skilled Services" -> SkilledProfessionalFields(
                        data = skilledProfessionalData,
                        onDataChange = { skilledProfessionalData = it }
                    )
                    "Work as Agent" -> AgentFields(
                        data = agentData,
                        onDataChange = { agentData = it }
                    )
                    "Find Housing" -> HousingSeekerFields(
                        data = housingSeekerData,
                        onDataChange = { housingSeekerData = it }
                    )
                    "Get Social Support" -> SupportBeneficiaryFields(
                        data = supportBeneficiaryData,
                        onDataChange = { supportBeneficiaryData = it }
                    )
                    "Hire Employees" -> EmployerFields(
                        data = employerData,
                        onDataChange = { employerData = it }
                    )
                    "List Properties" -> PropertyOwnerFields(
                        data = propertyOwnerData,
                        onDataChange = { propertyOwnerData = it }
                    )
                    "Just Exploring" -> JustExploringMessage()
                    else -> Spacer(modifier = Modifier.height(0.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button - Now with special handling for Just Exploring
            PivotaPrimaryButton(
                text = if (selectedPurpose == "Just Exploring") "Start Exploring" else "Continue",
                onClick = {
                    if (selectedPurpose != null && !isLoading) {
                        if (selectedPurpose == "Just Exploring") {
                            // Navigate directly to registration for Just Exploring
                            onJustExploring()
                        } else {
                            isLoading = true
                            val purposeData = when (selectedPurpose) {
                                "Find a Job" -> jobSeekerData.toMap()
                                "Offer Skilled Services" -> skilledProfessionalData.toMap()
                                "Work as Agent" -> agentData.toMap()
                                "Find Housing" -> housingSeekerData.toMap()
                                "Get Social Support" -> supportBeneficiaryData.toMap()
                                "Hire Employees" -> employerData.toMap()
                                "List Properties" -> propertyOwnerData.toMap()
                                else -> emptyMap()
                            }
                            onContinue(selectedPurpose!!, purposeData)
                        }
                    }
                },
                enabled = selectedPurpose != null && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                icon = if (selectedPurpose == "Just Exploring")
                    ImageVector.vectorResource(R.drawable.ic_explore)
                else
                    ImageVector.vectorResource(R.drawable.ic_person)
            )

            // Add spacing between buttons
            Spacer(modifier = Modifier.height(16.dp))

            // Continue with Google Button
            AuthGoogleButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinueWithGoogle
            )

            // Add spacing before divider
            Spacer(modifier = Modifier.height(16.dp))

            // DIVIDER WITH "OR"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = " OR ",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Add spacing after divider
            Spacer(modifier = Modifier.height(16.dp))

            // Skip to Dashboard Button
            PivotaSkipButton(
                text = "Skip to Dashboard",
                onClick = onSkipToDashboard,
                modifier = Modifier.fillMaxWidth(),
                icon = ImageVector.vectorResource(R.drawable.ic_skip)
            )

            // Show loading overlay if needed
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Note
            Text(
                text = "You can always add more roles from dashboard",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
        }
    }

    // Styled Bottom Sheet for Purpose Selection
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = colorScheme.surface,
            tonalElevation = 8.dp,
            dragHandle = { BottomSheetDefaults.DragHandle(color = colorScheme.outlineVariant) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Your Purpose",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    )

                    IconButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colorScheme.outlineVariant.copy(0.5f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Close",
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Choose the primary way you'll use Pivota",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Purpose options list
                purposeOptions.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPurpose = option.label
                                showBottomSheet = false
                            }
                            .padding(vertical = 4.dp),
                        color = if (selectedPurpose == option.label)
                            colorScheme.primary.copy(alpha = 0.08f)
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Icon container
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (selectedPurpose == option.label)
                                            colorScheme.primary.copy(alpha = 0.15f)
                                        else
                                            colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = if (selectedPurpose == option.label)
                                        colorScheme.primary
                                    else
                                        colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (selectedPurpose == option.label)
                                            FontWeight.Bold
                                        else
                                            FontWeight.SemiBold,
                                        color = if (selectedPurpose == option.label)
                                            colorScheme.primary
                                        else
                                            colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 2
                                )
                            }

                            if (selectedPurpose == option.label) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


// Updated PurposeOption with description
data class PurposeOption(
    val label: String,
    val icon: ImageVector,
    val emoji: String,
    val description: String
)

// Data classes for dynamic fields (unchanged)
data class JobSeekerData(
    var headline: String = "",
    var isActivelySeeking: Boolean = true,
    var skills: String = "",
    var industries: String = "",
    var jobTypes: String = "",
    var seniorityLevel: String = "",
    var expectedSalary: String = "",
    var noticePeriod: String = "",
    var workAuthorization: String = ""
)

data class SkilledProfessionalData(
    var profession: String = "",
    var otherProfession: String = "",
    var specialties: String = "",
    var yearsExperience: String = "",
    var serviceAreas: String = "",
    var hourlyRate: String = "",
    var licenseNumber: String = ""
)

data class AgentData(
    var agentType: String = "",
    var specializations: String = "",
    var serviceAreas: String = "",
    var commissionRate: String = "",
    var licenseNumber: String = ""
)

data class HousingSeekerData(
    var propertyType: String = "",
    var minBedrooms: String = "",
    var maxBedrooms: String = "",
    var minBudget: String = "",
    var maxBudget: String = "",
    var preferredAreas: String = "",
    var moveInDate: String = ""
)

data class SupportBeneficiaryData(
    var supportTypes: List<String> = emptyList(),
    var urgentNeeds: String = "",
    var location: String = "",
    var familySize: String = ""
)

data class EmployerData(
    var businessName: String = "",
    var industrySector: String = "",
    var companySize: String = "",
    var preferredSkills: String = ""
)

data class PropertyOwnerData(
    var professionalStatus: String = "",
    var propertyCount: String = "",
    var propertyTypes: String = "",
    var serviceAreas: String = ""
)

@Composable
fun JustExploringMessage() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✨",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No additional details needed!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You can complete your profile later from your dashboard. Start browsing to discover opportunities that matter to you.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Extension functions to convert data to Map (unchanged)
fun JobSeekerData.toMap() = mapOf(
    "skills" to skills,
    "expectedSalary" to expectedSalary,
)

fun SkilledProfessionalData.toMap() = mapOf(
    "profession" to if (profession == "Other") otherProfession else profession,
    "specialties" to specialties,
    "yearsExperience" to yearsExperience,
    "serviceAreas" to serviceAreas,
    "hourlyRate" to hourlyRate,
    "licenseNumber" to licenseNumber
)

fun AgentData.toMap() = mapOf(
    "agentType" to agentType,
    "specializations" to specializations,
    "serviceAreas" to serviceAreas,
    "commissionRate" to commissionRate,
    "licenseNumber" to licenseNumber
)

fun HousingSeekerData.toMap() = mapOf(
    "propertyType" to propertyType,
    "minBedrooms" to minBedrooms,
    "maxBedrooms" to maxBedrooms,
    "minBudget" to minBudget,
    "maxBudget" to maxBudget,
    "preferredAreas" to preferredAreas,
    "moveInDate" to moveInDate
)

fun SupportBeneficiaryData.toMap() = mapOf(
    "supportTypes" to supportTypes,
    "urgentNeeds" to urgentNeeds,
    "location" to location,
    "familySize" to familySize
)

fun EmployerData.toMap() = mapOf(
    "businessName" to businessName,
    "industrySector" to industrySector,
    "companySize" to companySize,
    "preferredSkills" to preferredSkills
)

fun PropertyOwnerData.toMap() = mapOf(
    "professionalStatus" to professionalStatus,
    "propertyCount" to propertyCount,
    "propertyTypes" to propertyTypes,
    "serviceAreas" to serviceAreas
)