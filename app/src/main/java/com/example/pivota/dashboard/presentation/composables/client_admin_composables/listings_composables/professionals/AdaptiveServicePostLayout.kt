package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.professionals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import com.example.pivota.core.presentations.composables.SnackbarType
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.presentation.viewmodels.client_admin_viewmodels.CategoriesState
import com.example.pivota.dashboard.presentation.viewmodels.client_admin_viewmodels.PostServiceUiState
import com.example.pivota.dashboard.presentation.viewmodels.client_admin_viewmodels.PostServiceViewModel
import com.example.pivota.dashboard.presentation.viewmodels.client_admin_viewmodels.PricingUnitsState
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

// Default price units (fallback when backend not available)
private val DEFAULT_PRICE_UNITS = listOf(
    "PER_HOUR", "PER_DAY", "PER_WEEK", "PER_MONTH", "PER_YEAR", "FIXED",
    "PER_VISIT", "PER_SESSION", "PER_UNIT", "PER_SQUARE_FOOT", "PER_SQUARE_METER",
    "PER_TRIP", "PER_PAGE", "PER_TEST", "PER_COURSE", "PER_BOOTH", "PER_EVENT",
    "PER_WATT", "PERCENTAGE", "PACKAGE"
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveServicePostLayout(
    onBack: () -> Unit,
    viewModel: PostServiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val pricingUnitsState by viewModel.pricingUnitsState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val context = LocalContext.current
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Handle success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessSnackbar = true
            kotlinx.coroutines.delay(1500)
            onBack()
            viewModel.resetState()
        }
    }

    // Handle error
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            errorMessage = it
            showErrorSnackbar = true
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                icon = Icons.Default.Add,
                title = "Post a Service",
                onBack = onBack
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isWide) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        color = colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        ServicePostFormContent(
                            viewModel = viewModel,
                            uiState = uiState,
                            categoriesState = categoriesState,
                            pricingUnitsState = pricingUnitsState,
                            onSubmit = {
                                viewModel.submitService(
                                    onSuccess = {},
                                    onError = {}
                                )
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxHeight()
                            .background(colorScheme.background)
                            .padding(32.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ServiceLivePreview(
                            title = uiState.title.ifEmpty { "Service Title" },
                            category = uiState.category.ifEmpty { "Category Name" },
                            price = uiState.basePrice.ifEmpty { "0.00" },
                            currency = uiState.currency,
                            priceUnit = uiState.priceUnit,
                            location = if (uiState.locationCity.isNotEmpty())
                                "${uiState.locationCity}${uiState.locationNeighborhood?.let { ", $it" } ?: ""}"
                            else "City, Neighborhood",
                            experience = uiState.yearsExperience.ifEmpty { "X" }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .imePadding()
                ) {
                    ServicePostFormContent(
                        viewModel = viewModel,
                        uiState = uiState,
                        categoriesState = categoriesState,
                        pricingUnitsState = pricingUnitsState,
                        onSubmit = {
                            viewModel.submitService(
                                onSuccess = {},
                                onError = {}
                            )
                        }
                    )
                }
            }

            // Success Snackbar
            if (showSuccessSnackbar) {
                PivotaSnackbar(
                    message = "Service posted successfully!",
                    type = SnackbarType.SUCCESS,
                    duration = 3000,
                    onDismiss = { showSuccessSnackbar = false }
                )
            }

            // Error Snackbar
            if (showErrorSnackbar && errorMessage.isNotBlank()) {
                PivotaSnackbar(
                    message = errorMessage,
                    type = SnackbarType.ERROR,
                    duration = 4000,
                    onDismiss = {
                        showErrorSnackbar = false
                        errorMessage = ""
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServicePostFormContent(
    viewModel: PostServiceViewModel,
    uiState: PostServiceUiState,
    categoriesState: CategoriesState,
    pricingUnitsState: PricingUnitsState,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    // Local UI state for availability
    var availability by remember {
        mutableStateOf(
            DayOfWeek.values().map { day ->
                ServiceDayAvailability(
                    day = day.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    isAvailable = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY,
                    openTime = "09:00",
                    closeTime = "17:00"
                )
            }
        )
    }

    var expandedDayIndex by remember { mutableStateOf<Int?>(null) }

    // Sync availability with ViewModel when it changes
    LaunchedEffect(availability) {
        val domainAvailability = availability.map { day ->
            DayAvailability(
                day = day.day,
                open = day.openTime,
                close = day.closeTime,
                isClosed = !day.isAvailable
            )
        }
        viewModel.updateAvailability(domainAvailability)
    }

    val isLoadingCategories = categoriesState is CategoriesState.Loading
    val categoriesError = (categoriesState as? CategoriesState.Error)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Service Details
        ServiceSectionCard(title = "Service Details") {
            ServiceLabel("Service Title *")
            ServiceTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                placeholder = "e.g. Professional House Painting"
            )

            ServiceLabel("Category *")
            ServiceDropdown(
                value = uiState.category,
                onValueChange = { categoryName ->
                    val categoryId = uiState.categoryIdMap[categoryName] ?: ""
                    viewModel.updateCategory(categoryName, categoryId)
                },
                options = uiState.availableCategories,
                placeholder = "Select Category",
                isLoading = isLoadingCategories,
                enabled = !isLoadingCategories && categoriesError == null
            )

            // Show error if categories failed to load
            if (categoriesError != null) {
                Text(
                    text = "⚠️ $categoriesError",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            ServiceLabel("Description *")
            ServiceTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                placeholder = "Describe your service, skills, and what makes you unique",
                singleLine = false,
                minLines = 4
            )
        }

        // Section 2: Pricing
        ServiceSectionCard(title = "Pricing") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    ServiceLabel("Base Price *")
                    ServiceTextField(
                        value = uiState.basePrice,
                        onValueChange = viewModel::updateBasePrice,
                        placeholder = "0.00",
                        keyboardType = KeyboardType.Number
                    )

                    // Show price validation error if any
                    uiState.priceValidationError?.let { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    ServiceLabel("Currency *")
                    ServiceDropdown(
                        value = uiState.currency,
                        onValueChange = viewModel::updateCurrency,
                        options = listOf("KES", "USD", "EUR", "GBP"),
                        placeholder = "Select Currency"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ServiceLabel("Price Unit *")

            // Use dynamic allowed units from backend if available, otherwise use defaults
            val availableUnits = if (uiState.allowedPriceUnits.isNotEmpty()) {
                uiState.allowedPriceUnits
            } else {
                DEFAULT_PRICE_UNITS
            }
            val isLoadingPricing = pricingUnitsState is PricingUnitsState.Loading

            ServiceDropdown(
                value = uiState.priceUnit,
                onValueChange = viewModel::updatePriceUnit,
                options = availableUnits,
                placeholder = "Select pricing unit",
                isLoading = isLoadingPricing,
                enabled = pricingUnitsState !is PricingUnitsState.Loading
            )

            // Show unit description and price range
            val selectedUnitRule = uiState.pricingRules.find { it.unit == uiState.priceUnit }
            if (selectedUnitRule != null) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = selectedUnitRule.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Price range: ${selectedUnitRule.currency} ${selectedUnitRule.minPrice} - ${selectedUnitRule.maxPrice ?: "No max"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (selectedUnitRule.experienceRequired) {
                        Text(
                            text = "⚠️ Years of experience required for this category",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.error
                        )
                    }
                    if (selectedUnitRule.notesRequired) {
                        Text(
                            text = "📝 Additional notes required for this category",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary
                        )
                    }
                }
            }
        }

        // Section 3: Experience
        ServiceSectionCard(title = "Experience") {
            ServiceLabel("Years of Experience")
            ServiceTextField(
                value = uiState.yearsExperience,
                onValueChange = viewModel::updateYearsExperience,
                placeholder = "e.g. 5",
                keyboardType = KeyboardType.Number
            )
        }

        // Section 4: Availability
        ServiceSectionCard(title = "Availability") {
            Text(
                "Set your working hours for each day",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            availability.forEachIndexed { index, dayAvailability ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    expandedDayIndex = if (expandedDayIndex == index) null else index
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = dayAvailability.isAvailable,
                                    onCheckedChange = { isChecked ->
                                        availability = availability.toMutableList().apply {
                                            this[index] = dayAvailability.copy(isAvailable = isChecked)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    )
                                )

                                Text(
                                    dayAvailability.day,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (dayAvailability.isAvailable) FontWeight.Medium else FontWeight.Normal
                                    ),
                                    color = if (dayAvailability.isAvailable) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }

                            if (dayAvailability.isAvailable) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "${dayAvailability.openTime} - ${dayAvailability.closeTime}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        if (expandedDayIndex == index) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (expandedDayIndex == index) "Collapse" else "Expand",
                                        tint = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Text(
                                    "Closed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                        }

                        if (expandedDayIndex == index && dayAvailability.isAvailable) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = colorScheme.outline.copy(alpha = 0.3f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column {
                                    Text(
                                        "Open Time",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ServiceTimeField(
                                        value = dayAvailability.openTime,
                                        onValueChange = { newTime ->
                                            availability = availability.toMutableList().apply {
                                                this[index] = dayAvailability.copy(openTime = newTime)
                                            }
                                        }
                                    )
                                }
                                Column {
                                    Text(
                                        "Close Time",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ServiceTimeField(
                                        value = dayAvailability.closeTime,
                                        onValueChange = { newTime ->
                                            availability = availability.toMutableList().apply {
                                                this[index] = dayAvailability.copy(closeTime = newTime)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Location
        ServiceSectionCard(title = "Location") {
            ServiceLabel("City / Town *")
            ServiceTextField(
                value = uiState.locationCity,
                onValueChange = viewModel::updateLocationCity,
                placeholder = "e.g. Nairobi",
                leadingIcon = Icons.Outlined.Place
            )

            ServiceLabel("Neighborhood")
            ServiceTextField(
                value = uiState.locationNeighborhood,
                onValueChange = viewModel::updateLocationNeighborhood,
                placeholder = "e.g. Westlands"
            )
        }

        // Section 6: Additional Notes
        ServiceSectionCard(title = "Additional Notes", optional = true) {
            ServiceTextField(
                value = uiState.additionalNotes,
                onValueChange = viewModel::updateAdditionalNotes,
                placeholder = "Tools provided, service duration, special instructions, etc.",
                singleLine = false,
                minLines = 3
            )
        }

        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            shape = RoundedCornerShape(8.dp),
            enabled = !uiState.isLoading && pricingUnitsState !is PricingUnitsState.Loading && categoriesState !is CategoriesState.Loading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Post Service", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            "Tip: Complete all required fields to post your service.",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ServiceLivePreview(
    title: String = "Service Title",
    category: String = "Category Name",
    price: String = "0.00",
    currency: String = "KES",
    priceUnit: String = "PER_HOUR",
    location: String = "City, Neighborhood",
    experience: String = "X"
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Service Preview",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onPrimaryContainer
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            category,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "$currency ${formatPrice(price)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        )
                        Text(
                            formatPriceUnit(priceUnit),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Place,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        location,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "$experience yrs exp",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: String): String {
    return if (price.isNotEmpty()) {
        val priceDouble = price.toDoubleOrNull() ?: 0.0
        String.format("%,.0f", priceDouble)
    } else {
        "0.00"
    }
}

private fun formatPriceUnit(unit: String): String {
    return when (unit) {
        "PER_HOUR" -> "/hour"
        "PER_DAY" -> "/day"
        "PER_WEEK" -> "/week"
        "PER_MONTH" -> "/month"
        "PER_YEAR" -> "/year"
        "PER_VISIT" -> "/visit"
        "PER_SESSION" -> "/session"
        "PER_UNIT" -> "/unit"
        "PER_SQUARE_FOOT" -> "/sq ft"
        "PER_SQUARE_METER" -> "/sq m"
        "PER_TRIP" -> "/trip"
        "PER_PAGE" -> "/page"
        "PER_TEST" -> "/test"
        "PER_COURSE" -> "/course"
        "PER_BOOTH" -> "/booth"
        "PER_EVENT" -> "/event"
        "PER_WATT" -> "/watt"
        "PERCENTAGE" -> "%"
        "FIXED" -> "fixed"
        "PACKAGE" -> "package"
        else -> ""
    }
}

@Composable
fun ServiceTimeField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.width(100.dp),
        placeholder = { Text("HH:MM") },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun ServiceSectionCard(
    title: String,
    optional: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                if (optional) {
                    Text(
                        " (Optional)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ServiceLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(bottom = 6.dp, top = 4.dp)
    )
}

@Composable
fun ServiceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    prefix: String? = null,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = colorScheme.onSurface.copy(alpha = 0.5f)) },
        prefix = if (prefix != null) { { Text(prefix, color = colorScheme.onSurface.copy(alpha = 0.5f)) } } else null,
        leadingIcon = if (leadingIcon != null) { { Icon(leadingIcon, contentDescription = null, tint = colorScheme.onSurface.copy(alpha = 0.5f)) } } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorScheme.outline,
            focusedBorderColor = colorScheme.primary,
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface
        ),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )
    val colorScheme = MaterialTheme.colorScheme

    // Determine if dropdown should be interactive
    val isInteractive = enabled && options.isNotEmpty() && !isLoading

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        enabled = false,
        placeholder = {
            when {
                isLoading -> Text("Loading...", color = colorScheme.onSurface.copy(alpha = 0.5f))
                else -> Text(placeholder, color = colorScheme.onSurface.copy(alpha = 0.5f))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isInteractive) {
                if (isInteractive) {
                    showBottomSheet = true
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorScheme.outline,
            focusedBorderColor = colorScheme.outline,
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledBorderColor = colorScheme.outline,
            disabledTextColor = colorScheme.onSurface,
            disabledPlaceholderColor = colorScheme.onSurface.copy(alpha = 0.5f)
        ),
        trailingIcon = {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colorScheme.primary
                    )
                }
                else -> {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isInteractive) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    )

    if (showBottomSheet && options.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = colorScheme.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.width(40.dp).height(4.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = colorScheme.onSurface.copy(alpha = 0.3f)
                    ) {}
                }
            },
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colorScheme.onSurface
                    )

                    IconButton(onClick = { showBottomSheet = false }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.2f))

                // Options list
                options.forEachIndexed { index, option ->
                    val isSelected = option == value

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onValueChange(option)
                                showBottomSheet = false
                            },
                        color = if (isSelected) colorScheme.primaryContainer else Color.Transparent,
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                ),
                                color = if (isSelected) colorScheme.primary else colorScheme.onSurface
                            )

                            if (isSelected) {
                                Icon(
                                    Icons.Outlined.Check,
                                    contentDescription = "Selected",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (index != options.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colorScheme.outline.copy(alpha = 0.1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

data class ServiceDayAvailability(
    val day: String,
    var isAvailable: Boolean = true,
    var openTime: String = "09:00",
    var closeTime: String = "17:00"
)