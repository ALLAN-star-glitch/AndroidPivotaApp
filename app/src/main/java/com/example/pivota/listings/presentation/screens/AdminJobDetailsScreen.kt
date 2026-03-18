package com.example.pivota.admin.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Help
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import com.example.pivota.core.presentation.composables.StatusBadge
import com.example.pivota.core.presentation.composables.StatusBadgeSize
import com.example.pivota.core.presentation.composables.StatusBadgeStyle
import com.example.pivota.ui.theme.PivotaConnectTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Admin Job Listing UI Model
 */
data class AdminJobListingUiModel(
    val id: String,
    val title: String,
    val companyName: String,
    val companyLogoUrl: String? = null,
    val location: String,
    val exactLocation: String? = null,
    val jobType: String, // "Full-time", "Part-time", "Contract", etc.
    val status: JobStatus,
    val postedDate: Date,
    val expiryDate: Date? = null,
    val views: Int = 0,
    val applications: Int = 0,
    val newApplications: Int = 0,
    val reviewedApplications: Int = 0,
    val description: String,
    val requirements: List<String> = emptyList(),
    val skills: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val isVerified: Boolean = true,
    val employerName: String,
    val employerVerified: Boolean = true,
    val averageTimeToApply: Double = 0.0, // in days
    val applicationFunnel: ApplicationFunnel = ApplicationFunnel()
)

enum class JobStatus {
    ACTIVE,
    PAUSED,
    CLOSED,
    PENDING_REVIEW;

    fun displayName(): String = when (this) {
        ACTIVE -> "Active"
        PAUSED -> "Paused"
        CLOSED -> "Closed"
        PENDING_REVIEW -> "Pending Review"
    }

    @Composable
    fun color(): Color = when (this) {
        ACTIVE -> Color(0xFF10B981) // Success Green (keeping as is, not in theme)
        PAUSED -> MaterialTheme.colorScheme.onSurfaceVariant // Warm Gray equivalent
        CLOSED -> MaterialTheme.colorScheme.error // Sunset Red
        PENDING_REVIEW -> MaterialTheme.colorScheme.tertiary // Baobab Gold
    }

    fun icon(): ImageVector = when (this) {
        ACTIVE -> Icons.Filled.CheckCircle
        PAUSED -> Icons.Filled.Pause
        CLOSED -> Icons.Filled.Cancel
        PENDING_REVIEW -> Icons.Filled.HourglassEmpty
    }

    fun outlinedIcon(): ImageVector = when (this) {
        ACTIVE -> Icons.Outlined.CheckCircle
        PAUSED -> Icons.Outlined.Pause
        CLOSED -> Icons.Outlined.Cancel
        PENDING_REVIEW -> Icons.Outlined.HourglassEmpty
    }
}


data class ApplicationFunnel(
    val viewed: Int = 100,
    val applied: Int = 24,
    val reviewed: Int = 12
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminJobDetailsScreen(
    jobListing: AdminJobListingUiModel,
    onNavigateBack: () -> Unit,
    onEditJob: (String) -> Unit,
    onDuplicateJob: (String) -> Unit,
    onArchiveJob: (String) -> Unit,
    onDeleteJob: (String) -> Unit,
    onPauseJob: (String) -> Unit,
    onResumeJob: (String) -> Unit,
    onCloseJob: (String) -> Unit,
    onViewApplicants: (String) -> Unit,
    onShareJob: (String) -> Unit,
    onViewLogs: (String) -> Unit,
    onCopyJobLink: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val clipboardManager = LocalClipboardManager.current

    // State management
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showCloseDialog by remember { mutableStateOf(false) }
    var showCopyConfirmation by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(true) }
    var jobStatus by remember { mutableStateOf(jobListing.status) }

    // Animation for copy confirmation
    val copyConfirmationAlpha = animateFloatAsState(
        targetValue = if (showCopyConfirmation) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "copy_confirmation_alpha"
    )

    // Format dates
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val postedDateStr = dateFormat.format(jobListing.postedDate)
    val expiryDateStr = jobListing.expiryDate?.let { dateFormat.format(it) } ?: "No expiry"

    // Format numbers
    val viewsFormatted = formatNumberHousingDetails(jobListing.views)
    val applicationsFormatted = formatNumber(jobListing.applications)

    // Responsive layout
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            AdminJobDetailsTopBar(
                title = jobListing.title,
                onNavigateBack = onNavigateBack,
                onMenuClick = { showMenu = true }
            )
        },
        bottomBar = {
            AdminJobDetailsBottomBar(
                jobStatus = jobStatus,
                onEditClick = { onEditJob(jobListing.id) },
                onCloseClick = { showCloseDialog = true },
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

                        // Job Overview Card
                        AdminJobOverviewCard(
                            jobListing = jobListing,
                            jobStatus = jobStatus,
                            postedDateStr = postedDateStr,
                            expiryDateStr = expiryDateStr,
                            viewsFormatted = viewsFormatted,
                            applicationsFormatted = applicationsFormatted,
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Applicants Overview Card
                        AdminApplicantsCard(
                            totalApplicants = jobListing.applications,
                            newApplicants = jobListing.newApplications,
                            reviewedApplicants = jobListing.reviewedApplications,
                            onViewAllClick = { onViewApplicants(jobListing.id) },
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

                        // Job Description Card (Expandable)
                        AdminJobDescriptionCard(
                            description = jobListing.description,
                            requirements = jobListing.requirements,
                            skills = jobListing.skills,
                            benefits = jobListing.benefits,
                            isExpanded = isDescriptionExpanded,
                            onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded },
                            onCopyLink = {
                                onCopyJobLink(jobListing.id)
                                clipboardManager.setText(AnnotatedString("https://pivotaconnect.com/jobs/${jobListing.id}"))
                                showCopyConfirmation = true
                            },
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Management Tools Section
                        AdminManagementTools(
                            jobStatus = jobStatus,
                            onEditClick = { onEditJob(jobListing.id) },
                            onDuplicateClick = { onDuplicateJob(jobListing.id) },
                            onPauseClick = { showPauseDialog = true },
                            onResumeClick = { onResumeJob(jobListing.id) },
                            onCloseClick = { showCloseDialog = true },
                            onDeleteClick = { showDeleteDialog = true },
                            onShareClick = { onShareJob(jobListing.id) },
                            onLogsClick = { onViewLogs(jobListing.id) },
                            colorScheme = colorScheme,
                            isWide = isWide
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Analytics & Insights
                        AdminAnalyticsCard(
                            views = jobListing.views,
                            applications = jobListing.applications,
                            averageTimeToApply = jobListing.averageTimeToApply,
                            funnel = jobListing.applicationFunnel,
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

                    // Job Overview Card
                    AdminJobOverviewCard(
                        jobListing = jobListing,
                        jobStatus = jobStatus,
                        postedDateStr = postedDateStr,
                        expiryDateStr = expiryDateStr,
                        viewsFormatted = viewsFormatted,
                        applicationsFormatted = applicationsFormatted,
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Job Description Card (Expandable)
                    AdminJobDescriptionCard(
                        description = jobListing.description,
                        requirements = jobListing.requirements,
                        skills = jobListing.skills,
                        benefits = jobListing.benefits,
                        isExpanded = isDescriptionExpanded,
                        onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded },
                        onCopyLink = {
                            onCopyJobLink(jobListing.id)
                            clipboardManager.setText(AnnotatedString("https://pivotaconnect.com/jobs/${jobListing.id}"))
                            showCopyConfirmation = true
                        },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Applicants Overview Card
                    AdminApplicantsCard(
                        totalApplicants = jobListing.applications,
                        newApplicants = jobListing.newApplications,
                        reviewedApplicants = jobListing.reviewedApplications,
                        onViewAllClick = { onViewApplicants(jobListing.id) },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Management Tools Section
                    AdminManagementTools(
                        jobStatus = jobStatus,
                        onEditClick = { onEditJob(jobListing.id) },
                        onDuplicateClick = { onDuplicateJob(jobListing.id) },
                        onPauseClick = { showPauseDialog = true },
                        onResumeClick = { onResumeJob(jobListing.id) },
                        onCloseClick = { showCloseDialog = true },
                        onDeleteClick = { showDeleteDialog = true },
                        onShareClick = { onShareJob(jobListing.id) },
                        onLogsClick = { onViewLogs(jobListing.id) },
                        colorScheme = colorScheme,
                        isWide = isWide
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Analytics & Insights
                    AdminAnalyticsCard(
                        views = jobListing.views,
                        applications = jobListing.applications,
                        averageTimeToApply = jobListing.averageTimeToApply,
                        funnel = jobListing.applicationFunnel,
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
                onEditJob(jobListing.id)
            },
            leadingIcon = {
                Icon(Icons.Outlined.Edit, contentDescription = null, tint = colorScheme.primary)
            }
        )
        DropdownMenuItem(
            text = { Text("Duplicate", color = colorScheme.primary) },
            onClick = {
                showMenu = false
                onDuplicateJob(jobListing.id)
            },
            leadingIcon = {
                Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = colorScheme.primary)
            }
        )
        DropdownMenuItem(
            text = { Text("Archive", color = colorScheme.onSurfaceVariant) },
            onClick = {
                showMenu = false
                onArchiveJob(jobListing.id)
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
        AdminConfirmationDialog(
            title = "Delete Job Posting?",
            message = "This action cannot be undone. The job will be permanently removed from the platform.",
            icon = Icons.Filled.Delete,
            iconColor = Color(0xFFBA2D2D),
            confirmText = "Delete",
            onConfirm = {
                onDeleteJob(jobListing.id)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showPauseDialog) {
        AdminConfirmationDialog(
            title = if (jobStatus == JobStatus.PAUSED) "Resume Job Posting?" else "Pause Job Posting?",
            message = if (jobStatus == JobStatus.PAUSED) {
                "The job will become visible to applicants again."
            } else {
                "The job will no longer be visible to applicants. You can resume anytime."
            },
            icon = if (jobStatus == JobStatus.PAUSED) Icons.Filled.PlayArrow else Icons.Filled.Pause,
            iconColor = Color(0xFFC95D3A), // Warm Terracotta
            confirmText = if (jobStatus == JobStatus.PAUSED) "Resume" else "Pause",
            onConfirm = {
                if (jobStatus == JobStatus.PAUSED) {
                    onResumeJob(jobListing.id)
                    jobStatus = JobStatus.ACTIVE
                } else {
                    onPauseJob(jobListing.id)
                    jobStatus = JobStatus.PAUSED
                }
                showPauseDialog = false
            },
            onDismiss = { showPauseDialog = false }
        )
    }

    if (showCloseDialog) {
        AdminConfirmationDialog(
            title = "Close Job Posting?",
            message = "The job will be marked as closed and removed from active listings.",
            icon = Icons.Filled.Lock,
            iconColor = Color(0xFFBA2D2D),
            confirmText = "Close",
            onConfirm = {
                onCloseJob(jobListing.id)
                jobStatus = JobStatus.CLOSED
                showCloseDialog = false
            },
            onDismiss = { showCloseDialog = false }
        )
    }
}

/* ───────── TOP BAR ───────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminJobDetailsTopBar(
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
fun AdminJobDetailsBottomBar(
    jobStatus: JobStatus,
    onEditClick: () -> Unit,
    onCloseClick: () -> Unit,
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
            // Close Job button (secondary)
            if (jobStatus != JobStatus.CLOSED) {
                OutlinedButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Close Job",
                        fontSize = if (isWide) 16.sp else 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Edit Job button (primary)
            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .weight(if (jobStatus == JobStatus.CLOSED) 1f else 1f)
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
                    "Edit Job",
                    fontSize = if (isWide) 16.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/* ───────── JOB OVERVIEW CARD ───────── */

@Composable
fun AdminJobOverviewCard(
    jobListing: AdminJobListingUiModel,
    jobStatus: JobStatus,
    postedDateStr: String,
    expiryDateStr: String,
    viewsFormatted: String,
    applicationsFormatted: String,
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
            // Left column - Company Logo
            Surface(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Top),
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.surfaceVariant
            ) {
                if (jobListing.companyLogoUrl != null) {
                    AsyncImage(
                        model = jobListing.companyLogoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Business,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right column - Job details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title and Company
                Text(
                    text = jobListing.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary // African Sapphire
                )

                Text(
                    text = jobListing.companyName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Job Type and Location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = jobListing.jobType,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.secondary, // Warm Terracotta
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = jobListing.location,
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
                    StatusBadge(
                        status = jobStatus,
                        style = StatusBadgeStyle.PILL,
                        size = StatusBadgeSize.MEDIUM,
                        showIcon = true,
                        showLabel = true,
                        animated = jobStatus == JobStatus.PENDING_REVIEW
                    )

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
                if (jobListing.isVerified) {
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
                            text = "Verified Employer",
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

/* ───────── JOB DESCRIPTION CARD ───────── */

@Composable
fun AdminJobDescriptionCard(
    description: String,
    requirements: List<String>,
    skills: List<String>,
    benefits: List<String>,
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
                    text = "Job Details",
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
                    // Description
                    Text(
                        text = "About the Job",
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

                    // Requirements
                    if (requirements.isNotEmpty()) {
                        Text(
                            text = "Requirements",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        requirements.forEach { requirement ->
                            AdminBulletPoint(text = requirement, colorScheme = colorScheme)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Skills
                    if (skills.isNotEmpty()) {
                        Text(
                            text = "Skills Needed",
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
                            skills.forEach { skill ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = skill,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Benefits
                    if (benefits.isNotEmpty()) {
                        Text(
                            text = "Benefits",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        benefits.forEach { benefit ->
                            AdminBulletPoint(
                                text = benefit,
                                icon = Icons.Outlined.CheckCircle,
                                colorScheme = colorScheme
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                            "Copy Job Link",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBulletPoint(
    text: String,
    icon: ImageVector = Icons.Outlined.Circle,
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colorScheme.secondary, // Warm Terracotta
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface
        )
    }
}

/* ───────── APPLICANTS CARD ───────── */

@Composable
fun AdminApplicantsCard(
    totalApplicants: Int,
    newApplicants: Int,
    reviewedApplicants: Int,
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
                        text = "Applicants",
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
                            text = totalApplicants.toString(),
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
                ApplicantStat(
                    value = totalApplicants.toString(),
                    label = "Total",
                    colorScheme = colorScheme
                )

                ApplicantStat(
                    value = newApplicants.toString(),
                    label = "New",
                    valueColor = colorScheme.tertiary, // Baobab Gold
                    colorScheme = colorScheme
                )

                ApplicantStat(
                    value = reviewedApplicants.toString(),
                    label = "Reviewed",
                    colorScheme = colorScheme
                )

                ApplicantStat(
                    value = (totalApplicants - reviewedApplicants).toString(),
                    label = "Pending",
                    colorScheme = colorScheme
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            val reviewProgress = if (totalApplicants > 0) {
                reviewedApplicants.toFloat() / totalApplicants.toFloat()
            } else 0f

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Review Progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${(reviewProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(reviewProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colorScheme.primary)
                    )
                }

                Text(
                    text = "$reviewedApplicants/$totalApplicants reviewed",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // New badge for new applicants
            if (newApplicants > 0) {
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
                            text = "$newApplicants new ${if (newApplicants == 1) "applicant" else "applicants"}",
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
fun ApplicantStat(
    value: String,
    label: String,
    valueColor: Color? = null,
    colorScheme: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor ?: colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

/* ───────── MANAGEMENT TOOLS ───────── */

@Composable
fun AdminManagementTools(
    jobStatus: JobStatus,
    onEditClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onCloseClick: () -> Unit,
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
                add(ManagementAction("Edit", Icons.Outlined.Edit, colorScheme.primary, onEditClick))
                if (jobStatus == JobStatus.PAUSED) {
                    add(ManagementAction("Resume", Icons.Filled.PlayArrow, colorScheme.secondary, onResumeClick))
                } else if (jobStatus == JobStatus.ACTIVE) {
                    add(ManagementAction("Pause", Icons.Filled.Pause, colorScheme.secondary, onPauseClick))
                }
                add(ManagementAction("Duplicate", Icons.Outlined.ContentCopy, colorScheme.primary, onDuplicateClick))
                add(ManagementAction("Share", Icons.Outlined.Share, colorScheme.primary, onShareClick))
                add(ManagementAction("Logs", Icons.Outlined.History, colorScheme.onSurfaceVariant, onLogsClick))
                add(ManagementAction("Close", Icons.Outlined.Lock, Color(0xFFBA2D2D), onCloseClick))
                add(ManagementAction("Delete", Icons.Outlined.Delete, Color(0xFFBA2D2D), onDeleteClick))
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
                        ManagementActionCard(
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

data class ManagementAction(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ManagementActionCard(
    action: ManagementAction,
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
fun AdminAnalyticsCard(
    views: Int,
    applications: Int,
    averageTimeToApply: Double,
    funnel: ApplicationFunnel,
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
                    AnalyticsStatCard(
                        icon = Icons.Outlined.Visibility,
                        value = formatNumber(views),
                        label = "Views",
                        trend = "+18%",
                        colorScheme = colorScheme
                    )
                }
                item {
                    AnalyticsStatCard(
                        icon = Icons.Outlined.Description,
                        value = formatNumber(applications),
                        label = "Applications",
                        trend = "+5%",
                        colorScheme = colorScheme
                    )
                }
                item {
                    AnalyticsStatCard(
                        icon = Icons.Outlined.Timer,
                        value = String.format("%.1f days", averageTimeToApply),
                        label = "Avg. Time",
                        trend = null,
                        colorScheme = colorScheme
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Application Funnel
            Text(
                text = "Application Funnel",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FunnelRow(
                label = "Viewed",
                value = funnel.viewed,
                percentage = 100,
                color = colorScheme.primary,
                colorScheme = colorScheme
            )

            FunnelRow(
                label = "Applied",
                value = funnel.applied,
                percentage = (funnel.applied.toFloat() / funnel.viewed.toFloat() * 100).toInt(),
                color = colorScheme.secondary,
                colorScheme = colorScheme
            )

            FunnelRow(
                label = "Reviewed",
                value = funnel.reviewed,
                percentage = (funnel.reviewed.toFloat() / funnel.viewed.toFloat() * 100).toInt(),
                color = colorScheme.tertiary,
                colorScheme = colorScheme
            )

            Spacer(modifier = Modifier.height(12.dp))

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
fun AnalyticsStatCard(
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

@Composable
fun FunnelRow(
    label: String,
    value: Int,
    percentage: Int,
    color: Color,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface
            )
            Text(
                text = "$value ($percentage%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

/* ───────── CONFIRMATION DIALOG ───────── */

@Composable
fun AdminConfirmationDialog(
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
private fun formatNumberHousingDetails(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

/* ───────── PREVIEW ───────── */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminJobDetailsPreview() {
    PivotaConnectTheme {
        AdminJobDetailsScreen(
            jobListing = createSampleAdminJobListing(),
            onNavigateBack = {},
            onEditJob = {},
            onDuplicateJob = {},
            onArchiveJob = {},
            onDeleteJob = {},
            onPauseJob = {},
            onResumeJob = {},
            onCloseJob = {},
            onViewApplicants = {},
            onShareJob = {},
            onViewLogs = {},
            onCopyJobLink = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 840)
@Composable
fun AdminJobDetailsTabletPreview() {
    PivotaConnectTheme {
        AdminJobDetailsScreen(
            jobListing = createSampleAdminJobListing(),
            onNavigateBack = {},
            onEditJob = {},
            onDuplicateJob = {},
            onArchiveJob = {},
            onDeleteJob = {},
            onPauseJob = {},
            onResumeJob = {},
            onCloseJob = {},
            onViewApplicants = {},
            onShareJob = {},
            onViewLogs = {},
            onCopyJobLink = {}
        )
    }
}

private fun createSampleAdminJobListing(): AdminJobListingUiModel {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -5)
    val postedDate = calendar.time

    calendar.add(Calendar.DAY_OF_YEAR, 25)
    val expiryDate = calendar.time

    return AdminJobListingUiModel(
        id = "job-123",
        title = "Senior Software Engineer",
        companyName = "Tech Innovations Africa",
        companyLogoUrl = null,
        location = "Nairobi, Kenya",
        exactLocation = "Westlands, Nairobi",
        jobType = "Full-time",
        status = JobStatus.ACTIVE,
        postedDate = postedDate,
        expiryDate = expiryDate,
        views = 2456,
        applications = 24,
        newApplications = 12,
        reviewedApplications = 15,
        description = "We are looking for a talented Senior Software Engineer to join our growing team in Nairobi. You will be responsible for developing and maintaining high-quality mobile and web applications that serve users across Africa.",
        requirements = listOf(
            "Bachelor's degree in Computer Science or related field",
            "5+ years of experience in software development",
            "Strong knowledge of Kotlin/Java for Android or Swift for iOS",
            "Experience with RESTful APIs and cloud services"
        ),
        skills = listOf(
            "Android Development",
            "Kotlin",
            "REST APIs",
            "Firebase",
            "Git"
        ),
        benefits = listOf(
            "Health Insurance",
            "Transport allowance",
            "Meal vouchers",
            "Learning & Development budget"
        ),
        isVerified = true,
        employerName = "Sarah Omondi",
        employerVerified = true,
        averageTimeToApply = 3.2,
        applicationFunnel = ApplicationFunnel(
            viewed = 100,
            applied = 24,
            reviewed = 12
        )
    )
}