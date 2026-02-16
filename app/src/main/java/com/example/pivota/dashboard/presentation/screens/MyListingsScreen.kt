package com.example.pivota.dashboard.presentation.screens

import com.example.pivota.dashboard.presentation.viewmodels.MyListingsViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass

import com.example.pivota.dashboard.domain.*
import com.example.pivota.dashboard.presentation.model.*


/* ────────────── SCREEN ────────────── */

@Composable
fun MyListingsScreen(
    viewModel: MyListingsViewModel = hiltViewModel(),
    onListingClick: (ListingUiModel) -> Unit,
    onPostListingClick: () -> Unit
) {
    val listings by viewModel.filteredListings.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.currentStatusFilter.collectAsStateWithLifecycle()

    // ────────────── ADAPTIVE BREAKPOINTS ──────────────
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val softBackground = Color(0xFFF6FAF9)

    Scaffold(
        containerColor = softBackground,
        topBar = { MyListingsHeader() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StatusSelector(
                selected = selectedFilter,
                onSelected = viewModel::updateStatusFilter
            )

            if (listings.isEmpty()) {
                ElegantEmptyState(onPostListingClick)
            } else {
                // Adaptive Layout: Grid for Medium/Expanded, List for Compact
                LazyVerticalGrid(
                    columns = if (isWide) GridCells.Fixed(2) else GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 12.dp,
                        bottom = 32.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listings, key = { it.id }) { listing ->
                        ListingCard(
                            listing = listing,
                            onClick = { onListingClick(listing) }
                        )
                    }
                }
            }
        }
    }
}

/* ────────────── UI COMPONENTS ────────────── */

@Composable
private fun MyListingsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "My Listings",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1E),
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text = "Everything you’ve posted in one place",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
        }
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, Color.LightGray.copy(0.3f)),
            onClick = { /* Open Filter Sheet */ }
        ) {
            Icon(
                Icons. Default.FilterList,
                null,
                tint = Color(0xFF006565),
                modifier = Modifier.padding(10.dp).size(20.dp)
            )
        }
    }
}

@Composable
private fun StatusSelector(
    selected: ListingFilter,
    onSelected: (ListingFilter) -> Unit
) {
    val primaryTeal = Color(0xFF006565)

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Use the 'items' DSL for Lazy components
        items(ListingFilter.entries.toTypedArray()) { filter ->
            val isSelected = selected == filter

            Surface(
                onClick = { onSelected(filter) },
                shape = CircleShape,
                color = if (isSelected) primaryTeal else Color.Transparent,
                border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray.copy(0.4f)),
                modifier = Modifier.height(38.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = if (isSelected) Color.White else Color.Gray,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ListingCard(listing: ListingUiModel, onClick: () -> Unit) {
    val primaryTeal = Color(0xFF006565)
    val mutedGold = Color(0xFFE9C16C)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 0.8.dp,
        modifier = Modifier
            .fillMaxWidth()
            // This allows the card to occupy the height of the tallest item in the row
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            // 1. Top Content (Flexible space)
            // Weight(1f) ensures this expands, pushing the footer to the bottom
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        listing.category.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = primaryTeal,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.size(3.dp).background(Color.LightGray, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        listing.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                    maxLines = 1, // Keep single line for strict uniformity
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listing.descriptionPreview,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Optional hint - weight(1f) handles the extra space if this is missing
                listing.performanceHint?.let { hint ->
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(mutedGold.copy(0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = mutedGold, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when(hint) {
                                PerformanceHint.HighInterest -> "High interest this week"
                                PerformanceHint.NewResponses -> "New responses today"
                                is PerformanceHint.Custom -> hint.message
                            },
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8A6D2B), fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            // 2. Bottom Content (Footer)
            // This will now be perfectly aligned across all cards in a row
            Column {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(0.3f))
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MetricItem(Icons.Default.Visibility, listing.views.toString())
                        MetricItem(Icons.Default.ChatBubbleOutline, listing.messages.toString())
                        MetricItem(Icons.Default.PendingActions, listing.requests.toString())
                    }
                    Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricItem(icon: ImageVector, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        Spacer(Modifier.width(6.dp))
        Text(count, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ElegantEmptyState(onPostListingClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(modifier = Modifier.size(80.dp), color = Color(0xFF006565).copy(0.05f), shape = CircleShape) {
            Icon(Icons.Rounded.PostAdd, null, tint = Color(0xFF006565), modifier = Modifier.padding(20.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("No listings yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text("Post your first job, service, or request to get started.", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray), textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onPostListingClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006565)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text("Post Listing", fontWeight = FontWeight.Bold)
        }
    }
}