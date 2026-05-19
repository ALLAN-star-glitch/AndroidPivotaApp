package com.example.pivota.welcome.presentation.composables.purpose_selection

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.ui.theme.*
import com.example.pivota.welcome.presentation.state.HousingSeekerFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HousingSeekerFields(
    data: HousingSeekerFormData,
    onDataChange: (HousingSeekerFormData) -> Unit
): Boolean {
    val searchTypes = listOf("RENTAL", "SALE", "BOTH")

    val propertyTypes = listOf(
        "APARTMENT", "HOUSE", "BEDSITTER", "ROOM",
        "STUDIO", "TOWNHOUSE", "LAND", "CONDO", "VILLA"
    )

    var showBottomSheet by remember { mutableStateOf(false) }
    val selectedCount = data.propertyTypes.size
    val hasSelectedSearchType = data.searchType.isNotEmpty()

    // Validation logic
    val isValid = hasSelectedSearchType && selectedCount > 0

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(24.dp))
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

            // Property Types Section - Only visible when search type is selected
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
                    // Property type header
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
                                text = if (selectedCount > 0)
                                    "${selectedCount} type${if (selectedCount > 1) "s" else ""} selected"
                                else
                                    "Select property types to continue",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = if (selectedCount == 0)
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Change Button (removed Select button logic)
                        if (selectedCount > 0) {
                            Button(
                                onClick = { showBottomSheet = true },
                                modifier = Modifier.height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Change",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Change",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Display selected property types as chips
                    if (selectedCount > 0) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(data.propertyTypes) { propertyType ->
                                        SelectedPropertyChip(
                                            label = propertyType.replaceFirstChar { it.uppercase() },
                                            onRemove = {
                                                val updated = data.propertyTypes.filter { it != propertyType }
                                                onDataChange(data.copy(propertyTypes = updated))
                                            }
                                        )
                                    }
                                }

                                // Clear all button for multiple selections
                                if (selectedCount > 1) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { onDataChange(data.copy(propertyTypes = emptyList())) },
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(
                                                text = "Clear all",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                // Progress indicator
                                if (selectedCount < propertyTypes.size) {
                                    ElegantProgressBar(
                                        progress = selectedCount.toFloat() / propertyTypes.size
                                    )
                                }

                                // Completion message
                                if (selectedCount == propertyTypes.size) {
                                    Text(
                                        text = "✓ Great! You've selected all property types",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.alpha(0.8f)
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty state - show hint to select
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showBottomSheet = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Add",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tap to select property types",
                                    style = MaterialTheme.typography.bodyMedium.copy(
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

    // Bottom Sheet for Property Selection
    if (showBottomSheet) {
        PropertyTypeBottomSheet(
            propertyTypes = propertyTypes,
            selectedTypes = data.propertyTypes,
            onSelectionChange = { updatedSelection ->
                onDataChange(data.copy(propertyTypes = updatedSelection))
            },
            onDismiss = { showBottomSheet = false }
        )
    }

    return isValid
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTypeBottomSheet(
    propertyTypes: List<String>,
    selectedTypes: List<String>,
    onSelectionChange: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var localSelectedTypes by remember(selectedTypes) { mutableStateOf(selectedTypes.toSet()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Property Types",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp
                    )
                )

                Text(
                    text = "Choose all property types you're interested in",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Selection count
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${localSelectedTypes.size} of ${propertyTypes.size} selected",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            // Divider
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // Checkbox list
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                propertyTypes.forEach { propertyType ->
                    val formattedLabel = propertyType.replaceFirstChar { it.uppercase() }
                    val isSelected = localSelectedTypes.contains(propertyType)

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localSelectedTypes = if (isSelected) {
                                    localSelectedTypes - propertyType
                                } else {
                                    localSelectedTypes + propertyType
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    localSelectedTypes = if (checked) {
                                        localSelectedTypes + propertyType
                                    } else {
                                        localSelectedTypes - propertyType
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Text(
                                text = formattedLabel,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(start = 8.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Clear all button
                OutlinedButton(
                    onClick = { localSelectedTypes = emptySet() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All")
                }

                // Apply button
                Button(
                    onClick = {
                        onSelectionChange(localSelectedTypes.toList())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = localSelectedTypes.isNotEmpty()
                ) {
                    Text("Apply (${localSelectedTypes.size})")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SelectedPropertyChip(
    label: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
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
                imageVector = Icons.Default.Close,
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