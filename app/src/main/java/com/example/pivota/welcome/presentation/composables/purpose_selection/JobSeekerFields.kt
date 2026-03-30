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
fun JobSeekerFields(
    data: JobSeekerData,
    onDataChange: (JobSeekerData) -> Unit
) {
    var currentSkillInput by remember { mutableStateOf("") }
    var currentIndustryInput by remember { mutableStateOf("") }
    var currentJobTypeInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Parse skills from data.skills string into a list
    val skillsList = remember(data.skills) {
        data.skills.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Parse industries from data.industries string into a list
    val industriesList = remember(data.industries) {
        data.industries.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Parse job types from data.jobTypes string into a list
    val jobTypesList = remember(data.jobTypes) {
        data.jobTypes.split(",")
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
            // Auto-clear error after 2 seconds
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

    // Seniority Level options
    val seniorityLevels = listOf("Entry Level", "Mid Level", "Senior Level")

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
                text = "PURPOSE DETAILS: Job Seeker",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Headline
            OutlinedTextField(
                value = data.headline,
                onValueChange = { onDataChange(data.copy(headline = it)) },
                label = { Text("Professional Headline") },
                placeholder = { Text("e.g., Experienced Welder with 5+ years experience") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    Text(
                        "A brief professional summary that appears on your profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            // Actively Seeking Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Actively Seeking",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Show employers you're open to opportunities",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = data.isActivelySeeking,
                    onCheckedChange = { onDataChange(data.copy(isActivelySeeking = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }

            // Skills Section
            SkillInputSection(
                title = "Skills",
                itemsList = skillsList,
                currentInput = currentSkillInput,
                onCurrentInputChange = { currentSkillInput = it },
                onAddItem = { skill ->
                    addItem(skill, skillsList, { newSkills -> onDataChange(data.copy(skills = newSkills)) }) { currentSkillInput = "" }
                },
                onRemoveItem = { skill ->
                    removeItem(skill, skillsList, { newSkills -> onDataChange(data.copy(skills = newSkills)) })
                },
                placeholder = "e.g., Welding, Carpentry, Plumbing",
                suggestions = listOf("Welding", "Carpentry", "Plumbing", "Electrical", "Painting", "Masonry"),
                showDuplicateError = showDuplicateError
            )

            // Industries Section
            SkillInputSection(
                title = "Industries",
                itemsList = industriesList,
                currentInput = currentIndustryInput,
                onCurrentInputChange = { currentIndustryInput = it },
                onAddItem = { industry ->
                    addItem(industry, industriesList, { newIndustries -> onDataChange(data.copy(industries = newIndustries)) }) { currentIndustryInput = "" }
                },
                onRemoveItem = { industry ->
                    removeItem(industry, industriesList, { newIndustries -> onDataChange(data.copy(industries = newIndustries)) })
                },
                placeholder = "e.g., Construction, Manufacturing, Technology",
                suggestions = listOf("Construction", "Manufacturing", "Technology", "Healthcare"),
                showDuplicateError = showDuplicateError
            )

            // Job Types Section
            SkillInputSection(
                title = "Job Types",
                itemsList = jobTypesList,
                currentInput = currentJobTypeInput,
                onCurrentInputChange = { currentJobTypeInput = it },
                onAddItem = { jobType ->
                    addItem(jobType, jobTypesList, { newJobTypes -> onDataChange(data.copy(jobTypes = newJobTypes)) }) { currentJobTypeInput = "" }
                },
                onRemoveItem = { jobType ->
                    removeItem(jobType, jobTypesList, { newJobTypes -> onDataChange(data.copy(jobTypes = newJobTypes)) })
                },
                placeholder = "e.g., Full-time, Part-time, Contract",
                suggestions = listOf("Full-time", "Part-time", "Contract", "Freelance", "Remote"),
                showDuplicateError = showDuplicateError
            )

            // Seniority Level
            Text(
                text = "Seniority Level",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                seniorityLevels.forEach { level ->
                    FilterChip(
                        selected = data.seniorityLevel == level,
                        onClick = { onDataChange(data.copy(seniorityLevel = level)) },
                        label = { Text(level) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Expected Salary
            OutlinedTextField(
                value = data.expectedSalary,
                onValueChange = { onDataChange(data.copy(expectedSalary = it)) },
                label = { Text("Expected Salary (KES)") },
                placeholder = { Text("45,000") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
fun SkillInputSection(
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
                    // Use AssistChip with a custom clickable trailing icon
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