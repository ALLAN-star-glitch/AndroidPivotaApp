package com.example.pivota.admin.presentation.screens

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
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import com.example.pivota.listings.presentation.composables.SimpleHousingImageGallery
import com.example.pivota.ui.theme.PivotaConnectTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Add this data class for dialog configuration
data class DialogConfig(
    val title: String,
    val message: String,
    val icon: ImageVector,
    val color: Color
)

// Add this data class for management actions
data class ManagementActionHousingDetails(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

/**
 * Admin Housing Listing UI Model Extension
 * Adding missing fields to match job details functionality
 */
data class AdminHousingListingUiModel(
    val id: String,
    val title: String,
    val price: String,
    val location: String,
    val exactLocation: String? = null,
    val propertyType: String,
    val status: ListingStatus,
    val postedDate: Date,
    val expiryDate: Date? = null,
    val views: Int = 0,
    val messages: Int = 0,
    val requests: Int = 0,
    val newInquiries: Int = 0,
    val description: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val squareMeters: Int,
    val facilities: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val imageRes: List<Int> = emptyList(), // For preview
    val isVerified: Boolean = true,
    val ownerName: String,
    val ownerVerified: Boolean = true,
    val rating: Double = 0.0,
    val isForSale: Boolean = false,
    val averageResponseTime: Double = 0.0, // in hours
    val viewsTrend: String = "+12%",
    val messagesTrend: String = "+8%",
    val requestsTrend: String = "+5%"
)

// Extension function to convert from HousingListingUiModel
fun HousingListingUiModel.toAdminHousingListingUiModel(): AdminHousingListingUiModel {
    return AdminHousingListingUiModel(
        id = this.id,
        title = this.title,
        price = this.price,
        location = this.location,
        exactLocation = this.location,
        propertyType = this.propertyType,
        status = this.status,
        postedDate = Date(), // You'll need to add postedDate to HousingListingUiModel
        expiryDate = null, // You'll need to add expiryDate to HousingListingUiModel
        views = this.views,
        messages = this.messages,
        requests = this.requests,
        newInquiries = 0, // You'll need to add this
        description = this.description,
        bedrooms = this.bedrooms,
        bathrooms = this.bathrooms,
        squareMeters = this.squareMeters,
        facilities = listOf("Water", "Electricity", "Parking", "Wi-Fi", "Security", "Gym"),
        imageUrls = emptyList(),
        imageRes = this.imageList,
        isVerified = this.isVerified,
        ownerName = "Property Owner",
        ownerVerified = this.isVerified,
        rating = this.rating,
        isForSale = this.isForSale,
        averageResponseTime = 2.5,
        viewsTrend = "+12%",
        messagesTrend = "+8%",
        requestsTrend = "+5%"
    )
}

// Extension function to get all images for gallery
fun AdminHousingListingUiModel.getAllImages(): List<Any> {
    return if (imageUrls.isNotEmpty()) imageUrls else imageRes
}

// Extension function to map ListingStatus to JobStatus style
fun ListingStatus.toDisplayStatus(): ListingStatusDisplay = when (this) {
    ListingStatus.AVAILABLE -> ListingStatusDisplay.AVAILABLE
    ListingStatus.PENDING -> ListingStatusDisplay.PENDING
    ListingStatus.RENTED -> ListingStatusDisplay.RENTED
    ListingStatus.SOLD -> ListingStatusDisplay.SOLD
    ListingStatus.INACTIVE -> ListingStatusDisplay.INACTIVE
    else -> ListingStatusDisplay.INACTIVE
}

enum class ListingStatusDisplay {
    AVAILABLE,
    PENDING,
    RENTED,
    SOLD,
    INACTIVE;

    fun displayName(): String = when (this) {
        AVAILABLE -> "Available"
        PENDING -> "Pending Review"
        RENTED -> "Rented"
        SOLD -> "Sold"
        INACTIVE -> "Inactive"
    }

    @Composable
    fun color(): Color = when (this) {
        AVAILABLE -> Color(0xFF10B981) // Success Green
        PENDING -> MaterialTheme.colorScheme.tertiary // Baobab Gold
        RENTED, SOLD -> MaterialTheme.colorScheme.error // Sunset Red
        INACTIVE -> MaterialTheme.colorScheme.onSurfaceVariant // Gray
    }

    fun icon(): ImageVector = when (this) {
        AVAILABLE -> Icons.Filled.CheckCircle
        PENDING -> Icons.Filled.HourglassEmpty
        RENTED -> Icons.Filled.HomeWork
        SOLD -> Icons.Filled.Sell
        INACTIVE -> Icons.Filled.Pause
    }

    fun outlinedIcon(): ImageVector = when (this) {
        AVAILABLE -> Icons.Outlined.CheckCircle
        PENDING -> Icons.Outlined.HourglassEmpty
        RENTED -> Icons.Outlined.HomeWork
        SOLD -> Icons.Outlined.Sell
        INACTIVE -> Icons.Outlined.Pause
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHouseDetailsScreen(
    housingListing: HousingListingUiModel,
    onNavigateBack: () -> Unit,
    onEditListing: (String) -> Unit,
    onDuplicateListing: (String) -> Unit,
    onArchiveListing: (String) -> Unit,
    onDeleteListing: (String) -> Unit,
    onPauseListing: (String) -> Unit,
    onResumeListing: (String) -> Unit,
    onMarkAvailable: (String) -> Unit,
    onMarkRented: (String) -> Unit,
    onMarkSold: (String) -> Unit,
    onViewInquiries: (String) -> Unit,
    onShareListing: (String) -> Unit,
    onViewLogs: (String) -> Unit,
    onCopyListingLink: (String) -> Unit
) {
    // Convert to admin UI model
    val adminListing = remember(housingListing) {
        housingListing.toAdminHousingListingUiModel()
    }

    AdminHouseDetailsScreenContent(
        housingListing = adminListing,
        onNavigateBack = onNavigateBack,
        onEditListing = onEditListing,
        onDuplicateListing = onDuplicateListing,
        onArchiveListing = onArchiveListing,
        onDeleteListing = onDeleteListing,
        onPauseListing = onPauseListing,
        onResumeListing = onResumeListing,
        onMarkAvailable = onMarkAvailable,
        onMarkRented = onMarkRented,
        onMarkSold = onMarkSold,
        onViewInquiries = onViewInquiries,
        onShareListing = onShareListing,
        onViewLogs = onViewLogs,
        onCopyListingLink = onCopyListingLink
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHouseDetailsScreenContent(
    housingListing: AdminHousingListingUiModel,
    onNavigateBack: () -> Unit,
    onEditListing: (String) -> Unit,
    onDuplicateListing: (String) -> Unit,
    onArchiveListing: (String) -> Unit,
    onDeleteListing: (String) -> Unit,
    onPauseListing: (String) -> Unit,
    onResumeListing: (String) -> Unit,
    onMarkAvailable: (String) -> Unit,
    onMarkRented: (String) -> Unit,
    onMarkSold: (String) -> Unit,
    onViewInquiries: (String) -> Unit,
    onShareListing: (String) -> Unit,
    onViewLogs: (String) -> Unit,
    onCopyListingLink: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val clipboardManager = LocalClipboardManager.current

    // State management
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showStatusChangeDialog by remember { mutableStateOf(false) }
    var pendingStatusChange by remember { mutableStateOf<ListingStatusDisplay?>(null) }
    var showCopyConfirmation by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(true) }
    var listingStatus by remember { mutableStateOf(housingListing.status.toDisplayStatus()) }

    // Animation for copy confirmation
    val copyConfirmationAlpha = animateFloatAsState(
        targetValue = if (showCopyConfirmation) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "copy_confirmation_alpha"
    )

    // Format price
    val priceValue = extractPriceValue(housingListing.price)
    val formattedPrice = NumberFormat.getInstance(Locale.US).format(priceValue)

    // Format dates
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val postedDateStr = dateFormat.format(housingListing.postedDate)
    val expiryDateStr = housingListing.expiryDate?.let { dateFormat.format(it) } ?: "No expiry"

    // Format numbers
    val viewsFormatted = formatNumber(housingListing.views)
    val inquiriesFormatted = formatNumber(housingListing.messages + housingListing.requests)

    // Responsive layout
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            AdminHouseDetailsTopBar(
                title = housingListing.title,
                onNavigateBack = onNavigateBack,
                onMenuClick = { showMenu = true }
            )
        },
        bottomBar = {
            AdminHouseDetailsBottomBar(
                listingStatus = listingStatus,
                onEditClick = { onEditListing(housingListing.id) },
                onStatusChangeClick = {
                    when (listingStatus) {
                        ListingStatusDisplay.AVAILABLE -> {
                            pendingStatusChange = ListingStatusDisplay.RENTED
                            showStatusChangeDialog = true
                        }
                        ListingStatusDisplay.PENDING -> {
                            pendingStatusChange = ListingStatusDisplay.AVAILABLE
                            showStatusChangeDialog = true
                        }
                        ListingStatusDisplay.RENTED, ListingStatusDisplay.SOLD -> {
                            pendingStatusChange = ListingStatusDisplay.AVAILABLE
                            showStatusChangeDialog = true
                        }
                        else -> {
                            pendingStatusChange = ListingStatusDisplay.AVAILABLE
                            showStatusChangeDialog = true
                        }
                    }
                },
                isWide = isWide
            )
        },
        containerColor = Color(0xFFFAFAFA) // Soft off-white background
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
                        .padding(horizontal = 24.dp)
                ) {
                    // Left Pane
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(end = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Property Overview Card
                        AdminPropertyOverviewCard(
                            housingListing = housingListing,
                            listingStatus = listingStatus,
                            formattedPrice = formattedPrice,
                            postedDateStr = postedDateStr,
                            expiryDateStr = expiryDateStr,
                            viewsFormatted = viewsFormatted,
                            inquiriesFormatted = inquiriesFormatted,
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Inquiries Overview Card
                        AdminInquiriesCard(
                            totalInquiries = housingListing.messages + housingListing.requests,
                            newInquiries = housingListing.newInquiries,
                            messages = housingListing.messages,
                            requests = housingListing.requests,
                            onViewAllClick = { onViewInquiries(housingListing.id) },
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
                    }

                    // Right Pane
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(start = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Property Description Card (Expandable)
                        AdminPropertyDescriptionCard(
                            description = housingListing.description,
                            facilities = housingListing.facilities,
                            bedrooms = housingListing.bedrooms,
                            bathrooms = housingListing.bathrooms,
                            squareMeters = housingListing.squareMeters,
                            ownerName = housingListing.ownerName,
                            ownerVerified = housingListing.ownerVerified,
                            rating = housingListing.rating,
                            isExpanded = isDescriptionExpanded,
                            onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded },
                            onCopyLink = {
                                onCopyListingLink(housingListing.id)
                                clipboardManager.setText(AnnotatedString("https://pivotaconnect.com/houses/${housingListing.id}"))
                                showCopyConfirmation = true
                            },
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Management Tools Section
                        AdminHouseManagementTools(
                            listingStatus = listingStatus,
                            onEditClick = { onEditListing(housingListing.id) },
                            onDuplicateClick = { onDuplicateListing(housingListing.id) },
                            onPauseClick = { showPauseDialog = true },
                            onResumeClick = { onResumeListing(housingListing.id) },
                            onMarkAvailableClick = {
                                pendingStatusChange = ListingStatusDisplay.AVAILABLE
                                showStatusChangeDialog = true
                            },
                            onMarkRentedClick = {
                                pendingStatusChange = ListingStatusDisplay.RENTED
                                showStatusChangeDialog = true
                            },
                            onMarkSoldClick = {
                                pendingStatusChange = ListingStatusDisplay.SOLD
                                showStatusChangeDialog = true
                            },
                            onDeleteClick = { showDeleteDialog = true },
                            onShareClick = { onShareListing(housingListing.id) },
                            onLogsClick = { onViewLogs(housingListing.id) },
                            colorScheme = colorScheme,
                            isWide = isWide
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Analytics & Insights
                        AdminPropertyAnalyticsCard(
                            views = housingListing.views,
                            messages = housingListing.messages,
                            requests = housingListing.requests,
                            averageResponseTime = housingListing.averageResponseTime,
                            viewsTrend = housingListing.viewsTrend,
                            messagesTrend = housingListing.messagesTrend,
                            requestsTrend = housingListing.requestsTrend,
                            onViewFullAnalytics = { /* Navigate to full analytics */ },
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
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

                    // Image Gallery
                    SimpleHousingImageGallery(
                        images = housingListing.getAllImages(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Property Overview Card
                    AdminPropertyOverviewCard(
                        housingListing = housingListing,
                        listingStatus = listingStatus,
                        formattedPrice = formattedPrice,
                        postedDateStr = postedDateStr,
                        expiryDateStr = expiryDateStr,
                        viewsFormatted = viewsFormatted,
                        inquiriesFormatted = inquiriesFormatted,
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Property Description Card (Expandable)
                    AdminPropertyDescriptionCard(
                        description = housingListing.description,
                        facilities = housingListing.facilities,
                        bedrooms = housingListing.bedrooms,
                        bathrooms = housingListing.bathrooms,
                        squareMeters = housingListing.squareMeters,
                        ownerName = housingListing.ownerName,
                        ownerVerified = housingListing.ownerVerified,
                        rating = housingListing.rating,
                        isExpanded = isDescriptionExpanded,
                        onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded },
                        onCopyLink = {
                            onCopyListingLink(housingListing.id)
                            clipboardManager.setText(AnnotatedString("https://pivotaconnect.com/houses/${housingListing.id}"))
                            showCopyConfirmation = true
                        },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Inquiries Overview Card
                    AdminInquiriesCard(
                        totalInquiries = housingListing.messages + housingListing.requests,
                        newInquiries = housingListing.newInquiries,
                        messages = housingListing.messages,
                        requests = housingListing.requests,
                        onViewAllClick = { onViewInquiries(housingListing.id) },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Management Tools Section
                    AdminHouseManagementTools(
                        listingStatus = listingStatus,
                        onEditClick = { onEditListing(housingListing.id) },
                        onDuplicateClick = { onDuplicateListing(housingListing.id) },
                        onPauseClick = { showPauseDialog = true },
                        onResumeClick = { onResumeListing(housingListing.id) },
                        onMarkAvailableClick = {
                            pendingStatusChange = ListingStatusDisplay.AVAILABLE
                            showStatusChangeDialog = true
                        },
                        onMarkRentedClick = {
                            pendingStatusChange = ListingStatusDisplay.RENTED
                            showStatusChangeDialog = true
                        },
                        onMarkSoldClick = {
                            pendingStatusChange = ListingStatusDisplay.SOLD
                            showStatusChangeDialog = true
                        },
                        onDeleteClick = { showDeleteDialog = true },
                        onShareClick = { onShareListing(housingListing.id) },
                        onLogsClick = { onViewLogs(housingListing.id) },
                        colorScheme = colorScheme,
                        isWide = isWide
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Analytics & Insights
                    AdminPropertyAnalyticsCard(
                        views = housingListing.views,
                        messages = housingListing.messages,
                        requests = housingListing.requests,
                        averageResponseTime = housingListing.averageResponseTime,
                        viewsTrend = housingListing.viewsTrend,
                        messagesTrend = housingListing.messagesTrend,
                        requestsTrend = housingListing.requestsTrend,
                        onViewFullAnalytics = { /* Navigate to full analytics */ },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
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

            // Auto-hide copy confirmation
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
                onEditListing(housingListing.id)
            },
            leadingIcon = {
                Icon(Icons.Outlined.Edit, contentDescription = null, tint = colorScheme.primary)
            }
        )
        DropdownMenuItem(
            text = { Text("Duplicate", color = colorScheme.primary) },
            onClick = {
                showMenu = false
                onDuplicateListing(housingListing.id)
            },
            leadingIcon = {
                Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = colorScheme.primary)
            }
        )
        DropdownMenuItem(
            text = { Text("Archive", color = colorScheme.onSurfaceVariant) },
            onClick = {
                showMenu = false
                onArchiveListing(housingListing.id)
            },
            leadingIcon = {
                Icon(Icons.Outlined.Archive, contentDescription = null, tint = colorScheme.onSurfaceVariant)
            }
        )
        Divider()
        DropdownMenuItem(
            text = { Text("Delete", color = Color(0xFFBA2D2D)) },
            onClick = {
                showMenu = false
                showDeleteDialog = true
            },
            leadingIcon = {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFBA2D2D))
            }
        )
    }

    // Confirmation Dialogs
    if (showDeleteDialog) {
        AdminConfirmationDialogHousingDetails(
            title = "Delete Property Listing?",
            message = "This action cannot be undone. The property will be permanently removed from the platform.",
            icon = Icons.Filled.Delete,
            iconColor = Color(0xFFBA2D2D),
            confirmText = "Delete",
            onConfirm = {
                onDeleteListing(housingListing.id)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showPauseDialog) {
        AdminConfirmationDialogHousingDetails(
            title = if (listingStatus == ListingStatusDisplay.INACTIVE) "Resume Listing?" else "Pause Listing?",
            message = if (listingStatus == ListingStatusDisplay.INACTIVE) {
                "The property will become visible to potential tenants/buyers again."
            } else {
                "The property will no longer be visible to potential tenants/buyers. You can resume anytime."
            },
            icon = if (listingStatus == ListingStatusDisplay.INACTIVE) Icons.Filled.PlayArrow else Icons.Filled.Pause,
            iconColor = Color(0xFFC95D3A), // Warm Terracotta
            confirmText = if (listingStatus == ListingStatusDisplay.INACTIVE) "Resume" else "Pause",
            onConfirm = {
                if (listingStatus == ListingStatusDisplay.INACTIVE) {
                    onResumeListing(housingListing.id)
                    listingStatus = ListingStatusDisplay.AVAILABLE
                } else {
                    onPauseListing(housingListing.id)
                    listingStatus = ListingStatusDisplay.INACTIVE
                }
                showPauseDialog = false
            },
            onDismiss = { showPauseDialog = false }
        )
    }

    if (showStatusChangeDialog && pendingStatusChange != null) {
        val dialogConfig = when (pendingStatusChange) {
            ListingStatusDisplay.AVAILABLE -> DialogConfig(
                title = "Mark as Available?",
                message = "The property will be listed as available for rent/sale.",
                icon = Icons.Filled.CheckCircle,
                color = Color(0xFF10B981)
            )
            ListingStatusDisplay.RENTED -> DialogConfig(
                title = "Mark as Rented?",
                message = "The property will be marked as rented and removed from active listings.",
                icon = Icons.Filled.HomeWork,
                color = Color(0xFFBA2D2D)
            )
            ListingStatusDisplay.SOLD -> DialogConfig(
                title = "Mark as Sold?",
                message = "The property will be marked as sold and removed from active listings.",
                icon = Icons.Filled.Sell,
                color = Color(0xFFBA2D2D)
            )
            else -> DialogConfig(
                title = "Change Status?",
                message = "Are you sure you want to change the property status?",
                icon = Icons.Filled.Info,
                color = colorScheme.primary
            )
        }

        AdminConfirmationDialogHousingDetails(
            title = dialogConfig.title,
            message = dialogConfig.message,
            icon = dialogConfig.icon,
            iconColor = dialogConfig.color,
            confirmText = "Confirm",
            onConfirm = {
                when (pendingStatusChange) {
                    ListingStatusDisplay.AVAILABLE -> {
                        onMarkAvailable(housingListing.id)
                        listingStatus = ListingStatusDisplay.AVAILABLE
                    }
                    ListingStatusDisplay.RENTED -> {
                        onMarkRented(housingListing.id)
                        listingStatus = ListingStatusDisplay.RENTED
                    }
                    ListingStatusDisplay.SOLD -> {
                        onMarkSold(housingListing.id)
                        listingStatus = ListingStatusDisplay.SOLD
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

/* ───────── TOP BAR ───────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHouseDetailsTopBar(
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = colorScheme.onSurfaceVariant
                )
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

/* ───────── BOTTOM BAR ───────── */

@Composable
fun AdminHouseDetailsBottomBar(
    listingStatus: ListingStatusDisplay,
    onEditClick: () -> Unit,
    onStatusChangeClick: () -> Unit,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        color = colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Change button (contextual)
            val statusButtonText = when (listingStatus) {
                ListingStatusDisplay.AVAILABLE -> "Mark as Rented"
                ListingStatusDisplay.PENDING -> "Approve Listing"
                ListingStatusDisplay.RENTED -> "List Again"
                ListingStatusDisplay.SOLD -> "List Again"
                ListingStatusDisplay.INACTIVE -> "Activate Listing"
            }

            val statusButtonColor = when (listingStatus) {
                ListingStatusDisplay.AVAILABLE -> Color(0xFFBA2D2D)
                ListingStatusDisplay.PENDING -> Color(0xFF10B981)
                ListingStatusDisplay.RENTED, ListingStatusDisplay.SOLD -> Color(0xFF10B981)
                ListingStatusDisplay.INACTIVE -> Color(0xFF10B981)
            }

            OutlinedButton(
                onClick = onStatusChangeClick,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, statusButtonColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = statusButtonColor
                )
            ) {
                Icon(
                    when (listingStatus) {
                        ListingStatusDisplay.AVAILABLE -> Icons.Outlined.HomeWork
                        ListingStatusDisplay.PENDING -> Icons.Outlined.CheckCircle
                        ListingStatusDisplay.RENTED, ListingStatusDisplay.SOLD -> Icons.Outlined.PlayArrow
                        ListingStatusDisplay.INACTIVE -> Icons.Outlined.PlayArrow
                    },
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    statusButtonText,
                    fontSize = if (isWide) 16.sp else 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Edit button (primary)
            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Edit Listing",
                    fontSize = if (isWide) 16.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/* ───────── PROPERTY OVERVIEW CARD ───────── */

/* ───────── PROPERTY OVERVIEW CARD ───────── */

@Composable
fun AdminPropertyOverviewCard(
    housingListing: AdminHousingListingUiModel,
    listingStatus: ListingStatusDisplay,
    formattedPrice: String,
    postedDateStr: String,
    expiryDateStr: String,
    viewsFormatted: String,
    inquiriesFormatted: String,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Left column - Property Image (now curved with RoundedCornerShape)
            Surface(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Top)
                    .clip(RoundedCornerShape(12.dp)), // Added clip for curved corners
                color = colorScheme.surfaceVariant
            ) {
                if (housingListing.getAllImages().isNotEmpty()) {
                    AsyncImage(
                        model = housingListing.getAllImages().first(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Home,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right column - Property details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = housingListing.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary // African Sapphire
                )

                Text(
                    text = "${housingListing.bedrooms} bed • ${housingListing.bathrooms} bath • ${housingListing.squareMeters} m²",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Text(
                    text = buildAnnotatedString {
                        append("KES $formattedPrice")
                        if (!housingListing.isForSale) {
                            append("/month")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.secondary // Warm Terracotta
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Property Type and Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = housingListing.propertyType,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = housingListing.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status and Metadata Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    Surface(
                        shape = CircleShape,
                        color = listingStatus.color().copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                listingStatus.icon(),
                                contentDescription = null,
                                tint = listingStatus.color(),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = listingStatus.displayName(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = listingStatus.color()
                            )
                        }
                    }

                    // View count
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = viewsFormatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dates Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Posted: $postedDateStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Expires: $expiryDateStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Verification Badge
                if (housingListing.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Verified Owner",
                            color = colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ───────── PROPERTY DESCRIPTION CARD ───────── */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminPropertyDescriptionCard(
    description: String,
    facilities: List<String>,
    bedrooms: Int,
    bathrooms: Int,
    squareMeters: Int,
    ownerName: String,
    ownerVerified: Boolean,
    rating: Double,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCopyLink: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Property Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    // Key Specs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SpecItem(
                            icon = Icons.Outlined.Bed,
                            value = bedrooms.toString(),
                            label = "Bedrooms",
                            colorScheme = colorScheme
                        )
                        SpecItem(
                            icon = Icons.Outlined.Shower,
                            value = bathrooms.toString(),
                            label = "Bathrooms",
                            colorScheme = colorScheme
                        )
                        SpecItem(
                            icon = Icons.Outlined.SquareFoot,
                            value = "$squareMeters",
                            label = "m²",
                            colorScheme = colorScheme
                        )
                    }

                    // Description
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurface,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Facilities
                    if (facilities.isNotEmpty()) {
                        Text(
                            text = "Facilities & Amenities",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            facilities.forEach { facility ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val icon = when(facility.lowercase()) {
                                            "water" -> Icons.Outlined.WaterDrop
                                            "electricity" -> Icons.Outlined.Bolt
                                            "parking" -> Icons.Outlined.LocalParking
                                            "wi-fi" -> Icons.Outlined.Wifi
                                            "security" -> Icons.Outlined.Security
                                            "gym" -> Icons.Outlined.FitnessCenter
                                            else -> Icons.Outlined.CheckCircle
                                        }
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = facility,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Owner Info
                    Text(
                        text = "Owner",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = ownerName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = if (ownerVerified) "Verified Owner" else "Unverified",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Rating
                        if (rating > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = String.format("%.1f", rating),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Copy Link Button
                    OutlinedButton(
                        onClick = onCopyLink,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Link,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Copy Listing Link",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/* ───────── INQUIRIES CARD ───────── */

@Composable
fun AdminInquiriesCard(
    totalInquiries: Int,
    newInquiries: Int,
    messages: Int,
    requests: Int,
    onViewAllClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inquiries",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = totalInquiries.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "View All →",
                    color = colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InquiriesStat(
                    value = messages.toString(),
                    label = "Messages",
                    colorScheme = colorScheme
                )

                InquiriesStat(
                    value = requests.toString(),
                    label = "Requests",
                    colorScheme = colorScheme
                )

                InquiriesStat(
                    value = (totalInquiries - newInquiries).toString(),
                    label = "Responded",
                    colorScheme = colorScheme
                )
            }

            // New badge for new inquiries
            if (newInquiries > 0) {
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colorScheme.tertiary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.FiberNew,
                            contentDescription = null,
                            tint = colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$newInquiries new ${if (newInquiries == 1) "inquiry" else "inquiries"}",
                            color = colorScheme.tertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InquiriesStat(
    value: String,
    label: String,
    colorScheme: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

/* ───────── MANAGEMENT TOOLS ───────── */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminHouseManagementTools(
    listingStatus: ListingStatusDisplay,
    onEditClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onMarkAvailableClick: () -> Unit,
    onMarkRentedClick: () -> Unit,
    onMarkSoldClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onLogsClick: () -> Unit,
    colorScheme: ColorScheme,
    isWide: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Action Grid - 2 columns on mobile, 4 on tablet
            val columns = if (isWide) 4 else 2

            val actions = buildList {
                add(ManagementActionHousingDetails("Edit", Icons.Outlined.Edit, colorScheme.primary, onEditClick))

                // Status-specific actions
                when (listingStatus) {
                    ListingStatusDisplay.INACTIVE -> {
                        add(ManagementActionHousingDetails("Resume", Icons.Filled.PlayArrow, colorScheme.secondary, onResumeClick))
                    }
                    ListingStatusDisplay.AVAILABLE -> {
                        add(ManagementActionHousingDetails("Pause", Icons.Filled.Pause, colorScheme.secondary, onPauseClick))
                        add(ManagementActionHousingDetails("Mark Rented", Icons.Outlined.HomeWork, Color(0xFFBA2D2D), onMarkRentedClick))
                        add(ManagementActionHousingDetails("Mark Sold", Icons.Outlined.Sell, Color(0xFFBA2D2D), onMarkSoldClick))
                    }
                    ListingStatusDisplay.PENDING -> {
                        add(ManagementActionHousingDetails("Approve", Icons.Outlined.CheckCircle, Color(0xFF10B981), onMarkAvailableClick))
                        add(ManagementActionHousingDetails("Reject", Icons.Outlined.Cancel, Color(0xFFBA2D2D), onMarkAvailableClick)) // Reject would hide it
                    }
                    ListingStatusDisplay.RENTED, ListingStatusDisplay.SOLD -> {
                        add(ManagementActionHousingDetails("List Again", Icons.Outlined.PlayArrow, Color(0xFF10B981), onMarkAvailableClick))
                    }
                }

                add(ManagementActionHousingDetails("Duplicate", Icons.Outlined.ContentCopy, colorScheme.primary, onDuplicateClick))
                add(ManagementActionHousingDetails("Share", Icons.Outlined.Share, colorScheme.primary, onShareClick))
                add(ManagementActionHousingDetails("Logs", Icons.Outlined.History, colorScheme.onSurfaceVariant, onLogsClick))
                add(ManagementActionHousingDetails("Delete", Icons.Outlined.Delete, Color(0xFFBA2D2D), onDeleteClick))
            }

            // Chunk actions into rows
            actions.chunked(columns).forEach { rowActions ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowActions.forEach { action ->
                        ManagementActionCardHousingDetails(
                            action = action,
                            modifier = Modifier.weight(1f),
                            colorScheme = colorScheme
                        )
                    }

                    // Fill empty slots if needed
                    repeat(columns - rowActions.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ManagementActionCardHousingDetails(
    action: ManagementActionHousingDetails,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme
) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable { action.onClick() },
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surface,
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = action.label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = action.color,
                textAlign = TextAlign.Center
            )
        }
    }
}

/* ───────── ANALYTICS CARD ───────── */

@Composable
fun AdminPropertyAnalyticsCard(
    views: Int,
    messages: Int,
    requests: Int,
    averageResponseTime: Double,
    viewsTrend: String,
    messagesTrend: String,
    requestsTrend: String,
    onViewFullAnalytics: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with AI badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Performance Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colorScheme.tertiary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = colorScheme.tertiary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI SmartMatch™",
                            color = colorScheme.tertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AnalyticsStatCardHousingDetails(
                        icon = Icons.Outlined.Visibility,
                        value = formatNumber(views),
                        label = "Views",
                        trend = viewsTrend,
                        colorScheme = colorScheme
                    )
                }
                item {
                    AnalyticsStatCardHousingDetails(
                        icon = Icons.Outlined.ChatBubbleOutline,
                        value = formatNumber(messages),
                        label = "Messages",
                        trend = messagesTrend,
                        colorScheme = colorScheme
                    )
                }
                item {
                    AnalyticsStatCardHousingDetails(
                        icon = Icons.Outlined.PendingActions,
                        value = formatNumber(requests),
                        label = "Requests",
                        trend = requestsTrend,
                        colorScheme = colorScheme
                    )
                }
                item {
                    AnalyticsStatCardHousingDetails(
                        icon = Icons.Outlined.Timer,
                        value = String.format("%.1f hrs", averageResponseTime),
                        label = "Avg. Response",
                        trend = null,
                        colorScheme = colorScheme
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // View Full Analytics Button
            OutlinedButton(
                onClick = onViewFullAnalytics,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colorScheme.outlineVariant)
            ) {
                Icon(
                    Icons.Outlined.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Full Analytics")
            }
        }
    }
}

@Composable
fun AnalyticsStatCardHousingDetails(
    icon: ImageVector,
    value: String,
    label: String,
    trend: String?,
    colorScheme: ColorScheme
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                if (trend != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = colorScheme.tertiary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = trend,
                            color = colorScheme.tertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ───────── SPEC ITEM ───────── */

@Composable
fun SpecItem(
    icon: ImageVector,
    value: String,
    label: String,
    colorScheme: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = colorScheme.onSurfaceVariant
        )
    }
}

/* ───────── CONFIRMATION DIALOG ───────── */

@Composable
fun AdminConfirmationDialogHousingDetails(
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
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Surface(
                    shape = CircleShape,
                    color = iconColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant)
                    ) {
                        Text("Cancel", color = colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = iconColor
                        )
                    ) {
                        Text(confirmText, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/* ───────── UTILITY FUNCTIONS ───────── */

@SuppressLint("DefaultLocale")
fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

fun extractPriceValue(priceString: String): Int {
    return try {
        val cleaned = priceString
            .replace("KES", "")
            .replace("KSh", "")
            .replace("Ksh", "")
            .replace(",", "")
            .replace(" ", "")
            .trim()

        when {
            cleaned.endsWith("M", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDoubleOrNull() ?: 0.0
                (number * 1_000_000).toInt()
            }
            cleaned.endsWith("K", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDoubleOrNull() ?: 0.0
                (number * 1_000).toInt()
            }
            else -> cleaned.toIntOrNull() ?: 0
        }
    } catch (e: Exception) {
        0
    }
}

/* ───────── PREVIEW ───────── */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminHouseDetailsPreview() {
    PivotaConnectTheme {
        AdminHouseDetailsScreenContent(
            housingListing = createSampleAdminHousingListing(),
            onNavigateBack = {},
            onEditListing = {},
            onDuplicateListing = {},
            onArchiveListing = {},
            onDeleteListing = {},
            onPauseListing = {},
            onResumeListing = {},
            onMarkAvailable = {},
            onMarkRented = {},
            onMarkSold = {},
            onViewInquiries = {},
            onShareListing = {},
            onViewLogs = {},
            onCopyListingLink = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 840)
@Composable
fun AdminHouseDetailsTabletPreview() {
    PivotaConnectTheme {
        AdminHouseDetailsScreenContent(
            housingListing = createSampleAdminHousingListing(),
            onNavigateBack = {},
            onEditListing = {},
            onDuplicateListing = {},
            onArchiveListing = {},
            onDeleteListing = {},
            onPauseListing = {},
            onResumeListing = {},
            onMarkAvailable = {},
            onMarkRented = {},
            onMarkSold = {},
            onViewInquiries = {},
            onShareListing = {},
            onViewLogs = {},
            onCopyListingLink = {}
        )
    }
}

fun createSampleAdminHousingListing(): AdminHousingListingUiModel {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -7)
    val postedDate = calendar.time

    calendar.add(Calendar.DAY_OF_YEAR, 23)
    val expiryDate = calendar.time

    return AdminHousingListingUiModel(
        id = "house-123",
        title = "Modern 2-Bedroom Apartment in Westlands",
        price = "KES 45,000",
        location = "Westlands, Nairobi",
        exactLocation = "Woodvale Grove, Westlands",
        propertyType = "Apartment",
        status = ListingStatus.AVAILABLE,
        postedDate = postedDate,
        expiryDate = expiryDate,
        views = 1832,
        messages = 28,
        requests = 12,
        newInquiries = 8,
        description = "Spacious and well-lit apartment located in the heart of Westlands. Features include modern finishes, ample parking, and 24/7 security. Close to shopping malls, restaurants, and public transport.",
        bedrooms = 2,
        bathrooms = 2,
        squareMeters = 85,
        facilities = listOf(
            "Water", "Electricity", "Parking", "Wi-Fi", "Security", "Gym", "Swimming Pool"
        ),
        imageRes = listOf(
            R.drawable.property_placeholder1,
            R.drawable.property_placeholder2,
            R.drawable.property_placeholder3,
            R.drawable.property_placeholder4
        ),
        isVerified = true,
        ownerName = "Sarah Omondi",
        ownerVerified = true,
        rating = 4.5,
        isForSale = false,
        averageResponseTime = 2.5,
        viewsTrend = "+18%",
        messagesTrend = "+12%",
        requestsTrend = "+8%"
    )
}