package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.PivotaFullScreenLoading
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import com.example.pivota.core.presentations.composables.SnackbarType
import com.example.pivota.dashboard.domain.model.listings_models.professionals.Booking
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.professionals.NumberPicker
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.BookingNavigationEvent
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.CreateBookingViewModel
import com.example.pivota.ui.theme.PivotaConnectTheme
import com.example.pivota.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

enum class BookingStep(val title: String, val stepNumber: Int) {
    DETAILS("Booking Details", 1),
    REVIEW_PAYMENT("Review & Payment", 2)
}

// Duration type matching backend DTO
enum class DurationType(val displayName: String, val apiField: String) {
    HOURS("hours", "durationHours"),
    DAYS("days", "durationDays"),
    WEEKS("weeks", "durationWeeks"),
    MONTHS("months", "durationMonths");

    companion object {
        fun fromPriceUnit(priceUnit: String): DurationType {
            return when (priceUnit) {
                "PER_HOUR" -> HOURS
                "PER_DAY" -> DAYS
                "PER_WEEK" -> WEEKS
                "PER_MONTH" -> MONTHS
                else -> HOURS
            }
        }

        fun getAllowedTypes(priceUnit: String): List<DurationType> {
            return when (priceUnit) {
                "PER_HOUR" -> listOf(HOURS)
                "PER_DAY" -> listOf(DAYS)
                "PER_WEEK" -> listOf(WEEKS)
                "PER_MONTH" -> listOf(MONTHS)
                "PER_SESSION", "FIXED" -> emptyList()
                else -> listOf(HOURS, DAYS, WEEKS, MONTHS)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProfessionalServiceBookingScreen(
    serviceOffering: ServiceOffering,
    contractorId: String,
    clientId: String,
    onNavigateBack: () -> Unit,
    onBookingComplete: (Booking) -> Unit,
    viewModel: CreateBookingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    var currentStep by remember { mutableStateOf(BookingStep.DETAILS) }

    // Success Dialog state
    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdBooking by remember { mutableStateOf<Booking?>(null) }

    // Snackbar state
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Form state for Step 1
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    // Duration fields matching backend DTO
    var durationHours by remember { mutableStateOf("") }
    var durationDays by remember { mutableStateOf("") }
    var durationWeeks by remember { mutableStateOf("") }
    var durationMonths by remember { mutableStateOf("") }

    var selectedLocation by remember { mutableStateOf("") }
    var customerNotes by remember { mutableStateOf("") }
    var proposedPrice by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    // Validation state
    var dateError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var proposedPriceError by remember { mutableStateOf<String?>(null) }

    // Handle navigation events from ViewModel
    LaunchedEffect(navigationEvent) {
        when (val event = navigationEvent) {
            is BookingNavigationEvent.BookingCreated -> {
                createdBooking = event.booking
                successMessage = "Booking request sent successfully!"
                showSuccessSnackbar = true
                viewModel.clearNavigationEvent()
            }
            is BookingNavigationEvent.Error -> {
                errorMessage = event.message
                showErrorSnackbar = true
                viewModel.clearNavigationEvent()
            }
            null -> { /* Do nothing */ }
        }
    }

    // Handle success - show snackbar first, then dialog (matches AdaptiveServicePostLayout)
    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            delay(1500)
            showSuccessDialog = true
            viewModel.resetSuccess()
        }
    }

    val priceUnit = serviceOffering.priceUnit
    val isFixed = priceUnit == "FIXED" || priceUnit == "PER_SESSION"
    val durationType = DurationType.fromPriceUnit(priceUnit)
    val requiresDuration = !isFixed

    // Get the current duration value based on price unit
    val currentDurationValue = when (durationType) {
        DurationType.HOURS -> durationHours.toIntOrNull() ?: 0
        DurationType.DAYS -> durationDays.toIntOrNull() ?: 0
        DurationType.WEEKS -> durationWeeks.toIntOrNull() ?: 0
        DurationType.MONTHS -> durationMonths.toIntOrNull() ?: 0
    }

    val currentDurationField = when (durationType) {
        DurationType.HOURS -> durationHours
        DurationType.DAYS -> durationDays
        DurationType.WEEKS -> durationWeeks
        DurationType.MONTHS -> durationMonths
    }

    val onDurationChange = when (durationType) {
        DurationType.HOURS -> { value: String -> durationHours = value }
        DurationType.DAYS -> { value: String -> durationDays = value }
        DurationType.WEEKS -> { value: String -> durationWeeks = value }
        DurationType.MONTHS -> { value: String -> durationMonths = value }
    }

// Calculate base total based on price unit type
    val baseTotal = when (serviceOffering.priceUnit) {
        "FIXED", "PER_SESSION" -> serviceOffering.basePrice  // No multiplication
        else -> serviceOffering.basePrice * currentDurationValue  // Multiply for PER_HOUR, PER_DAY, etc.
    }

// Also update the proposed total calculation
    val proposedTotal = if (proposedPrice.isNotBlank()) {
        val proposed = proposedPrice.toDoubleOrNull()
        when (serviceOffering.priceUnit) {
            "FIXED", "PER_SESSION" -> proposed  // No multiplication
            else -> proposed?.let { it * currentDurationValue }
        }
    } else null

    val finalTotal = proposedTotal ?: baseTotal
    val bookingFee = if (serviceOffering.useCustomBookingFee && serviceOffering.customBookingFeeEnabled == true) {
        serviceOffering.customBookingFeeAmount ?: 0.0
    } else 0.0
    val grandTotal = finalTotal + bookingFee

    fun isStep1Valid(): Boolean {
        var isValid = true
        dateError = if (selectedDate == null) "Please select a date" else null
        if (dateError != null) isValid = false

        if (requiresDuration) {
            durationError = when {
                currentDurationField.isBlank() -> "Please enter duration"
                currentDurationValue <= 0 -> "Please enter a valid duration"
                else -> null
            }
            if (durationError != null) isValid = false
        }

        locationError = if (selectedLocation.isBlank()) "Please select a location" else null
        if (locationError != null) isValid = false

        if (proposedPrice.isNotBlank()) {
            val proposed = proposedPrice.toDoubleOrNull()
            proposedPriceError = when {
                proposed == null -> "Please enter a valid price"
                serviceOffering.minNegotiablePrice != null && proposed < serviceOffering.minNegotiablePrice ->
                    "Price too low. Minimum: ${serviceOffering.currency} ${formatNumber(serviceOffering.minNegotiablePrice)}"
                serviceOffering.maxNegotiablePrice != null && proposed > serviceOffering.maxNegotiablePrice ->
                    "Price too high. Maximum: ${serviceOffering.currency} ${formatNumber(serviceOffering.maxNegotiablePrice)}"
                else -> null
            }
            if (proposedPriceError != null) isValid = false
        }

        return isValid
    }

    // Date picker dialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance()
                selected.set(year, month, dayOfMonth, 14, 0)
                selectedDate = selected.time
                dateError = null
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePicker = false }
        }.show()
    }

    // Reset success dialog when navigating away
    DisposableEffect(Unit) {
        onDispose {
            if (showSuccessDialog) {
                viewModel.resetSuccess()
            }
        }
    }

    // Use a Box at the root level for loading overlay
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Book Service", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep == BookingStep.DETAILS) {
                                onNavigateBack()
                            } else {
                                currentStep = BookingStep.DETAILS
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.surface,
                        titleContentColor = colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isWide) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Surface(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight(),
                            color = colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            BookingFormContent(
                                currentStep = currentStep,
                                serviceOffering = serviceOffering,
                                selectedDate = selectedDate,
                                onDateClick = { date ->
                                    selectedDate = date
                                    dateError = null
                                },
                                dateError = dateError,
                                durationValue = currentDurationField,
                                onDurationValueChange = onDurationChange,
                                durationType = durationType,
                                requiresDuration = requiresDuration,
                                isFixed = isFixed,
                                durationError = durationError,
                                selectedLocation = selectedLocation,
                                onLocationChange = { selectedLocation = it },
                                locationError = locationError,
                                customerNotes = customerNotes,
                                onCustomerNotesChange = { customerNotes = it },
                                proposedPrice = proposedPrice,
                                onProposedPriceChange = { proposedPrice = it },
                                proposedPriceError = proposedPriceError,
                                isNegotiable = serviceOffering.isNegotiable,
                                currency = serviceOffering.currency,
                                minNegotiablePrice = serviceOffering.minNegotiablePrice,
                                maxNegotiablePrice = serviceOffering.maxNegotiablePrice,
                                finalTotal = finalTotal,
                                bookingFee = bookingFee,
                                grandTotal = grandTotal,
                                durationInt = currentDurationValue,
                                durationUnitDisplay = durationType.displayName,
                                onNext = {
                                    if (isStep1Valid()) {
                                        currentStep = BookingStep.REVIEW_PAYMENT
                                    }
                                },
                                onBack = { currentStep = BookingStep.DETAILS },
                                onConfirm = {
                                    selectedDate?.let { date ->
                                        val priceUnit = serviceOffering.priceUnit
                                        val isFixedOrSession = priceUnit == "FIXED" || priceUnit == "PER_SESSION"

                                        viewModel.createBooking(
                                            serviceOffering = serviceOffering,
                                            contractorId = contractorId,
                                            clientId = clientId,
                                            selectedDate = date,
                                            // For HOURS: convert to Int (API expects Int)
                                            durationHours = if (!isFixedOrSession && priceUnit == "PER_HOUR" && currentDurationValue > 0) {
                                                currentDurationValue  // This is already Int from toIntOrNull()
                                            } else null,
                                            // For DAYS/WEEKS/MONTHS: already Int
                                            durationDays = if (!isFixedOrSession && priceUnit == "PER_DAY" && currentDurationValue > 0) {
                                                currentDurationValue
                                            } else null,
                                            durationWeeks = if (!isFixedOrSession && priceUnit == "PER_WEEK" && currentDurationValue > 0) {
                                                currentDurationValue
                                            } else null,
                                            durationMonths = if (!isFixedOrSession && priceUnit == "PER_MONTH" && currentDurationValue > 0) {
                                                currentDurationValue
                                            } else null,
                                            selectedLocation = selectedLocation,
                                            customerNotes = customerNotes,
                                            proposedPrice = proposedPrice.toDoubleOrNull()
                                        )
                                    }
                                } ,
                                isStep1Valid = { isStep1Valid() }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.8f)
                                .fillMaxHeight()
                                .background(colorScheme.background)
                                .padding(24.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            BookingSummaryPanel(
                                serviceOffering = serviceOffering,
                                selectedDate = selectedDate,
                                duration = currentDurationValue,
                                durationUnit = durationType.displayName,
                                selectedLocation = selectedLocation,
                                customerNotes = customerNotes,
                                proposedPrice = proposedPrice.toDoubleOrNull(),
                                finalTotal = finalTotal,
                                bookingFee = bookingFee,
                                grandTotal = grandTotal,
                                currency = serviceOffering.currency
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        BookingStepper(
                            currentStep = currentStep,
                            isStep1Complete = selectedDate != null && (!requiresDuration || currentDurationValue > 0) && selectedLocation.isNotBlank(),
                            onStepClick = { step ->
                                if (step == BookingStep.REVIEW_PAYMENT && isStep1Valid()) {
                                    currentStep = step
                                } else if (step == BookingStep.DETAILS) {
                                    currentStep = step
                                }
                            }
                        )

                        BookingFormContent(
                            currentStep = currentStep,
                            serviceOffering = serviceOffering,
                            selectedDate = selectedDate,
                            onDateClick = { date ->
                                selectedDate = date
                                dateError = null
                            },
                            dateError = dateError,
                            durationValue = currentDurationField,
                            onDurationValueChange = onDurationChange,
                            durationType = durationType,
                            requiresDuration = requiresDuration,
                            isFixed = isFixed,
                            durationError = durationError,
                            selectedLocation = selectedLocation,
                            onLocationChange = { selectedLocation = it },
                            locationError = locationError,
                            customerNotes = customerNotes,
                            onCustomerNotesChange = { customerNotes = it },
                            proposedPrice = proposedPrice,
                            onProposedPriceChange = { proposedPrice = it },
                            proposedPriceError = proposedPriceError,
                            isNegotiable = serviceOffering.isNegotiable,
                            currency = serviceOffering.currency,
                            minNegotiablePrice = serviceOffering.minNegotiablePrice,
                            maxNegotiablePrice = serviceOffering.maxNegotiablePrice,
                            finalTotal = finalTotal,
                            bookingFee = bookingFee,
                            grandTotal = grandTotal,
                            durationInt = currentDurationValue,
                            durationUnitDisplay = durationType.displayName,
                            onNext = {
                                if (isStep1Valid()) {
                                    currentStep = BookingStep.REVIEW_PAYMENT
                                }
                            },
                            onBack = { currentStep = BookingStep.DETAILS },
                            onConfirm = {
                                selectedDate?.let { date ->
                                    val priceUnit = serviceOffering.priceUnit
                                    val isFixedOrSession = priceUnit == "FIXED" || priceUnit == "PER_SESSION"

                                    viewModel.createBooking(
                                        serviceOffering = serviceOffering,
                                        contractorId = contractorId,
                                        clientId = clientId,
                                        selectedDate = date,
                                        // Only send duration if NOT fixed/session AND value > 0
                                        durationHours = (if (!isFixedOrSession && priceUnit == "PER_HOUR" && currentDurationValue > 0) {
                                            currentDurationValue.toInt()
                                        } else null) as Double? as Int?,
                                        durationDays = if (!isFixedOrSession && priceUnit == "PER_DAY" && currentDurationValue > 0) {
                                            currentDurationValue.toInt()
                                        } else null,
                                        durationWeeks = if (!isFixedOrSession && priceUnit == "PER_WEEK" && currentDurationValue > 0) {
                                            currentDurationValue.toInt()
                                        } else null,
                                        durationMonths = if (!isFixedOrSession && priceUnit == "PER_MONTH" && currentDurationValue > 0) {
                                            currentDurationValue.toInt()
                                        } else null,
                                        selectedLocation = selectedLocation,
                                        customerNotes = customerNotes,
                                        proposedPrice = proposedPrice.toDoubleOrNull()
                                    )
                                }
                            },
                            isStep1Valid = { isStep1Valid() }
                        )
                    }
                }
            }
        }

        // Loading overlay - placed at root level, outside Scaffold
        if (uiState.isLoading && !showSuccessDialog && !showSuccessSnackbar) {
            PivotaFullScreenLoading(
                message = "Processing your booking..."
            )
        }

        // Snackbars - placed at root level
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
                PivotaSnackbar(
                    message = errorMessage,
                    type = SnackbarType.ERROR,
                    duration = 5000,
                    onDismiss = {
                        showErrorSnackbar = false
                        errorMessage = ""
                    }
                )
            }
        }

// Success Dialog
        if (showSuccessDialog && createdBooking != null) {
            SuccessDialog(
                booking = createdBooking!!,
                serviceOffering = serviceOffering,  // Pass the service offering
                currency = serviceOffering.currency,
                grandTotal = grandTotal,
                onViewBooking = {
                    showSuccessDialog = false
                    onBookingComplete(createdBooking!!)
                },
                onClose = {
                    showSuccessDialog = false
                    onNavigateBack()
                }
            )
        }
    }
}



@Composable
private fun BookingFormContent(
    currentStep: BookingStep,
    serviceOffering: ServiceOffering,
    selectedDate: Date?,
    onDateClick: (Date) -> Unit,
    dateError: String?,
    durationValue: String,
    onDurationValueChange: (String) -> Unit,
    durationType: DurationType,
    requiresDuration: Boolean,
    isFixed: Boolean,
    durationError: String?,
    selectedLocation: String,
    onLocationChange: (String) -> Unit,
    locationError: String?,
    customerNotes: String,
    onCustomerNotesChange: (String) -> Unit,
    proposedPrice: String,
    onProposedPriceChange: (String) -> Unit,
    proposedPriceError: String?,
    isNegotiable: Boolean,
    currency: String,
    minNegotiablePrice: Double?,
    maxNegotiablePrice: Double?,
    finalTotal: Double,
    bookingFee: Double,
    grandTotal: Double,
    durationInt: Int,
    durationUnitDisplay: String,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    isStep1Valid: () -> Boolean
) {
    when (currentStep) {
        BookingStep.DETAILS -> {
            BookingDetailsStep(
                serviceOffering = serviceOffering,
                selectedDate = selectedDate,
                onDateClick = onDateClick,
                dateError = dateError,
                durationValue = durationValue,
                onDurationValueChange = onDurationValueChange,
                durationType = durationType,
                requiresDuration = requiresDuration,
                isFixed = isFixed,
                durationError = durationError,
                selectedLocation = selectedLocation,
                onLocationChange = onLocationChange,
                locationError = locationError,
                customerNotes = customerNotes,
                onCustomerNotesChange = onCustomerNotesChange,
                proposedPrice = proposedPrice,
                onProposedPriceChange = onProposedPriceChange,
                proposedPriceError = proposedPriceError,
                isNegotiable = isNegotiable,
                currency = currency,
                minNegotiablePrice = minNegotiablePrice,
                maxNegotiablePrice = maxNegotiablePrice,
                onNext = onNext
            )
        }
        BookingStep.REVIEW_PAYMENT -> {
            val safeSelectedDate = selectedDate
            if (safeSelectedDate != null) {
                ReviewPaymentStep(
                    serviceOffering = serviceOffering,
                    selectedDate = safeSelectedDate,
                    duration = durationInt,
                    durationUnit = durationUnitDisplay,
                    selectedLocation = selectedLocation,
                    customerNotes = customerNotes,
                    proposedPrice = proposedPrice.toDoubleOrNull(),
                    finalTotal = finalTotal,
                    bookingFee = bookingFee,
                    grandTotal = grandTotal,
                    currency = currency,
                    onBack = onBack,
                    onConfirm = onConfirm
                )
            }
        }
    }
}

@Composable
fun BookingStepper(
    currentStep: BookingStep,
    isStep1Complete: Boolean,
    onStepClick: (BookingStep) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val lazyListState = rememberLazyListState()

    Column {
        HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.3f))

        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(BookingStep.values().size) { index ->
                val step = BookingStep.values()[index]
                val isCompleted = when (step) {
                    BookingStep.DETAILS -> isStep1Complete
                    BookingStep.REVIEW_PAYMENT -> currentStep == BookingStep.REVIEW_PAYMENT && isStep1Complete
                }
                val isCurrent = step == currentStep
                val isLast = index == BookingStep.values().lastIndex

                StepIndicator(
                    stepNumber = step.stepNumber,
                    title = step.title,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isLast = isLast,
                    colorScheme = colorScheme,
                    onClick = {
                        if (step == BookingStep.DETAILS || (step == BookingStep.REVIEW_PAYMENT && isStep1Complete)) {
                            onStepClick(step)
                        }
                    }
                )
            }
        }

        HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.3f))
    }
}

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
        AnimatedContent(
            targetState = Triple(isCompleted, isCurrent, stepNumber),
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.7f) togetherWith
                        fadeOut(animationSpec = tween(100)) + scaleOut(targetScale = 0.7f)
            }
        ) { (completed, current, num) ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            completed -> colorScheme.primary
                            current -> colorScheme.primary.copy(alpha = 0.15f)
                            else -> colorScheme.surfaceVariant
                        }
                    )
                    .border(
                        width = if (current) 2.dp else 0.dp,
                        color = colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    completed -> Icon(
                        Icons.Outlined.Check,
                        contentDescription = "Completed",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    else -> Text(
                        num.toString(),
                        color = if (current) colorScheme.primary else colorScheme.onSurfaceVariant,
                        fontWeight = if (current) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingDetailsStep(
    serviceOffering: ServiceOffering,
    selectedDate: Date?,
    onDateClick: (Date) -> Unit,
    dateError: String?,
    durationValue: String,
    onDurationValueChange: (String) -> Unit,
    durationType: DurationType,
    requiresDuration: Boolean,
    isFixed: Boolean,
    durationError: String?,
    selectedLocation: String,
    onLocationChange: (String) -> Unit,
    locationError: String?,
    customerNotes: String,
    onCustomerNotesChange: (String) -> Unit,
    proposedPrice: String,
    onProposedPriceChange: (String) -> Unit,
    proposedPriceError: String?,
    isNegotiable: Boolean,
    currency: String,
    minNegotiablePrice: Double?,
    maxNegotiablePrice: Double?,
    onNext: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()
    val coverageAreas = serviceOffering.coverageAreas
    val context = LocalContext.current

    // Date and Time picker state
    var showDateTimePicker by remember { mutableStateOf(false) }
    var tempSelectedDateTime by remember { mutableStateOf(selectedDate ?: Date()) }

    // Custom Date and Time Picker Dialog
    if (showDateTimePicker) {
        ThemeAwareDateTimePickerDialog(
            initialDate = tempSelectedDateTime,
            onDateSelected = { dateTime ->
                onDateClick(dateTime)
                tempSelectedDateTime = dateTime
                showDateTimePicker = false
            },
            onDismiss = {
                showDateTimePicker = false
            },
            colorScheme = colorScheme
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Service Hero Card
        ServiceHeroCard(
            serviceOffering = serviceOffering,
            currency = currency
        )

        AnimatedFormSection(title = "Date & Time", icon = Icons.Outlined.CalendarToday) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateTimePicker = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (dateError != null)
                        colorScheme.errorContainer.copy(alpha = 0.1f)
                    else
                        colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = colorScheme.primary
                        )
                        Column {
                            Text(
                                "Select Date & Time",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedDate?.let { formatDateTimeString(it) } ?: "Not selected",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedDate != null) FontWeight.Medium else FontWeight.Normal,
                                color = if (selectedDate != null) colorScheme.onSurface else colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = colorScheme.primary
                    )
                }
            }

            // Error message
            if (dateError != null) {
                Text(
                    text = dateError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.error,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            }
        }

        // Duration (same as before)
        if (requiresDuration && !isFixed) {
            AnimatedFormSection(title = "Duration", icon = Icons.Outlined.Schedule) {
                OutlinedTextField(
                    value = durationValue,
                    onValueChange = onDurationValueChange,
                    placeholder = { Text("Enter number of ${durationType.displayName}") },
                    leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null) },
                    isError = durationError != null,
                    supportingText = { durationError?.let { Text(it, color = colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Text(
                            durationType.displayName,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.3f),
                        cursorColor = colorScheme.primary
                    )
                )
            }
        }

        // Location (same as before)
        AnimatedFormSection(title = "Service Location", icon = Icons.Outlined.LocationOn) {
            if (coverageAreas.size == 1) {
                OutlinedTextField(
                    value = coverageAreas.first(),
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
                onLocationChange(coverageAreas.first())
            } else {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                        placeholder = { Text("Select your area") },
                        isError = locationError != null,
                        supportingText = { locationError?.let { Text(it, color = colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        coverageAreas.forEach { area ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (area == selectedLocation) {
                                            Icon(
                                                Icons.Outlined.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = colorScheme.primary
                                            )
                                        }
                                        Text(area)
                                    }
                                },
                                onClick = {
                                    onLocationChange(area)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Negotiable Price (same as before)
        if (isNegotiable) {
            AnimatedFormSection(title = "Negotiate Price (Optional)", icon = Icons.Outlined.CurrencyExchange) {
                OutlinedTextField(
                    value = proposedPrice,
                    onValueChange = onProposedPriceChange,
                    placeholder = { Text("Propose your price") },
                    leadingIcon = { Icon(Icons.Outlined.CurrencyExchange, contentDescription = null) },
                    isError = proposedPriceError != null,
                    supportingText = { proposedPriceError?.let { Text(it, color = colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { Text(currency, color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 8.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.3f),
                        cursorColor = colorScheme.primary
                    )
                )

                AnimatedVisibility(
                    visible = minNegotiablePrice != null || maxNegotiablePrice != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Acceptable range: ")
                            if (minNegotiablePrice != null) {
                                pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                                append("$currency ${formatNumber(minNegotiablePrice)}")
                                pop()
                            }
                            if (minNegotiablePrice != null && maxNegotiablePrice != null) {
                                append(" - ")
                            }
                            if (maxNegotiablePrice != null) {
                                pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                                append("$currency ${formatNumber(maxNegotiablePrice)}")
                                pop()
                            }
                        },
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }
            }
        }

        // Additional Notes (same as before)
        AnimatedFormSection(title = "Additional Notes (Optional)", icon = Icons.Outlined.Description) {
            OutlinedTextField(
                value = customerNotes,
                onValueChange = onCustomerNotesChange,
                placeholder = { Text("Any special requests or instructions?") },
                leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.3f),
                    cursorColor = colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Next Button with animation
        AnimatedButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            text = "Review Booking",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            enabled = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeAwareDateTimePickerDialog(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
    colorScheme: ColorScheme
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var tempDateTime by remember { mutableStateOf(initialDate) }

    val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.US)
    val timeFormat = SimpleDateFormat("h:mm a", Locale.US)

    // Date picker dialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance().apply { time = tempDateTime }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance()
                selected.set(year, month, dayOfMonth)
                // Preserve the time from current tempDateTime
                val currentTime = Calendar.getInstance().apply { time = tempDateTime }
                selected.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
                selected.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
                tempDateTime = selected.time
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePicker = false }
        }.show()
    }

    // Time picker dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time", fontWeight = FontWeight.Bold) },
            text = {
                ThemeAwareTimePicker(
                    initialTime = tempDateTime,
                    onTimeSelected = { newTime ->
                        tempDateTime = newTime
                        showTimePicker = false
                    },
                    colorScheme = colorScheme
                )
            },
            confirmButton = {},
            dismissButton = {},
            shape = RoundedCornerShape(24.dp)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = colorScheme.surface,
        title = {
            Text(
                "Select Date & Time",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Date Selection Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = dateFormat.format(tempDateTime),
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onSurface
                            )
                        }
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Change Date",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Time Selection Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Time",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = timeFormat.format(tempDateTime),
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onSurface
                            )
                        }
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Change Time",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onDateSelected(tempDateTime) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                )
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun ThemeAwareTimePicker(
    initialTime: Date,
    onTimeSelected: (Date) -> Unit,
    colorScheme: ColorScheme
) {
    val calendar = Calendar.getInstance().apply {
        time = initialTime
    }

    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Select Time",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour Picker (0-23)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hour", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                NumberPicker(
                    value = selectedHour,
                    onValueChange = { selectedHour = it },
                    range = 0..23,
                    modifier = Modifier
                )
            }

            Text(
                ":",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Minute Picker (0-59)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Minute", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                NumberPicker(
                    value = selectedMinute,
                    onValueChange = { selectedMinute = it },
                    range = 0..59,
                    modifier = Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val newDate = Calendar.getInstance().apply {
                    time = initialTime
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }.time
                onTimeSelected(newDate)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primaryContainer,
                contentColor = colorScheme.onPrimaryContainer
            )
        ) {
            Text("Set Time", fontWeight = FontWeight.Medium)
        }
    }
}

// Add this helper function for date and time formatting
private fun formatDateTimeString(date: Date): String {
    return SimpleDateFormat("EEE, MMM d, yyyy • h:mm a", Locale.US).format(date)
}

@Composable
private fun ServiceHeroCard(
    serviceOffering: ServiceOffering,
    currency: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(colorScheme.primary, colorScheme.primary.copy(alpha = 0.7f)),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Work,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Service",
                    fontSize = 11.sp,
                    color = colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Text(
                    serviceOffering.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "$currency ${formatNumber(serviceOffering.basePrice)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.primary
                    )
                    Text(
                        "/ ${formatUnitLabel(serviceOffering.priceUnit)}",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedFormSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = colorScheme.primary
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp),
                tint = colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(animationSpec = tween(300)),
            exit = fadeOut() + shrinkVertically(animationSpec = tween(200))
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        enabled = enabled
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        if (icon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
private fun ReviewPaymentStep(
    serviceOffering: ServiceOffering,
    selectedDate: Date,
    duration: Int,
    durationUnit: String,
    selectedLocation: String,
    customerNotes: String,
    proposedPrice: Double?,
    finalTotal: Double,
    bookingFee: Double,
    grandTotal: Double,
    currency: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()
    val isNegotiated = proposedPrice != null && proposedPrice != serviceOffering.basePrice
    val formattedDate = SimpleDateFormat("EEEE, MMM d, yyyy 'at' h:mm a", Locale.US).format(selectedDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Booking Summary Card
        AnimatedCard(elevation = 2.dp) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Booking Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                BookingSummaryRow(icon = Icons.Outlined.Work, label = "Service", value = serviceOffering.title)
                BookingSummaryRow(icon = Icons.Outlined.CalendarToday, label = "Date & Time", value = formattedDate)
                if (duration > 0) {
                    BookingSummaryRow(icon = Icons.Outlined.Schedule, label = "Duration", value = "$duration $durationUnit")
                }
                BookingSummaryRow(icon = Icons.Outlined.LocationOn, label = "Location", value = selectedLocation)
                if (customerNotes.isNotBlank()) {
                    BookingSummaryRow(icon = Icons.Outlined.Description, label = "Notes", value = customerNotes)
                }
            }
        }

        // Price Breakdown Card
        AnimatedCard(elevation = 1.dp) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Price Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (isNegotiated && proposedPrice != null) {
                    AnimatedPriceRow(
                        label = "Original price",
                        value = "$currency ${formatNumber(serviceOffering.basePrice)} / ${formatUnitLabel(serviceOffering.priceUnit)}",
                        color = colorScheme.onSurfaceVariant,
                        isStrikethrough = true
                    )
                    AnimatedPriceRow(
                        label = "Negotiated price",
                        value = "$currency ${formatNumber(proposedPrice)} / ${formatUnitLabel(serviceOffering.priceUnit)}",
                        color = colorScheme.primary,
                        isBold = true
                    )
                } else {
                    AnimatedPriceRow(
                        label = "Base price",
                        value = "$currency ${formatNumber(serviceOffering.basePrice)} / ${formatUnitLabel(serviceOffering.priceUnit)}",
                        color = colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()

                AnimatedPriceRow(
                    label = "Service total (${duration} $durationUnit)",
                    value = "$currency ${formatNumber(finalTotal)}",
                    color = colorScheme.onSurface,
                    isBold = true
                )

                if (bookingFee > 0) {
                    AnimatedPriceRow(
                        label = "Booking fee",
                        value = "$currency ${formatNumber(bookingFee)}",
                        color = colorScheme.tertiary
                    )
                    if (serviceOffering.customBookingFeeRefundable == true) {
                        Text(
                            text = "✓ Refundable on cancellation",
                            fontSize = 11.sp,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedPriceRow(
                    label = "Total Amount",
                    value = "$currency ${formatNumber(grandTotal)}",
                    color = colorScheme.primary,
                    isBold = true,
                    fontSize = 20
                )
            }
        }

        // Booking Protection Card
        AnimatedCard(elevation = 0.dp) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            tint = Color(0xFF10B981),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Booking Protection",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Your payment is secured in escrow. Released only after service completion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(2f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Text("Confirm Booking", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AnimatedCard(
    elevation: Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_transition")
    val animatedElevation by infiniteTransition.animateFloat(
        initialValue = elevation.value,
        targetValue = elevation.value * 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "elevation_animation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation.dp)
    ) {
        content()
    }
}

@Composable
private fun AnimatedPriceRow(
    label: String,
    value: String,
    color: Color,
    isBold: Boolean = false,
    isStrikethrough: Boolean = false,
    fontSize: Int = 14
) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) + slideInHorizontally() togetherWith
                    fadeOut(animationSpec = tween(100)) + slideOutHorizontally()
        }
    ) { animatedValue ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = fontSize.sp
                ),
                color = color.copy(alpha = if (isStrikethrough) 0.5f else 1f)
            )
            Text(
                animatedValue,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                    fontSize = fontSize.sp
                ),
                color = color,
                textDecoration = if (isStrikethrough) TextDecoration.LineThrough else null
            )
        }
    }
}

@Composable
private fun BookingSummaryPanel(
    serviceOffering: ServiceOffering,
    selectedDate: Date?,
    duration: Int,
    durationUnit: String,
    selectedLocation: String,
    customerNotes: String,
    proposedPrice: Double?,
    finalTotal: Double,
    bookingFee: Double,
    grandTotal: Double,
    currency: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val isNegotiated = proposedPrice != null && proposedPrice != serviceOffering.basePrice
    val formattedDate = selectedDate?.let {
        SimpleDateFormat("EEE, MMM d", Locale.US).format(it)
    } ?: "Not selected"

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Service Mini Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Your Booking",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    serviceOffering.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$currency ${formatNumber(if (isNegotiated) proposedPrice!! else serviceOffering.basePrice)} / ${formatUnitLabel(serviceOffering.priceUnit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick Summary with price breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Price Summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                // Service total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Service total", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                    Text(
                        "$currency ${formatNumber(finalTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Booking fee (if applicable)
                if (bookingFee > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Booking fee", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                            if (serviceOffering.customBookingFeeRefundable == true) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "(refundable)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.primary
                                )
                            }
                        }
                        Text(
                            "$currency ${formatNumber(bookingFee)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.tertiary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "$currency ${formatNumber(grandTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        }

        // Booking details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Booking Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                SummaryInfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Date",
                    value = formattedDate
                )

                if (duration > 0) {
                    SummaryInfoRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Duration",
                        value = "$duration $durationUnit"
                    )
                }

                SummaryInfoRow(
                    icon = Icons.Outlined.LocationOn,
                    label = "Location",
                    value = selectedLocation.ifEmpty { "Not selected" }
                )
            }
        }

        // Estimated total pill
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "You'll pay",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "$currency ${formatNumber(grandTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SummaryInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BookingSummaryRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SuccessDialog(
    booking: Booking,
    serviceOffering: ServiceOffering,  // Add this parameter
    currency: String,
    grandTotal: Double,
    onViewBooking: () -> Unit,
    onClose: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.pivota_success_lottie)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true
    )

    // Calculate booking fee from service offering
    val bookingFee = if (serviceOffering.useCustomBookingFee && serviceOffering.customBookingFeeEnabled == true) {
        serviceOffering.customBookingFeeAmount ?: 0.0
    } else 0.0

    AlertDialog(
        onDismissRequest = onClose,
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
                    text = "Booking Request Sent!",
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
                    text = "Your booking request has been sent to the professional.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Use externalId instead of id
                        Text(
                            text = "Booking ID: ${booking.externalId}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total: $currency ${formatNumber(grandTotal)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // Show breakdown if there's a booking fee
                        if (bookingFee > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Includes $currency ${formatNumber(bookingFee)} booking fee",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No payment has been processed yet. The professional will review your request and respond shortly.",
                    style = MaterialTheme.typography.bodySmall.copy(
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
                    onClick = onViewBooking,
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
                        text = "View My Booking",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue Browsing",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        dismissButton = {}
    )
}

private fun formatDateString(date: Date): String {
    return SimpleDateFormat("EEE, MMM d, yyyy • h:mm a", Locale.US).format(date)
}

private fun formatNumber(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    return formatter.format(price)
}

private fun formatUnitLabel(unit: String): String {
    return when (unit) {
        "PER_HOUR" -> "hour"
        "PER_DAY" -> "day"
        "PER_WEEK" -> "week"
        "PER_MONTH" -> "month"
        "PER_VISIT" -> "visit"
        "PER_SESSION" -> "session"
        "FIXED" -> "fixed"
        else -> unit.lowercase().replace("_", " ")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Professional Service Booking - Step 1",
    showBackground = true,
    heightDp = 800,
    widthDp = 400
)
@Composable
private fun PreviewBookingStep1() {
    PivotaConnectTheme(darkTheme = false) {
        ProfessionalServiceBookingScreen(
            serviceOffering = sampleServiceOffering,
            contractorId = "contractor-id",
            clientId = "client-id",
            onNavigateBack = {},
            onBookingComplete = {}
        )
    }
}

private val sampleServiceOffering = ServiceOffering(
    id = "1",
    externalId = "EXT123",
    skilledProfessionalId = "prof-123",
    professionalName = "John Doe",
    professionalAvatar = null,
    isVerified = true,
    title = "Professional House Painting Service",
    description = "Expert house painting services...",
    categoryId = "cat123",
    categoryName = "Painting",
    basePrice = 15000.0,
    priceUnit = "PER_DAY",
    currency = "KES",
    coverageAreas = listOf("Westlands", "Kilimani", "Lavington", "Karen"),
    availability = emptyList(),
    yearsExperience = 10,
    hourlyRate = 2000.0,
    status = "ACTIVE",
    averageRating = 4.8,
    reviewCount = 124,
    createdAt = "2024-01-01T00:00:00Z",
    updatedAt = "2024-01-01T00:00:00Z",
    isNegotiable = true,
    minNegotiablePrice = 12000.0,
    maxNegotiablePrice = 18000.0,
    useCustomBookingFee = true,
    customBookingFeeEnabled = true,
    customBookingFeeAmount = 500.0,
    customBookingFeeCurrency = "KES",
    customBookingFeeDescription = "Call-out fee for consultation",
    customBookingFeeRefundable = false
)