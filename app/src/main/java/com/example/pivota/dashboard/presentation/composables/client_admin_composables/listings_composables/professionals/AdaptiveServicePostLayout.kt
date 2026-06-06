package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.professionals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.pivota.core.presentations.composables.PivotaFullScreenLoading
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
import com.example.pivota.R
import com.example.pivota.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Default price units (fallback when backend not available)
private val DEFAULT_PRICE_UNITS = listOf(
    "PER_HOUR", "PER_DAY", "PER_WEEK", "PER_MONTH", "PER_YEAR", "FIXED",
    "PER_VISIT", "PER_SESSION", "PER_UNIT", "PER_SQUARE_FOOT", "PER_SQUARE_METER",
    "PER_TRIP", "PER_PAGE", "PER_TEST", "PER_COURSE", "PER_BOOTH", "PER_EVENT",
    "PER_WATT", "PERCENTAGE", "PACKAGE"
)

// Step definitions
enum class FormStep(val title: String, val stepNumber: Int) {
    CATEGORY("Category", 1),
    BASIC_INFO("Basic Info", 2),
    PRICING("Pricing", 3),
    AVAILABILITY("Availability", 4),
    COVERAGE_AREAS("Service Areas", 5)  // Changed from LOCATION
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AdaptiveServicePostLayout(
    onBack: () -> Unit,
    onNavigateToServiceDetails: ((String) -> Unit)? = null,
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
    var successMessage by remember { mutableStateOf("") }

    // Success Dialog state
    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdServiceId by remember { mutableStateOf<String?>(null) }

    // Current step
    var currentStep by remember { mutableStateOf(FormStep.CATEGORY) }

    // Handle back button
    val handleBack = {
        when (currentStep) {
            FormStep.CATEGORY -> onBack()
            FormStep.BASIC_INFO -> currentStep = FormStep.CATEGORY
            FormStep.PRICING -> currentStep = FormStep.BASIC_INFO
            FormStep.AVAILABILITY -> currentStep = FormStep.PRICING
            FormStep.COVERAGE_AREAS -> currentStep = FormStep.AVAILABILITY  // Changed from LOCATION
        }
    }

    // Handle error from ViewModel
    LaunchedEffect(uiState.error) {
        println("🔔 [AdaptiveServicePostLayout] error changed: ${uiState.error}")
        uiState.error?.let { error ->
            kotlinx.coroutines.delay(100)
            errorMessage = error
            showErrorSnackbar = true
            println("🔔 [AdaptiveServicePostLayout] Showing error snackbar: $error")
        }
    }

    // Handle success - show snackbar first, then dialog
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            successMessage = "Service posted successfully!"
            showSuccessSnackbar = true

            // Wait for snackbar to show, then show dialog
            kotlinx.coroutines.delay(1500)

            createdServiceId = uiState.createdOffering?.id
            showSuccessDialog = true
            viewModel.resetSuccess()
        }
    }

    // Use a Box at the root level to overlay everything
    Box(modifier = Modifier.fillMaxSize()) {
        // Scaffold with TopBar and content
        Scaffold(
            topBar = {
                TopBar(
                    title = "Post a Service",
                    onBack = handleBack
                )
            },
            containerColor = colorScheme.background
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Main content
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
                            ServicePostStepperContent(
                                viewModel = viewModel,
                                uiState = uiState,
                                categoriesState = categoriesState,
                                pricingUnitsState = pricingUnitsState,
                                currentStep = currentStep,
                                onStepChange = { currentStep = it },
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
                                subcategory = uiState.subcategory,
                                price = uiState.basePrice.ifEmpty { "0.00" },
                                currency = uiState.currency,
                                priceUnit = uiState.priceUnit,
                                location = if (uiState.coverageAreas.isNotEmpty())
                                    uiState.coverageAreas.joinToString(", ")
                                else "Service Areas",
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
                        ServicePostStepperContent(
                            viewModel = viewModel,
                            uiState = uiState,
                            categoriesState = categoriesState,
                            pricingUnitsState = pricingUnitsState,
                            currentStep = currentStep,
                            onStepChange = { currentStep = it },
                            onSubmit = {
                                viewModel.submitService(
                                    onSuccess = {},
                                    onError = {}
                                )
                            }
                        )
                    }
                }

                // Full screen loading OVERLAY
                if (uiState.isLoading) {
                    PivotaFullScreenLoading(
                        modifier = Modifier,
                        message = "Posting your service..."
                    )
                }
            }
        }

        // Snackbars - placed OUTSIDE Scaffold at the very top level
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
                .zIndex(2000f),
            contentAlignment = Alignment.TopCenter
        ) {
            // Success Snackbar
            if (showSuccessSnackbar) {
                PivotaSnackbar(
                    message = successMessage,
                    type = SnackbarType.SUCCESS,
                    duration = 3000,
                    onDismiss = {
                        showSuccessSnackbar = false
                        successMessage = ""
                    }
                )
            }

            // Error Snackbar
            if (showErrorSnackbar && errorMessage.isNotBlank()) {
                val isPermissionError = errorMessage.contains("permission", ignoreCase = true) ||
                        errorMessage.contains("professional", ignoreCase = true) ||
                        errorMessage.contains("permissions", ignoreCase = true) ||
                        errorMessage.contains("professional-services.create.own", ignoreCase = true) ||
                        errorMessage.contains("PROFILE_NOT_FOUND", ignoreCase = true)

                PivotaSnackbar(
                    message = errorMessage,
                    type = SnackbarType.ERROR,
                    duration = 8000,
                    actionText = if (isPermissionError) "Get Professional Status" else null,
                    onAction = if (isPermissionError) {
                        {
                            showErrorSnackbar = false
                            errorMessage = ""
                            viewModel.resetError()
                        }
                    } else null,
                    onDismiss = {
                        showErrorSnackbar = false
                        errorMessage = ""
                        viewModel.resetError()
                    }
                )
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        SuccessDialog(
            message = "Your service has been posted successfully!",
            onViewListing = {
                showSuccessDialog = false
                createdServiceId?.let { serviceId ->
                    onNavigateToServiceDetails?.invoke(serviceId)
                } ?: onBack()
            },
            onPostAnother = {
                showSuccessDialog = false
                viewModel.resetState()
                currentStep = FormStep.CATEGORY
            },
            onDismiss = {
                // Don't dismiss on back press, force user to choose
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ServicePostStepperContent(
    viewModel: PostServiceViewModel,
    uiState: PostServiceUiState,
    categoriesState: CategoriesState,
    pricingUnitsState: PricingUnitsState,
    currentStep: FormStep,
    onStepChange: (FormStep) -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    var expandedDayIndex by remember { mutableStateOf<Int?>(null) }

    // LazyListState for auto-scrolling stepper
    val lazyListState = rememberLazyListState()

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
    val categories = (categoriesState as? CategoriesState.Success)?.categories ?: emptyList()
    val topLevelCategories = categories.filter { it.parentId == null }
    val selectedParentCategory = topLevelCategories.find { it.name == uiState.category }
    val hasSubcategories = selectedParentCategory?.hasSubcategories == true
    val showSubcategoryField = hasSubcategories && uiState.availableSubcategories.isNotEmpty()
    val subcategories = emptyList<com.example.pivota.dashboard.domain.model.listings_models.general.Category>()

    // Check if category step is complete
    val isCategoryComplete = uiState.category.isNotBlank() &&
            (!showSubcategoryField || uiState.subcategory.isNotBlank())

    // Check if basic info is complete
    val isBasicInfoComplete = isCategoryComplete &&
            uiState.title.isNotBlank() &&
            uiState.description.isNotBlank()

    // Check if pricing is complete
    val isPricingComplete = uiState.basePrice.isNotBlank() &&
            uiState.basePrice.toDoubleOrNull() != null &&
            uiState.priceUnit.isNotBlank() &&
            uiState.priceValidationError == null

    // To: Track if user has confirmed availability
    var hasConfirmedAvailability by remember { mutableStateOf(false) }

    // Only mark as complete when user clicks "Continue" from availability step
    val isAvailabilityComplete = hasConfirmedAvailability

    // Check if location is complete
    val isCoverageAreasComplete = uiState.coverageAreas.isNotEmpty()

    // Auto-scroll to current step when it changes
    LaunchedEffect(currentStep) {
        val targetIndex = FormStep.values().indexOf(currentStep)
        if (targetIndex >= 0) {
            lazyListState.animateScrollToItem(
                index = targetIndex,
                scrollOffset = 0
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.3f))

        // Auto-scrolling stepper with LazyRow
        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(FormStep.values().size) { index ->
                val step = FormStep.values()[index]
                val isCompleted = when (step) {
                    FormStep.CATEGORY -> isCategoryComplete
                    FormStep.BASIC_INFO -> isBasicInfoComplete
                    FormStep.PRICING -> isPricingComplete
                    FormStep.AVAILABILITY -> isAvailabilityComplete
                    FormStep.COVERAGE_AREAS -> isCoverageAreasComplete  // Changed from LOCATION
                }
                val isCurrent = step == currentStep
                val isLast = index == FormStep.values().lastIndex

                StepIndicator(
                    stepNumber = step.stepNumber,
                    title = step.title,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isLast = isLast,
                    colorScheme = colorScheme,
                    onClick = {
                        // Allow navigation to completed steps or previous steps
                        if (step.ordinal <= currentStep.ordinal || isCompleted) {
                            onStepChange(step)
                        }
                    }
                )
            }
        }

        HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.3f))

        // Step Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                FormStep.CATEGORY -> {
                    ServiceCategoryStep(
                        uiState = uiState,
                        viewModel = viewModel,
                        categoriesState = categoriesState,
                        topLevelCategories = topLevelCategories,
                        subcategories = subcategories,
                        colorScheme = colorScheme,
                        scrollState = scrollState,
                        onNext = {
                            if (isCategoryComplete) {
                                onStepChange(FormStep.BASIC_INFO)
                            }
                        }
                    )
                }
                FormStep.BASIC_INFO -> {
                    ServiceBasicInfoStep(
                        uiState = uiState,
                        viewModel = viewModel,
                        colorScheme = colorScheme,
                        scrollState = scrollState,
                        onNext = {
                            if (isBasicInfoComplete) {
                                onStepChange(FormStep.PRICING)
                            }
                        },
                        onBack = { onStepChange(FormStep.CATEGORY) }
                    )
                }
                FormStep.PRICING -> {
                    ServicePricingStep(
                        uiState = uiState,
                        viewModel = viewModel,
                        pricingUnitsState = pricingUnitsState,
                        colorScheme = colorScheme,
                        scrollState = scrollState,
                        onNext = { onStepChange(FormStep.AVAILABILITY) },
                        onBack = { onStepChange(FormStep.BASIC_INFO) }
                    )
                }
                FormStep.AVAILABILITY -> {
                    ServiceAvailabilityStep(
                        availability = availability,
                        onAvailabilityChange = { availability = it },
                        expandedDayIndex = expandedDayIndex,
                        onExpandedDayChange = { expandedDayIndex = it },
                        colorScheme = colorScheme,
                        scrollState = scrollState,
                        onNext = {
                            hasConfirmedAvailability = true
                            onStepChange(FormStep.COVERAGE_AREAS)
                        },
                        onBack = { onStepChange(FormStep.PRICING) }
                    )
                }
                FormStep.COVERAGE_AREAS -> {  // Changed from LOCATION
                    ServiceLocationStep(
                        uiState = uiState,
                        viewModel = viewModel,
                        colorScheme = colorScheme,
                        scrollState = scrollState,
                        onSubmit = onSubmit,
                        onBack = { onStepChange(FormStep.AVAILABILITY) },
                        isLoading = uiState.isLoading
                    )
                }
            }
        }
    }
}

// Replace the entire StepIndicator composable with this:

@Composable
fun StepIndicator(
    stepNumber: Int,
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean,
    colorScheme: ColorScheme,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(enabled = isCurrent || isCompleted) { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step Circle with number or checkmark
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> colorScheme.primary
                        isCurrent -> colorScheme.primary.copy(alpha = 0.15f)
                        else -> colorScheme.surfaceVariant
                    }
                )
                .border(
                    width = if (isCurrent) 2.dp else 0.dp,
                    color = colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> Icon(
                    Icons.Outlined.Check,
                    contentDescription = "Completed",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                else -> Text(
                    stepNumber.toString(),
                    color = if (isCurrent) colorScheme.primary else colorScheme.onSurfaceVariant,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Step Title
        Column {
            Text(
                "Step $stepNumber",
                fontSize = 10.sp,
                color = if (isCompleted || isCurrent) colorScheme.primary else colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isCompleted || isCurrent) colorScheme.onSurface else colorScheme.onSurfaceVariant
            )
        }

        // Arrow icon between steps (except last)
        if (!isLast) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceCategoryStep(
    uiState: PostServiceUiState,
    viewModel: PostServiceViewModel,
    categoriesState: CategoriesState,
    topLevelCategories: List<com.example.pivota.dashboard.domain.model.listings_models.general.Category>,
    subcategories: List<com.example.pivota.dashboard.domain.model.listings_models.general.Category>,
    colorScheme: ColorScheme,
    scrollState: androidx.compose.foundation.ScrollState,
    onNext: () -> Unit
) {
    val isLoadingCategories = categoriesState is CategoriesState.Loading
    val categoriesError = (categoriesState as? CategoriesState.Error)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Choose a category",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface
        )

        Text(
            "Select the category that best describes your service",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        ServiceLabel("Category *")

        // Category dropdown
        ServiceDropdown(
            value = uiState.category,
            onValueChange = { categoryName ->
                val categoryId = uiState.categoryIdMap[categoryName] ?: ""
                println("Category selected: $categoryName, ID: $categoryId")
                viewModel.updateCategory(categoryName, categoryId)
            },
            options = topLevelCategories.map { it.name },
            placeholder = "Select Category",
            isLoading = isLoadingCategories,
            enabled = !isLoadingCategories && categoriesError == null && uiState.availableCategories.isNotEmpty()
        )

        if (categoriesError != null) {
            Text(
                text = "⚠️ $categoriesError",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Subcategory Field - Calculate visibility directly from UI state
        val hasSubcategories = topLevelCategories.find { it.name == uiState.category }?.hasSubcategories == true
        val availableSubs = uiState.availableSubcategories

        println("🔍 ServiceCategoryStep - Category: ${uiState.category}")
        println("🔍 hasSubcategories: $hasSubcategories")
        println("🔍 availableSubcategories: $availableSubs")

        AnimatedVisibility(
            visible = hasSubcategories && availableSubs.isNotEmpty(),
            enter = fadeIn() + expandVertically(animationSpec = tween(300)),
            exit = fadeOut() + shrinkVertically(animationSpec = tween(200))
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                ServiceLabel("Subcategory *")
                ServiceDropdown(
                    value = uiState.subcategory,
                    onValueChange = { subcategoryName ->
                        val subcategoryId = uiState.subcategoryIdMap[subcategoryName] ?: ""
                        println("Subcategory selected: $subcategoryName, ID: $subcategoryId")
                        viewModel.updateSubcategory(subcategoryName, subcategoryId)
                    },
                    options = availableSubs,
                    placeholder = "Select Subcategory",
                    isLoading = false,
                    enabled = availableSubs.isNotEmpty()
                )

                Text(
                    text = "Selecting a subcategory helps clients find you more easily",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            shape = RoundedCornerShape(8.dp),
            enabled = uiState.category.isNotBlank() && (!hasSubcategories || uiState.subcategory.isNotBlank())
        ) {
            Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceBasicInfoStep(
    uiState: PostServiceUiState,
    viewModel: PostServiceViewModel,
    colorScheme: ColorScheme,
    scrollState: androidx.compose.foundation.ScrollState,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Tell us about your service",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface
        )

        Text(
            "Provide the basic information about your offering",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        ServiceLabel("Service Title *")
        ServiceTextField(
            value = uiState.title,
            onValueChange = viewModel::updateTitle,
            placeholder = "e.g. Professional House Painting"
        )

        ServiceLabel("Description *")
        ServiceTextField(
            value = uiState.description,
            onValueChange = viewModel::updateDescription,
            placeholder = "Describe your service, skills, and what makes you unique",
            singleLine = false,
            minLines = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(2f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                enabled = uiState.title.isNotBlank() && uiState.description.isNotBlank()
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ServicePricingStep(
    uiState: PostServiceUiState,
    viewModel: PostServiceViewModel,
    pricingUnitsState: PricingUnitsState,
    colorScheme: ColorScheme,
    scrollState: androidx.compose.foundation.ScrollState,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val isLoadingPricing = pricingUnitsState is PricingUnitsState.Loading
    val availableUnits = if (uiState.allowedPriceUnits.isNotEmpty()) {
        uiState.allowedPriceUnits
    } else {
        DEFAULT_PRICE_UNITS
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Set your pricing",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface
        )

        Text(
            "Choose how you want to charge for your service",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                ServiceLabel("Base Price *")
                ServiceTextField(
                    value = uiState.basePrice,
                    onValueChange = viewModel::updateBasePrice,
                    placeholder = "0.00",
                    keyboardType = KeyboardType.Number
                )
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
        ServiceDropdown(
            value = uiState.priceUnit,
            onValueChange = viewModel::updatePriceUnit,
            options = availableUnits,
            placeholder = "Select pricing unit",
            isLoading = isLoadingPricing,
            enabled = pricingUnitsState !is PricingUnitsState.Loading
        )

        val selectedUnitRule = uiState.pricingRules.find { it.unit == uiState.priceUnit }
        if (selectedUnitRule != null) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
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
                        text = "Years of experience required",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.error
                    )
                }
                if (selectedUnitRule.notesRequired) {
                    Text(
                        text = "Additional notes required",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ServiceLabel("Years of Experience")
        ServiceTextField(
            value = uiState.yearsExperience,
            onValueChange = viewModel::updateYearsExperience,
            placeholder = "e.g. 5",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(2f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                enabled = uiState.basePrice.isNotBlank() && uiState.basePrice.toDoubleOrNull() != null && uiState.priceValidationError == null
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceAvailabilityStep(
    availability: List<ServiceDayAvailability>,
    onAvailabilityChange: (List<ServiceDayAvailability>) -> Unit,
    expandedDayIndex: Int?,
    onExpandedDayChange: (Int?) -> Unit,
    colorScheme: ColorScheme,
    scrollState: androidx.compose.foundation.ScrollState,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var showTimePickerForDay by remember { mutableStateOf<Int?>(null) }
    var isSelectingOpenTime by remember { mutableStateOf(true) }
    var tempDayIndex by remember { mutableStateOf(0) }
    var tempIsOpenTime by remember { mutableStateOf(true) }

    // Time picker dialog
    if (showTimePickerForDay != null) {
        val dayIndex = showTimePickerForDay!!
        val currentDay = availability[dayIndex]

        TimePickerDialog(
            title = if (tempIsOpenTime) "Select Open Time" else "Select Close Time",
            initialTime = if (tempIsOpenTime) currentDay.openTime else currentDay.closeTime,
            onTimeSelected = { selectedTime ->
                val updatedList = availability.toMutableList()
                if (tempIsOpenTime) {
                    updatedList[dayIndex] = currentDay.copy(openTime = selectedTime)
                } else {
                    updatedList[dayIndex] = currentDay.copy(closeTime = selectedTime)
                }
                onAvailabilityChange(updatedList)
                showTimePickerForDay = null
            },
            onDismiss = { showTimePickerForDay = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Set your availability",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface
        )

        Text(
            "Let clients know when you're available to work",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        availability.forEachIndexed { index, dayAvailability ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (dayAvailability.isAvailable)
                        colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    else
                        colorScheme.surfaceVariant.copy(alpha = 0.1f)
                )
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { onExpandedDayChange(if (expandedDayIndex == index) null else index) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = dayAvailability.isAvailable,
                                onCheckedChange = { isChecked ->
                                    onAvailabilityChange(
                                        availability.toMutableList().apply {
                                            this[index] = dayAvailability.copy(isAvailable = isChecked)
                                        }
                                    )
                                },
                                colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary)
                            )
                            Text(
                                dayAvailability.day,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (dayAvailability.isAvailable) FontWeight.Medium else FontWeight.Normal
                                ),
                                color = if (dayAvailability.isAvailable) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        if (dayAvailability.isAvailable) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        tempDayIndex = index
                                        tempIsOpenTime = true
                                        showTimePickerForDay = index
                                    },
                                color = colorScheme.primaryContainer
                            ) {
                                Text(
                                    formatTimeTo12Hour(dayAvailability.openTime),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onPrimaryContainer
                                )
                            }
                            Text("—", color = colorScheme.onSurfaceVariant)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        tempDayIndex = index
                                        tempIsOpenTime = false
                                        showTimePickerForDay = index
                                    },
                                color = colorScheme.primaryContainer
                            ) {
                                Text(
                                    formatTimeTo12Hour(dayAvailability.closeTime),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onPrimaryContainer
                                )
                            }
                            Icon(
                                if (expandedDayIndex == index) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
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
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colorScheme.outline.copy(alpha = 0.3f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TimeSelectionButton(
                                label = "Open Time",
                                time = dayAvailability.openTime,
                                onClick = {
                                    tempDayIndex = index
                                    tempIsOpenTime = true
                                    showTimePickerForDay = index
                                },
                                colorScheme = colorScheme,
                                modifier = Modifier.weight(1f)
                            )
                            TimeSelectionButton(
                                label = "Close Time",
                                time = dayAvailability.closeTime,
                                onClick = {
                                    tempDayIndex = index
                                    tempIsOpenTime = false
                                    showTimePickerForDay = index
                                },
                                colorScheme = colorScheme,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Back", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(2f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun TimeSelectionButton(
    label: String,
    time: String,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            color = colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Text(
                formatTimeTo12Hour(time),  // Format the time here
                modifier = Modifier.padding(vertical = 12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions for 12-hour format with AM/PM
fun formatTimeTo12Hour(time24: String): String {
    val parts = time24.split(":")
    if (parts.size != 2) return time24
    val hour = parts[0].toIntOrNull() ?: 9
    val minute = parts[1]
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val ampm = if (hour < 12) "AM" else "PM"
    return "$displayHour:${minute.padStart(2, '0')} $ampm"
}

fun convertTo24Hour(hour12: Int, minute: Int, isAM: Boolean): String {
    val hour24 = when {
        isAM && hour12 == 12 -> 0
        !isAM && hour12 == 12 -> 12
        !isAM -> hour12 + 12
        else -> hour12
    }
    return String.format("%02d:%02d", hour24, minute)
}

fun parseTimeTo12HourState(time24: String): Triple<Int, Int, Boolean> {
    val parts = time24.split(":")
    val hour24 = parts[0].toIntOrNull() ?: 9
    val minute = parts[1].toIntOrNull() ?: 0

    val hour12 = when {
        hour24 == 0 || hour24 == 12 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val isAM = hour24 < 12
    return Triple(hour12, minute, isAM)
}

@Composable
fun TimePickerDialog(
    title: String,
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val (initialHour12, initialMinute, initialIsAM) = remember(initialTime) {
        parseTimeTo12HourState(initialTime)
    }

    var selectedHour12 by remember { mutableStateOf(initialHour12) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var isAM by remember { mutableStateOf(initialIsAM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Time picker row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours (1-12)
                    NumberPicker12Hour(
                        value = selectedHour12,
                        onValueChange = { selectedHour12 = it },
                        range = 1..12,
                        modifier = Modifier
                    )

                    Text(
                        ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minutes (0-59)
                    NumberPicker(
                        value = selectedMinute,
                        onValueChange = { selectedMinute = it },
                        range = 0..59,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // AM/PM toggle
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isAM,
                            onClick = { isAM = true },
                            label = { Text("AM", fontSize = 14.sp) },
                            modifier = Modifier.width(60.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = !isAM,
                            onClick = { isAM = false },
                            label = { Text("PM", fontSize = 14.sp) },
                            modifier = Modifier.width(60.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val time24 = convertTo24Hour(selectedHour12, selectedMinute, isAM)
                    onTimeSelected(time24)
                }
            ) {
                Text("OK", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}


@Composable
fun NumberPicker12Hour(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    val items = range.toList()
    val initialIndex = items.indexOf(value).coerceAtLeast(0)
    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

    // For manual text input
    var isEditing by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(selectedIndex) {
        if (!isEditing) {
            onValueChange(items[selectedIndex])
            textInput = items[selectedIndex].toString()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Up arrow - just click
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (selectedIndex < items.size - 1)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )
                .clickable(
                    enabled = selectedIndex < items.size - 1
                ) {
                    if (selectedIndex < items.size - 1) selectedIndex++
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Increase",
                tint = if (selectedIndex < items.size - 1)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }

        // Current value - editable on tap
        Surface(
            modifier = Modifier
                .size(80.dp, 50.dp)
                .clickable { isEditing = true },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isEditing) {
                    BasicTextField(
                        value = textInput,
                        onValueChange = { newText ->
                            if (newText.isEmpty() || newText.all { it.isDigit() }) {
                                textInput = newText
                                val newValue = newText.toIntOrNull()
                                if (newValue != null && newValue in range) {
                                    val newIndex = items.indexOf(newValue)
                                    if (newIndex != -1) {
                                        selectedIndex = newIndex
                                        isEditing = false
                                    }
                                }
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                    )
                } else {
                    Text(
                        text = items[selectedIndex].toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Down arrow - just click
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (selectedIndex > 0)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )
                .clickable(
                    enabled = selectedIndex > 0
                ) {
                    if (selectedIndex > 0) selectedIndex--
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Decrease",
                tint = if (selectedIndex > 0)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    val items = range.toList()
    val initialIndex = items.indexOf(value).coerceAtLeast(0)
    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

    // For manual text input
    var isEditing by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(selectedIndex) {
        if (!isEditing) {
            onValueChange(items[selectedIndex])
            textInput = items[selectedIndex].toString()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Up arrow - just click
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (selectedIndex < items.size - 1)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )
                .clickable(
                    enabled = selectedIndex < items.size - 1
                ) {
                    if (selectedIndex < items.size - 1) selectedIndex++
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Increase",
                tint = if (selectedIndex < items.size - 1)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }

        // Current value - editable on tap
        Surface(
            modifier = Modifier
                .size(80.dp, 50.dp)
                .clickable { isEditing = true },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isEditing) {
                    BasicTextField(
                        value = textInput,
                        onValueChange = { newText ->
                            if (newText.isEmpty() || newText.all { it.isDigit() }) {
                                textInput = newText
                                val newValue = newText.toIntOrNull()
                                if (newValue != null && newValue in range) {
                                    val newIndex = items.indexOf(newValue)
                                    if (newIndex != -1) {
                                        selectedIndex = newIndex
                                        isEditing = false
                                    }
                                }
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                    )
                } else {
                    Text(
                        text = String.format("%02d", items[selectedIndex]),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Down arrow - just click
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (selectedIndex > 0)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )
                .clickable(
                    enabled = selectedIndex > 0
                ) {
                    if (selectedIndex > 0) selectedIndex--
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Decrease",
                tint = if (selectedIndex > 0)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}


@Composable
fun ServiceLocationStep(
    uiState: PostServiceUiState,
    viewModel: PostServiceViewModel,
    colorScheme: ColorScheme,
    scrollState: androidx.compose.foundation.ScrollState,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    // State for coverage areas input
    var currentAreaInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    var showAddHint by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Predefined common cities/areas in Kenya
    val suggestedAreas = listOf(
        "Nairobi CBD", "Westlands", "Kilimani", "Karen", "Runda",
        "Mombasa CBD", "Nyali", "Bamburi", "Kisumu CBD", "Milimani",
        "Nakuru CBD", "Eldoret CBD", "Thika", "Kiambu", "Ruaka"
    )

    // Get current coverage areas list
    val coverageAreasList = remember(uiState.coverageAreas) {
        uiState.coverageAreas.toMutableList()
    }

    fun addCoverageArea(input: String) {
        val trimmed = input.trim()
        if (trimmed.isNotEmpty() && !coverageAreasList.contains(trimmed)) {
            val updated = coverageAreasList.toMutableList().apply { add(trimmed) }
            viewModel.updateCoverageAreas(updated)
            currentAreaInput = ""
            showDuplicateError = false
            showAddHint = false
        } else if (trimmed.isNotEmpty()) {
            showDuplicateError = true
            coroutineScope.launch {
                delay(1500)
                showDuplicateError = false
            }
        }
    }

    fun removeCoverageArea(area: String) {
        val updated = coverageAreasList.filter { it != area }
        viewModel.updateCoverageAreas(updated)
    }

    fun clearAllAreas() {
        viewModel.updateCoverageAreas(emptyList())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Where do you provide your service?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface
        )

        Text(
            "Tell clients which areas you serve",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Coverage Areas Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with counter and clear button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Service Areas *",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "Add all areas where you offer this service",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Areas counter with clear button
                    if (coverageAreasList.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedContent(
                                targetState = coverageAreasList.size,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(200)) +
                                            scaleIn(initialScale = 0.7f) togetherWith
                                            fadeOut(animationSpec = tween(100)) +
                                            scaleOut(targetScale = 0.7f)
                                }
                            ) { count ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = colorScheme.primaryContainer,
                                    modifier = Modifier
                                ) {
                                    Text(
                                        text = "$count",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }

                            ClearButton(
                                onClick = { clearAllAreas() }
                            )
                        }
                    }
                }

                // Areas Pills Row
                if (coverageAreasList.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(coverageAreasList) { area ->
                            ElegantPillWithRemove(
                                label = area,
                                onRemove = { removeCoverageArea(area) },
                                colorScheme = colorScheme
                            )
                        }
                    }
                }

                // Add Area Section - With plus icon inside input field
                var isFocused by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    BasicTextField(
                        value = currentAreaInput,
                        onValueChange = {
                            currentAreaInput = it
                            if (it.isNotBlank() && !showAddHint) {
                                showAddHint = true
                            } else if (it.isBlank() && showAddHint) {
                                showAddHint = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .onFocusChanged { isFocused = it.isFocused },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = colorScheme.onSurface,
                            lineHeight = 20.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (currentAreaInput.isNotBlank()) {
                                    addCoverageArea(currentAreaInput)
                                }
                            }
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (currentAreaInput.isEmpty()) {
                                        Text(
                                            text = if (coverageAreasList.isEmpty())
                                                "Type an area (e.g., Nairobi CBD, Westlands)"
                                            else
                                                "Type another area",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 14.sp,
                                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        )
                                    }
                                    innerTextField()
                                }

                                AnimatedVisibility(
                                    visible = currentAreaInput.isNotBlank(),
                                    enter = scaleIn() + fadeIn(),
                                    exit = scaleOut() + fadeOut()
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (currentAreaInput.isNotBlank()) {
                                                addCoverageArea(currentAreaInput)
                                            }
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Area",
                                            modifier = Modifier.size(24.dp),
                                            tint = colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    )

                    // Custom border for the input field
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = when {
                                    showDuplicateError -> colorScheme.error
                                    isFocused -> colorScheme.primary
                                    else -> colorScheme.outline.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }

                // Error or hint message
                if (showDuplicateError) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            modifier = Modifier.size(16.dp),
                            tint = colorScheme.error
                        )
                        Text(
                            text = "This area has already been added",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = colorScheme.error
                            )
                        )
                    }
                } else if (showAddHint && currentAreaInput.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Hint",
                            modifier = Modifier.size(16.dp),
                            tint = colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Press the + button or Done key to add",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    }
                }

                // Suggested areas (quick-add chips)
                if (suggestedAreas.isNotEmpty() && coverageAreasList.size < 20) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Popular areas",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(suggestedAreas.filter { !coverageAreasList.contains(it) }.take(10)) { area ->
                                SuggestionChip(
                                    onClick = { addCoverageArea(area) },
                                    label = { Text(area, fontSize = 12.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = colorScheme.surfaceVariant,
                                        labelColor = colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Additional Notes (Optional)
        ServiceLabel("Additional Notes (Optional)")
        ServiceTextField(
            value = uiState.additionalNotes,
            onValueChange = viewModel::updateAdditionalNotes,
            placeholder = "Tools provided, service duration, special instructions, etc.",
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(2f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading && coverageAreasList.isNotEmpty()
            ) {
                Text("Post Service", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ClearButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Surface(
        modifier = Modifier.scale(scale),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
        onClick = {
            coroutineScope.launch {
                isPressed = true
                onClick()
                delay(80)
                isPressed = false
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "Clear",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    }
}
@Composable
fun ElegantPillWithRemove(
    label: String,
    onRemove: () -> Unit,
    colorScheme: ColorScheme
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    Surface(
        modifier = Modifier.scale(scale),
        shape = RoundedCornerShape(100.dp),
        color = colorScheme.primaryContainer,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = colorScheme.primary.copy(alpha = 0.2f)
        ),
        onClick = { }
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = colorScheme.onPrimaryContainer
                )
            )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        isPressed = true
                        onRemove()
                        delay(80)
                        isPressed = false
                    }
                },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(14.dp),
                    tint = colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ServiceLivePreview(
    title: String = "Service Title",
    category: String = "Category Name",
    subcategory: String = "",
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
                Text("Service Preview", style = MaterialTheme.typography.titleMedium, color = colorScheme.onPrimaryContainer)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                        Text(category, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                        if (subcategory.isNotBlank()) {
                            Text(
                                text = "→ $subcategory",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.tertiary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "$currency ${formatPrice(price)}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colorScheme.primary)
                        )
                        Text(formatPriceUnit(priceUnit), style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(location, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                    Text("$experience yrs exp", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSecondaryContainer, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}

private fun formatPrice(price: String): String = if (price.isNotEmpty()) {
    val priceDouble = price.toDoubleOrNull() ?: 0.0
    String.format("%,.0f", priceDouble)
} else "0.00"

private fun formatPriceUnit(unit: String): String = when (unit) {
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

@Composable
fun ServiceTimeField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.width(100.dp),
        placeholder = { Text("HH:MM") },
        shape = RoundedCornerShape(8.dp), singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun ServiceSectionCard(title: String, optional: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                if (optional) Text(" (Optional)", style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurface.copy(alpha = 0.5f)))
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ServiceLabel(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), modifier = Modifier.padding(bottom = 6.dp, top = 4.dp))
}

@Composable
fun ServiceTextField(
    value: String, onValueChange: (String) -> Unit, placeholder: String,
    prefix: String? = null, leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    singleLine: Boolean = true, minLines: Int = 1, keyboardType: KeyboardType = KeyboardType.Text
) {
    val colorScheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = colorScheme.onSurface.copy(alpha = 0.5f)) },
        prefix = if (prefix != null) { { Text(prefix, color = colorScheme.onSurface.copy(alpha = 0.5f)) } } else null,
        leadingIcon = if (leadingIcon != null) { { Icon(leadingIcon, contentDescription = null, tint = colorScheme.onSurface.copy(alpha = 0.5f)) } } else null,
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorScheme.outline,
            focusedBorderColor = colorScheme.primary,
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface
        ),
        singleLine = singleLine, minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(modifier = Modifier.height(12.dp))
}


@Composable
fun SuccessDialog(
    message: String,
    onViewListing: () -> Unit,
    onPostAnother: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.pivota_success_lottie)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Success",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "What would you like to do next?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onViewListing,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View My Service",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                OutlinedButton(
                    onClick = onPostAnother,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Post Another Service",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        dismissButton = {}
    )
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
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
    val colorScheme = MaterialTheme.colorScheme
    val isInteractive = enabled && options.isNotEmpty() && !isLoading

    // Simple contains search (case insensitive)
    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) {
            options
        } else {
            options.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        enabled = false,
        placeholder = {
            when {
                isLoading -> Text("Loading Categories...", color = colorScheme.onSurface.copy(alpha = 0.5f))
                else -> Text(placeholder, color = colorScheme.onSurface.copy(alpha = 0.5f))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isInteractive) {
                if (isInteractive) {
                    searchQuery = ""
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
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = colorScheme.primary)
                else -> Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = if (isInteractive) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    )

    if (showBottomSheet && options.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                searchQuery = ""
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = colorScheme.surface,
            dragHandle = {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.width(40.dp).height(4.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = colorScheme.onSurface.copy(alpha = 0.3f)
                    ) {}
                }
            },
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // Search Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onSurface
                    )
                    IconButton(onClick = {
                        showBottomSheet = false
                        searchQuery = ""
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = colorScheme.onSurfaceVariant)
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...", color = colorScheme.onSurface.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorScheme.outline,
                        focusedBorderColor = colorScheme.primary,
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface
                    ),
                    singleLine = true
                )

                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.2f))

                // Options List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (filteredOptions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (searchQuery.isNotBlank()) "No matching categories found" else "No results found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(
                            count = filteredOptions.size,
                            key = { index -> filteredOptions[index] }
                        ) { index ->
                            val option = filteredOptions[index]
                            val isSelected = option == value
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onValueChange(option)
                                        showBottomSheet = false
                                        searchQuery = ""
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
                                    // Highlight matching text
                                    HighlightedText(
                                        text = option,
                                        highlight = searchQuery,
                                        isSelected = isSelected,
                                        colorScheme = colorScheme
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
                            if (index != filteredOptions.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = colorScheme.outline.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HighlightedText(
    text: String,
    highlight: String,
    isSelected: Boolean,
    colorScheme: ColorScheme
) {
    if (highlight.isBlank()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (isSelected) colorScheme.primary else colorScheme.onSurface
        )
    } else {
        val lowerText = text.lowercase()
        val lowerHighlight = highlight.lowercase()
        val startIndex = lowerText.indexOf(lowerHighlight)

        if (startIndex >= 0) {
            val endIndex = startIndex + highlight.length

            val annotatedString = buildAnnotatedString {
                // Text before highlight
                append(text.substring(0, startIndex))
                // Highlighted text
                pushStyle(
                    SpanStyle(
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        background = colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
                append(text.substring(startIndex, endIndex.coerceAtMost(text.length)))
                pop()
                // Text after highlight
                append(text.substring(endIndex.coerceAtMost(text.length)))
            }

            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                ),
                color = if (isSelected) colorScheme.primary else colorScheme.onSurface
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                ),
                color = if (isSelected) colorScheme.primary else colorScheme.onSurface
            )
        }
    }
}





data class ServiceDayAvailability(
    val day: String,
    var isAvailable: Boolean = true,
    var openTime: String = "09:00",
    var closeTime: String = "17:00"
)