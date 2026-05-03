package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.dashboard.domain.EmployerType


@Composable
fun ModernJobCard(
    title: String,
    company: String,
    location: String,
    salary: String,
    type: String,
    description: String,  // Added description parameter
    isVerified: Boolean,
    employerType: EmployerType = EmployerType.ORGANIZATION,
    profileImageRes: Any? = null,
    companyLogoRes: Any? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: (Boolean) -> Unit = {},
    onViewClick: () -> Unit = {},
    onApplyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Using MaterialTheme colors from your theme
    val primaryColor = MaterialTheme.colorScheme.primary      // African Sapphire
    val secondaryColor = MaterialTheme.colorScheme.secondary  // Warm Terracotta
    val tertiaryColor = MaterialTheme.colorScheme.tertiary    // Baobab Gold
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    var favoriteState by remember { mutableStateOf(isFavorite) }

    // Determine which image resource to use
    val imageRes = remember(profileImageRes, companyLogoRes) {
        profileImageRes ?: companyLogoRes
    }

    // Determine shape and size based on employer type
    val (shape, containerSize) = remember(employerType) {
        if (employerType == EmployerType.INDIVIDUAL) {
            Pair(CircleShape, 60.dp)
        } else {
            Pair(RoundedCornerShape(10.dp), 70.dp)
        }
    }

    Card(
        modifier = modifier
            .clickable { onViewClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Profile/Logo Image Section with Coil
                Box(
                    modifier = Modifier
                        .size(containerSize)
                        .clip(shape)
                        .background(primaryColor.copy(0.05f))
                        .align(if (employerType == EmployerType.INDIVIDUAL)
                            Alignment.CenterHorizontally else Alignment.Start)
                ) {
                    if (imageRes != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageRes)
                                .crossfade(true)
                                .size(containerSize.roundToPx())
                                .build(),
                            contentDescription = when (employerType) {
                                EmployerType.INDIVIDUAL -> "$company profile picture"
                                EmployerType.ORGANIZATION -> "$company logo"
                            },
                            contentScale = if (employerType == EmployerType.INDIVIDUAL)
                                ContentScale.Crop
                            else
                                ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.job_placeholder1)
                        )
                    } else {
                        // Fallback when no image is provided
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(primaryColor.copy(0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (employerType == EmployerType.INDIVIDUAL)
                                    Icons.Default.Person
                                else
                                    Icons.Default.Work,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(
                    if (employerType == EmployerType.INDIVIDUAL) 12.dp else 10.dp
                ))

                // Title and company
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = onSurfaceColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (isVerified) {
                                Icon(
                                    Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = company,
                            fontSize = 13.sp,
                            color = onSurfaceVariantColor,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = onSurfaceVariantColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 13.sp,
                        color = onSurfaceVariantColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description - NEW SECTION
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = onSurfaceVariantColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Salary and type
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = salary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = primaryColor,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = secondaryColor.copy(0.1f),
                        tonalElevation = 0.dp
                    ) {
                        Text(
                            text = type,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = secondaryColor,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // View Button
                    Button(
                        onClick = onViewClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = onPrimaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "View",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = onPrimaryColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    // Apply Button
                    OutlinedButton(
                        onClick = onApplyClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = secondaryColor
                        ),
                        border = BorderStroke(1.dp, secondaryColor),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Work,
                                contentDescription = null,
                                tint = secondaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Apply",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = secondaryColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // Favorite Icon
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(surfaceColor.copy(alpha = 0.9f))
                    .clickable {
                        favoriteState = !favoriteState
                        onFavoriteClick(favoriteState)
                    }
            ) {
                Icon(
                    imageVector = if (favoriteState) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (favoriteState) "Remove from favorites" else "Add to favorites",
                    tint = if (favoriteState) tertiaryColor else onSurfaceVariantColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(16.dp)
                )
            }
        }
    }
}

// Extension function to convert Dp to Pixels
@Composable
private fun Dp.roundToPx(): Int = (this.value * androidx.compose.ui.platform.LocalDensity.current.density).toInt()