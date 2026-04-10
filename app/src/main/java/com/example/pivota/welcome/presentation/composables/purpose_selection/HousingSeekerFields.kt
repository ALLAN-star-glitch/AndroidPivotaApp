package com.example.pivota.welcome.presentation.composables.purpose_selection

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.ui.theme.*
import com.example.pivota.welcome.presentation.state.HousingSeekerFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HousingSeekerFields(
    data: HousingSeekerFormData,
    onDataChange: (HousingSeekerFormData) -> Unit
): Boolean { // Return validation state
    val searchTypes = listOf("RENTAL", "SALE", "BOTH")

    val propertyTypes = listOf(
        "APARTMENT", "HOUSE", "BEDSITTER", "ROOM",
        "STUDIO", "TOWNHOUSE", "LAND", "CONDO", "VILLA"
    )

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var lastSelectedIndex by remember { mutableStateOf(-1) }

    val selectedCount = data.propertyTypes.size
    val hasSelectedSearchType = data.searchType.isNotEmpty()

    // Validation logic - button enabled when search type selected AND at least one property type
    val isValid = hasSelectedSearchType && selectedCount > 0

    // Smart auto-scroll: finds and scrolls to next unselected property type
    LaunchedEffect(data.propertyTypes) {
        if (lastSelectedIndex != -1 && data.propertyTypes.isNotEmpty()) {
            coroutineScope.launch {
                delay(50)

                var nextIndex = -1

                for (i in lastSelectedIndex + 1 until propertyTypes.size) {
                    if (!data.propertyTypes.contains(propertyTypes[i])) {
                        nextIndex = i
                        break
                    }
                }

                if (nextIndex == -1) {
                    for (i in 0 until lastSelectedIndex) {
                        if (!data.propertyTypes.contains(propertyTypes[i])) {
                            nextIndex = i
                            break
                        }
                    }
                }

                if (nextIndex != -1 && nextIndex < propertyTypes.size) {
                    listState.animateScrollToItem(nextIndex)
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(24.dp),
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Elegant Header
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            )
                        )
                )

                Text(
                    text = "What are you\nlooking for?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Search Type - Scrollable Elegant Pills
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select option *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(searchTypes) { type ->
                        val isSelected = data.searchType == type

                        ElegantPill(
                            selected = isSelected,
                            onClick = {
                                val updatedType = if (data.searchType == type) "" else type
                                onDataChange(
                                    data.copy(
                                        searchType = updatedType,
                                        isLookingForRental = updatedType == "RENTAL" || updatedType == "BOTH",
                                        isLookingToBuy = updatedType == "SALE" || updatedType == "BOTH",
                                        propertyTypes = if (updatedType.isEmpty()) emptyList() else data.propertyTypes
                                    )
                                )
                            },
                            label = when(type) {
                                "RENTAL" -> "For Rent"
                                "SALE" -> "For Sale"
                                else -> "Both"
                            },
                            modifier = Modifier
                        )
                    }
                }
            }

            // Property Types - Animated conditional display
            AnimatedVisibility(
                visible = hasSelectedSearchType,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) +
                        scaleIn(
                            initialScale = 0.95f,
                            animationSpec = tween(400)
                        ),
                exit = fadeOut(animationSpec = tween(200)) +
                        slideOutVertically(
                            targetOffsetY = { -20 },
                            animationSpec = tween(200)
                        ) +
                        scaleOut(
                            targetScale = 0.98f,
                            animationSpec = tween(200)
                        )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Property type header with elegant counter and clear button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Property Types *",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Text(
                                text = "Select all that apply",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Clear selection button
                            if (selectedCount > 0) {
                                ClearSelectionButton(
                                    onClick = {
                                        onDataChange(data.copy(propertyTypes = emptyList()))
                                    }
                                )
                            }

                            // Animated counter
                            AnimatedContent(
                                targetState = selectedCount,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(200)) +
                                            scaleIn(initialScale = 0.7f) togetherWith
                                            fadeOut(animationSpec = tween(100)) +
                                            scaleOut(targetScale = 0.7f)
                                }
                            ) { count ->
                                if (count > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            text = "$count",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Property type pills in horizontal scroll with scroll state
                    LazyRow(
                        state = listState,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(propertyTypes.size) { index ->
                            val type = propertyTypes[index]
                            val isSelected = data.propertyTypes.contains(type)

                            ElegantPill(
                                selected = isSelected,
                                onClick = {
                                    val updated = if (data.propertyTypes.contains(type)) {
                                        data.propertyTypes.filter { it != type }
                                    } else {
                                        lastSelectedIndex = index
                                        data.propertyTypes + type
                                    }
                                    onDataChange(data.copy(propertyTypes = updated))
                                },
                                label = type.replaceFirstChar { it.uppercase() },
                                modifier = Modifier
                            )
                        }
                    }

                    // Validation message when search type selected but no property types
                    if (hasSelectedSearchType && selectedCount == 0) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(initialOffsetY = { 20 })
                        ) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Info,
                                        contentDescription = "Info",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Please select at least one property type to continue",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Elegant progress indicator
                    if (selectedCount > 0 && selectedCount < propertyTypes.size) {
                        ElegantProgressBar(
                            progress = selectedCount.toFloat() / propertyTypes.size
                        )
                    }

                    // Smart navigation hint
                    if (selectedCount > 0 && selectedCount < propertyTypes.size) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(initialOffsetY = { 20 }),
                            exit = fadeOut(animationSpec = tween(200))
                        ) {
                            Text(
                                text = "✨ Automatically scrolling to next option",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.alpha(0.7f)
                            )
                        }
                    }

                    // Completion message with clear button
                    if (selectedCount == propertyTypes.size) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(initialOffsetY = { 20 })
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓ All property types selected",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.weight(1f).alpha(0.8f)
                                )

                                TextButton(
                                    onClick = { onDataChange(data.copy(propertyTypes = emptyList())) },
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = "Clear all",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    return isValid // Return validation state
}

@Composable
fun ClearSelectionButton(
    onClick: () -> Unit
) {
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
        modifier = Modifier
            .scale(scale)
            .shadow(0.dp),
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Close,
                contentDescription = "Clear",
                modifier = Modifier.size(12.dp),
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
fun ElegantPill(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.97f
            selected -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (selected) 3.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "backgroundColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250),
        label = "textColor"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (selected) 0.8f else 0.1f,
        animationSpec = tween(250),
        label = "borderAlpha"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(100.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(100.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha)
        ),
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
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textColor
                )
            )

            AnimatedVisibility(
                visible = selected,
                enter = scaleIn(initialScale = 0.5f) + fadeIn(),
                exit = scaleOut(targetScale = 0.5f) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun ElegantProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "progress"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }

        Text(
            text = "${(progress * 100).toInt()}% completed",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        )
    }
}