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
fun HousingSeekerFields(
    data: HousingSeekerData,
    onDataChange: (HousingSeekerData) -> Unit
) {
    var currentAreaInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Parse preferred areas from data.preferredAreas string into a list
    val areasList = remember(data.preferredAreas) {
        data.preferredAreas.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Generic add function for areas
    fun addArea(currentInput: String, currentList: List<String>, onAdd: (String) -> Unit, onClearInput: () -> Unit) {
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

    // Generic remove function for areas
    fun removeArea(areaToRemove: String, currentList: List<String>, onRemove: (String) -> Unit) {
        val newItems = currentList.filter { it != areaToRemove }.joinToString(", ")
        onRemove(newItems)
    }

    // Property Types
    val propertyTypes = listOf("Apartment", "House", "Bedsitter", "Room", "Studio", "Townhouse", "Land")

    // Common areas in Nairobi for suggestions
    val areaSuggestions = listOf(
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
                text = "PURPOSE DETAILS: Housing Seeker",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Property Type
            Text(
                text = "Property Type",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(propertyTypes) { type ->
                    FilterChip(
                        selected = data.propertyType == type,
                        onClick = { onDataChange(data.copy(propertyType = type)) },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Bedrooms range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = data.minBedrooms,
                    onValueChange = { onDataChange(data.copy(minBedrooms = it)) },
                    label = { Text("Min Bedrooms") },
                    placeholder = { Text("0") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = data.maxBedrooms,
                    onValueChange = { onDataChange(data.copy(maxBedrooms = it)) },
                    label = { Text("Max Bedrooms") },
                    placeholder = { Text("5") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Budget range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = data.minBudget,
                    onValueChange = { onDataChange(data.copy(minBudget = it)) },
                    label = { Text("Min Budget (KES)") },
                    placeholder = { Text("10,000") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = data.maxBudget,
                    onValueChange = { onDataChange(data.copy(maxBudget = it)) },
                    label = { Text("Max Budget (KES)") },
                    placeholder = { Text("100,000") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Preferred Areas Section (Pill Input)
            AreaInputSection(
                title = "Preferred Areas",
                itemsList = areasList,
                currentInput = currentAreaInput,
                onCurrentInputChange = { currentAreaInput = it },
                onAddItem = { area ->
                    addArea(area, areasList, { newAreas -> onDataChange(data.copy(preferredAreas = newAreas)) }) { currentAreaInput = "" }
                },
                onRemoveItem = { area ->
                    removeArea(area, areasList, { newAreas -> onDataChange(data.copy(preferredAreas = newAreas)) })
                },
                placeholder = "e.g., Kilimani, Kileleshwa, Westlands",
                suggestions = areaSuggestions,
                showDuplicateError = showDuplicateError
            )

            // Move-in Date
            OutlinedTextField(
                value = data.moveInDate,
                onValueChange = { onDataChange(data.copy(moveInDate = it)) },
                label = { Text("Move-in Date") },
                placeholder = { Text("MM/DD/YYYY") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
    }
}

@Composable
fun AreaInputSection(
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