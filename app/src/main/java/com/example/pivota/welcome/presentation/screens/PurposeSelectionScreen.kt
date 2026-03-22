package com.example.pivota.welcome.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.ui.theme.*


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PurposeSelectionScreenContent(
    onContinue: (purpose: String, purposeData: Map<String, Any>) -> Unit,
    onBack: () -> Unit,
    currentStep: Int = 2,
    totalSteps: Int = 6,
    modifier: Modifier = Modifier
) {
    var selectedPurpose by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
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

    // Purpose options
    val purposeOptions = listOf(
        PurposeOption("Find a Job", Icons.Default.Work, "🔍"),
        PurposeOption("Offer Skilled Services", Icons.Default.Build, "🔧"),
        PurposeOption("Work as Agent", Icons.Default.Person, "🤝"),
        PurposeOption("Find Housing", Icons.Default.Home, "🏠"),
        PurposeOption("Get Social Support", Icons.Default.Favorite, "❤️"),
        PurposeOption("Hire Employees", Icons.Default.Business, "👔"),
        PurposeOption("List Properties", Icons.Default.House, "📋"),
        PurposeOption("Just Exploring", Icons.Default.Explore, "✨")
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
            // Header with Back Button and Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Logo
                Icon(
                    painter = painterResource(id = R.drawable.transparentpivlogo),
                    contentDescription = "PivotaConnect Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                // Placeholder for balance
                Spacer(modifier = Modifier.size(40.dp))
            }

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

            // Dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPurpose ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = {
                            Text(
                                "Select your primary purpose",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        purposeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = option.icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            option.label,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }
                                },
                                onClick = {
                                    selectedPurpose = option.label
                                    isDropdownExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (selectedPurpose == option.label)
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
                                        else Color.Transparent
                                    )
                            )
                        }
                    }
                }
            }

            // Helper text below dropdown
            Text(
                text = "You can add more roles from your dashboard later",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Fields based on selection
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

            // Continue Button using custom button
            PivotaPrimaryButton(
                text = "Continue",
                onClick = {
                    if (selectedPurpose != null && !isLoading) {
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
                },
                enabled = selectedPurpose != null && !isLoading,
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
}

// Data classes for dynamic fields
data class JobSeekerData(
    var skills: String = "",
    var experienceLevel: String = "",
    var expectedSalary: String = "",
    var cvUrl: String? = null
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

// Field composables for each purpose (simplified for brevity)
@Composable
fun JobSeekerFields(
    data: JobSeekerData,
    onDataChange: (JobSeekerData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Job Seeker",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Skills field
            OutlinedTextField(
                value = data.skills,
                onValueChange = { onDataChange(data.copy(skills = it)) },
                label = { Text("Skills (comma-separated)") },
                placeholder = { Text("welding, metal fabrication, oxy cutting") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Experience Level
            Text(
                text = "Experience Level",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = data.experienceLevel == "Entry Level",
                    onClick = { onDataChange(data.copy(experienceLevel = "Entry Level")) },
                    label = { Text("Entry Level") }
                )
                FilterChip(
                    selected = data.experienceLevel == "Mid Level",
                    onClick = { onDataChange(data.copy(experienceLevel = "Mid Level")) },
                    label = { Text("Mid Level") }
                )
                FilterChip(
                    selected = data.experienceLevel == "Senior Level",
                    onClick = { onDataChange(data.copy(experienceLevel = "Senior Level")) },
                    label = { Text("Senior Level") }
                )
            }

            // Expected Salary
            OutlinedTextField(
                value = data.expectedSalary,
                onValueChange = { onDataChange(data.copy(expectedSalary = it)) },
                label = { Text("Expected Salary (KES)") },
                placeholder = { Text("1,500") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // CV Upload
            OutlinedButton(
                onClick = { /* Handle CV upload */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload CV (PDF)")
            }
        }
    }
}

@Composable
fun SkilledProfessionalFields(
    data: SkilledProfessionalData,
    onDataChange: (SkilledProfessionalData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Skilled Professional",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Profession selection
            Text(
                text = "Profession",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val professions = listOf("Electrician", "Plumber", "Carpenter", "Welder")
                professions.forEach { prof ->
                    FilterChip(
                        selected = data.profession == prof,
                        onClick = { onDataChange(data.copy(profession = prof, otherProfession = "")) },
                        label = { Text(prof) }
                    )
                }
            }

            // Other profession input
            OutlinedTextField(
                value = data.otherProfession,
                onValueChange = { onDataChange(data.copy(profession = "Other", otherProfession = it)) },
                label = { Text("Other Profession") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Specialties
            OutlinedTextField(
                value = data.specialties,
                onValueChange = { onDataChange(data.copy(specialties = it)) },
                label = { Text("Specialties (comma-separated)") },
                placeholder = { Text("wiring, lighting installation, fault diagnosis") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Years Experience
            OutlinedTextField(
                value = data.yearsExperience,
                onValueChange = { onDataChange(data.copy(yearsExperience = it)) },
                label = { Text("Years Experience") },
                placeholder = { Text("5") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Service Areas
            OutlinedTextField(
                value = data.serviceAreas,
                onValueChange = { onDataChange(data.copy(serviceAreas = it)) },
                label = { Text("Service Areas (comma-separated)") },
                placeholder = { Text("Nairobi, Kiambu, Ruiru") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Hourly Rate
            OutlinedTextField(
                value = data.hourlyRate,
                onValueChange = { onDataChange(data.copy(hourlyRate = it)) },
                label = { Text("Hourly Rate (KES)") },
                placeholder = { Text("500") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // License Number (optional)
            OutlinedTextField(
                value = data.licenseNumber,
                onValueChange = { onDataChange(data.copy(licenseNumber = it)) },
                label = { Text("License Number (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun AgentFields(
    data: AgentData,
    onDataChange: (AgentData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Agent",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Agent Type selection
            Text(
                text = "Agent Type",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val agentTypes = listOf("Housing Agent", "Recruitment Agent", "Broker")
                agentTypes.forEach { type ->
                    FilterChip(
                        selected = data.agentType == type,
                        onClick = { onDataChange(data.copy(agentType = type)) },
                        label = { Text(type) }
                    )
                }
            }

            // Specializations
            OutlinedTextField(
                value = data.specializations,
                onValueChange = { onDataChange(data.copy(specializations = it)) },
                label = { Text("Specializations (comma-separated)") },
                placeholder = { Text("residential, commercial, luxury") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Service Areas
            OutlinedTextField(
                value = data.serviceAreas,
                onValueChange = { onDataChange(data.copy(serviceAreas = it)) },
                label = { Text("Service Areas (comma-separated)") },
                placeholder = { Text("Nairobi, Kiambu, Kajiado") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Commission Rate
            OutlinedTextField(
                value = data.commissionRate,
                onValueChange = { onDataChange(data.copy(commissionRate = it)) },
                label = { Text("Commission Rate (%)") },
                placeholder = { Text("5") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // License Number (optional)
            OutlinedTextField(
                value = data.licenseNumber,
                onValueChange = { onDataChange(data.copy(licenseNumber = it)) },
                label = { Text("License Number (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun HousingSeekerFields(
    data: HousingSeekerData,
    onDataChange: (HousingSeekerData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Housing Seeker",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Property Type
            Text(
                text = "Property Type",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val propertyTypes = listOf("Apartment", "House", "Bedsitter", "Room", "Land")
                propertyTypes.forEach { type ->
                    FilterChip(
                        selected = data.propertyType == type,
                        onClick = { onDataChange(data.copy(propertyType = type)) },
                        label = { Text(type) }
                    )
                }
            }

            // Bedrooms range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = data.minBedrooms,
                    onValueChange = { onDataChange(data.copy(minBedrooms = it)) },
                    label = { Text("Min Bedrooms") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = data.maxBedrooms,
                    onValueChange = { onDataChange(data.copy(maxBedrooms = it)) },
                    label = { Text("Max Bedrooms") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Budget range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = data.minBudget,
                    onValueChange = { onDataChange(data.copy(minBudget = it)) },
                    label = { Text("Min Budget (KES)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = data.maxBudget,
                    onValueChange = { onDataChange(data.copy(maxBudget = it)) },
                    label = { Text("Max Budget (KES)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Preferred Areas
            OutlinedTextField(
                value = data.preferredAreas,
                onValueChange = { onDataChange(data.copy(preferredAreas = it)) },
                label = { Text("Preferred Areas (comma-separated)") },
                placeholder = { Text("Kilimani, Kileleshwa, Westlands") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Move-in Date
            OutlinedTextField(
                value = data.moveInDate,
                onValueChange = { onDataChange(data.copy(moveInDate = it)) },
                label = { Text("Move-in Date") },
                placeholder = { Text("MM/DD/YYYY") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun SupportBeneficiaryFields(
    data: SupportBeneficiaryData,
    onDataChange: (SupportBeneficiaryData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Support Beneficiary",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Support Types (multi-select)
            Text(
                text = "Type of Support Needed (select all that apply)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            val supportOptions = listOf("Food", "Shelter", "Medical", "Counseling", "Training", "Legal", "Cash Assistance")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(supportOptions.size) { index ->
                    val option = supportOptions[index]
                    FilterChip(
                        selected = data.supportTypes.contains(option),
                        onClick = {
                            val newList = if (data.supportTypes.contains(option)) {
                                data.supportTypes.filter { it != option }
                            } else {
                                data.supportTypes + option
                            }
                            onDataChange(data.copy(supportTypes = newList))
                        },
                        label = { Text(option) }
                    )
                }
            }

            // Urgent Needs
            OutlinedTextField(
                value = data.urgentNeeds,
                onValueChange = { onDataChange(data.copy(urgentNeeds = it)) },
                label = { Text("Urgent Needs") },
                placeholder = { Text("Food for family of 4, immediate shelter") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Location
            OutlinedTextField(
                value = data.location,
                onValueChange = { onDataChange(data.copy(location = it)) },
                label = { Text("Location") },
                placeholder = { Text("Kawangware, Nairobi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Family Size
            OutlinedTextField(
                value = data.familySize,
                onValueChange = { onDataChange(data.copy(familySize = it)) },
                label = { Text("Family Size") },
                placeholder = { Text("4") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
fun EmployerFields(
    data: EmployerData,
    onDataChange: (EmployerData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Employer",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Business Name
            OutlinedTextField(
                value = data.businessName,
                onValueChange = { onDataChange(data.copy(businessName = it)) },
                label = { Text("Business Name") },
                placeholder = { Text("Wanjiku Hardware Solutions") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Industry Sector
            Text(
                text = "Industry Sector",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val industries = listOf("Construction", "Tech", "Healthcare")
                industries.forEach { industry ->
                    FilterChip(
                        selected = data.industrySector == industry,
                        onClick = { onDataChange(data.copy(industrySector = industry)) },
                        label = { Text(industry) }
                    )
                }
                FilterChip(
                    selected = data.industrySector == "Other",
                    onClick = { onDataChange(data.copy(industrySector = "Other")) },
                    label = { Text("Other") }
                )
            }

            // Company Size
            Text(
                text = "Company Size",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sizes = listOf("1-10", "11-50", "51-200", "201-500", "500+")
                sizes.forEach { size ->
                    FilterChip(
                        selected = data.companySize == size,
                        onClick = { onDataChange(data.copy(companySize = size)) },
                        label = { Text(size) }
                    )
                }
            }

            // Preferred Skills
            OutlinedTextField(
                value = data.preferredSkills,
                onValueChange = { onDataChange(data.copy(preferredSkills = it)) },
                label = { Text("Preferred Skills for Hiring (comma-separated)") },
                placeholder = { Text("welding, carpentry, plumbing") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun PropertyOwnerFields(
    data: PropertyOwnerData,
    onDataChange: (PropertyOwnerData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Property Owner",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Professional Status
            Text(
                text = "Professional Status",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = data.professionalStatus == "Individual Owner",
                    onClick = { onDataChange(data.copy(professionalStatus = "Individual Owner")) },
                    label = { Text("Individual Owner") }
                )
                FilterChip(
                    selected = data.professionalStatus == "Professional Landlord",
                    onClick = { onDataChange(data.copy(professionalStatus = "Professional Landlord")) },
                    label = { Text("Professional Landlord") }
                )
            }

            // Number of Properties
            OutlinedTextField(
                value = data.propertyCount,
                onValueChange = { onDataChange(data.copy(propertyCount = it)) },
                label = { Text("Number of Properties Owned") },
                placeholder = { Text("3") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Property Types
            OutlinedTextField(
                value = data.propertyTypes,
                onValueChange = { onDataChange(data.copy(propertyTypes = it)) },
                label = { Text("Property Types Owned (comma-separated)") },
                placeholder = { Text("apartments, commercial spaces") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Service Areas
            OutlinedTextField(
                value = data.serviceAreas,
                onValueChange = { onDataChange(data.copy(serviceAreas = it)) },
                label = { Text("Service Areas (comma-separated)") },
                placeholder = { Text("Kilimani, Westlands, Ruaka") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

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

data class PurposeOption(
    val label: String,
    val icon: ImageVector,
    val emoji: String
)

// Extension function to convert data to Map
fun JobSeekerData.toMap() = mapOf(
    "skills" to skills,
    "experienceLevel" to experienceLevel,
    "expectedSalary" to expectedSalary,
    "cvUrl" to (cvUrl ?: "")
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