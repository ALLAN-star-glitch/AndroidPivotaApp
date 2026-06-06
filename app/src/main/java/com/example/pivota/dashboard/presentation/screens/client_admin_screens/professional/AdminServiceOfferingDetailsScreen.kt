package com.example.pivota.dashboard.presentation.screens.client_admin_screens.professional

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import com.example.pivota.R
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.professionals.formatTimeTo12Hour
import com.example.pivota.ui.theme.PivotaConnectTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Data classes
data class AdminServiceDialogConfig(
    val title: String,
    val message: String,
    val icon: ImageVector,
    val color: Color
)

data class AdminServiceManagementAction(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

enum class AdminServiceStatus {
    ACTIVE,
    PENDING,
    PAUSED,
    ARCHIVED,
    DRAFT;

    fun displayName(): String = when (this) {
        ACTIVE -> "Active"
        PENDING -> "Pending Review"
        PAUSED -> "Paused"
        ARCHIVED -> "Archived"
        DRAFT -> "Draft"
    }

    @Composable
    fun color(): Color = when (this) {
        ACTIVE -> Color(0xFF10B981)
        PENDING -> MaterialTheme.colorScheme.tertiary
        PAUSED -> MaterialTheme.colorScheme.secondary
        ARCHIVED -> MaterialTheme.colorScheme.onSurfaceVariant
        DRAFT -> MaterialTheme.colorScheme.outline
    }

    fun icon(): ImageVector = when (this) {
        ACTIVE -> Icons.Filled.CheckCircle
        PENDING -> Icons.Filled.HourglassEmpty
        PAUSED -> Icons.Filled.Pause
        ARCHIVED -> Icons.Filled.Archive
        DRAFT -> Icons.Filled.Description
    }
}

data class AdminServiceOfferingUiModel(
    val id: String,
    val title: String,
    val description: String,
    val categoryName: String,
    val basePrice: Double,
    val priceUnit: String,
    val currency: String,
    val coverageAreas: List<String>,
    val status: AdminServiceStatus,
    val postedDate: Date,
    val expiryDate: Date? = null,
    val views: Int = 0,
    val messages: Int = 0,
    val bookings: Int = 0,
    val newInquiries: Int = 0,
    val yearsExperience: Int,
    val availability: List<DayAvailability>,
    val professionalName: String,
    val professionalAvatar: String?,
    val isVerified: Boolean,
    val averageRating: Double,
    val reviewCount: Int,
    val averageResponseTime: Double = 0.0,
    val viewsTrend: String = "+12%",
    val messagesTrend: String = "+8%",
    val bookingsTrend: String = "+5%"
)

// Helper functions
private fun formatServiceNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

private fun formatPriceUnitLabel(unit: String): String {
    return when (unit) {
        "PER_HOUR" -> "/hour"
        "PER_DAY" -> "/day"
        "PER_WEEK" -> "/week"
        "PER_MONTH" -> "/month"
        "PER_YEAR" -> "/year"
        "PER_VISIT" -> "/visit"
        "PER_SESSION" -> "/session"
        "FIXED" -> " fixed price"
        "PACKAGE" -> " package"
        else -> "/${unit.lowercase().replace("_", " ")}"
    }
}

// Conversion function
fun ServiceOffering.toAdminServiceOfferingUiModel(): AdminServiceOfferingUiModel {
    val status = when (status.uppercase()) {
        "ACTIVE" -> AdminServiceStatus.ACTIVE
        "PENDING" -> AdminServiceStatus.PENDING
        "PAUSED" -> AdminServiceStatus.PAUSED
        "ARCHIVED" -> AdminServiceStatus.ARCHIVED
        "DRAFT" -> AdminServiceStatus.DRAFT
        else -> AdminServiceStatus.ACTIVE
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    return AdminServiceOfferingUiModel(
        id = id,
        title = title,
        description = description,
        categoryName = categoryName,
        basePrice = basePrice,
        priceUnit = priceUnit,
        currency = currency,
        coverageAreas = coverageAreas,
        status = status,
        postedDate = try { dateFormat.parse(createdAt) } catch (e: Exception) { Date() },
        yearsExperience = yearsExperience ?: 0,
        availability = availability,
        professionalName = professionalName,
        professionalAvatar = professionalAvatar,
        isVerified = isVerified,
        averageRating = averageRating,
        reviewCount = reviewCount,
        views = 0,
        messages = 0,
        bookings = 0,
        newInquiries = 0
    )
}

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceOfferingDetailsScreen(
    serviceOffering: ServiceOffering,
    onNavigateBack: () -> Unit,
    onEditService: (String) -> Unit,
    onDuplicateService: (String) -> Unit,
    onArchiveService: (String) -> Unit,
    onDeleteService: (String) -> Unit,
    onPauseService: (String) -> Unit,
    onResumeService: (String) -> Unit,
    onMarkActive: (String) -> Unit,
    onViewInquiries: (String) -> Unit,
    onShareService: (String) -> Unit,
    onViewLogs: (String) -> Unit,
    onCopyServiceLink: (String) -> Unit
) {
    val adminListing = remember(serviceOffering) {
        serviceOffering.toAdminServiceOfferingUiModel()
    }

    AdminServiceOfferingDetailsScreenContent(
        serviceOffering = adminListing,
        onNavigateBack = onNavigateBack,
        onEditService = onEditService,
        onDuplicateService = onDuplicateService,
        onArchiveService = onArchiveService,
        onDeleteService = onDeleteService,
        onPauseService = onPauseService,
        onResumeService = onResumeService,
        onMarkActive = onMarkActive,
        onViewInquiries = onViewInquiries,
        onShareService = onShareService,
        onViewLogs = onViewLogs,
        onCopyServiceLink = onCopyServiceLink
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceOfferingDetailsScreenContent(
    serviceOffering: AdminServiceOfferingUiModel,
    onNavigateBack: () -> Unit,
    onEditService: (String) -> Unit,
    onDuplicateService: (String) -> Unit,
    onArchiveService: (String) -> Unit,
    onDeleteService: (String) -> Unit,
    onPauseService: (String) -> Unit,
    onResumeService: (String) -> Unit,
    onMarkActive: (String) -> Unit,
    onViewInquiries: (String) -> Unit,
    onShareService: (String) -> Unit,
    onViewLogs: (String) -> Unit,
    onCopyServiceLink: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val clipboardManager = LocalClipboardManager.current

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showStatusChangeDialog by remember { mutableStateOf(false) }
    var pendingStatusChange by remember { mutableStateOf<AdminServiceStatus?>(null) }
    var showCopyConfirmation by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(true) }
    var serviceStatus by remember { mutableStateOf(serviceOffering.status) }

    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(serviceOffering.basePrice)
    val location = if (serviceOffering.coverageAreas.isNotEmpty()) {
        if (serviceOffering.coverageAreas.size == 1) {
            serviceOffering.coverageAreas.first()
        } else {
            "${serviceOffering.coverageAreas.first()} +${serviceOffering.coverageAreas.size - 1}"
        }
    } else {
        "Location not specified"
    }

    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val postedDateStr = dateFormat.format(serviceOffering.postedDate)

    val viewsFormatted = formatServiceNumber(serviceOffering.views)
    val inquiriesFormatted = formatServiceNumber(serviceOffering.messages + serviceOffering.bookings)

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            AdminServiceDetailsTopBar(
                title = serviceOffering.title,
                onNavigateBack = onNavigateBack,
                onMenuClick = { showMenu = true }
            )
        },
        bottomBar = {
            AdminServiceDetailsBottomBar(
                serviceStatus = serviceStatus,
                onEditClick = { onEditService(serviceOffering.id) },
                onStatusChangeClick = {
                    when (serviceStatus) {
                        AdminServiceStatus.ACTIVE -> {
                            pendingStatusChange = AdminServiceStatus.PAUSED
                            showStatusChangeDialog = true
                        }
                        AdminServiceStatus.PENDING -> {
                            pendingStatusChange = AdminServiceStatus.ACTIVE
                            showStatusChangeDialog = true
                        }
                        AdminServiceStatus.PAUSED -> {
                            pendingStatusChange = AdminServiceStatus.ACTIVE
                            showStatusChangeDialog = true
                        }
                        else -> {
                            pendingStatusChange = AdminServiceStatus.ACTIVE
                            showStatusChangeDialog = true
                        }
                    }
                },
                isWide = isWide
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isWide) {
                // TWO PANE LAYOUT (Tablet/Desktop)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left Pane - Image and Overview
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        AdminServiceOverviewCard(
                            serviceOffering = serviceOffering,
                            serviceStatus = serviceStatus,
                            formattedPrice = formattedPrice,
                            postedDateStr = postedDateStr,
                            viewsFormatted = viewsFormatted,
                            inquiriesFormatted = inquiriesFormatted,
                            location = location,
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AdminServiceInquiriesCard(
                            totalInquiries = serviceOffering.messages + serviceOffering.bookings,
                            newInquiries = serviceOffering.newInquiries,
                            messages = serviceOffering.messages,
                            bookings = serviceOffering.bookings,
                            onViewAllClick = { onViewInquiries(serviceOffering.id) },
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(80.dp))
                    }

                    // Right Pane - Details and Management
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        AdminServiceDescriptionCard(
                            description = serviceOffering.description,
                            coverageAreas = serviceOffering.coverageAreas,
                            availability = serviceOffering.availability,
                            yearsExperience = serviceOffering.yearsExperience,
                            professionalName = serviceOffering.professionalName,
                            professionalAvatar = serviceOffering.professionalAvatar,
                            isVerified = serviceOffering.isVerified,
                            rating = serviceOffering.averageRating,
                            reviewCount = serviceOffering.reviewCount,
                            isExpanded = isDescriptionExpanded,
                            onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded },
                            onCopyLink = {
                                onCopyServiceLink(serviceOffering.id)
                                clipboardManager.setText(AnnotatedString("https://pivotaconnect.com/services/${serviceOffering.id}"))
                                showCopyConfirmation = true
                            },
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AdminServiceManagementTools(
                            serviceStatus = serviceStatus,
                            onEditClick = { onEditService(serviceOffering.id) },
                            onDuplicateClick = { onDuplicateService(serviceOffering.id) },
                            onPauseClick = { showPauseDialog = true },
                            onResumeClick = { onResumeService(serviceOffering.id) },
                            onMarkActiveClick = {
                                pendingStatusChange = AdminServiceStatus.ACTIVE
                                showStatusChangeDialog = true
                            },
                            onDeleteClick = { showDeleteDialog = true },
                            onShareClick = { onShareService(serviceOffering.id) },
                            onLogsClick = { onViewLogs(serviceOffering.id) },
                            colorScheme = colorScheme,
                            isWide = isWide
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AdminServiceAnalyticsCard(
                            views = serviceOffering.views,
                            messages = serviceOffering.messages,
                            bookings = serviceOffering.bookings,
                            averageResponseTime = serviceOffering.averageResponseTime,
                            viewsTrend = serviceOffering.viewsTrend,
                            messagesTrend = serviceOffering.messagesTrend,
                            bookingsTrend = serviceOffering.bookingsTrend,
                            onViewFullAnalytics = {},
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            } else {
                // SINGLE PANE LAYOUT (Mobile)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    AdminServiceOverviewCard(
                        serviceOffering = serviceOffering,
                        serviceStatus = serviceStatus,
                        formattedPrice = formattedPrice,
                        postedDateStr = postedDateStr,
                        viewsFormatted = viewsFormatted,
                        inquiriesFormatted = inquiriesFormatted,
                        location = location,
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminServiceDescriptionCard(
                        description = serviceOffering.description,
                        coverageAreas = serviceOffering.coverageAreas,
                        availability = serviceOffering.availability,
                        yearsExperience = serviceOffering.yearsExperience,
                        professionalName = serviceOffering.professionalName,
                        professionalAvatar = serviceOffering.professionalAvatar,
                        isVerified = serviceOffering.isVerified,
                        rating = serviceOffering.averageRating,
                        reviewCount = serviceOffering.reviewCount,
                        isExpanded = isDescriptionExpanded,
                        onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded },
                        onCopyLink = {
                            onCopyServiceLink(serviceOffering.id)
                            clipboardManager.setText(AnnotatedString("https://pivotaconnect.com/services/${serviceOffering.id}"))
                            showCopyConfirmation = true
                        },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminServiceInquiriesCard(
                        totalInquiries = serviceOffering.messages + serviceOffering.bookings,
                        newInquiries = serviceOffering.newInquiries,
                        messages = serviceOffering.messages,
                        bookings = serviceOffering.bookings,
                        onViewAllClick = { onViewInquiries(serviceOffering.id) },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminServiceManagementTools(
                        serviceStatus = serviceStatus,
                        onEditClick = { onEditService(serviceOffering.id) },
                        onDuplicateClick = { onDuplicateService(serviceOffering.id) },
                        onPauseClick = { showPauseDialog = true },
                        onResumeClick = { onResumeService(serviceOffering.id) },
                        onMarkActiveClick = {
                            pendingStatusChange = AdminServiceStatus.ACTIVE
                            showStatusChangeDialog = true
                        },
                        onDeleteClick = { showDeleteDialog = true },
                        onShareClick = { onShareService(serviceOffering.id) },
                        onLogsClick = { onViewLogs(serviceOffering.id) },
                        colorScheme = colorScheme,
                        isWide = isWide
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminServiceAnalyticsCard(
                        views = serviceOffering.views,
                        messages = serviceOffering.messages,
                        bookings = serviceOffering.bookings,
                        averageResponseTime = serviceOffering.averageResponseTime,
                        viewsTrend = serviceOffering.viewsTrend,
                        messagesTrend = serviceOffering.messagesTrend,
                        bookingsTrend = serviceOffering.bookingsTrend,
                        onViewFullAnalytics = {},
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Copy confirmation tooltip
            AnimatedVisibility(
                visible = showCopyConfirmation,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colorScheme.primary,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = "✓ Link copied to clipboard",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            LaunchedEffect(showCopyConfirmation) {
                if (showCopyConfirmation) {
                    delay(2000)
                    showCopyConfirmation = false
                }
            }
        }
    }

    // Dropdown Menu
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .shadow(4.dp)
    ) {
        DropdownMenuItem(
            text = { Text("Edit", color = colorScheme.primary) },
            onClick = {
                showMenu = false
                onEditService(serviceOffering.id)
            },
            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = colorScheme.primary) }
        )
        DropdownMenuItem(
            text = { Text("Duplicate", color = colorScheme.primary) },
            onClick = {
                showMenu = false
                onDuplicateService(serviceOffering.id)
            },
            leadingIcon = { Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = colorScheme.primary) }
        )
        DropdownMenuItem(
            text = { Text("Archive", color = colorScheme.onSurfaceVariant) },
            onClick = {
                showMenu = false
                onArchiveService(serviceOffering.id)
            },
            leadingIcon = { Icon(Icons.Outlined.Archive, contentDescription = null, tint = colorScheme.onSurfaceVariant) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text("Delete", color = Color(0xFFBA2D2D)) },
            onClick = {
                showMenu = false
                showDeleteDialog = true
            },
            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFBA2D2D)) }
        )
    }

    // Dialogs
    if (showDeleteDialog) {
        AdminServiceConfirmationDialog(
            title = "Delete Service?",
            message = "This action cannot be undone. The service will be permanently removed.",
            icon = Icons.Filled.Delete,
            iconColor = Color(0xFFBA2D2D),
            confirmText = "Delete",
            onConfirm = {
                onDeleteService(serviceOffering.id)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showPauseDialog) {
        AdminServiceConfirmationDialog(
            title = if (serviceStatus == AdminServiceStatus.PAUSED) "Resume Service?" else "Pause Service?",
            message = if (serviceStatus == AdminServiceStatus.PAUSED) {
                "The service will become visible to potential clients again."
            } else {
                "The service will no longer be visible to potential clients. You can resume anytime."
            },
            icon = if (serviceStatus == AdminServiceStatus.PAUSED) Icons.Filled.PlayArrow else Icons.Filled.Pause,
            iconColor = Color(0xFFC95D3A),
            confirmText = if (serviceStatus == AdminServiceStatus.PAUSED) "Resume" else "Pause",
            onConfirm = {
                if (serviceStatus == AdminServiceStatus.PAUSED) {
                    onResumeService(serviceOffering.id)
                    serviceStatus = AdminServiceStatus.ACTIVE
                } else {
                    onPauseService(serviceOffering.id)
                    serviceStatus = AdminServiceStatus.PAUSED
                }
                showPauseDialog = false
            },
            onDismiss = { showPauseDialog = false }
        )
    }

    if (showStatusChangeDialog && pendingStatusChange != null) {
        val dialogConfig = when (pendingStatusChange) {
            AdminServiceStatus.ACTIVE -> AdminServiceDialogConfig(
                title = "Mark as Active?",
                message = "The service will be listed as active and visible to clients.",
                icon = Icons.Filled.CheckCircle,
                color = Color(0xFF10B981)
            )
            AdminServiceStatus.PAUSED -> AdminServiceDialogConfig(
                title = "Pause Service?",
                message = "The service will be paused and hidden from clients.",
                icon = Icons.Filled.Pause,
                color = Color(0xFFC95D3A)
            )
            else -> AdminServiceDialogConfig(
                title = "Change Status?",
                message = "Are you sure you want to change the service status?",
                icon = Icons.Filled.Info,
                color = colorScheme.primary
            )
        }

        AdminServiceConfirmationDialog(
            title = dialogConfig.title,
            message = dialogConfig.message,
            icon = dialogConfig.icon,
            iconColor = dialogConfig.color,
            confirmText = "Confirm",
            onConfirm = {
                when (pendingStatusChange) {
                    AdminServiceStatus.ACTIVE -> {
                        onMarkActive(serviceOffering.id)
                        serviceStatus = AdminServiceStatus.ACTIVE
                    }
                    AdminServiceStatus.PAUSED -> {
                        onPauseService(serviceOffering.id)
                        serviceStatus = AdminServiceStatus.PAUSED
                    }
                    else -> {}
                }
                showStatusChangeDialog = false
                pendingStatusChange = null
            },
            onDismiss = {
                showStatusChangeDialog = false
                pendingStatusChange = null
            }
        )
    }
}

// Top Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceDetailsTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    onMenuClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onSurfaceVariant)
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More options", tint = colorScheme.onSurfaceVariant)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surface,
            scrolledContainerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface,
            navigationIconContentColor = colorScheme.onSurfaceVariant,
            actionIconContentColor = colorScheme.onSurfaceVariant
        )
    )
}

// Bottom Bar
@Composable
fun AdminServiceDetailsBottomBar(
    serviceStatus: AdminServiceStatus,
    onEditClick: () -> Unit,
    onStatusChangeClick: () -> Unit,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(color = colorScheme.surface, shadowElevation = 8.dp, tonalElevation = 2.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val statusButtonText = when (serviceStatus) {
                AdminServiceStatus.ACTIVE -> "Pause Service"
                AdminServiceStatus.PENDING -> "Approve Service"
                AdminServiceStatus.PAUSED -> "Resume Service"
                AdminServiceStatus.ARCHIVED -> "Restore Service"
                AdminServiceStatus.DRAFT -> "Publish Service"
            }
            val statusButtonColor = when (serviceStatus) {
                AdminServiceStatus.ACTIVE -> Color(0xFFC95D3A)
                AdminServiceStatus.PENDING -> Color(0xFF10B981)
                AdminServiceStatus.PAUSED -> Color(0xFF10B981)
                AdminServiceStatus.ARCHIVED -> Color(0xFF10B981)
                AdminServiceStatus.DRAFT -> Color(0xFF10B981)
            }
            OutlinedButton(
                onClick = onStatusChangeClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, statusButtonColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = statusButtonColor)
            ) {
                Icon(
                    when (serviceStatus) {
                        AdminServiceStatus.ACTIVE -> Icons.Outlined.Pause
                        AdminServiceStatus.PENDING -> Icons.Outlined.CheckCircle
                        else -> Icons.Outlined.PlayArrow
                    },
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(statusButtonText, fontSize = if (isWide) 16.sp else 14.sp, fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = onEditClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Service", fontSize = if (isWide) 16.sp else 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// Overview Card
@Composable
fun AdminServiceOverviewCard(
    serviceOffering: AdminServiceOfferingUiModel,
    serviceStatus: AdminServiceStatus,
    formattedPrice: String,
    postedDateStr: String,
    viewsFormatted: String,
    inquiriesFormatted: String,
    location: String,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Surface(
                modifier = Modifier.size(72.dp).align(Alignment.Top).clip(RoundedCornerShape(12.dp)),
                color = colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Work, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = serviceOffering.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Text(text = serviceOffering.categoryName, style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append("$formattedPrice")
                        append(formatPriceUnitLabel(serviceOffering.priceUnit))
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = colorScheme.secondary, modifier = Modifier.size(14.dp))
                    Text(text = location, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 2.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = serviceStatus.color().copy(alpha = 0.1f)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(serviceStatus.icon(), contentDescription = null, tint = serviceStatus.color(), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = serviceStatus.displayName(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = serviceStatus.color())
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Visibility, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Text(text = viewsFormatted, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DateRange, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Text(text = "Posted: $postedDateStr", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                    }
                    if (serviceOffering.isVerified) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Verified, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text(text = "Verified Pro", color = colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

// Description Card
@Composable
fun AdminServiceDescriptionCard(
    description: String,
    coverageAreas: List<String>,
    availability: List<DayAvailability>,
    yearsExperience: Int,
    professionalName: String,
    professionalAvatar: String?,
    isVerified: Boolean,
    rating: Double,
    reviewCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCopyLink: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().clickable { onToggleExpand() }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Service Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Icon(imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = if (isExpanded) "Collapse" else "Expand", tint = colorScheme.primary)
            }
            AnimatedVisibility(visible = isExpanded, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(text = "Description", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = colorScheme.primary)
                    Text(text = description, style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface, modifier = Modifier.padding(bottom = 16.dp))

                    if (coverageAreas.isNotEmpty()) {
                        Text(text = "Service Areas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = colorScheme.primary)
                        Text(text = coverageAreas.joinToString(", "), style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface, modifier = Modifier.padding(bottom = 16.dp))
                    }

                    Text(text = "Availability", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = colorScheme.primary)
                    availability.filter { !it.isClosed }.forEach { dayAvailability ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = dayAvailability.day, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(text = formatTimeTo12Hour(dayAvailability.open) + " - " + formatTimeTo12Hour(dayAvailability.close), style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(text = "Professional", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = colorScheme.primary, modifier = Modifier.padding(top = 16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Surface(modifier = Modifier.size(48.dp).clip(CircleShape), color = colorScheme.primaryContainer) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = professionalName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = colorScheme.onSurface)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Work, contentDescription = null, modifier = Modifier.size(14.dp), tint = colorScheme.onSurfaceVariant)
                                Text(text = "$yearsExperience years experience", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                            }
                            if (isVerified) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Verified, tint = colorScheme.primary, modifier = Modifier.size(14.dp), contentDescription = null)
                                    Text("Verified Professional", color = colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (rating > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = "Rating", tint = colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text(text = String.format("%.1f", rating), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                                Text(text = "($reviewCount)", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = onCopyLink, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, colorScheme.outlineVariant)) {
                        Icon(Icons.Outlined.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Service Link", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// Inquiries Card
@Composable
fun AdminServiceInquiriesCard(
    totalInquiries: Int,
    newInquiries: Int,
    messages: Int,
    bookings: Int,
    onViewAllClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Inquiries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = CircleShape, color = colorScheme.primary.copy(alpha = 0.1f)) {
                        Text(text = totalInquiries.toString(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                Text(text = "View All →", color = colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onViewAllClick() })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                AdminServiceInquiriesStat(value = messages.toString(), label = "Messages", colorScheme = colorScheme)
                AdminServiceInquiriesStat(value = bookings.toString(), label = "Bookings", colorScheme = colorScheme)
                AdminServiceInquiriesStat(value = (totalInquiries - newInquiries).toString(), label = "Responded", colorScheme = colorScheme)
            }
            if (newInquiries > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(20.dp), color = colorScheme.tertiary.copy(alpha = 0.1f)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.FiberNew, contentDescription = null, tint = colorScheme.tertiary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$newInquiries new ${if (newInquiries == 1) "inquiry" else "inquiries"}", color = colorScheme.tertiary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminServiceInquiriesStat(value: String, label: String, colorScheme: ColorScheme) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
    }
}

// Management Tools
@Composable
fun AdminServiceManagementTools(
    serviceStatus: AdminServiceStatus,
    onEditClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onMarkActiveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onLogsClick: () -> Unit,
    colorScheme: ColorScheme,
    isWide: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.padding(bottom = 12.dp))
            val columns = if (isWide) 4 else 2
            val actions = buildList {
                add(AdminServiceManagementAction("Edit", Icons.Outlined.Edit, colorScheme.primary, onEditClick))
                when (serviceStatus) {
                    AdminServiceStatus.PAUSED -> add(AdminServiceManagementAction("Resume", Icons.Filled.PlayArrow, colorScheme.secondary, onResumeClick))
                    AdminServiceStatus.ACTIVE -> add(AdminServiceManagementAction("Pause", Icons.Filled.Pause, colorScheme.secondary, onPauseClick))
                    AdminServiceStatus.PENDING -> add(AdminServiceManagementAction("Approve", Icons.Outlined.CheckCircle, Color(0xFF10B981), onMarkActiveClick))
                    else -> {}
                }
                add(AdminServiceManagementAction("Duplicate", Icons.Outlined.ContentCopy, colorScheme.primary, onDuplicateClick))
                add(AdminServiceManagementAction("Share", Icons.Outlined.Share, colorScheme.primary, onShareClick))
                add(AdminServiceManagementAction("Logs", Icons.Outlined.History, colorScheme.onSurfaceVariant, onLogsClick))
                add(AdminServiceManagementAction("Delete", Icons.Outlined.Delete, Color(0xFFBA2D2D), onDeleteClick))
            }
            actions.chunked(columns).forEach { rowActions ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowActions.forEach { action ->
                        AdminServiceManagementActionCard(action = action, modifier = Modifier.weight(1f), colorScheme = colorScheme)
                    }
                    repeat(columns - rowActions.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
fun AdminServiceManagementActionCard(action: AdminServiceManagementAction, modifier: Modifier = Modifier, colorScheme: ColorScheme) {
    Surface(
        modifier = modifier.height(100.dp).clickable { action.onClick() },
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surface,
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(action.icon, contentDescription = null, tint = action.color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = action.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = action.color, textAlign = TextAlign.Center)
        }
    }
}

// Analytics Card
@Composable
fun AdminServiceAnalyticsCard(
    views: Int,
    messages: Int,
    bookings: Int,
    averageResponseTime: Double,
    viewsTrend: String,
    messagesTrend: String,
    bookingsTrend: String,
    onViewFullAnalytics: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Performance Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Surface(shape = RoundedCornerShape(20.dp), color = colorScheme.tertiary.copy(alpha = 0.1f)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = colorScheme.tertiary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "AI SmartMatch™", color = colorScheme.tertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item { AdminServiceAnalyticsStatCard(icon = Icons.Outlined.Visibility, value = formatServiceNumber(views), label = "Views", trend = viewsTrend, colorScheme = colorScheme) }
                item { AdminServiceAnalyticsStatCard(icon = Icons.Outlined.ChatBubbleOutline, value = formatServiceNumber(messages), label = "Messages", trend = messagesTrend, colorScheme = colorScheme) }
                item { AdminServiceAnalyticsStatCard(icon = Icons.Outlined.BookOnline, value = formatServiceNumber(bookings), label = "Bookings", trend = bookingsTrend, colorScheme = colorScheme) }
                item { AdminServiceAnalyticsStatCard(icon = Icons.Outlined.Timer, value = String.format("%.1f hrs", averageResponseTime), label = "Avg. Response", trend = null, colorScheme = colorScheme) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onViewFullAnalytics, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, colorScheme.outlineVariant)) {
                Icon(Icons.Outlined.BarChart, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Full Analytics")
            }
        }
    }
}

@Composable
fun AdminServiceAnalyticsStatCard(icon: ImageVector, value: String, label: String, trend: String?, colorScheme: ColorScheme) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                if (trend != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = CircleShape, color = colorScheme.tertiary.copy(alpha = 0.1f)) {
                        Text(text = trend, color = colorScheme.tertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colorScheme.primary)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
        }
    }
}

// Confirmation Dialog
@Composable
fun AdminServiceConfirmationDialog(
    title: String,
    message: String,
    icon: ImageVector,
    iconColor: Color,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = colorScheme.surface, tonalElevation = 4.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.1f), modifier = Modifier.size(64.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp)) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colorScheme.onSurface, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, colorScheme.outlineVariant)) {
                        Text("Cancel", color = colorScheme.onSurfaceVariant)
                    }
                    Button(onClick = onConfirm, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = iconColor)) {
                        Text(confirmText, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Preview for Mobile (Single Pane)
@Preview(
    name = "Service Offering Details - Mobile",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    device = "spec:width=360dp,height=800dp,dpi=480"
)
@Composable
fun AdminServiceOfferingDetailsPreview() {
    PivotaConnectTheme(darkTheme = false) {
        val sample = createSampleAdminServiceOffering()
        AdminServiceOfferingDetailsScreenContent(
            serviceOffering = sample,
            onNavigateBack = {},
            onEditService = {},
            onDuplicateService = {},
            onArchiveService = {},
            onDeleteService = {},
            onPauseService = {},
            onResumeService = {},
            onMarkActive = {},
            onViewInquiries = {},
            onShareService = {},
            onViewLogs = {},
            onCopyServiceLink = {}
        )
    }
}

// Preview for Tablet (Two Pane) - Use specific tablet device
@Preview(
    name = "Service Offering Details - Tablet",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    device = "id:pixel_c"
)
@Composable
fun AdminServiceOfferingDetailsTabletPreview() {
    PivotaConnectTheme(darkTheme = false) {
        val sample = createSampleAdminServiceOffering()
        AdminServiceOfferingDetailsScreenContent(
            serviceOffering = sample,
            onNavigateBack = {},
            onEditService = {},
            onDuplicateService = {},
            onArchiveService = {},
            onDeleteService = {},
            onPauseService = {},
            onResumeService = {},
            onMarkActive = {},
            onViewInquiries = {},
            onShareService = {},
            onViewLogs = {},
            onCopyServiceLink = {}
        )
    }
}

// Alternative: Force tablet layout with explicit dimensions
@Preview(
    name = "Service Offering Details - Tablet Forced",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    widthDp = 840,
    heightDp = 1024
)
@Composable
fun AdminServiceOfferingDetailsTabletForcedPreview() {
    PivotaConnectTheme(darkTheme = false) {
        val sample = createSampleAdminServiceOffering()
        AdminServiceOfferingDetailsScreenContent(
            serviceOffering = sample,
            onNavigateBack = {},
            onEditService = {},
            onDuplicateService = {},
            onArchiveService = {},
            onDeleteService = {},
            onPauseService = {},
            onResumeService = {},
            onMarkActive = {},
            onViewInquiries = {},
            onShareService = {},
            onViewLogs = {},
            onCopyServiceLink = {}
        )
    }
}

fun createSampleAdminServiceOffering(): AdminServiceOfferingUiModel {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -7)
    val postedDate = calendar.time

    return AdminServiceOfferingUiModel(
        id = "service-123",
        title = "Professional House Painting Service",
        description = "Expert house painting services with high-quality materials. We offer interior and exterior painting for residential and commercial properties. 10+ years of experience with guaranteed satisfaction.",
        categoryName = "Painting",
        basePrice = 15000.0,
        priceUnit = "PER_DAY",
        currency = "KES",
        coverageAreas = listOf("Westlands", "Kilimani", "Lavington", "Karen"),
        status = AdminServiceStatus.ACTIVE,
        postedDate = postedDate,
        yearsExperience = 10,
        availability = listOf(
            DayAvailability("Monday", "09:00", "17:00", false),
            DayAvailability("Tuesday", "09:00", "17:00", false),
            DayAvailability("Wednesday", "09:00", "17:00", false),
            DayAvailability("Thursday", "09:00", "17:00", false),
            DayAvailability("Friday", "09:00", "17:00", false),
            DayAvailability("Saturday", "10:00", "14:00", false),
            DayAvailability("Sunday", "00:00", "00:00", true)
        ),
        professionalName = "John Doe",
        professionalAvatar = null,
        isVerified = true,
        averageRating = 4.8,
        reviewCount = 124,
        views = 1832,
        messages = 28,
        bookings = 12,
        newInquiries = 8,
        averageResponseTime = 2.5,
        viewsTrend = "+18%",
        messagesTrend = "+12%",
        bookingsTrend = "+8%"
    )
}