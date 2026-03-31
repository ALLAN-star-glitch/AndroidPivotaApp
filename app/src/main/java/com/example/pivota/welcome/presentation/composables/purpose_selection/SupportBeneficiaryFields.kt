package com.example.pivota.welcome.presentation.composables.purpose_selection

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.welcome.presentation.screens.SupportBeneficiaryData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SupportBeneficiaryFields(
    data: SupportBeneficiaryData,
    onDataChange: (SupportBeneficiaryData) -> Unit
) {
    var currentUrgentNeedInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val urgentNeedsList = remember(data.urgentNeeds) {
        data.urgentNeeds.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun addNeed(input: String) {
        val trimmed = input.trim()
        if (trimmed.isNotEmpty() && !urgentNeedsList.contains(trimmed)) {
            val updated = if (urgentNeedsList.isEmpty()) trimmed
            else "${urgentNeedsList.joinToString(", ")}, $trimmed"

            onDataChange(data.copy(urgentNeeds = updated))
            currentUrgentNeedInput = ""
            showDuplicateError = false
        } else {
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

    val supportOptions = listOf(
        "Food", "Shelter", "Medical", "Education", "Cash", "Other"
    )

    val urgentNeedSuggestions = listOf(
        "Food for family",
        "Emergency shelter",
        "Medical help",
        "School fees"
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
                text = "Quick Help Request",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Support Type
            Text(
                text = "What do you need help with?",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(supportOptions) { option ->
                    FilterChip(
                        selected = data.supportTypes.contains(option),
                        onClick = {
                            val updated = if (data.supportTypes.contains(option)) {
                                data.supportTypes - option
                            } else {
                                data.supportTypes + option
                            }
                            onDataChange(data.copy(supportTypes = updated))
                        },
                        label = { Text(option) }
                    )
                }
            }

            // Urgent Needs (short + simple)
            SimpleInputSection(
                title = "Describe your need (optional)",
                itemsList = urgentNeedsList,
                currentInput = currentUrgentNeedInput,
                onCurrentInputChange = { currentUrgentNeedInput = it },
                onAddItem = { addNeed(it) },
                onRemoveItem = { removeNeed(it) },
                placeholder = "e.g., Need food for children",
                suggestions = urgentNeedSuggestions,
                showDuplicateError = showDuplicateError
            )

            // Location (single field, not pills)
            OutlinedTextField(
                value = data.location,
                onValueChange = { onDataChange(data.copy(location = it)) },
                label = { Text("Your location (optional)") },
                placeholder = { Text("e.g., Kibera, Nairobi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun SimpleInputSection(
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )

        if (itemsList.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = {},
                        label = { Text(item) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItem(item) }
                            )
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = currentInput,
            onValueChange = onCurrentInputChange,
            placeholder = { Text(placeholder) },
            trailingIcon = {
                IconButton(
                    onClick = { onAddItem(currentInput) },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onAddItem(currentInput) }
            ),
            isError = showDuplicateError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (suggestions.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = { onAddItem(suggestion) },
                        label = { Text(suggestion) }
                    )
                }
            }
        }
    }
}