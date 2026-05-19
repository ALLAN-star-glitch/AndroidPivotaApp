package com.example.pivota.welcome.presentation.composables.purpose_selection

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.ui.theme.*
import com.example.pivota.welcome.presentation.screens.SuggestionPill
import com.example.pivota.welcome.presentation.state.SupportBeneficiaryFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SupportBeneficiaryFields(
    data: SupportBeneficiaryFormData,
    onDataChange: (SupportBeneficiaryFormData) -> Unit
): Boolean {

    var currentUrgentNeedInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    var showAddHint by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val urgentNeedsList = remember(data.urgentNeeds) {
        data.urgentNeeds.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    val supportOptions = listOf(
        "Food", "Shelter", "Medical", "Education", "Cash", "Other"
    )

    val urgentNeedSuggestions = listOf(
        "Food for family",
        "Emergency shelter",
        "Medical help",
        "School fees",
        "Clothing",
        "Transport"
    )

    val hasSelectedSupport = data.supportTypes.isNotEmpty()
    val selectedCount = data.supportTypes.size

    // Auto-scroll state for support types
    val supportListState = rememberLazyListState()
    var lastSelectedSupportIndex by remember { mutableStateOf(-1) }

    // Validation - at least one support type selected
    val isValid = data.supportTypes.isNotEmpty()

    // Smart auto-scroll for support types
    LaunchedEffect(data.supportTypes) {
        if (lastSelectedSupportIndex != -1 && data.supportTypes.isNotEmpty()) {
            coroutineScope.launch {
                delay(50)

                var nextIndex = -1

                for (i in lastSelectedSupportIndex + 1 until supportOptions.size) {
                    if (!data.supportTypes.contains(supportOptions[i])) {
                        nextIndex = i
                        break
                    }
                }

                if (nextIndex == -1) {
                    for (i in 0 until lastSelectedSupportIndex) {
                        if (!data.supportTypes.contains(supportOptions[i])) {
                            nextIndex = i
                            break
                        }
                    }
                }

                if (nextIndex != -1 && nextIndex < supportOptions.size) {
                    supportListState.animateScrollToItem(nextIndex)
                }
            }
        }
    }

    fun addNeed(input: String) {
        val trimmed = input.trim()
        if (trimmed.isNotEmpty() && !urgentNeedsList.contains(trimmed)) {
            val updated = if (urgentNeedsList.isEmpty()) trimmed
            else "${urgentNeedsList.joinToString(", ")}, $trimmed"

            onDataChange(data.copy(urgentNeeds = updated))
            currentUrgentNeedInput = ""
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

    fun removeNeed(item: String) {
        val updated = urgentNeedsList.filter { it != item }.joinToString(", ")
        onDataChange(data.copy(urgentNeeds = updated))
    }

    fun clearAllNeeds() {
        onDataChange(data.copy(urgentNeeds = ""))
    }

    fun clearAllSupportTypes() {
        onDataChange(data.copy(supportTypes = emptyList()))
    }

    // Dynamic placeholder text
    val needPlaceholder = if (urgentNeedsList.isEmpty())
        "Type your need (e.g., Food for family)"
    else
        "Type another need"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
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
                    text = "Support\nRequest",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Tell us how we can help",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Support Type Section - Multi-select Elegant Pills with auto-scroll
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "What do you need help with? *",
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

                    // Animated counter only
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
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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

                LazyRow(
                    state = supportListState,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(supportOptions.size) { index ->
                        val option = supportOptions[index]
                        val isSelected = data.supportTypes.contains(option)

                        ElegantPill(
                            selected = isSelected,
                            onClick = {
                                val updated = if (data.supportTypes.contains(option)) {
                                    data.supportTypes - option
                                } else {
                                    lastSelectedSupportIndex = index
                                    data.supportTypes + option
                                }
                                onDataChange(data.copy(supportTypes = updated))
                            },
                            label = option,
                            modifier = Modifier
                        )
                    }
                }

                // Clear button below the pills
                if (selectedCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ClearSelectionButton(
                            onClick = { clearAllSupportTypes() }
                        )
                    }
                }

                // Validation message for support type
                if (data.supportTypes.isEmpty()) {
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
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Please select at least one support type to continue",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }
                    }
                }

                // Smart navigation hint for support types
                if (selectedCount > 0 && selectedCount < supportOptions.size) {
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
            }

            // Urgent Needs Section - Animated conditional display
            AnimatedVisibility(
                visible = hasSelectedSupport,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Describe Your Need",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Text(
                                text = "Tell us more about your situation (optional)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Counter and Clear button for urgent needs
                        if (urgentNeedsList.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedContent(
                                    targetState = urgentNeedsList.size,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(200)) +
                                                scaleIn(initialScale = 0.7f) togetherWith
                                                fadeOut(animationSpec = tween(100)) +
                                                scaleOut(targetScale = 0.7f)
                                    }
                                ) { count ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            text = "$count",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                }

                                ClearButton(
                                    onClick = { clearAllNeeds() }
                                )
                            }
                        }
                    }

                    // Urgent Needs Pills Row
                    if (urgentNeedsList.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(urgentNeedsList) { need ->
                                ElegantPillWithRemove(
                                    label = need,
                                    onRemove = { removeNeed(need) }
                                )
                            }
                        }
                    }

                    // Add Need Section - Redesigned like job skills with plus icon inside input
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Input field with trailing plus icon
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                BasicTextField(
                                    value = currentUrgentNeedInput,
                                    onValueChange = {
                                        currentUrgentNeedInput = it
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
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (currentUrgentNeedInput.isNotBlank()) {
                                                addNeed(currentUrgentNeedInput)
                                            }
                                        }
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
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
                                                if (currentUrgentNeedInput.isEmpty()) {
                                                    Text(
                                                        text = needPlaceholder,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                        )
                                                    )
                                                }
                                                innerTextField()
                                            }

                                            AnimatedVisibility(
                                                visible = currentUrgentNeedInput.isNotBlank(),
                                                enter = scaleIn() + fadeIn(),
                                                exit = scaleOut() + fadeOut()
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        if (currentUrgentNeedInput.isNotBlank()) {
                                                            addNeed(currentUrgentNeedInput)
                                                        }
                                                    },
                                                    modifier = Modifier.size(40.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = "Add Need",
                                                        modifier = Modifier.size(24.dp),
                                                        tint = MaterialTheme.colorScheme.primary
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
                                                showDuplicateError -> MaterialTheme.colorScheme.error
                                                isFocused -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
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
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "This need has already been added",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            } else if (showAddHint && currentUrgentNeedInput.isNotBlank()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Hint",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Press the + button or Done key to add",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Quick Suggestions (preserved and enhanced)
                    if (urgentNeedSuggestions.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Quick suggestions",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    letterSpacing = 0.4.sp
                                )
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(urgentNeedSuggestions) { suggestion ->
                                    val isAlreadyAdded = urgentNeedsList.contains(suggestion)

                                    SuggestionPill(
                                        label = suggestion,
                                        isAdded = isAlreadyAdded,
                                        onClick = {
                                            if (!isAlreadyAdded) {
                                                addNeed(suggestion)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Location Field (always visible)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Your Location",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                OutlinedTextField(
                    value = data.location,
                    onValueChange = { onDataChange(data.copy(location = it)) },
                    placeholder = {
                        Text(
                            "e.g., Kibera, Nairobi",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    supportingText = {
                        Text(
                            "Your location helps us connect you with local resources",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                )
            }
        }
    }

    return isValid
}