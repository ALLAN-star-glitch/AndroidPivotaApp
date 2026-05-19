package com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.dashboard.domain.model.profile_models.ProfessionalProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.SuccessGreen

// Helper function to determine file type from URL
private fun getFileType(url: String): String {
    val extension = url.substringAfterLast(".").lowercase()
    return when (extension) {
        "jpg", "jpeg", "png", "gif", "webp", "bmp" -> "image"
        "pdf" -> "pdf"
        "doc", "docx" -> "word"
        "xls", "xlsx" -> "excel"
        "ppt", "pptx" -> "powerpoint"
        "txt" -> "text"
        "zip", "rar" -> "archive"
        else -> "document"
    }
}

// Helper function to extract filename from URL
private fun getFileNameFromUrl(url: String): String {
    // Remove query parameters if any
    val cleanUrl = url.substringBefore("?")
    // Get everything after the last slash
    val fullFileName = cleanUrl.substringAfterLast("/")
    // Decode URL encoding (e.g., %20 -> space)
    val decodedName = java.net.URLDecoder.decode(fullFileName, "UTF-8")

    // Remove timestamp prefix if present (e.g., "1778415792270-142.pdf" -> "142.pdf")
    val nameWithoutTimestamp = decodedName.replace(Regex("^\\d+-"), "")

    return nameWithoutTimestamp
}

// Helper function to truncate long filenames
private fun truncateFileName(fileName: String, maxLength: Int = 15): String {
    return if (fileName.length > maxLength) {
        val extension = fileName.substringAfterLast(".")
        val nameWithoutExt = fileName.substringBeforeLast(".")
        val truncatedName = nameWithoutExt.take(maxLength - extension.length - 3)
        "$truncatedName...$extension"
    } else {
        fileName
    }
}

@Composable
fun PortfolioItemCard(
    url: String,
    onClick: () -> Unit
) {
    val fileType = getFileType(url)
    val isImage = fileType == "image"

    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isImage -> {
                    // Display image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Portfolio image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.ic_launcher_background)
                    )
                }
                fileType == "pdf" -> {
                    // PDF thumbnail
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = "PDF Document",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "PDF",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                else -> {
                    // Generic document thumbnail
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = "Document",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = fileType.uppercase(),
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalTabContent(
    professionalProfile: ProfessionalProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditSpecialties: () -> Unit,
    onEditServiceAreas: () -> Unit,
    onEditBusinessDetails: () -> Unit,
    onEditPortfolio: () -> Unit,
    onPortfolioItemClick: (String, String) -> Unit = { url, type -> }
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview Section
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
                if (!professionalProfile.title.isNullOrBlank()) {
                    Text(
                        text = professionalProfile.title!!,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Experience
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Experience",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${professionalProfile.yearsExperience ?: 0}+ years",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }

                    // Rating
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                String.format("%.1f", professionalProfile.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                "/5",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                " (${professionalProfile.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Jobs
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Jobs",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${professionalProfile.completedJobs}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                }

                if (professionalProfile.isVerified) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
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

        // Specialties Section (only if has data)
        if (professionalProfile.specialties.isNotEmpty()) {
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
                        items(professionalProfile.specialties) { specialty ->
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

        // Service Areas Section (only if has data)
        if (professionalProfile.serviceAreas.isNotEmpty()) {
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
                        items(professionalProfile.serviceAreas) { area ->
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

        // Business Details Section
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
            Column(modifier = Modifier.padding(16.dp)) {
                if (!professionalProfile.licenseNumber.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Badge,
                        label = "License Number",
                        subtitle = professionalProfile.licenseNumber!!,
                        onClick = {},
                        iconColor = if (professionalProfile.isVerified) SuccessGreen else primaryColor
                    )
                }
                if (!professionalProfile.insuranceInfo.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Security,
                        label = "Insurance",
                        subtitle = professionalProfile.insuranceInfo!!,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (professionalProfile.hourlyRate != null) {
                    ProfileItem(
                        icon = Icons.Default.AttachMoney,
                        label = "Hourly Rate",
                        subtitle = "KES ${professionalProfile.hourlyRate}",
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (professionalProfile.dailyRate != null) {
                    ProfileItem(
                        icon = Icons.Default.AttachMoney,
                        label = "Daily Rate",
                        subtitle = "KES ${professionalProfile.dailyRate}",
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!professionalProfile.paymentTerms.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Payment,
                        label = "Payment Terms",
                        subtitle = professionalProfile.paymentTerms!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Portfolio Section (only if has portfolio items)
        if (professionalProfile.portfolioImages.isNotEmpty()) {
            ProfileSection(
                title = "Portfolio (${professionalProfile.portfolioImages.size})",
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
                    items(professionalProfile.portfolioImages) { itemUrl ->
                        PortfolioItemCard(
                            url = itemUrl,
                            onClick = { onPortfolioItemClick(itemUrl, getFileType(itemUrl)) }
                        )
                    }
                }
            }
        }
    }
}