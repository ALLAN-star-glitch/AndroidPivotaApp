package com.example.pivota.welcome.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PropertyOwnerFields(
    data: PropertyOwnerData,
    onDataChange: (PropertyOwnerData) -> Unit
) {
    var currentPropertyTypeInput by remember { mutableStateOf("") }
    var currentServiceAreaInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Parse property types from data.propertyTypes string into a list
    val propertyTypesList = remember(data.propertyTypes) {
        data.propertyTypes.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Parse service areas from data.serviceAreas string into a list
    val serviceAreasList = remember(data.serviceAreas) {
        data.serviceAreas.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Generic add function for any list
    fun addItem(currentInput: String, currentList: List<String>, onAdd: (String) -> Unit, onClearInput: () -> Unit) {
        val trimmedItem = currentInput.trim()
        if (trimmedItem.isNotEmpty() && !currentList.contains(trimmedItem)) {
            val newItems = if (currentList.isEmpty()) {
                trimmedItem
            } else {
                "${currentList.joinToString(", ")}, $trimmedItem"
            }
            onAdd(newItems)
            onClearInput()
            showDuplicateError = false
        } else if (currentList.contains(trimmedItem)) {
            showDuplicateError = true
            coroutineScope.launch {
                delay(2000)
                showDuplicateError = false
            }
        }
    }

    // Generic remove function
    fun removeItem(itemToRemove: String, currentList: List<String>, onRemove: (String) -> Unit) {
        val newItems = currentList.filter { it != itemToRemove }.joinToString(", ")
        onRemove(newItems)
    }

    // Professional Status options
    val professionalStatuses = listOf("Individual Owner", "Professional Landlord", "Property Manager", "Real Estate Investor")

    // Common property types for suggestions
    val propertyTypeSuggestions = listOf(
        "Apartments", "Single Family Homes", "Commercial Spaces", "Office Buildings",
        "Retail Spaces", "Industrial Properties", "Land", "Vacation Rentals", "Student Housing"
    )

    // Common service areas for suggestions
    val serviceAreaSuggestions = listOf(
        "Kilimani", "Kileleshwa", "Westlands", "Lavington", "Karen",
        "Ruiru", "Thika", "Kiambu", "Embakasi", "Donholm",
        "Buruburu", "Lang'ata", "South B", "South C", "Parklands"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Property Owner",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Professional Status
            Text(
                text = "Professional Status",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(professionalStatuses) { status ->
                    FilterChip(
                        selected = data.professionalStatus == status,
                        onClick = { onDataChange(data.copy(professionalStatus = status)) },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Number of Properties
            OutlinedTextField(
                value = data.propertyCount,
                onValueChange = { onDataChange(data.copy(propertyCount = it)) },
                label = { Text("Number of Properties Owned") },
                placeholder = { Text("3") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Property Types Section (Pill Input)
            PropertyTypeInputSection(
                title = "Property Types Owned",
                itemsList = propertyTypesList,
                currentInput = currentPropertyTypeInput,
                onCurrentInputChange = { currentPropertyTypeInput = it },
                onAddItem = { propertyType ->
                    addItem(propertyType, propertyTypesList, { newTypes -> onDataChange(data.copy(propertyTypes = newTypes)) }) { currentPropertyTypeInput = "" }
                },
                onRemoveItem = { propertyType ->
                    removeItem(propertyType, propertyTypesList, { newTypes -> onDataChange(data.copy(propertyTypes = newTypes)) })
                },
                placeholder = "e.g., Apartments, Commercial Spaces, Land",
                suggestions = propertyTypeSuggestions,
                showDuplicateError = showDuplicateError
            )

            // Service Areas Section (Pill Input)
            ServiceAreaInputSection(
                title = "Service Areas",
                itemsList = serviceAreasList,
                currentInput = currentServiceAreaInput,
                onCurrentInputChange = { currentServiceAreaInput = it },
                onAddItem = { area ->
                    addItem(area, serviceAreasList, { newAreas -> onDataChange(data.copy(serviceAreas = newAreas)) }) { currentServiceAreaInput = "" }
                },
                onRemoveItem = { area ->
                    removeItem(area, serviceAreasList, { newAreas -> onDataChange(data.copy(serviceAreas = newAreas)) })
                },
                placeholder = "e.g., Kilimani, Westlands, Ruaka",
                suggestions = serviceAreaSuggestions,
                showDuplicateError = showDuplicateError
            )
        }
    }
}

@Composable
fun PropertyTypeInputSection(
    title: String,
    itemsList: List<String>,
    currentInput: String,
    onCurrentInputChange: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    placeholder: String,
    suggestions: List<String>,
    showDuplicateError: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // Items Pills Row
        if (itemsList.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                item,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItem(item) },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Input Field
        OutlinedTextField(
            value = currentInput,
            onValueChange = {
                onCurrentInputChange(it)
            },
            label = {
                Text(
                    if (itemsList.isEmpty()) "Add $title" else "Add another $title",
                    color = if (showDuplicateError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            onAddItem(currentInput)
                        }
                    },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (currentInput.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (currentInput.isNotBlank()) {
                        onAddItem(currentInput)
                    }
                }
            ),
            isError = showDuplicateError,
            supportingText = {
                if (showDuplicateError) {
                    Text(
                        "This item has already been added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (itemsList.isNotEmpty()) {
                    Text(
                        "${itemsList.size} item${if (itemsList.size > 1) "s" else ""} added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

        // Suggestions chips
        if (suggestions.isNotEmpty()) {
            Text(
                text = "Suggestions:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = {
                            if (!itemsList.contains(suggestion)) {
                                onAddItem(suggestion)
                            }
                        },
                        label = {
                            Text(
                                suggestion,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        enabled = !itemsList.contains(suggestion),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceAreaInputSection(
    title: String,
    itemsList: List<String>,
    currentInput: String,
    onCurrentInputChange: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    placeholder: String,
    suggestions: List<String>,
    showDuplicateError: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // Items Pills Row
        if (itemsList.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                item,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItem(item) },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Input Field
        OutlinedTextField(
            value = currentInput,
            onValueChange = {
                onCurrentInputChange(it)
            },
            label = {
                Text(
                    if (itemsList.isEmpty()) "Add $title" else "Add another $title",
                    color = if (showDuplicateError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            onAddItem(currentInput)
                        }
                    },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (currentInput.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (currentInput.isNotBlank()) {
                        onAddItem(currentInput)
                    }
                }
            ),
            isError = showDuplicateError,
            supportingText = {
                if (showDuplicateError) {
                    Text(
                        "This area has already been added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (itemsList.isNotEmpty()) {
                    Text(
                        "${itemsList.size} area${if (itemsList.size > 1) "s" else ""} added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

        // Suggestions chips
        if (suggestions.isNotEmpty()) {
            Text(
                text = "Suggestions:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = {
                            if (!itemsList.contains(suggestion)) {
                                onAddItem(suggestion)
                            }
                        },
                        label = {
                            Text(
                                suggestion,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        enabled = !itemsList.contains(suggestion),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}